package com.tongbanjie.tarzan.common;

/**
 * OrderBy条件 <p/>
 *
 * 用法：
    <if test="orderByClause != null">
        ${orderByClause}
    </if>
 * @author zixiao
 * @date 17/5/5
 */
public class OrderBy {

    public static final String ORDER_BY = "order by";

    public static final String SEPARATOR = ",";

    public static final String SPACE = " ";

    public static final String ORDER_BY_ID_ASC = "order by id asc";

    public static final String ORDER_BY_ID_DESC = "order by id desc";

    /**
     * 排序条件子句
     * 例：order by id desc
     */
    private String orderByClause;

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }
}