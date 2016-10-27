package com.tongbanjie.tevent.store.baymax;

import com.tongbanjie.baymax.router.strategy.function.ModFunction;

import java.util.Map;

/**
 * 用于奇数值列的 Mod 函数<p>
 * 对奇数自增的Id，使分表数据均匀
 * (columnValue+1)%分表数
 *
 * @author zixiao
 * @date 16/10/26
 */
public class OddValueModFunction extends ModFunction {

	/**
	 * 奇数值的数据均匀分布
	 * (columnValue+1)%分表数
	 * @param columnValue
	 * @param extension
	 * @return
	 */
	@Override
	public Integer execute(String columnValue, Map<String, Object> extension) {
		return super.execute(translate(columnValue), extension);
	}

	private String translate(String columnValue){
		int oldInt = Integer.parseInt(columnValue);
		int newInt = (oldInt+1)/2;

		return String.valueOf(newInt);
	}

}
