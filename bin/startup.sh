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

export TARZAN_HOME=$(dirname "$PWD")
export LIB_PATH=${TARZAN_HOME}/lib
export CLASSPATH=.:${TARZAN_HOME}/conf:${CLASSPATH}

#===========================================================================================
# JVM Configuration
#===========================================================================================
JAVA_OPT="${JAVA_OPT} -server -Xms2g -Xmx2g -Xmn768m -XX:PermSize=128m -XX:MaxPermSize=256m"
JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8 -XX:+DisableExplicitGC"
JAVA_OPT="${JAVA_OPT} -verbose:gc -Xloggc:${TARZAN_HOME}/log/tarzan_gc.log -XX:+PrintGCDetails"
JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${LIB_PATH}"
#JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"

#===========================================================================================
# Tarzan Startup
#===========================================================================================
export APP_MAIN="com.tongbanjie.tarzan.server.ServerStartup"
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
        printf "Starting the server "
        if [ ! -d "${TARZAN_HOME}/log" ]; then
          mkdir "${TARZAN_HOME}/log"
        fi

        nohup $JAVA_HOME/bin/java ${JAVA_OPT} -cp ${CLASSPATH} ${APP_MAIN} 2 > ${TARZAN_HOME}/log/startup.log &

        for i in {1..10} #循环检测5秒
            do
                printf "."
                sleep 0.5

                getPid
                if [ $serverPid -ne 0 ]; then
                    break;
                fi
            done
        for i in {1..5} #等待2.5秒,确认是否启动成功
            do
                printf "."
                sleep 0.5
            done
        printf ".\n"

        getPid
        if [ $serverPid -ne 0 ]; then
            echo "Start server(pid=$serverPid) successfully!"
        else
            echo "Start server failed! Please check the log files 'startup.log' or 'error.log' for details."
        fi
    fi
}

startup
