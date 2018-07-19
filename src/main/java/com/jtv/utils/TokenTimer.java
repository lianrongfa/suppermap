package com.jtv.utils;

import com.jtv.config.ConfigContext;
import com.jtv.config.ConfigProperties;

import javax.management.timer.Timer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author lianrongfa
 * @date 2018/7/16
 */
public class TokenTimer implements Runnable{
    private ScheduledExecutorService executorService=
            Executors.newSingleThreadScheduledExecutor();

    private static final String urlSuffix="/iserver/services/security/tokens.rjson";

    private void pullToken() {

        ConfigProperties configProperties = ConfigContext.getConfigProperties();

        String serverUrl = configProperties.getServerUrl()+urlSuffix;
        String serverUserName = configProperties.getServerUserName();
        String serverPassword = configProperties.getServerPassword();

        String param="{'userName': '"+serverUserName+"'," +
                "'password': '"+serverPassword+"'," +
                "'clientType': 'RequestIP'," +
                "'ip':'"+getIp()+"'," +
                "'expiration': 60}";
        String token = HttpUtil.httpRequest(serverUrl, "POST", param);

        configProperties.setToken(token);
    }

    private String getIp() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String ip=addr.getHostAddress().toString(); //获取本机ip
        return ip;
    }

    public void executor(){
        executorService.scheduleAtFixedRate(this,0,59, TimeUnit.MINUTES);
    }

    public void run() {
        pullToken();
    }
}
