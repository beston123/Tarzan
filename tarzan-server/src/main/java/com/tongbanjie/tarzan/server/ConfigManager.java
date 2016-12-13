package com.tongbanjie.tarzan.server;

import com.tongbanjie.tarzan.common.Constants;
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

    public static String configFilePath = Constants.CLASSPATH_PREFIX + Constants.TARZAN_CONFIG_FILE;

    public static String logFilePath = Constants.CLASSPATH_PREFIX + Constants.TARZAN_CONFIG_LOG;

    public static boolean checkConfigFiles() throws IOException {
        String tarzanHome = System.getenv(Constants.TARZAN_HOME);

        if( StringUtils.isBlank(tarzanHome) ){
            throw new IOException(String.format("The env $%s has not set.", Constants.TARZAN_HOME));
        }

        if(!isDirectory(tarzanHome)){
            throw new IOException(String.format("The %s '%s' is not a directory.", Constants.TARZAN_HOME, tarzanHome));
        }

        String configPath = tarzanHome + File.separator + Constants.TARZAN_CONFIG_PATH;
        if(!isDirectory(configPath)){
            throw new IOException(String.format("The conf path '%s' is not a directory.", configPath));
        }

        configFilePath = configPath + File.separator + Constants.TARZAN_CONFIG_FILE;
        checkFileExists(configFilePath);

        logFilePath = configPath + File.separator + Constants.TARZAN_CONFIG_LOG;
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
