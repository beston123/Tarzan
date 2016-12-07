package com.tongbanjie.tevent.common.validator;

import com.baidu.unbiz.fluentvalidator.*;

/**
 * 〈非空验证器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class NotNullValidator extends ValidatorHandler implements Validator {

    private static final String NOT_NULL_MSG = "the '%s' can not be null %s";

    private static final String EMPTY_CONDITION = "";

    private String field;

    private String conditionMsg;

    public NotNullValidator(String field) {
        this.field = field;
    }

    public NotNullValidator(String field, String conditionMsg) {
        this.field = field;
        this.conditionMsg = conditionMsg;
    }

    @Override
    public boolean validate(ValidatorContext context, Object o) {
        if(o == null){
            if(conditionMsg == null){
                conditionMsg = EMPTY_CONDITION;
            }
            String errorMsg = String.format(NOT_NULL_MSG, field, conditionMsg);
            context.addError(ValidationError.create(errorMsg));
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Result ret = FluentValidator.checkAll().failFast()
                .on("abc", new NotNullValidator("string"))
                .on(null, new NotNullValidator("obj", "when 1==1")).when(1==1)
                .doValidate()
                .result(ResultCollectors.toSimple());
        if(!ret.isSuccess()){
            System.out.println("Param error: "+ret.getErrors().get(0));
        }
    }
}
