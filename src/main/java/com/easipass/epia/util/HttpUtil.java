package com.easipass.epia.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by StrangeDragon on 2019/7/12 11:25
 **/
@Configuration
public class HttpUtil {
    public static String port;
    public static String contextPath;

    @Value("${server.port:8080}")
    public void setPort(String port) {
        this.port = port;
    }

    @Value("${server.context-path:/}")
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public static String getRestUrl(String serverName) {
        return "http://" + currentServerIp() + ":" + port + contextPath + serverName;
    }

    public static String currentServerIp() {
        InetAddress ia = null;
        try {
            ia = ia.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ia.getHostAddress();
    }
}
