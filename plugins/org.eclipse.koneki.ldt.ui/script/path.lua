--------------------------------------------------------------------------------
-- Copyright (c) 2011 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
--------------------------------------------------------------------------------

path = package.path
parse = true
while parse do
	last = string.find(path, ";")
	if last == nil then
		parse = false
		last = string.find(path, '?')
		if last == nil then
			print( path )
		else
			print( string.sub(path, 1, last - 1) )
		end
	else
		print( string.sub(path, 1, string.find(path, '?') - 1) )
		path = string.sub(path, last + 1);
	end
end

