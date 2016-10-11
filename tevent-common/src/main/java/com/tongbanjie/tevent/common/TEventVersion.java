package com.tongbanjie.tevent.common;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public abstract class TEventVersion {

    public static final Version CURRENT = Version.V0_1_0;

    public enum Version{

        V0_1_0(1, "0.1.0");

        private int value;

        private String name = null;

        private Version(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public int getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        public static Version valueOf(int value) {
            for (Version tmp : values()) {
                if (tmp.value == value) {
                    return tmp;
                }
            }
            return null;
        }

    }

    public static String getVersionName(int value) {
        Version data = Version.valueOf(value);
        if(data == null){
            return "Unknown";
        }
        return data.getName();
    }

    public static Version getVersion(int value) {
        return Version.valueOf(value);
    }

    public static void main(String[] args) {
        System.out.println(TEventVersion.getVersion(1));
        System.out.println(TEventVersion.getVersionName(999));
    }

}
