package com.tongbanjie.tevent.server;

import com.tongbanjie.tevent.common.Constants;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 配置管理器 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/2
 */
public class ConfigManager {

    public static String configFilePath = "classpath:config.properties";

    public static String logFilePath = "classpath:log4j.properties";

    public static boolean checkConfigFiles() throws IOException {
        String teventHome = System.getenv(Constants.TEVENT_HOME);

        if( StringUtils.isBlank(teventHome) ){
            throw new IOException(String.format("The env $%s has not set.", Constants.TEVENT_HOME));
        }

        if(!isDirectory(teventHome)){
            throw new IOException(String.format("The %s '%s' is not a directory.", Constants.TEVENT_HOME, teventHome));
        }

        String configPath = teventHome + File.separator + Constants.TEVENT_CONFIG_PATH;
        if(!isDirectory(configPath)){
            throw new IOException(String.format("The conf path '%s' is not a directory.", configPath));
        }

        configFilePath = configPath + File.separator + Constants.TEVENT_CONFIG_FILE;
        checkFileExists(configFilePath);

        logFilePath = configPath + File.separator + Constants.TEVENT_CONFIG_LOG;
        checkFileExists(logFilePath);

        return true;
    }

    public static void checkFileExists(String filePath) throws IOException {
        File file = new File(filePath);
        if(!file.exists()) {
            throw new IOException(String.format("The file '%s' is not exists.", filePath));
        }

    }

    public static boolean isDirectory(String filePath){
        File dir = new File(filePath);
        if(dir.isDirectory() && dir.exists()){
            return true;
        }
        return false;
    }



}
