from environment import Environment, j
import os

#Environment(os.path.join(os.getcwd(), "lua51")).lua("5.1.5").install_rock("luasocket")
testsrc = os.path.dirname(os.path.abspath(__file__))
testroot = j(testsrc, "test-environments")
dbgtransport = j(testsrc, "packages", "debuggertransport", "debuggertransport-1.0-1.rockspec")


Environment(j(testroot, "luajit2"), True) \
    .luajit("2.0.0") \
    .make(dbgtransport) \
    .finalize("debugger.transport.core")

Environment(j(testroot, "luajit2-52compat"), True) \
    .luajit("2.0.0", "XCFLAGS=-DLUAJIT_ENABLE_LUA52COMPAT") \
    .make(dbgtransport) \
    .finalize("debugger.transport.core")

Environment(j(testroot, "lua51-core"), True) \
    .lua("5.1.5") \
    .make(dbgtransport) \
    .finalize("debugger.transport.core")

Environment(j(testroot, "lua52"), True) \
    .lua("5.2.1") \
    .make(dbgtransport) \
    .finalize("debugger.transport.core")

Environment(j(testroot, "lua51-socket"), True) \
    .lua("5.1.5") \
    .install("luasocket") \
    .finalize("debugger.transport.luasocket")
