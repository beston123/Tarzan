package com.tongbanjie.tarzan.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * 网络工具类 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/11
 * @see https://github.com/alibaba/dubbo/blob/master/dubbo-common/src/main/java/com/alibaba/dubbo/common/utils/NetUtils.java
 */
public class NetworkUtils {

    private NetworkUtils(){}

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkUtils.class);

    private static final Pattern IP_PATTERN       = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    public static final String  ANYHOST          = "0.0.0.0";

    public static final String  LOCALHOST        = "127.0.0.1";

    private static volatile InetAddress LOCAL_ADDRESS = null;

    /**
     * 获取本机IP地址
     * @return
     */
    public static String getLocalHostIp(){
        return getHostIp(getLocalAddress());
    }

    public static String getHostIp(InetAddress netAddress){
        if(null == netAddress){
            return null;
        }
        return netAddress.getHostAddress();
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            LOGGER.warn("Failed to get local ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    LOGGER.warn("Failed to get local ip address, " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        LOGGER.warn("Failed to get local ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.warn("Failed to get local ip address, " + e.getMessage(), e);
        }

        LOGGER.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
    }

    public static void main(String[] args) {
        System.out.println(getLocalHostIp());
    }

}
