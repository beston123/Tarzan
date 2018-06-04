package com.tongbanjie.tarzan.store.sharding;

/**
 * 〈虚拟分表算法〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/5/29
 */
public class VirtualTableSharding {

    /**
     * 最大真实节点数
     */
    private int max;

    /**
     * 真实节点的数量
     */
    private int actual;

    private int[] bucket;

    public void init()  {
        bucket = new int[max];

        int length = max / actual;
        int lengthIndex = 0;

        int suffix = 0;

        for (int i = 0; i < max; i++) {
            bucket[i] = suffix;
            lengthIndex ++;
            if (lengthIndex == length){
                lengthIndex = 0;
                suffix = i + 1;
            }
        }
    }

    public VirtualTableSharding(int max, int actual){
        this.actual = actual;
        this.max = max;
        this.init();
    }

    public int getTableSuffix(Long columnValue) {
        return bucket[((Long) (columnValue % max)).intValue()];
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getActual() {
        return actual;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }
}
