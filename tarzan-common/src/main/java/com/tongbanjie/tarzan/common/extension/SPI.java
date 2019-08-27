package com.tongbanjie.tarzan.common.extension;

import java.lang.annotation.*;

/**
 * 〈SPI〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/18
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    String value() default "";

}
