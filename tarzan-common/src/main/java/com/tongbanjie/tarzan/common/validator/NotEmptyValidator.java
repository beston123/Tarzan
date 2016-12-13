package com.tongbanjie.tarzan.common.validator;

import com.baidu.unbiz.fluentvalidator.*;

/**
 * 〈非空验证器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public abstract class NotEmptyValidator<T> extends ValidatorHandler<T> implements Validator<T> {

    private static final String NOT_EMPTY_MSG = "the '%s' can not be not empty %s";

    private static final String EMPTY_CONDITION = "";

    private String field;

    private String conditionMsg;

    public NotEmptyValidator(String field) {
        this.field = field;
    }

    public NotEmptyValidator(String field, String conditionMsg) {
        this.field = field;
        this.conditionMsg = conditionMsg;
    }

    @Override
    public boolean validate(ValidatorContext context, T o) {
        boolean validate;
        if(o == null){
            validate = false;
        }else{
            validate = notEmpty(o);
        }
        if(!validate){
            if(conditionMsg == null){
                conditionMsg = EMPTY_CONDITION;
            }
            String errorMsg = String.format(NOT_EMPTY_MSG, field, conditionMsg);
            context.addError(ValidationError.create(errorMsg));
        }
        return validate;
    }

    protected abstract boolean notEmpty(T o);

}
