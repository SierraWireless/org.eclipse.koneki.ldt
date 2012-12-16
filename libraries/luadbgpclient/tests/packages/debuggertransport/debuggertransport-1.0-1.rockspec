package = "DebuggerTransport"
version = "1.0-1"
source = {
    url = ""
}
description = {
    summary = "Minimal transport backed for Lua DBGP client.",
    detailed = [[
        This backend is Posix only and shoul  only be used for tests.
    ]],
    homepage = "http://eclipse.org/koneki/ldt/",
    license = "Eclipse Public License v1.0"
}
dependencies = {
    "lua >= 5.1"
}
build = {
    type = "builtin",
    modules = {
        debuggertransport = {
            sources = { "dbgtransport.c" },
            defines = { "BUILD_SERVER" },
        },
        ["debugger.transport.core"] = "core.lua",
    }
}
