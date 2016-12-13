package com.tongbanjie.tarzan.common;

/**
 * 版本 <p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/29
 */
public abstract class TarzanVersion {

    public static final Version CURRENT = Version.V0_5_0;

    public enum Version{

        V0_1_0(1, "0.1.0"),
        V0_2_0(2, "0.2.0"),
        V0_3_0(3, "0.3.0"),
        V0_3_1(4, "0.3.1"),
        V0_5_0(5, "0.5.0");

        private int value;

        private String name = null;

        Version(int value, String name) {
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
        System.out.println(TarzanVersion.getVersion(1));
        System.out.println(TarzanVersion.getVersionName(999));
    }

}
