package com.tongbanjie.tarzan.store.sharding;

import io.shardingjdbc.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingjdbc.core.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.util.Collection;

/**
 * 〈分表策略〉<p>
 *  最大支持256张分表，实际存储到64张表
 * suffix: [000,004,008,012,016,020,024,028,032,036,040,044,048,052,056,060,064,068,072,076,080,084,088,092,096,100,104,108,112,116,120,124,128,132,136,140,144,148,152,156,160,164,168,172,176,180,184,188,192,196,200,204,208,212,216,220,224,228,232,236,240,244,248,252]
 *
 * @author zixiao
 * @date 18/5/29
 */
public class PreciseTableSharding256_64 implements PreciseShardingAlgorithm<Long> {

    private final VirtualTableSharding virtualTableSharding = new VirtualTableSharding(256,64);

    private final String TABLE_SUFFIX = "_%03d";

    @Override
    public String doSharding(final Collection<String> availableTargetNames, final PreciseShardingValue<Long> shardingValue) {
        for (String each : availableTargetNames) {
            if (each.endsWith(getTableSuffix256_64(shardingValue.getValue()))) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }

    private String getTableSuffix256_64(Long shardingValue){
        int index = virtualTableSharding.getTableSuffix(shardingValue);
        return String.format(TABLE_SUFFIX, index);
    }

}
