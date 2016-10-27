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
import com.tongbanjie.tevent.store.StoreConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
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
        start(createServerController(args));
    }

    public static ServerController createServerController(String[] args) {
        //配置加载
        loadConfig();

        final ServerConfig serverConfig = new ServerConfig();
        final NettyServerConfig nettyServerConfig = new NettyServerConfig();
        final StoreConfig storeConfig = new StoreConfig();

        try {
            if (serverConfig.getServerId() < 0 || serverConfig.getServerId() > 31) {
                System.out.println("ServerId must between 0 and 31");
                System.exit(-3);
            }

            final ServerController controller = new ServerController(//
                serverConfig, //
                nettyServerConfig, //
                storeConfig
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
            e.printStackTrace();
            System.exit(-1);
        }

        return null;
    }

    public static ServerController start(ServerController controller) {
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

    private static void loadConfig(){
        String conf = System.getProperty(Constants.TEVENT_SERVER_CONF, "classpath:config.properties");
        try {
            Properties properties = new Properties();
            if (conf.startsWith(Constants.CLASSPATH_PREFIX)) {
                conf = StringUtils.substringAfter(conf, Constants.CLASSPATH_PREFIX);
                properties.load(ServerStartup.class.getClassLoader().getResourceAsStream(conf));
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(conf);
                }finally {
                    if(fis != null){
                        fis.close();
                    }
                }
                properties.load(fis);
            }
            Set<String> propNames = properties.stringPropertyNames();
            for(String propName : propNames){
                System.setProperty(propName, properties.getProperty(propName));
            }
        } catch (IOException e) {
            System.out.println("Configuration load failed, file:" + conf);
            System.exit(-3);
        }
    }
}
