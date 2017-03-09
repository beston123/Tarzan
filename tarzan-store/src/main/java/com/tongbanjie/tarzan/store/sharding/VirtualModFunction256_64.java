package com.tongbanjie.tarzan.store.sharding;

import com.tongbanjie.baymax.router.strategy.function.VirtualModFunction;

/**
 * 虚拟表的分区算法 <p>
 *  最大支持256张分表，实际存储到64张表
 * suffix: [000,004,008,012,016,020,024,028,032,036,040,044,048,052,056,060,064,068,072,076,080,084,088,092,096,100,104,108,112,116,120,124,128,132,136,140,144,148,152,156,160,164,168,172,176,180,184,188,192,196,200,204,208,212,216,220,224,228,232,236,240,244,248,252]
 *
 * @author zixiao
 * @date 16/10/26
 */
public class VirtualModFunction256_64 extends VirtualModFunction {

    public VirtualModFunction256_64(){
        super(256, 64);
    }

}
