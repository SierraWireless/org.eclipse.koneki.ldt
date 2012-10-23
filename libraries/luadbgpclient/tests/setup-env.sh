#!/bin/bash
# setup all tests environments using LuaDist

TESTS_ROOT=$PWD/testroot
LUADIST_DIR=$TESTS_ROOT/luadist
LUADIST=$LUADIST_DIR/_install/bin/luadist

# as some scripts uses LuaDist internal functions, checkout a working version (as some internals may change without notice)
LUADIST_REV=215bc8569a897fd2af470b23a6eacb0859819a75

SRCDIR=$(cd $(dirname "$0"); pwd)

# install LuaDist
mkdir -p $TESTS_ROOT
if [[ ! -d $LUADIST_DIR ]]
then
  git clone git://github.com/LuaDist/bootstrap.git $LUADIST_DIR
  cd $LUADIST_DIR
  git checkout $LUADIST_REV
  git submodule update --init
  ./bootstrap
fi

# download test packages once for all
cd $SRCDIR/packages
/bin/sh download.sh

# write settings into setup-env.lua
sed -i "s/^local TESTS_ROOT = .*$/local TESTS_ROOT = '$(echo $TESTS_ROOT | sed -e 's/[\/&]/\\&/g' )'/g" $SRCDIR/run-tests.lua

echo "--------------------------------------------------------------------------------"
echo "-- Environment set up"
echo "-- Run tests with $LUADIST_DIR/_install/bin/lua run-tests.lua"
echo 
