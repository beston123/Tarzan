package com.tongbanjie.tarzan.admin.spring;

import java.beans.PropertyEditorSupport;

/**
 * Integer转换器
 * 解决整数枚举值选择“全部”，value＝“”时，转Integer报错的问题
 * <select class="form-control" name='status'>
 *  <option value="">全部</option>
 *  <option value="0">状态A</option>
 *  <option value="1">状态B</option>
 * </select>
 * @author zixiao
 * @date 16/2/3
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class IntegerEditor extends PropertyEditorSupport {
    @Override
    public String getAsText() {
        Integer value = (Integer)this.getValue();
        return value != null?value.toString():"";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        Integer value=null;
        if(text!=null&&!text.equals("")){
            value=Integer.valueOf(text);
        }
        setValue(value);
    }
}