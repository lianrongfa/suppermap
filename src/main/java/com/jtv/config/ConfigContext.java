package com.jtv.config;

import com.jtv.utils.TokenTimer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author lianrongfa
 * @date 2018/7/16
 */
public class ConfigContext {
    private final static ConfigProperties configProperties=new ConfigProperties();


    private static Properties properties;


    //工作空间 用来分辨该工作空间是否已发布;
    private WorkspaceMark workspaceMark;

    static {
        try {
            InputStream inputStream = ConfigContext.class.getClassLoader().getResourceAsStream("configuration.properties");
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final static ConfigContext configContext=new ConfigContext();

    private ConfigContext(){
        parseProperties();
        new TokenTimer().executor();
    }

    public static void main(String[] args) {
        System.out.println(configContext);
    }

    private void parseProperties() {

        Class<ConfigProperties> clazz = ConfigProperties.class;

        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {

            try {
                String key = entry.getKey().toString();

                Field field = clazz.getDeclaredField(key);
                if (field!=null){
                    field.setAccessible(true);
                    field.set(getConfigProperties(),entry.getValue());
                }

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

    public static ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public static class WorkspaceMark {
        private String key;
        private String value;

        public WorkspaceMark(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public WorkspaceMark getWorkspaceMark() {
        return workspaceMark;
    }

    public void setWorkspaceMark(WorkspaceMark workspaceMark) {
        this.workspaceMark = workspaceMark;
    }

    public static ConfigContext getConfigContext() {
        return configContext;
    }
}
