package com.easipass.epia.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by lql on 2018/12/25 13:40
 **/
public class DBService {
    private static Logger logger = LoggerFactory.getLogger(DBService.class);
    /**
     * 默认的数据源实例
     */
    private DataSource dataSource;
    /**
     * 事务管理器
     */
    private DataSourceTransactionManager transactionManager;//主要针对于JdbcTemplate开发
    private TransactionStatus transactionStatus;//主要描述事务具体的运行状态
    private DefaultTransactionDefinition def;//定义事务的一些相关信息。例如： 隔离 传播 超时 只读
    private JdbcTemplate jdbcTemplate = new JdbcTemplate();
    private boolean isStartTransAction = false;
    /**
     * 默认的事务类型为自动合并事务
     */
    private int transactionType = TransactionDefinition.PROPAGATION_NESTED;//PROPAGATION_NESTED:一种嵌套事务，它是使用SavePoint来实现的。

    //    private IRollBack iRollBack;
    private Object injectObject;

    public DBService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DBService(DataSource dataSource, int transactionType) {
        this.dataSource = dataSource;
        this.transactionType = transactionType;
    }

//    public DBService(DataSource dataSource, int transactionType, IRollBack iRollBack) {
//        this.dataSource = dataSource;
//        this.transactionType = transactionType;
//        this.iRollBack = iRollBack;
//    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * 注入回滚处理类
     *
     * @param iRollBack
     */
//    public void injectRollback(IRollBack iRollBack, Object injectObject) {
//        this.iRollBack = iRollBack;
//        this.injectObject = injectObject;
//    }

    /**
     * 新建一个事务
     */
    public void startTransaction() {
        transactionManager = new DataSourceTransactionManager(dataSource);
        def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(this.transactionType);
        transactionStatus = transactionManager.getTransaction(def);
        isStartTransAction = true;
        logger.debug("事务开启");
    }

    /**
     * 创建当前事务中保存的点位
     *
     * @return
     */
    public Object createSavePoint() {
        return transactionStatus.createSavepoint();
    }

    /**
     * 回滚当前事务到上一个点
     *
     * @param object
     */
    public void rollbackToSavePoint(Object object) {
        transactionStatus.rollbackToSavepoint(object);
    }

    /**
     * 执行查询
     *
     * @param sql
     * @param paramsList
     * @return
     */
    public List<Map<String, Object>> select(String sql, List paramsList) {
        if (null == jdbcTemplate.getDataSource())
            jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate.queryForList(sql, paramsList.toArray());
    }

    public List<Map<String, Object>> select(String sql) {
        if (null == jdbcTemplate.getDataSource())
            jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 参数类型自定义的操作
     *
     * @return
     */
    public int update(String sql, PreparedStatementSetter pss) {
        if (!isStartTransAction)
            startTransaction();
        int num;
        try {
            if (null == jdbcTemplate.getDataSource())
                jdbcTemplate.setDataSource(dataSource);
            num = jdbcTemplate.update(sql, pss);
        } catch (Exception ex) {
            rollback(injectObject);
            throw ex;
        }
        return num;
    }

    /**
     * 执行更新
     *
     * @param sql
     * @param paramsList
     * @return
     */
    public int update(String sql, List paramsList) {
        if (!isStartTransAction)
            startTransaction();
        int num;
        try {
            if (null == jdbcTemplate.getDataSource())
                jdbcTemplate.setDataSource(dataSource);
            num = jdbcTemplate.update(sql, paramsList.toArray());
        } catch (Exception ex) {
            rollback(injectObject);
            throw ex;
        }
        return num;
    }

    public int update(String sql) {
        if (!isStartTransAction)
            startTransaction();
        int num;
        try {
            if (null == jdbcTemplate.getDataSource())
                jdbcTemplate.setDataSource(dataSource);
            num = jdbcTemplate.update(sql);
        } catch (Exception ex) {
            rollback(injectObject);
            throw ex;
        }
        return num;
    }

    public void execute(String sql) {
        if (!isStartTransAction)
            startTransaction();
        try {
            if (null == jdbcTemplate.getDataSource())
                jdbcTemplate.setDataSource(dataSource);
            jdbcTemplate.execute(sql);
        } catch (Exception ex) {
            rollback(injectObject);
            throw ex;
        }
    }

    /**
     * 执行多个sql语句
     *
     * @param sqls
     * @return
     */
    public int[] batch(String[] sqls) {
        if (!isStartTransAction)
            startTransaction();
        int[] nums;
        try {
            if (null == jdbcTemplate.getDataSource())
                jdbcTemplate.setDataSource(dataSource);
            nums = jdbcTemplate.batchUpdate(sqls);
        } catch (Exception ex) {
            rollback(injectObject);
            throw ex;
        }
        return nums;
    }

    /**
     * 一条语句,根据参数列表多次执行
     *
     * @param sql
     * @param params
     * @return
     */
    public int[] batch(String sql, List<Object[]> params) {
        if (!isStartTransAction)
            startTransaction();
        int[] nums;
        try {
            if (null == jdbcTemplate.getDataSource())
                jdbcTemplate.setDataSource(dataSource);
            nums = jdbcTemplate.batchUpdate(sql, params);
        } catch (Exception ex) {
            rollback(injectObject);
            throw ex;
        }
        return nums;
    }

    /**
     * 事务提交
     */
    public void commit() {
        if (isStartTransAction) {
            if (!transactionStatus.isCompleted() && !transactionStatus.isRollbackOnly()) {
                logger.info("提交成功");
                transactionManager.commit(transactionStatus);
            } else {
                logger.info("错误的使用commit,事务是否完成的状态:" + transactionStatus.isCompleted() + ",事务是否已经被标记回滚:" + transactionStatus.isRollbackOnly());
                throw new UnsupportedOperationException("错误的使用commit,事务是否完成的状态:" + transactionStatus.isCompleted() + ",事务是否已经被标记回滚:" + transactionStatus.isRollbackOnly());
            }
            destroy();
        }
    }

    /**
     * 销毁当前事务对象
     */
    public void destroy() {
        isStartTransAction = false;
        transactionManager = null;
        this.def = null;
        this.transactionStatus = null;
        this.jdbcTemplate = null;
    }


    /**
     * 事务回滚,同时执行带有入参的后处理
     *
     * @param object
     */
    public void rollback(Object object) {
        if (isStartTransAction) {
            //事务回滚
            if (!transactionStatus.isCompleted() && !transactionStatus.isRollbackOnly()) {
                logger.info("回滚成功!");
                transactionManager.rollback(transactionStatus);
            } else {
                logger.info("错误的使用rollback,事务完成状态:" + transactionStatus.isCompleted() + ",事务是否已被标记回滚:" + transactionStatus.isRollbackOnly());
            }
//            if (iRollBack != null)
//                iRollBack.exec(object);
            destroy();
        }
    }

    /**
     * 事务回滚
     */
    public void rollback() {
        if (isStartTransAction) {
            //事务回滚
            if (!transactionStatus.isCompleted() && !transactionStatus.isRollbackOnly()) {
                logger.info("回滚成功!");
                transactionManager.rollback(transactionStatus);
            } else {
                logger.info("错误的使用rollback,事务是否完成:" + transactionStatus.isCompleted() + ",事务是否已经被标记回滚:" + transactionStatus.isRollbackOnly());
            }
//            if (iRollBack != null)
//                iRollBack.exec(null);
            destroy();
        }
    }

    /**
     * 依据类型设置参数
     *
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public int updateByType(String sql, List params) throws Exception {
        if (!isStartTransAction)
            startTransaction();
        int num;
        try {
            if (null == jdbcTemplate.getDataSource())
                jdbcTemplate.setDataSource(dataSource);
            num = jdbcTemplate.update(sql, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement) throws SQLException {
                    setType(preparedStatement, params);
                }
            });

        } catch (Exception ex) {
            rollback(injectObject);
            throw ex;
        }
        return num;

    }

    private void setType(PreparedStatement preparedStatement, List params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object object = params.get(i);
            int num = i + 1;
            if (object instanceof Byte[]) {
                preparedStatement.setBlob(num, new SerialBlob((byte[]) object));
            } else if (object instanceof String) {
                preparedStatement.setString(num, String.valueOf(object));
            } else if (object instanceof InputStream) {
                preparedStatement.setBlob(num, (InputStream) object);
            } else {
                preparedStatement.setObject(num, object);
            }
        }
    }

    public static void main(String args[]) throws Exception {
        /**
         * 初始化数据源
         */

        System.out.println(DBService.class);
//        /**
//         * 使用DPServcie工厂创建
//         */
//        List list = DBFactory.createDBService("test").select("select * from reports");
//        System.out.println(list.size());
//
//        /**
//         * 使用服务介绍
//         */
//        DBService DBService = new DBService();
//        DBService.setDataSourceName("test");
////        DBService.startTransaction();
//        DBService.injectRollback(new Test(), "入参");
//        int num = DBService.update("delete from multifiletable  where id=6");
//        System.out.println(num);
//        Object point1 = DBService.createSavePoint();
//        num = DBService.update("delete from multifiletable  where id=7");
//        System.out.println(num);
//        Object point2 = DBService.createSavePoint();
//        DBService.rollbackToSavePoint(point1);
//        DBService.commit();

    }
}
