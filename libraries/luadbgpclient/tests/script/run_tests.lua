-------------------------------------------------------------------------------
-- Copyright (c) 2011 Sierra Wireless and others.
-- All rights reserved. This program and the accompanying materials
-- are made available under the terms of the Eclipse Public License v1.0
-- which accompanies this distribution, and is available at
-- http://www.eclipse.org/legal/epl-v10.html
--
-- Contributors:
--     Sierra Wireless - initial API and implementation
-------------------------------------------------------------------------------
-- Run tests in console using plain Lua interpreter. 
-- The package "lunatest" MUST be in package.path before running ths script.
-- This script must be run it's own directory.

local CODE_PATH = "../../../bundles/org.eclipse.koneki.embedded.lua.core/script"
local PATH_SEP = package.config:sub(3,3)

package.path = package.path .. PATH_SEP .. CODE_PATH .. "/?.lua" .. PATH_SEP .. "./?.lua"

local lunatest = require "lunatest"

lunatest.suite "test_debugintrospection"
lunatest.run()
