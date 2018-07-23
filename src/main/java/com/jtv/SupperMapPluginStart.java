package com.jtv;

import com.jtv.parse.ImportDwg;

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

    private final ScheduledExecutorService executorService= Executors.newScheduledThreadPool(1);

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        executorService.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                new ImportDwg().parse();
            }
        }, 60, 120, TimeUnit.SECONDS);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        executorService.shutdown();
    }
}
