package com.jtv.publish;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jtv.config.ConfigContext;
import com.jtv.config.ConfigProperties;
import com.jtv.utils.HttpUtil;
import com.jtv.utils.TokenTimer;

/**
 * @author lianrongfa
 * @date 2018/7/16
 */
public class PublishService {

    private static final ConfigProperties configProperties = ConfigContext.getConfigProperties();

    private static final String urlSuffix = "/manager/workspaces.rjson?returnContent=true";

    //HGCGSDVig7-ZCVB9aAlo7mqNoTqM14_WIvgiRKs6Bp4z3JsMkgoKpvSXecV5-nKZRpJHSornIWQGK2T6dity3Q..
    public static void main(String[] args) {


        PublishService publishService = new PublishService();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        publishService.publish();
    }

    /**
     * 检查工作空间是否已经发布
     * @return true:已发布 / false：未发布
     */
    private boolean checkWorkspace() {
        boolean b=false;
        String url = buildUrl();
        String request = HttpUtil.httpRequest(url, "GET", null);
        if (request != null && !"".equals(request)) {

            JSONArray jsonArray = (JSONArray) JSONArray.parse(request);

            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                String name = jsonObject.get("name").toString();
                if (name.equals(configProperties.getWorkSpace())) {

                    ConfigContext.WorkspaceMark workspaceMark = ConfigContext.getConfigContext().getWorkspaceMark();

                    if(workspaceMark==null){
                        workspaceMark = new ConfigContext.WorkspaceMark(name, o.toString());
                        ConfigContext.getConfigContext().setWorkspaceMark(workspaceMark);
                    }

                    b=true;
                    break;
                }
            }
        }

        return b;
    }

    public void publish() {

        ConfigContext.WorkspaceMark workspaceMark = ConfigContext.getConfigContext().getWorkspaceMark();

        //检查是否已经发布
        if(workspaceMark==null||!workspaceMark.getKey().equals(configProperties.getWorkSpace())){
            boolean b = checkWorkspace();

            if(!b){
                String url = buildUrl();
                String param = buildParam();

                String request = HttpUtil.httpRequest(url, "POST", param);

                ConfigContext.WorkspaceMark mark = new ConfigContext.WorkspaceMark(configProperties.getWorkSpace(), request);

                ConfigContext.getConfigContext().setWorkspaceMark(mark);

                System.out.println(request);
            }
        }

    }

    private String buildUrl() {
        StringBuilder sb = new StringBuilder(configProperties.getServerUrl());
        sb.append(urlSuffix);
        sb.append("&token=");
        String token = configProperties.getToken();
        if(token==null||"".equals(token)){
            new TokenTimer().pullToken();
        }
        sb.append(configProperties.getToken());

        return sb.toString();
    }

    private String buildParam() {
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
