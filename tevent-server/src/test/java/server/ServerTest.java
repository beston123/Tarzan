package server;

import com.tongbanjie.tevent.common.Constants;
import com.tongbanjie.tevent.server.ServerStartup;
import org.apache.commons.lang3.Validate;

/**
 * Server启动 测试 <p>
 * 设置环境变量
 * TEVENT_HOME=project所在路径
 * 例：/Users/zixiao/Work/ware/tevent
 *
 * @author zixiao
 * @date 16/11/3
 */
public class ServerTest {

    public static void main(String[] args) {
        Validate.notBlank(System.getenv(Constants.TEVENT_HOME), "Please set env '$" + Constants.TEVENT_HOME + "'");

        String[] params = {Constants.RUN_IN_IDE};
        ServerStartup.start(params);
    }

}
