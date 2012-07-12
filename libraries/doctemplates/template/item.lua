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
return 
[[<a id="$(anchor(_item))" />
<dl class="function">
<dt>
# --
# -- Resolve item type definition
# --
# local typedef
#if  _item.parent and _item.type and _item.type.tag == 'internaltyperef' then
#	if _item.parent.tag == 'recordtypedef' then
#		local file = _item.parent.parent
#		typedef = file.types[ _item.type.typename ]
#	elseif _item.parent.tag == 'file' then
#		typedef = _item.parent.types[ _item.type.typename ]
#	end
#end
# --
# -- Show item type for internal type
# --
#if _item.type and (not typedef or typedef.tag ~= 'functiontypedef') then
#	--Show link only when available 
#	local link = fulllinkto(_item.type)
#	if link then
		<em>$( link )</em>
#	else
		<em>$(prettyname(_item.type))</em>
#	end
#end
<strong>$( prettyname(_item) )</strong>
</dt>
<dd>
# if _item.shortdescription then
	$( format(_item.shortdescription) )
# end
# if _item.description and #_item.description > 0 then
	<br/>$( format(_item.description) )
# end
#
# --
# -- For function definitions, describe parameters and return values
# --
#if typedef and typedef.tag == 'functiontypedef' then
#	--
#	-- Describe parameters
#	--
#	local fdef = typedef
#
#	-- Adjust parameter count if first one is 'self'
#	local paramcount
#	if #fdef.params > 0 and fdef.params[1].name == 'self' then
#		paramcount = #fdef.params - 1
#	else
#		paramcount = #fdef.params
#	end
#
#	-- List parameters
#	if paramcount > 0 then
		<h$(i)>Parameter$( paramcount > 1 and 's' )</h$(i)>
		<ul>
#		for position, param in ipairs( fdef.params ) do
#			if not (position == 1 and param.name == 'self') then
				<li>
				<code><em>$(param.name) $(param.optional and 'optional') $(param.hidden and 'hidden')</em></code>: $( format(param.description) )
				</li>
#			end
#		end
		</ul>
#	end
#
#	--
#	-- Describe returns types
#	--
#	if fdef and #fdef.returns > 0 then
		<h$(i)>Return value$(#fdef.returns > 1 and 's')</h$(i)>
#		--
#		-- Format nice type list
#		--
#		local function niceparmlist( parlist )
#			local typelist = {}
#			for position, type in ipairs(parlist) do
#				typelist[#typelist + 1] = prettyname( type )
#				-- Append end separator or separating comma 
#				typelist[#typelist + 1] = position == #parlist and ':' or ', '
#			end
#			return table.concat( typelist )
#		end
#		--
#		-- Generate a list if they are several return clauses
#		--
#		if #fdef.returns > 1 then
			<ol>
#			for position, ret in ipairs(fdef.returns) do
				<li>
#				if #ret.types > 0 and #niceparmlist(ret.types) > 0 then
					<em>$( niceparmlist(ret.types) )</em>
#				end
				$(ret.description and format(ret.description))
				</li>
#			end
			</ol>
#		else
#			local isreturn = fdef.returns and #fdef.returns > 0 and #niceparmlist(fdef.returns[1].types) > 0
#			local isdescription = fdef.returns and fdef.returns[1].description and #format(fdef.returns[1].description) > 0
#			if isreturn or isdescription  then
				<p>
#			end
#			-- Show return type if provided
#			if isreturn then
				<em>$( niceparmlist(fdef.returns[1].types) )</em>
#			end
#			if isdescription then
				$( format(fdef.returns[1].description) )
#			end
#			if isreturn or isdescription then
				</p>
#			end
#		end
#	end
#end
#--
#-- Show usage samples
#--
#if _item.metadata and _item.metadata.usage then
	$( applytemplate(_item.metadata.usage, i) )
#end
</dd>
</dl>]]
