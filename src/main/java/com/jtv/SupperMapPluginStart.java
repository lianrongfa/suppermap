package com.jtv;

import com.jtv.parse.ImportDwg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author lianrongfa
 * @date 2018/7/20
 */
public class SupperMapPluginStart implements ServletContextListener{

    private final static Logger logger= LoggerFactory.getLogger(SupperMapPluginStart.class);

    private final ScheduledExecutorService executorService= Executors.newScheduledThreadPool(1);

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        executorService.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    new ImportDwg().parse();
                    logger.info("解析完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1, 5, TimeUnit.MINUTES);
        logger.info("auto-improt-file-plugin 启动成功");
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        executorService.shutdown();
        logger.info("auto-improt-file-plugin 停止");
    }
}
