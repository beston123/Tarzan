package com.tongbanjie.tevent.store.sharding;

import com.tongbanjie.baymax.router.strategy.function.VirtualModFunction;

/**
 * 虚拟表的分区算法 <p>
 *  最大支持128张分表，真实存到4张表（000,032,064,096）
 * suffix: [0, 32, 64, 96]
 *
 * @author zixiao
 * @date 16/10/26
 */
public class VirtualModFunction128_4 extends VirtualModFunction {

    public VirtualModFunction128_4(){
        super(128, 4);
    }

}
