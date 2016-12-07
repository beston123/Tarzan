package com.tongbanjie.tevent.admin.spring;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;

/**
 * 〈StringEscape〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/11/16
 */
public class StringEscapeEditor extends PropertyEditorSupport {

    private boolean escapeHTML;
    private boolean escapeJavaScript;
    private boolean escapeSQL;

    public StringEscapeEditor(){
        super();
    }

    public StringEscapeEditor(boolean escapeHTML, boolean escapeJavaScript, boolean escapeSQL){
        super();
        this.escapeHTML = escapeHTML;
        this.escapeJavaScript = escapeJavaScript;
        this.escapeSQL = escapeSQL;
    }

    @Override
    public void setAsText(String text) {
        if (text == null || text.isEmpty()) {
            setValue(null);
        } else {
            String value = text;
            if (escapeHTML) {
                value = StringEscapeUtils.escapeHtml4(value);
            }
            if (escapeJavaScript) {
                value = StringEscapeUtils.escapeEcmaScript(value);
            }
            if (escapeSQL) {
                value = escapeSql(value);
            }
            setValue(value);
        }
    }

    private static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.replace(str, "'", "''");
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : "";
    }
}
