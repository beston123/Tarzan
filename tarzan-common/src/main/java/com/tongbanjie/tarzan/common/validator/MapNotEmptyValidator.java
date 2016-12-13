package com.tongbanjie.tarzan.common.validator;

import java.util.Map;

/**
 * 〈Map非空验证器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class MapNotEmptyValidator extends NotEmptyValidator<Map<?, ?>> {


    public MapNotEmptyValidator(String field) {
        super(field);
    }

    public MapNotEmptyValidator(String field, String conditionMsg) {
        super(field, conditionMsg);
    }

    @Override
    protected boolean notEmpty(Map<?, ?> o) {
        if(o.isEmpty()){
            return false;
        }
        return true;
    }

}
