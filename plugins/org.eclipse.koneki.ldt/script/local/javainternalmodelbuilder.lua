--------------------------------------------------------------------------------
--  Copyright (c) 2012 Sierra Wireless.
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
--
--  Contributors:
--       Kevin KIN-FOO <kkinfoo@sierrawireless.com>
--           - initial API and implementation and initial documentation
--------------------------------------------------------------------------------
local J = {}
local javaapimodelbuilder = require 'javaapimodelbuilder'

local javainternalmodelfactory =  require 'javainternalmodelfactory'
local javaapimodelfactory =  require 'javaapimodelfactory'  

--------------------------------------
-- create internal content java object
function J._internalcontent(_internalcontent)

	-- Setting body
	local handledexpr ={}
	local jblock = J._block(_internalcontent.content,handledexpr)
	local jinternalcontent = javainternalmodelfactory.newinternalmodel(jblock)

	-- Appending global variables
	for _, _item in ipairs(_internalcontent.unknownglobalvars) do
		local jitem = javaapimodelbuilder._item(_item)
		javainternalmodelfactory.addunknownglobalvar(jinternalcontent,jitem)

		-- add occurrences
		for _,_occurrence in ipairs(_item.occurrences) do
			jidentifier = handledexpr[_occurrence]
			if jidentifier then
				javaapimodelfactory.addoccurrence(jitem,jidentifier)
			end
		end
	end

	return jinternalcontent
end

--------------------------------------
-- create block java object
function J._block(_block,handledexpr)
	-- Setting source range
	local jblock = javainternalmodelfactory.newblock(_block.sourcerange.min,
																						 _block.sourcerange.max)

	-- Append nodes to block
	for _, _expr in pairs(_block.content) do
		local jexpr = J._expression(_expr,handledexpr)
		javainternalmodelfactory.addcontent(jblock,jexpr)
	end

	for _, _localvar in pairs(_block.localvars) do
		-- Create Java item
		local jitem = javaapimodelbuilder._item(_localvar.item,true)
		if _localvar.item.type and _localvar.item.type.tag == "exprtyperef" then
			javaapimodelfactory.setexpression(jitem,handledexpr[_localvar.item.type.expression])
		end

		-- add occurrence
		for _,_occurrence in ipairs(_localvar.item.occurrences) do
			jidentifier = handledexpr[_occurrence]
			if jidentifier then
				javaapimodelfactory.addoccurrence(jitem,jidentifier)
			end
		end

		-- Append Java local variable definition
		local jlocalvar = javainternalmodelfactory.newlocalvar(jitem, _localvar.scope.min, _localvar.scope.max) 
		javainternalmodelfactory.addlocalvar(jblock,jlocalvar)
	end
	return jblock
end

--------------------------------------
-- create expression java object
function J._expression(_expr,handledexpr)
	local tag = _expr.tag
	if tag == "MIdentifier" then
		return J._identifier(_expr,handledexpr)
	elseif tag == "MIndex" then
		return J._index(_expr,handledexpr)
	elseif tag == "MCall" then
		return J._call(_expr,handledexpr)
	elseif tag == "MInvoke" then
		return J._invoke(_expr,handledexpr)
	elseif tag == "MBlock" then
		return J._block(_expr,handledexpr)
	end
	return nil
end

--------------------------------------
-- create identifier java object
 function J._identifier(_identifier,handledexpr)
	local jidentifier = javainternalmodelfactory.newidentifier(_identifier.sourcerange.min,
																												_identifier.sourcerange.max)
	handledexpr[_identifier] =jidentifier
	return jidentifier
end

--------------------------------------
-- create index java object
function J._index(_index,handledexpr)
  local jindex = javainternalmodelfactory.newindex(_index.sourcerange.min,
																							_index.sourcerange.max,
																							J._expression(_index.left,handledexpr),
																							_index.right)

	handledexpr[_index] =jindex
	return jindex
end

--------------------------------------
-- create call java object
function J._call(_call,handledexpr)
	local jcall = javainternalmodelfactory.newcall(_call.sourcerange.min,
																						 _call.sourcerange.max,
																						 J._expression(_call.func,handledexpr))

	handledexpr[_call] =jcall
	return jcall
end

--------------------------------------
-- create invoke java object
function J._invoke(_invoke,handledexpr)
	local jinvoke = javainternalmodelfactory.newinvoke(_invoke.sourcerange.min,
																						 	  _invoke.sourcerange.max,
																						    _invoke.functionname,
																						    J._expression(_invoke.record,handledexpr))
																						 
	handledexpr[_invoke] =jinvoke
	return jinvoke
end
return J
