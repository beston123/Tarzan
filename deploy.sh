#!/bin/bash

rm -rf target

mvn -Dmaven.test.skip=true clean install assembly:assembly -U
