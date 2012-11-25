#!/bin/sh
# Downloads files that are required to build test environments

# Lua VMs
wget -O lua-5.1.5.tar https://github.com/LuaDist/lua/tarball/5.1.5
wget -O lua-5.2.tar https://github.com/LuaDist/lua/tarball/5.2
wget -O luajit-2.0.0-beta10.tar https://github.com/LuaDist/luajit/tarball/2.0.0-beta10
#~ wget -O luajit-1.1.8.tar https://github.com/LuaDist/luajit/tarball/1.1.8

# LuaSocket (unstable should brings 5.2 compat in near future)
wget -O luasocket-2.0.2.tar https://github.com/LuaDist/luasocket/tarball/2.0.2
#~ wget -O luasocket-unstable.tar https://github.com/LuaDist/luasocket/tarball/unstable

# LuaSys
#~ wget -O luasys-1.6.tar https://github.com/LuaDist/luasys/tarball/1.6

# Apache Portable Runtime (currently broken)
#~ wget -O apr-master.tar https://github.com/LuaDist/apr/tarball/master
#~ wget -O apr-util-master.tar https://github.com/LuaDist/apr-util/tarball/master
#~ wget -O lua-apr-master.tar https://github.com/LuaDist/lua-apr/tarball/master

# extract them to their directory (skip GitHub root dir with commit id)
for tarball in *.tar
do
  mkdir "${tarball%.*}"
  tar x --strip-components=1 --directory "${tarball%.*}" --file "$tarball"
  rm "$tarball"
done
