package com.tongbanjie.tarzan.common.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 网络工具类 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/11
 */
public class NetworkUtils {

    private NetworkUtils(){}

    public static InetAddress getInetAddress(){
        try{
            return InetAddress.getLocalHost();
        }catch(UnknownHostException e){
            System.out.println("Unknown host," + e.getMessage());
        }
        return null;
    }

    public static String getHostIp(InetAddress netAddress){
        if(null == netAddress){
            return null;
        }
        return netAddress.getHostAddress();
    }

    public static String getLocalHostIp(){
        return getHostIp(getInetAddress());
    }

    public static void main(String[] args) {
        getLocalHostIp();
    }

}
