--------------------------------------------------------------------------------
--  Copyright (c) 2011-2012 Sierra Wireless.
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
--
--  Contributors:
--       Kevin KIN-FOO <kkinfoo@sierrawireless.com>
--           - initial API and implementation and initial documentation
--------------------------------------------------------------------------------
local M = {}

local javaapimodelfactory =  require 'javaapimodelfactory'

local print = function (string) print(string) io.flush() end

local templateengine = require 'templateengine'

--
-- Update documentation templateengine environment
--
local templateengineenv = require 'template.utils'

-- Remove default implementation not supported from IDE
templateengineenv.anchortypes['externaltyperef'] = nil
templateengineenv.linktypes['externaltyperef'] = nil

--
-- So far, documentation embedded in the IDE does not support links very well.
-- To circumvent this, we will need to desactivate link generation while
-- generation documentation.

-- Cache of link generation
local originallinkto = templateengineenv.linkto

-- Restore link generators
local function enablelinks()
	templateengine.env.linkto = originallinkto
end

-- Disable link generators
local function disablelinks()
	templateengine.env.linkto = function()
		return nil, 'Link generation is disabled.'
	end
end

-- Links are disabled by default
disablelinks()

-- Handle only local item references
templateengineenv.linktypes['item'] = function(item)
	if item.parent and item.parent.tag == 'recordtypedef' then
		return string.format('#%s.%s', templateengineenv.anchor(item.parent), item.name)
	end
	return string.format('#%s', templateengineenv.anchor(item))
end

-- Perform actual environment update
for functionname, body in pairs( templateengineenv ) do
	templateengine.env[ functionname ] = body
end

-- create typeref
function M._typeref (_type)
	if not _type then return nil end
	if _type.tag == "externaltyperef" then
		return javaapimodelfactory.newexternaltyperef(_type.modulename, _type.typename)
	elseif _type.tag == "internaltyperef" then
		return javaapimodelfactory.newinternaltyperef(_type.typename)
	elseif _type.tag == "moduletyperef" then
		return javaapimodelfactory.newmoduletyperef(_type.modulename,_type.returnposition)
	elseif _type.tag == "exprtyperef" then
		return javaapimodelfactory.newexprtyperef(_type.returnposition)
	elseif _type.tag == "primitivetyperef" then
		return javaapimodelfactory.newprimitivetyperef(_type.typename)
	end
end

-- create item
function M._item(_item,notemplate)
	local description = ""
	if not notemplate then 
		description = templateengine.applytemplate(_item)
	end 

	local jitem = javaapimodelfactory.newitem(_item.name,
																						description,
																						_item.sourcerange.min,
																						_item.sourcerange.max,
																						M._typeref(_item.type))

	return jitem
end

-- create typedef
function M._typedef(_typedef)
	local jtypedef
	-- Dealing with records
	if _typedef.tag == "recordtypedef" then

		jtypedef = javaapimodelfactory.newrecordtypedef(_typedef.name,
		                                                templateengine.applytemplate(_typedef),
		 																								_typedef.sourcerange.min,
		 																								_typedef.sourcerange.max)

		-- Appending fields
		for _, _item in pairs(_typedef.fields) do
			local jitem =  M._item(_item)
			javaapimodelfactory.addfield(jtypedef, jitem)
		end

	elseif _typedef.tag == "functiontypedef" then
		-- Dealing with function
		jtypedef = javaapimodelfactory.newfunctiontypedef()

		-- Appending parameters
		for _, _param in ipairs(_typedef.params) do
			javaapimodelfactory.addparam(jtypedef,_param.name, M._typeref(_param.type), _param.description)
		end

		-- Appending returned types
		for _, _return in ipairs(_typedef.returns) do
			local jreturn = javaapimodelfactory.newreturn()
			for _, _type in ipairs( _return.types ) do
				javaapimodelfactory.addtype(jreturn,M._typeref(_type))
			end
			javaapimodelfactory.functionaddreturn(jtypedef,jreturn)
		end
	end
	return jtypedef
end

-- create lua file api
function M._file(_file)

	-- Enable links just for module file objects 
	enablelinks()
	local jfile = javaapimodelfactory.newfileapi(templateengine.applytemplate(_file))
	disablelinks()

	-- Adding gloval variables
	for _, _item in pairs(_file.globalvars) do
		-- Fill Java item
		javaapimodelfactory.addglobalvar(jfile,M._item(_item))
	end

	-- Adding returned types
	for _, _return in ipairs(_file.returns) do
		local jreturn = javaapimodelfactory.newreturn()
		for _, _type in ipairs( _return.types ) do
			javaapimodelfactory.addtype(jreturn,M._typeref(_type))
		end
		javaapimodelfactory.fileapiaddreturn(jfile,jreturn)
	end

	-- Adding types defined in files
	for _, _typedef in pairs(_file.types) do
		javaapimodelfactory.addtypedef(jfile,_typedef.name,M._typedef(_typedef))
	end

	return jfile
end
return M
