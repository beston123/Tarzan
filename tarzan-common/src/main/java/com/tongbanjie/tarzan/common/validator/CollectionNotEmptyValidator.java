package com.tongbanjie.tarzan.common.validator;

import java.util.Collection;

/**
 * 〈Collection非空验证器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class CollectionNotEmptyValidator extends NotEmptyValidator<Collection<?>> {


    public CollectionNotEmptyValidator(String field) {
        super(field);
    }

    public CollectionNotEmptyValidator(String field, String conditionMsg) {
        super(field, conditionMsg);
    }

    @Override
    protected boolean notEmpty(Collection<?> o) {
        if(o.isEmpty()){
            return false;
        }
        return true;
    }
}
