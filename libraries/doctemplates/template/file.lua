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
return[[#
<div id="content">
# --
# -- Module name
# --
# if _file.name then
   <h1>Module <code>$(_file.name)</code></h1>
# end
# --
# -- Descriptions
# --
# if _file.shortdescription then
   $( format(_file.shortdescription) )
# end
# if _file.description then
   <br/>$( format(_file.description) )
# end
# --
# -- Handle "@usage" special tag
# --
#if _file.metadata and _file.metadata.usage and #_file.metadata.usage > 0 then
	<h2>Usage examples</h2>
#	for _, usage in ipairs( _file.metadata.usage ) do
		<pre><code>$( securechevrons(usage.description) )</code></pre>
#	end
#end
# --
# -- Show quick description of current type
# --
# 
# -- show quick description for globals
# if not isempty(_file.globalvars) then
	<h2>Global(s)</h2>
	<table class="function_list">
#	for _, item in sortedpairs(_file.globalvars) do
		<tr>
		<td class="name" nowrap="nowrap">$( fulllinkto(item) )</a></td>
		<td class="summary">$( format(item.shortdescription) )</td>
		</tr>
# 	end
	</table>
# end
#
# -- get type corresponding to this file (module)
# local currenttype 
# if not isempty(_file.returns) and _file.returns[1] and not isempty(_file.returns[1].types) then
#  	local typeref = _file.returns[1].types[1]
#  	if typeref.tag == "internaltyperef" then 
#			local typedef = _file.types[typeref.typename]
#			if typedef and typedef.tag == "recordtypdef" then
#				currenttype = typedef 
#			end
#		end 
#	end
#
# -- show quick description type exposed by module
# if currenttype then
	<a id="$(anchor(currenttype))" />
	<h2>Type <code>$(currenttype.name)</code></h2>
	$( applytemplate(currenttype, 'index') )
# end
# --
# -- Show quick description of other types
# --
# if _file.types then
#	for name, type in sortedpairs( _file.types ) do
#		if type ~= currenttype and type.tag == 'recordtypedef' then
			<a id="$(anchor(type))" />
			<h2>Type <code>$(name)</code></h2>
			$( applytemplate(type, 'index') )
#		end
#	end
# end
# --
# -- Long description of globals
# --
# if not isempty(_file.globalvars) then
	<h2>Global(s)</h2>
#	for name, item in sortedpairs(_file.globalvars) do
		$( applytemplate(item) )
#	end
# end
# --
# -- Long description of current type
# --
# if currenttype then
	<h2>Type <code>$(currenttype.name)</code></h2>
	$( applytemplate(currenttype) )
# end
# --
# -- Long description of other types
# --
# if not isempty( _file.types ) then
#	for name, type in sortedpairs( _file.types ) do
#		if type ~= currenttype  and type.tag == 'recordtypedef' then
			<h2>Type <code>$(name)</code></h2>
			$( applytemplate(type) )
#		end
#	end
# end
</div>
]]