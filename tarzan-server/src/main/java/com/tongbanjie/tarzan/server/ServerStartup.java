/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.tongbanjie.tarzan.server;

import com.tongbanjie.tarzan.common.Constants;
import com.tongbanjie.tarzan.common.util.LogUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务启动入口 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 */
public class ServerStartup {

    private static final Logger LOGGER= LoggerFactory.getLogger(ServerStartup.class);

    public static void main(String[] args) {
        start(args);
    }

    public static ServerController start(String[] args) {
        ServerController controller = createServerController(args);
        try {
            controller.start();
            String successInfo = "The tarzan server[" + controller.getServerAddress() + "] boot success.";
            LOGGER.info(successInfo);
            LogUtils.stdInfo(successInfo, ServerStartup.class);
            return controller;
        } catch (Throwable e) {
            String errorMsg = "The tarzan server[" + controller.getServerAddress() + "] boot failed.";
            LOGGER.error(errorMsg, e);
            LogUtils.stdError(errorMsg, ServerStartup.class, e);
            System.exit(-1);
        }
        return null;
    }

    private static ServerController createServerController(String[] args) {
        try {
            /**
             * 1、配置加载和检查
             */
            try {
                loadConfig(args);
            } catch (IOException e) {
                throw new ServerException("Load Configuration file failed.", e);
            }

            /**
             * 2、初始化server
             */
            ApplicationContext act = new ClassPathXmlApplicationContext(Constants.TARZAN_CONTEXT);
            final ServerController controller = act.getBean(ServerController.class);

            controller.initialize();

            /**
             * 3、优雅停机：注册一个JVM关闭的钩子
             */
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                private volatile boolean hasShutdown = false;
                private AtomicInteger shutdownTimes = new AtomicInteger(0);

                @Override
                public void run() {
                    synchronized (this) {
                        LOGGER.info("shutdown hook was invoked, " + this.shutdownTimes.incrementAndGet());
                        if (!this.hasShutdown) {
                            this.hasShutdown = true;
                            long start = System.currentTimeMillis();
                            controller.shutdown();
                            long costs = System.currentTimeMillis() - start;
                            LOGGER.info("shutdown hook over, consuming time total(ms): " + costs);
                        }
                    }
                }
            }, "ShutdownHook"));

            return controller;

        } catch (Throwable e) {
            LOGGER.error("The tarzan server initialize failed.", e);
            LogUtils.stdError("The tarzan server initialize failed.", ServerStartup.class, e);
            System.exit(-1);
        }

        return null;
    }

    /**
     * 加载配置
     * @throws IOException
     */
    private static void loadConfig(String[] args) throws IOException {
        /*************** 检查配置文件是否存在 ***************/
        ConfigManager.checkConfigFiles();

        /*************** 加载日志配置 ***************/
        if( args.length > 0 && Constants.RUN_IN_IDE.equals(args[0]) ){
            //ide中运行模式, 加载默认日志配置
        }else{
            String logFilePath = ConfigManager.logFilePath;
            if(!ConfigManager.logFilePath.startsWith(Constants.CLASSPATH_PREFIX)) {
                PropertyConfigurator.configure(logFilePath);
            }
        }
    }

}
