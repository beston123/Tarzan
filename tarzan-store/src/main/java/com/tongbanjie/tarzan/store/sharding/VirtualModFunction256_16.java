package com.tongbanjie.tarzan.store.sharding;

import com.tongbanjie.baymax.router.strategy.function.VirtualModFunction;

/**
 * 虚拟表的分区算法 <p>
 *  最大支持256张分表，实际存储到16张表
 * suffix: [000,016,032,048,064,080,096,112,128,144,160,176,192,208,224,240]
 *
 * @author zixiao
 * @date 16/10/26
 */
public class VirtualModFunction256_16 extends VirtualModFunction {

    public VirtualModFunction256_16(){
        super(256, 16);
    }

}
