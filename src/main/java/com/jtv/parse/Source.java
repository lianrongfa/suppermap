package com.jtv.parse;

import com.jtv.config.ConfigContext;
import com.jtv.config.ConfigProperties;
import com.supermap.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lianrongfa
 * @date 2018/7/10
 */
public abstract class Source {

    private final static Logger logger= LoggerFactory.getLogger(Source.class);

    private ConfigProperties configProperties= ConfigContext.getConfigProperties();

    private  Workspace workspace;

    private Datasource ds;

    public Source(){
        workspaceOpen();
    }

    public void workspaceOpen(){
        workspace = new Workspace();

        WorkspaceConnectionInfo workspaceConnectionInfo = buildWorkspaceConnectionInfo();

        boolean open = workspace.open(workspaceConnectionInfo);
        if(!open){
            datasourceOpen();
            boolean b = workspace.create(workspaceConnectionInfo);
            if(!b){
                logger.error("工作空间创建失败！"+configProperties.toString());

            }
        }else{
            datasourceOpen();
        }

        workspaceConnectionInfo.dispose();
    }

    private WorkspaceConnectionInfo buildWorkspaceConnectionInfo() {
        WorkspaceConnectionInfo workspaceConnectionInfo = new
                WorkspaceConnectionInfo();
        workspaceConnectionInfo.setType(WorkspaceType.ORACLE);

        workspaceConnectionInfo.setServer(configProperties.getServer());

        workspaceConnectionInfo.setName(configProperties.getWorkSpace());
        workspaceConnectionInfo.setUser(configProperties.getUserName());
        workspaceConnectionInfo.setPassword(configProperties.getPassword());
        return workspaceConnectionInfo;
    }

    public void datasourceOpen(){


        Datasources datasources = workspace.getDatasources();

        ds = datasources.get(configProperties.getAlias());

        if(ds==null){
            DatasourceConnectionInfo info = buildDatasourceConnectionInfo();

            ds = datasources.open(info);
            if(ds==null){//未创建数据源，新建数据源
                ds = datasources.create(info);
                if(ds==null){
                    logger.error("数据源创建错误！"+configProperties.toString());
                }

            }
            info.dispose();
        }

    }

    private DatasourceConnectionInfo buildDatasourceConnectionInfo() {
        DatasourceConnectionInfo info = new DatasourceConnectionInfo();
        info.setEngineType(EngineType.ORACLEPLUS);
        info.setServer(configProperties.getServer());
        info.setAlias(configProperties.getAlias());
        info.setUser(configProperties.getUserName());
        info.setPassword(configProperties.getPassword());
        return info;
    }



    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public Datasource getDs() {
        return ds;
    }
}
