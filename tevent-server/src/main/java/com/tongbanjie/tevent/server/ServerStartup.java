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
package com.tongbanjie.tevent.server;

import com.tongbanjie.tevent.common.Constants;
import com.tongbanjie.tevent.rpc.netty.NettyServerConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
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
            String tip = "The tevent server[" + controller.getServerAddress() + "] boot success.";
            LOGGER.info(tip);
            return controller;
        }
        catch (Throwable e) {
            LOGGER.error("The tevent server[" + controller.getServerAddress() + "] boot failed.", e);
            System.exit(-1);
        }
        return null;
    }

    private static ServerController createServerController(String[] args) {
        //配置加载
        try {
            loadConfig(args);
        } catch (IOException e) {
            LOGGER.error("Configuration load failed.", e);
            System.exit(-3);
        }

        final ServerConfig serverConfig = new ServerConfig();
        final NettyServerConfig nettyServerConfig = new NettyServerConfig();

        try {
            if (serverConfig.getServerId() < 0 || serverConfig.getServerId() > 31) {
                LOGGER.error("ServerId must between 0 and 31 !");
                System.exit(-3);
            }

            final ServerController controller = new ServerController(//
                serverConfig, //
                nettyServerConfig//
                );

            boolean initResult = controller.initialize();
            if (!initResult) {
                controller.shutdown();
                System.exit(-3);
            }

            //优雅停机
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
        }
        catch (Throwable e) {
            LOGGER.error("The tevent server boot failed.", e);
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
        try {
            ConfigManager.checkConfigFiles();
        } catch (IOException e) {
            System.err.println("Check configuration file failed:");
            e.printStackTrace();
            throw e;
        }

        /*************** 加载日志配置 ***************/
        if( args.length > 0 && Constants.RUN_IN_IDE.equals(args[0]) ){
            //ide中运行模式, 加载默认日志配置
        }else{
            String logFilePath = ConfigManager.logFilePath;
            if(!ConfigManager.logFilePath.startsWith(Constants.CLASSPATH_PREFIX)) {
                PropertyConfigurator.configure(logFilePath);
            }
        }

        /*************** 加载业务配置 ***************/
        String configFilePath = ConfigManager.configFilePath;
        Properties properties = new Properties();
        InputStream is = null;
        try {
            if (configFilePath.startsWith(Constants.CLASSPATH_PREFIX)) {
                configFilePath = StringUtils.substringAfter(configFilePath, Constants.CLASSPATH_PREFIX);
                is = ServerStartup.class.getClassLoader().getResourceAsStream(configFilePath);
            } else {
                is = new FileInputStream(configFilePath);
            }
            properties.load(is);
        }finally {
            if(is != null){
                is.close();
            }
        }

        Set<String> propNames = properties.stringPropertyNames();
        for(String propName : propNames){
            System.setProperty(propName, properties.getProperty(propName));
        }

    }
}
