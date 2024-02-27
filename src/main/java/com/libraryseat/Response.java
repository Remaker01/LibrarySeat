package com.libraryseat;

import java.util.Map;

public class Response {
    private String uri,method,msg;
    private Map<String,?> extra;

    public Response() {}

    public Response(String uri, String method, String msg) {
        this.uri = uri;
        this.method = method;
        this.msg = msg;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, ?> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, ?> extra) {
        this.extra = extra;
    }
}
