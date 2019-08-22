package com.easipass.epia.db;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.easipass.epia.util.PropertiesUtil;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

/**
 * Created by lql on 2018/12/25 10:44
 **/
@Component
public class DataSourceConfig {
    private static Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    public static final String DEFAULT_DATASOURCE = "dataSource";// 系统资源默认使用的数据源
    public static final String dbConfig = "db.properties";

    private final String dataSource_URL = "dataSource.url";
    private final String dataSource_USERNAME = "dataSource.username";
    private final String dataSource_PASSWORD = "dataSource.password";
    private final String dataSource_DRIVER_CLASS_NAME = "dataSource.driver-class-name";
    private final String dataSource_TEST_SQL = "dataSource.testsql";

    /**
     * 注入数据源连接池
     *
     * @return
     */
    @Bean(name = DEFAULT_DATASOURCE)
    @Primary
    public DruidDataSource dataSource() throws Exception {
        logger.info("============================" + DEFAULT_DATASOURCE + "初始化===================================");
        DruidDataSource dataSource = new DruidDataSource();
        Configuration configuration = PropertiesUtil.getDirConfig(dbConfig);
        dataSource.setUrl(configuration.getString(dataSource_URL));
        dataSource.setUsername(configuration.getString(dataSource_USERNAME));//用户名
        dataSource.setPassword(configuration.getString(dataSource_PASSWORD));//密码
        dataSource.setDriverClassName(configuration.getString(dataSource_DRIVER_CLASS_NAME));
        dataSource.setInitialSize(30);
        dataSource.setMaxActive(500);
        dataSource.setMinIdle(5);
        dataSource.setMaxWait(60000);
        //是否启用PSCache,对游标的效率提升大
        dataSource.setPoolPreparedStatements(false);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
        //检查的语句
        dataSource.setValidationQuery(configuration.getString(dataSource_TEST_SQL));
        //申请时是否检查连接有效
        dataSource.setTestOnBorrow(true);
        //归还时是否检查连接有效
        dataSource.setTestOnReturn(true);
        dataSource.setTestWhileIdle(true);
        //连接池中最小的连接数小于minIdle时执行keepAlive操作,长连接
        dataSource.setKeepAlive(false);
        //连接闲置时间超过该值,执行有效性检查和关闭物理连接
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setRemoveAbandoned(true);

        //回收连接的超时时间30分钟
        dataSource.setRemoveAbandonedTimeout(1800);
        //执行查询的超时时间5分钟
        dataSource.setQueryTimeout(60 * 5);
        //执行事务的超时时间20分钟
        dataSource.setTransactionQueryTimeout(60 * 20);
        try {
            //开启Druid的监控统计功能，mergeStat代替stat表示sql合并,wall表示防御SQL注入攻击
//            dataSource.setFilters("mergeStat,wall,log4j");
            dataSource.setFilters("mergeStat,log4j2");//SpringBoot2.x 版本
//            dataSource.setFilters("mergeStat,log4j");//SpringBoot1.x 版本
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.setName(DEFAULT_DATASOURCE);
        return dataSource;
    }

//    @Bean(name = "datasource1")
//    public DruidDataSource dataSource2() throws Exception {
//        DruidDataSource dataSource = new DruidDataSource();
//        Configuration configuration = PropertiesUtil.getDirConfig(dbConfig);
//        dataSource.setUrl(configuration.getString(dataSource1_URL));
//        dataSource.setUsername(configuration.getString(dataSource1_USERNAME));//用户名
//        dataSource.setPassword(configuration.getString(dataSource1_PASSWORD));//密码
//        dataSource.setDriverClassName(configuration.getString(dataSource1_DRIVER_CLASS_NAME));
//        dataSource.setInitialSize(1);
//        dataSource.setMaxActive(100);
//        dataSource.setMinIdle(0);
//        dataSource.setMaxWait(60000);
//        //是否启用PSCache,对游标的效率提升大
//        dataSource.setPoolPreparedStatements(false);
//        dataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
//        //检查的语句
//        dataSource.setValidationQuery(configuration.getString(dataSource1_TEST_SQL));
//        //申请时是否检查连接有效
//        dataSource.setTestOnBorrow(true);
//        //归还时是否检查连接有效
//        dataSource.setTestOnReturn(true);
//        dataSource.setTestWhileIdle(true);
//        //连接池中最小的连接数小于minIdle时执行keepAlive操作,长连接
//        dataSource.setKeepAlive(false);
//        //连接闲置时间超过该值,执行有效性检查和关闭物理连接
//        dataSource.setTimeBetweenEvictionRunsMillis(60000);
//        dataSource.setMinEvictableIdleTimeMillis(300000);
//        dataSource.setRemoveAbandoned(true);
//        //回收连接的超时时间30分钟
//        dataSource.setRemoveAbandonedTimeout(1800);
//        //执行查询的超时时间5分钟
//        dataSource.setQueryTimeout(60 * 5);
//        //执行事务的超时时间20分钟
//        dataSource.setTransactionQueryTimeout(60 * 20);
//        try {
//            //开启Druid的监控统计功能，mergeStat代替stat表示sql合并,wall表示防御SQL注入攻击
//            dataSource.setFilters("mergeStat,log4j");
////            dataSource.setFilters("mergeStat,wall,log4j");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        dataSource.setName("datasource1");
//
//        return dataSource;
//    }

    @Bean
    public ServletRegistrationBean druidStatViewServlet() {
        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        //添加初始化参数：initParams

        //白名单 (没有配置或者为空，则允许所有访问)
        servletRegistrationBean.addInitParameter("allow", "192.168.12.150,127.0.0.1");
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
//		servletRegistrationBean.addInitParameter("deny","192.168.12.150");
        //登录查看信息的账号密码.
        servletRegistrationBean.addInitParameter("loginUsername", "epia");
        servletRegistrationBean.addInitParameter("loginPassword", "easipass");
        //是否能够重置数据(禁用HTML页面上的“Reset All”功能)
        servletRegistrationBean.addInitParameter("resetEnable", "true");
        return servletRegistrationBean;
    }

    /**
     * 注册一个：filterRegistrationBean,添加请求过滤规则
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter(
                "exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid2/*");
        return filterRegistrationBean;
    }

    /**
     * 监听Spring
     * 1.定义拦截器
     * 2.定义切入点
     * 3.定义通知类
     *
     * @return
     */
    @Bean
    public DruidStatInterceptor druidStatInterceptor() {
        return new DruidStatInterceptor();
    }

    @Bean
    public JdkRegexpMethodPointcut druidStatPointcut() {
        JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
        String patterns = "com.ft.*.*.service.*";
        String patterns2 = "com.ft.*.*.mapper.*";
        druidStatPointcut.setPatterns(patterns, patterns2);
        return druidStatPointcut;
    }

    @Bean
    public Advisor druidStatAdvisor() {
        return new DefaultPointcutAdvisor(druidStatPointcut(), druidStatInterceptor());
    }
}

