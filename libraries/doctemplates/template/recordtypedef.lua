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
return [[#
<a id ="$(anchor(_recordtypedef))"></a>
# --
# -- Descriptions
# --
#if _recordtypedef.shortdescription then
	<p>$( format( _recordtypedef.shortdescription ) )</p>
#end
#if _recordtypedef.description then
	<p>$( format( _recordtypedef.description ) )</p>
#end
#--
#-- Describe usage
#--
#if _recordtypedef.metadata and _recordtypedef.metadata.usage then
	<h2>Usage examples</h2>
#	for _, usage in ipairs(_recordtypedef.metadata.usage) do
		<pre><code>$( securechevrons(usage.description) )</code></pre>
#	end
#end
# --
# -- Describe type fields
# --
#if not isempty( _recordtypedef.fields ) then
	<h3>Field(s)</h3>
#	for name, item in sortedpairs( _recordtypedef.fields ) do
		$( applytemplate(item) )
#	end
#end ]]
