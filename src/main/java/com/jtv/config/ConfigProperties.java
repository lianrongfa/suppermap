package com.jtv.config;

/**
 * @author lianrongfa
 * @date 2018/7/13
 */
public class ConfigProperties {
    //oracle服务地址 eg：127.0.0.1/orcl
    private String server;
    //数据库用户名
    private String userName;
    //数据库密码
    private String password;
    //数据源别名
    private String alias;
    //工作空间名称
    private String workSpace;
    //文件绝对路径
    private String file;

    //IServer服务器地址
    private String serverUrl;
    //IServer服务器用户名
    private String serverUserName;
    //IServer服务器密码
    private String serverPassword;
    //token
    private volatile String token;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWorkSpace() {
        return workSpace;
    }

    public void setWorkSpace(String workSpace) {
        this.workSpace = workSpace;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUserName() {
        return serverUserName;
    }

    public void setServerUserName(String serverUserName) {
        this.serverUserName = serverUserName;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "ConfigProperties{" +
                "server='" + server + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", alias='" + alias + '\'' +
                ", workSpace='" + workSpace + '\'' +
                ", file='" + file + '\'' +
                ", serverUrl='" + serverUrl + '\'' +
                ", serverUserName='" + serverUserName + '\'' +
                ", serverPassword='" + serverPassword + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
