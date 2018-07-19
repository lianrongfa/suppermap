package com.jtv.publish;

import com.jtv.config.ConfigContext;
import com.jtv.config.ConfigProperties;
import com.jtv.utils.HttpUtil;

/**
 * @author lianrongfa
 * @date 2018/7/16
 */
public class PublishService {

    private static final ConfigProperties configProperties= ConfigContext.getConfigProperties();

    private static final String urlSuffix="/iserver/manager/workspaces.rjson?returnContent=true";

    //HGCGSDVig7-ZCVB9aAlo7mqNoTqM14_WIvgiRKs6Bp4z3JsMkgoKpvSXecV5-nKZRpJHSornIWQGK2T6dity3Q..
    public static void main(String[] args) {


    }

    public void publish(){
        String url = buildUrl();
        String param = buildParam();

        String request = HttpUtil.httpRequest(url, "POST", param);
        System.out.println(request);
    }
    private String buildUrl(){
        StringBuilder sb = new StringBuilder(configProperties.getServerUrl());
        sb.append(urlSuffix);
        sb.append("&token=");
        sb.append(configProperties.getToken());

        return sb.toString();
    }
    private String buildParam(){
        StringBuilder sb = new StringBuilder("{");
        sb.append("'workspaceConnectionInfo':'server=");
        sb.append(configProperties.getServer());
        sb.append(";username=");
        sb.append(configProperties.getUserName());
        sb.append(";password=");
        sb.append(configProperties.getPassword());
        sb.append(";type=ORACLE;database=;name=");
        sb.append(configProperties.getWorkSpace());
        sb.append(";driver=null'");
        sb.append(",'servicesTypes':['RESTMAP'],'isDataEditable':false,'isMultiInstance':false,'instanceCount':0,'dataProviderDelayCommitSetting':null");
        sb.append("}");
        return sb.toString();
    }

}
