#!/bin/sh

rm -rf target

mvn -Dmaven.test.skip=true clean package install assembly:assembly -U