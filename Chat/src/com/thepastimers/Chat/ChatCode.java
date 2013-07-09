package com.thepastimers.Chat;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 7/1/13
 * Time: 5:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatCode {
    String key;
    String code;
    String description;

    public ChatCode(String k, String c, String d) {
        key = k;
        code = c;
        description = d;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
