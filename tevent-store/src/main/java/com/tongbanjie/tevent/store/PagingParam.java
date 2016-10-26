package com.tongbanjie.tevent.store;

import java.io.Serializable;

/**
 * 分页参数 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/26
 */

public class PagingParam implements Serializable {

    private static final long serialVersionUID  = -4033028281824203441L;

    /**
     * 默认页码
     */
    private static final int  DEFAULT_PAGE_NO   = 1;

    /**
     * 最多每页显示记录数
     */
    private static final int  MAX_PAGE_SIZE     = 10000;

    /**
     * 默认每页显示记录数
     */
    private static final int  DEFAULT_PAGE_SIZE = 20;

    /**
     * 页码，从1开始
     */
    private int               pageNo            = DEFAULT_PAGE_NO;

    /**
     * 页面大小
     */
    private int               pageSize          = DEFAULT_PAGE_SIZE;

    public PagingParam() {
    }

    public PagingParam(int pageNo, int pageSize) {
        super();
        setPageNo(pageNo);
        setPageSize(pageSize);
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        if (pageNo < 1) {
            throw new IllegalArgumentException("页码最小为1");
        }
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize > MAX_PAGE_SIZE || pageSize < 1) {
            throw new IllegalArgumentException("每页显示条数范围为1~" + MAX_PAGE_SIZE + "条");
        }
        this.pageSize = pageSize;
    }

    public int getStartRow() {
        return (pageSize * (pageNo - 1)) + 1;
    }

    /**
     * offset的值
     * select * from table limit #{offset},#{pageSize}
     * @return
     */
    public int getMysqlStartRow() {
        return (pageSize * (pageNo - 1));
    }

    public int getEndRow() {
        return pageSize * pageNo;
    }

    public int getOffset(){
        return this.getMysqlStartRow();
    }

    public int getLimit(){
        return this.getPageSize();
    }

}
