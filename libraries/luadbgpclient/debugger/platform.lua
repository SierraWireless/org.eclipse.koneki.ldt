-------------------------------------------------------------------------------
-- Copyright (c) 2011-2012 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
-------------------------------------------------------------------------------
-- Platform/OS specific features and path handling.
-------------------------------------------------------------------------------

local url = require "debugger.url"
local util = require "debugger.util"

local M = { }

-- Get the execution plaform os (could be win or unix)
-- Used to manage file path difference between the 2 platform
local platform = nil

-- keep all computed URIs in cache (as they are quite long to compute)
local uri_cache = { }

-- parse a normalized path and return a table of each segment
-- you could precise the path seperator.
local function split(path,sep)
  local t = {}
  for w in path:gmatch("[^"..(sep or "/").."]+")do
    table.insert(t, w)
  end
  return t
end

--- Returns a RFC2396 compliant URI for given source, or false if the mapping failed
local function get_abs_file_uri (source)
    local uri
    if source:sub(1,1) == "@" then -- real source file
        local sourcepath = source:sub(2)
        local normalizedpath = M.normalize(sourcepath)
        if not M.is_path_absolute(normalizedpath) then
            normalizedpath = M.normalize(M.base_dir .. "/" .. normalizedpath)
        end
        return M.to_file_uri(normalizedpath)
    else -- dynamic code, stripped bytecode, tail return, ...
        return false
    end
end

--FIXME: as result is cached, changes in package.path that modify the module name are missed
-- (mostly affect main module when Lua interpreter is launched with an absolute path)
local function get_module_uri (source)
    if source:sub(1,1) == "@" then -- real source file
        local uri
        local sourcepath = source:sub(2)
        local normalizedpath = M.normalize(sourcepath)
        local luapathtable = split (package.path, ";")
        local is_source_absolute = M.is_path_absolute(sourcepath)
        -- workarround : Add always the ?.lua entry to support
        -- the case where file was loaded by : "lua myfile.lua"
        table.insert(luapathtable,"?.lua")
        for i,var in ipairs(luapathtable) do
            -- avoid relative patterns matching absolute ones (e.g. ?.lua matches anything)
            if M.is_path_absolute(var) == is_source_absolute then
                local escaped = string.gsub(M.normalize(var),"[%^%$%(%)%%%.%[%]%*%+%-%?]",function(c) return "%"..c end)
                local pattern = string.gsub(escaped,"%%%?","(.+)")
                local modulename = string.match(normalizedpath,pattern)
                if modulename then
                    modulename = string.gsub(modulename,"/",".");
                    -- if we find more than 1 possible modulename return the shorter
                    if not uri or string.len(uri)>string.len(modulename) then
                        uri = modulename
                    end
                end
            end
        end
        if uri then return "module:///"..uri end
    end
    return false
end

function M.get_uri (source)
    -- search in cache
    local uri = uri_cache[source]
    if uri ~= nil then return uri end

    -- not found, create uri
    if util.features.uri == "module" then
        uri = get_module_uri(source)
        if not uri then uri = get_abs_file_uri (source) end
    else
        uri =  get_abs_file_uri (source)
    end

    uri_cache[source] = uri
    return uri
end

-- get path file from uri
function M.get_path (uri)
    local parsed_path = assert(url.parse(uri))
    if parsed_path.scheme == "file" then
        return M.to_path(parsed_path)
    else
        -- search in cache
        -- we should surely calculate it instead of find in cache
        for k,v in pairs(uri_cache)do
            if v == uri then
                assert(k:sub(1,1) == "@")
                return k:sub(2)
            end
        end
    end
end

function M.normalize(path)
    local parts = { }
    for w in path:gmatch("[^/]+") do
        if     w == ".." then table.remove(parts)
        elseif w ~= "."  then table.insert(parts, w)
        end
    end
    return (path:sub(1,1) == "/" and "/" or "") .. table.concat(parts, "/")
end

function M.init(executionplatform,workingdirectory)
    --------------------------
    -- define current platform
    --------------------------
    -- check parameter
    if executionplatform and executionplatform ~= "unix" and executionplatform ~="win" then
        error("Unable to initialize platform module : execution platform should be 'unix' or 'win'.")
    end

    -- use parameter as current platform
    if executionplatform then
        platform = executionplatform
    else
        --if not define try to guess it.
        local function iswindows()
            local p = io.popen("echo %os%")
            if p then
                local result =p:read("*l")
                p:close()
                return result == "Windows_NT"
            end
            return false
        end
        
        status, iswin = pcall(iswindows)
        if status and iswin then
            platform = "win"
        else
            platform = "unix"
        end
    end

    --------------------------
    -- platform dependent function
    --------------------------
    if platform == "unix" then
        -- The Path separator character
        M.path_sep = "/"

        -- TODO the way to get the absolute path can be wrong if the program loads new source files by relative path after a cd.
        -- currently, the directory is registered on start, this allows program to load any source file and then change working dir,
        -- which is the most common use case.
        M.base_dir = workingdirectory or os.getenv("PWD")

        -- convert parsed URL table to file path  for the current OS (see url.parse from luasocket)
        M.to_file_uri = function (path) return url.build{scheme="file",authority="", path=path} end

        -- return true is the path is absolute
        -- the path must be normalized
        M.is_path_absolute = function (path) return path:sub(1,1) == "/" end

        -- convert absolute normalized path file to uri
        M.to_path = function (parsed_url) return url.unescape(parsed_url.path) end
    else
        -- Implementations for Windows, see UNIX versions for documentation.
        M.path_sep = "\\"
        M.is_path_absolute = function (path) return path:match("^%a:/") end
        M.to_file_uri = function (path) return url.build{scheme="file",authority="", path="/"..path} end
        M.to_path = function (parsed_url) return url.unescape(parsed_url.path):gsub("^/", "") end

        local unixnormalize = M.normalize
        M.normalize = function(path) return unixnormalize(path:gsub("\\","/"):lower()) end

        -- determine base dir
        local function getworkingdirectory()
            local p = io.popen("echo %cd%")
            if p then
                local res = p:read("*l")
                p:close()
                return M.normalize(res)
            end
        end
        M.base_dir = workingdirectory or getworkingdirectory()

    end

    if not M.base_dir then error("Unable to determine the working directory.") end
end

return M
