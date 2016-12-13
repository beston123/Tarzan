package com.tongbanjie.tarzan.common.validator;

import com.baidu.unbiz.fluentvalidator.FluentValidator;
import com.baidu.unbiz.fluentvalidator.Result;
import com.baidu.unbiz.fluentvalidator.ResultCollectors;

/**
 * 〈String非空验证器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class StringNotEmptyValidator extends NotEmptyValidator<String> {


    public StringNotEmptyValidator(String field) {
        super(field);
    }

    public StringNotEmptyValidator(String field, String conditionMsg) {
        super(field, conditionMsg);
    }

    @Override
    protected boolean notEmpty(String o) {
        if(o.isEmpty()){
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Result ret = FluentValidator.checkAll().failFast()
                .on("abc", new StringNotEmptyValidator("string1"))
                .on("", new StringNotEmptyValidator("string2"))
                .doValidate()
                .result(ResultCollectors.toSimple());
        if(!ret.isSuccess()){
            System.out.println("Param error: "+ret.getErrors().get(0));
        }
    }
}
