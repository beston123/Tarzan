package com.tongbanjie.tevent.registry;

/**
 * 支持自动恢复 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/10/12
 */
public interface RecoverableRegistry extends Registry{

    void recover();

}
