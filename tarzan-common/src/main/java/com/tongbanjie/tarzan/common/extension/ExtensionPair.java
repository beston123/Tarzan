package com.tongbanjie.tarzan.common.extension;

import java.text.MessageFormat;

/**
 * 〈扩展pair〉<p>
 *
 * @author zixiao
 * @date 2019/3/18
 */
public class ExtensionPair {

    private String name;

    private String className;

    public ExtensionPair(String name, String className) {
        this.name = name;
        this.className = className;
    }

    public static ExtensionPair build(Class<?> service, String extensionLine){
        String[] strings = extensionLine.split("=");
        if(strings.length != 2){
            throw new IllegalArgumentException(MessageFormat.format("Extension '{0}' format error, {1}", service.getName(), extensionLine));
        }
        return new ExtensionPair(strings[0].trim(), strings[1].trim());
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "ExtensionPair{" +
                "name='" + name + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
