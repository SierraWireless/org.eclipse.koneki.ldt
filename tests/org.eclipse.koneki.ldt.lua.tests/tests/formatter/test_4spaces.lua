require 'errnode'
local formatter = require 'luaformatter'
local string = require 'string'
local javaassert = java.require("org.junit.Assert")

local M = {}
function M.test(luainputpath, luareferencepath)

	-- Load provided source
	local luafile, errormessage = io.open(luainputpath, 'r')
	assert(
		luafile,
		string.format('Unable to read from %s.\n%s', luainputpath, errormessage or '')
	)
	local luasource = luafile:read('*a')
	luafile:close()

	-- format code
	local formattedCode = formatter.indentcode(luasource, '\n', true, '    ')
	assert(
		formattedCode,
		string.format('Unable to format %s.\n', luainputpath)
	)

	-- Load provided reference
	local referenceFile, errormessage = io.open(luareferencepath)
	assert(
		referenceFile,
		string.format('Unable to read reference from %s.\n%s', luareferencepath, errormessage or '')
	)
	local referenceCode = referenceFile:read('*a')

	-- Check equality by catching assertException from assertEquals
	local status, errormessage = pcall( function()
		javaassert:assertEquals("Formatting Error", referenceCode, formattedCode)
	end)
	return status or nil, tostring(errormessage)
end
return M
