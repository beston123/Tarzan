package com.tongbanjie.tarzan.store.sharding;

import com.tongbanjie.baymax.router.strategy.PartitionFunction;

/**
 * 〈Baymax分表分库工具〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/12
 */
public class BaymaxSharding {

    /**
     * 最大表数
     */
    private final int max;

    /**
     * 当前表数
     */
    private final int current;

    /**
     * 表名
     */
    private final String tableName;

    /**
     * 分表函数
     */
    private final PartitionFunction partitionFunction;

    private int step;

    private String createStatement;

    public BaymaxSharding(int max, int current, String tableName, PartitionFunction partitionFunction) {
        this.max = max;
        this.current = current;
        this.tableName = tableName;
        this.partitionFunction = partitionFunction;

        this.step = this.max / current;
    }

    public void setCreateStatement(String createStatement) {
        this.createStatement = createStatement.replace(tableName, tableName + "_%03d");
    }

    /**
     * 生成建表语句
     */
    public String getAllCreateStatement() {
        if(createStatement == null){
            return null;
        }
        StringBuffer creates = new StringBuffer();
        for (int i = 0; i < current; i++) {
            creates.append(String.format(createStatement, i * step)).append("\n").append("\n");
        }
        return creates.toString();
    }

    /**
     * 表后缀列表
     */
    public String getPostfix() {
        StringBuffer postfix = new StringBuffer();
        for (int i = 0; i < current; i++) {
            postfix.append(",").append(String.format("%03d", i * step));
        }
        return postfix.toString().substring(1);
    }

    /**
     * 计算id所在分表
     *
     * @param id
     */
    public int calcTablePartition(Long id) {
        return calcTablePartition(String.valueOf(id));
    }

    /**
     * 计算id所在分表
     *
     * @param id
     */
    public int calcTablePartition(String id) {
        int partition = partitionFunction.execute(id, null);
        return partition;
    }

}
