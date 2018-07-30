package com.jtv.utils;

import com.jtv.config.ConfigContext;
import com.jtv.config.ConfigProperties;
import com.jtv.parse.ImportDwg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static ScheduledExecutorService executorService=
            Executors.newSingleThreadScheduledExecutor();

    private final static Logger logger= LoggerFactory.getLogger(TokenTimer.class);
    private static final String urlSuffix="/services/security/tokens.rjson";

    public void pullToken() {

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
        logger.info("获得token："+token);
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

    public static void executor(){
        executorService.scheduleAtFixedRate(new TokenTimer(),1,59, TimeUnit.MINUTES);
    }

    public void run() {
        pullToken();
    }
}
