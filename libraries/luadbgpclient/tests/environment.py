#!/usr/bin/env python
# Lua DBGP client test bootstrapping code
# this file will download and compile all needed components to run tests

import os
import subprocess
import urllib2
import tarfile
import os
import shutil
import re
import cStringIO as StringIO

j = os.path.join
scriptdir = os.path.dirname(os.path.abspath(__file__))

def patch(path, *patterns):
    with open(path, "r") as f:
        lines = f.readlines()
    with open(path, "w") as f:
        for line in lines:
            for exp, repl in patterns: line = re.sub(exp, repl, line)
            f.write(re.sub(r'^# deb', 'deb', line))

def cleanpath(p):
    if os.path.exists(p): shutil.rmtree(p)
    os.makedirs(p)

def download(url, to):
    # as gzip module needs to seek in stream, we need to buffer file to download
    # hopefully, it never gets so big, so use memory buffer
    mode = "r"
    if url.endswith(".tar.gz") or url.endswith(".tgz"): mode = "r:gz"
    elif url.endswith(".tar.bz2"): mode = "r:bz2"

    buf = StringIO.StringIO()
    shutil.copyfileobj(urllib2.urlopen(url), buf)
    buf.seek(0)
    tarfile.open(fileobj=buf, mode=mode).extractall(to)

class Environment:
    def __init__(self, basedir, clear):
        self.basedir = os.path.abspath(basedir)
        self.builddir = j(basedir, "build")
        if clear:
            cleanpath(basedir)
            os.makedirs(self.builddir)

    def luajit(self, version, *additional_opts):
        download("http://luajit.org/download/LuaJIT-%s.tar.gz" % version, self.builddir)
        #download("http://localhost:8000/LuaJIT-%s.tar.gz" % version, self.builddir)
        cwd = j(self.builddir, "LuaJIT-"+version)
        opts = [ "PREFIX="+self.basedir, "INSTALL_INC="+j(self.basedir, "include") ] + list(additional_opts)
        subprocess.check_call([ "make" ] + opts, cwd=cwd)
        subprocess.check_call([ "make", "install" ] + opts, cwd=cwd)
        # copy on windows + .exe suffix
        os.symlink(j(self.basedir, "bin", "luajit"), j(self.basedir, "bin", "lua"))
        self._luarocks()
        return self

    def lua(self, version):
        download("http://www.lua.org/ftp/lua-%s.tar.gz" % version, self.builddir)
        #download("http://localhost:8000/lua-%s.tar.gz" % version, self.builddir)
        cwd = j(self.builddir, "lua-"+version)
        patch(j(cwd, "src", "luaconf.h"),
            ("^#define LUA_ROOT.*$", '#define LUA_ROOT "%s"' % (self.basedir + os.sep)))
        subprocess.check_call(("make", "linux"), cwd=cwd)
        subprocess.check_call(("make", "install", "INSTALL_TOP="+self.basedir), cwd=cwd)
        self._luarocks()
        return self

    def install(self, *args):
        bin = j(self.basedir, "bin", "luarocks")
        subprocess.check_call([bin, "install"] + list(args))
        return self

    def make(self, package):
        srcdir, rockspec = os.path.split(os.path.abspath(package))
        cwd = j(self.builddir, os.path.basename(srcdir))
        if os.path.exists(cwd): shutil.rmtree(cwd)
        shutil.copytree(srcdir, cwd)
        bin = j(self.basedir, "bin", "luarocks")
        subprocess.check_call([bin, "make", rockspec], cwd=cwd)
        return self

    def _luarocks(self, version="2.0.12"):
        # windows => http://luarocks.org/releases/luarocks-%s-win32.zip
        download("http://luarocks.org/releases/luarocks-%s.tar.gz" % version, self.builddir)
        #download("http://localhost:8000/luarocks-%s.tar.gz" % version, self.builddir)
        cwd = j(self.builddir, "luarocks-"+version)
        subprocess.check_call(("./configure", "--prefix="+self.basedir,
                                              "--with-lua="+self.basedir), cwd=cwd)
        subprocess.check_call(("make",), cwd=cwd)
        subprocess.check_call(("make", "install"), cwd=cwd)
        # tell LuaRocks to not use LuaSocket (as we need to open https links)
        with open(j(self.basedir, "etc", "luarocks", "config.lua"), "a") as f:
            f.write("\nfs_use_modules = false\n")


    def finalize(self, transport_name):
        self.install("https://raw.github.com/jdesgats/lunatest/master/lunatest-scm-0.rockspec")
        self.make(j(scriptdir, "packages", "luaxml", "luaxml-0.0.1-1.rockspec"))
        # make runner
        shutil.copy(j(scriptdir, "runner.lua"), self.basedir)
        patch(j(self.basedir, "runner.lua"),
            ("^local DEBUGGER_SOURCE = .*$", "local DEBUGGER_SOURCE = [[%s]]" % os.path.abspath(j(scriptdir, ".."))),
            ("^DBGP_TRANSPORT = .*$", "DBGP_TRANSPORT = [[%s]]" % transport_name))
        return self

