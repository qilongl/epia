package com.easipass.epia.db;

import com.easipass.epia.EpiaApplication;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

/**
 * Created by lql on 2018/12/25 14:04
 **/
public class DBFactory {
    /**
     * 创建DBService,使用默认的数据源
     *
     * @return
     */
    public static DBService createDBService(ApplicationContext ctx) {
        DataSource dataSource = (DataSource) ctx.getBean(DataSourceConfig.DEFAULT_DATASOURCE);
        DBService DBService = new DBService(dataSource);
        return DBService;
    }

    /**
     * 创建DBService,使用默认的容器，指定数据源
     *
     * @return
     */
    public static DBService createDBService(String dataSourceName) {
        DataSource dataSource = (DataSource) EpiaApplication.ctx.getBean(dataSourceName);
        DBService DBService = new DBService(dataSource);
        return DBService;
    }

    /**
     * 使用指定的数据源
     *
     * @param dataSourceName 数据源名称
     * @return
     */
    public static DBService createDBService(ApplicationContext ctx, String dataSourceName) {
        DataSource dataSource = (DataSource) ctx.getBean(dataSourceName);
        DBService DBService = new DBService(dataSource);
        return DBService;
    }
}
