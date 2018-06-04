package com.tongbanjie.tarzan.store.sharding;

/**
 * 〈Baymax分表分库工具〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/12/12
 */
public class TableShardingTool {

    /**
     * 表名
     */
    private final String tableName;

    /**
     * 分表函数
     */
    private final VirtualTableSharding tableSharding;

    /**
     * 表名后缀
     */
    private final String TABLE_SUFFIX = "%03d";

    /**
     * 表名格式
     */
    private final String TABLE_FORMAT = "%s_%03d";

    private final String CREATE_TABLE_LIKE = "CREATE TABLE `%s` LIKE `%s`;\n";

    /**
     * 表后缀 步长
     */
    private int step;

    private String createStatement;

    public TableShardingTool(VirtualTableSharding tableSharding, String tableName) {
        this.tableName = tableName;
        this.tableSharding = tableSharding;

        this.step = tableSharding.getMax() / tableSharding.getActual();
    }

    public void setCreateStatement(String createStatement) {
        this.createStatement = createStatement;
    }

    /**
     * 生成建表语句
     */
    public String getAllCreateStatement() {
        if(createStatement == null){
            return null;
        }
        StringBuffer creates = new StringBuffer();
        //第一张表
        String firstShardingTable = String.format(TABLE_FORMAT, tableName, 0);
        creates.append(createStatement.replace(tableName, firstShardingTable)).append("\n").append("\n");
        for (int i = 1; i < tableSharding.getActual(); i++) {
            String iShardingTable = String.format(TABLE_FORMAT, tableName, i * step);
            creates.append(String.format(CREATE_TABLE_LIKE, iShardingTable, firstShardingTable)).append("\n");
        }
        return creates.toString();
    }

    /**
     * 表后缀列表
     */
    public String getPostfix() {
        StringBuffer postfix = new StringBuffer();
        for (int i = 0; i < tableSharding.getActual(); i++) {
            postfix.append(",").append(String.format(TABLE_SUFFIX, i * step));
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
        int partition = tableSharding.getTableSuffix(Long.valueOf(id));
        return partition;
    }

}
