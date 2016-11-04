#!/bin/sh

#===========================================================================================
# Java Environment Setting
#===========================================================================================
error_exit ()
{
    echo "ERROR: $1 !!"
    exit 1
}

[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
[ ! -e "$JAVA_HOME/bin/java" ] && error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)!"

export JAVA_HOME

export TEVENT_HOME=$(dirname "$PWD")
export LIB_PATH=${TEVENT_HOME}/lib
export CLASSPATH=.:${TEVENT_HOME}/conf:${CLASSPATH}

#===========================================================================================
# JVM Configuration
#===========================================================================================
JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m -XX:PermSize=128m -XX:MaxPermSize=320m"
JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8 -XX:+DisableExplicitGC"
JAVA_OPT="${JAVA_OPT} -verbose:gc -Xloggc:${HOME}/tevent_gc.log -XX:+PrintGCDetails"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${LIB_PATH}"
#JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"

#===========================================================================================
# TEvent Startup
#===========================================================================================
export APP_MAIN="com.tongbanjie.tevent.server.ServerStartup"
export LC_ALL=zh_CN.UTF-8

serverPid=0

getPid(){
    javaps=`$JAVA_HOME/bin/jps -l | grep $APP_MAIN`
    if [ -n "$javaps" ]; then
        serverPid=`echo $javaps | awk '{print $1}'`
    else
        serverPid=0
    fi
}

startup(){
    getPid

    if [ $serverPid -ne 0 ]; then
        echo "The server(pid=$serverPid) has already been started!"
    else
        echo "Starting the server ..."
        if [ ! -d "${TEVENT_HOME}/log" ]; then
          mkdir "${TEVENT_HOME}/log"
        fi

		nohup $JAVA_HOME/bin/java ${JAVA_OPT} -cp ${CLASSPATH} ${APP_MAIN} 2 > ${TEVENT_HOME}/log/startup.log &

		for i in {1..20} #循环检测10秒
            do
                echo "."
                sleep 0.5

                getPid
                if [ $serverPid -ne 0 ]; then
                    break;
                fi
            done

        if [ $serverPid -ne 0 ]; then
            echo "Start server(pid=$serverPid) successfully!"
        else
            echo "Start server failed! Please check the log files 'startup.log' or 'error.log' for details."
        fi
    fi
}

startup
