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

require 'lunatest'
local function parse( source )
	if type(source) ~= 'string' then return false, "'string' expected" end
	require 'metalua.compiler'
	local tree = mlc.luastring_to_ast( source )
	return pcall( index, tree )
end

function test_set()
	local status, err = parse ("set = nil")
	assert_true(status, err)
end
