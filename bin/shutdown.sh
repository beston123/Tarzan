#!/bin/sh

#===========================================================================================
# TEvent Shutdown
#===========================================================================================
export APP_MAIN="com.tongbanjie.tevent.server.ServerStartup"

serverPid=0

getPid(){
    javaps=`$JAVA_HOME/bin/jps -l | grep $APP_MAIN`
    if [ -n "$javaps" ]; then
        serverPid=`echo $javaps | awk '{print $1}'`
    else
        serverPid=0
    fi
}

shutdown(){
    getPid

    if [ $serverPid -ne 0 ]; then
        echo "Stopping the server(pid=$serverPid)..."

        kill -15 $serverPid

        for i in {1..20} #循环检测10秒
            do
                echo "."
                sleep 0.5
                if [ $? -eq 0 ]; then
                    getPid
                    if [ $serverPid -eq 0 ]; then
                       break;
                    fi
                fi
            done

        if [ $serverPid -ne 0 ]; then
            echo "Stop server failed!"
        else
            echo "Stop server successfully!"
        fi
    else
        echo "The server is not running!"
    fi
}

shutdown
