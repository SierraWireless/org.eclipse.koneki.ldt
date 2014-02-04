package = "LuaXML"
version = "0.0.1-1"
source = {
    url = "http://manoelcampos.com/files/LuaXML-0.0.1-(lua5).tar.gz"
}
description = {
    summary = "Pure Lua XML parser.",
    detailed = [[
        This rockspec is only done for testing Lua DBGP client, do not use it in prod!
    ]],
    homepage = "http://lua-users.org/wiki/LuaXml",
    license = "MIT/X11"
}
dependencies = {
    "lua >= 5.1"
}
build = {
    type = "builtin",
    modules = {
        ["luaxml.xml"] = "xml.lua",
        ["luaxml.handler"] = "handler.lua",
        -- we don't need other stuff for now
    }
}
