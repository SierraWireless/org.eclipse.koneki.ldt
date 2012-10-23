-------------------------------------------------------------------------------
-- Copyright (c) 2012 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
-------------------------------------------------------------------------------

---
-- @module tablecompare
--
local M = {}

local function checks() end

local function pathconcat(pt, starti, endi)
	local t = {}
	local prev
	local empties = 0
	starti = starti or 1
	endi = endi or #pt

	for i = starti, endi do
		local v = pt[i]
		if not v then break
		elseif v == '' then
			empties = empties+1
		else
			table.insert(t, prev)
			prev = v
		end
	end
	table.insert(t, prev)
	return table.concat(t, '.', 1, endi-starti+1-empties)
end

--------------------------------------------------------------------------------
-- Cleans a path.
--
-- Removes trailing/preceding/doubling '.'.
--
-- @function [parent=#tablecompare] clean
-- @param path string containing the path to clean.
-- @return cleaned path as a string.
--
function M.clean(path)
	checks('string')
	local p = M.segments(path)
	return pathconcat(p)
end

--
-- @function [parent=#tablecompare] recursivePairs
-- @param t table to iterate.
-- @param prefix path prefix to prepend to the key path returned.
-- @return iterator function.
-- @usage {toto={titi=1, tutu=2}, tata = 3, tonton={4, 5}} will iterate through
-- ("toto.titi",1), ("toto.tutu",2), ("tata",3) ("tonton.1", 4), ("tonton.2"=5)
--
function M.recursivepairs(t, prefix)
	checks('table', '?string')
	local function it(t, prefix, cp)
		cp[t] = true
		local pp = prefix == "" and prefix or "."
		for k, v in pairs(t) do
			k = pp..tostring(k)
			if type(v) == 'table' then
				if not cp[v] then it(v, prefix..k, cp) end
			else
				coroutine.yield(prefix..k, v)
			end
		end
		cp[t] = nil
	end

	prefix = prefix or ""
	return coroutine.wrap(function() it(t, M.clean(prefix), {}) end)
end

--------------------------------------------------------------------------------
-- Splits a path into segments.
--
-- Each segment is delimited by '.' pattern.
--
-- @function [parent=#tablecompare] segments
-- @param path string containing the path to split.
-- @return list of split path elements.
--
function M.segments(path)
	checks('string')
	local t = {}
	local index, newindex, elt = 1
	repeat
		newindex = path:find(".", index, true) or #path+1 --last round
		elt = path:sub(index, newindex-1)
		elt = tonumber(elt) or elt
		if elt and elt ~= "" then table.insert(t, elt) end
		index = newindex+1
	until newindex==#path+1
	return t
end

---
-- @function [parent=#tablecompare] recursivepairs
-- @param t table to iterate.
-- @param prefix path prefix to prepend to the key path returned.
-- @return iterator function.
-- @usage {toto={titi=1, tutu=2}, tata = 3, tonton={4, 5}} will iterate through
-- ("toto.titi",1), ("toto.tutu",2), ("tata",3) ("tonton.1", 4), ("tonton.2"=5)
--
function M.recursivepairs(t, prefix)
	checks('table', '?string')
	local function it(t, prefix, cp)
		cp[t] = true
		local pp = prefix == "" and prefix or "."
		for k, v in pairs(t) do
			k = pp..tostring(k)
			if type(v) == 'table' then
				if not cp[v] then it(v, prefix..k, cp) end
			else
				coroutine.yield(prefix..k, v)
			end
		end
		cp[t] = nil
	end

	prefix = prefix or ""
	return coroutine.wrap(function() it(t, M.clean(prefix), {}) end)
end

function M.diff(t1, t2, norecurse)
	local d = {}
	local t3 = {}
	local rpairs = norecurse and pairs or M.recursivepairs
	for k, v in rpairs(t1) do t3[k] = v end
	for k, v in rpairs(t2) do
		if v ~= t3[k] then
			table.insert(d, k)
		end
		t3[k] = nil
	end
	for k, v in pairs(t3) do
		table.insert(d, k)
	end
	return d
end
---
-- @function [parent=#tablecompare] compare
-- @param #table t1
-- @param #table t2
--
local ignoredtypes = {
	['function'] = true,
	['thread']   = true,
	['userdata'] = true,
}

function M.compare(t1, t2)

	-- Build t1 copy
	local t3 = {}
	for k,v in M.recursivepairs(t1) do
		t3[k] = v
	end

	-- Browse recursively for differences with t2
	local differences = {}
	for k, v in M.recursivepairs( t2 ) do
		local t3valuetype = type( t3[k] )
		local t2valuetype = type( v )

		-- Values are different when their type differ
		if t3valuetype ~= t2valuetype then
			table.insert(differences, k)
		elseif not ignoredtypes[t3valuetype] and v ~= t3[k] then
			-- Same type but different values
			table.insert(differences, k)
		end
		t3[k] = nil
	end

	-- Loacate t1 keys which are not in t2
	for k, v in M.recursivepairs( t3 ) do
		table.insert(differences, k)
	end
	return differences
end
---
-- @function [parent=#tablecompare] stripfunctions
-- @param #table tab Table to strip
-- @return #table Table stripped from functions
--
function M.stripfunctions(tab, visitedtables)

	-- Avoid infinite self referenced table browsing 
	visitedtables = visitedtables or {}
	visitedtables[tab] = true

	for k, v in pairs( tab ) do
		local typev = type(v)
		if typev == 'function' then
			tab[k] = nil
		elseif typev == 'table' and not visitedtables[v] then
			M.stripfunctions(v, visitedtables)
		end
	end
	return tab
end
return M
