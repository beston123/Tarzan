package com.tongbanjie.tevent.common.util;

import com.tongbanjie.tevent.common.Result;
import com.tongbanjie.tevent.common.exception.*;
import org.apache.commons.lang3.StringUtils;

import static com.tongbanjie.tevent.common.FailResult.*;

/**
 * 〈Result验证器〉<p>
 * 〈不成功，抛出异常〉
 *
 * @author zixiao
 * @date 16/11/23
 */
public class ResultValidate {

    /**
     * Constructor. This class should not normally be instantiated.
     */
    public ResultValidate() {
        super();
    }

    public static void isTrue(Result<?> result){
        isTrue(result, result.getErrorDetail());
    }

    public static void isTrue(Result<?> result, String exceptionMsg){
        if (result.isSuccess()) {
            return;
        }
        String errorCode = result.getErrorCode();
        if(StringUtils.isNotBlank(errorCode)) {
            if (BUSINESS.getCode().equals(errorCode)) {
                throw new BusinessException(exceptionMsg);
            } else if (SYSTEM.getCode().equals(errorCode)) {
                throw new SystemException(exceptionMsg);
            } else if (RPC.getCode().equals(errorCode)) {
                throw new RpcException(exceptionMsg);
            } else if (STORE.getCode().equals(errorCode)) {
                throw new StoreException(exceptionMsg);
            } else if (PARAMETER.getCode().equals(errorCode)) {
                throw new ParameterException(exceptionMsg);
            } else if (TIMEOUT.getCode().equals(errorCode)){
                throw new TimeoutException(exceptionMsg);
            }
        }
        throw new TEventException(exceptionMsg);
    }

}
