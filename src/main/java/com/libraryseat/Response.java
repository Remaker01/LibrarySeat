package com.libraryseat;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Response {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String uri,method,msg = "";
    private Object data;

    public Response() {}

    public Response(String msg,Object data) {
        this.msg = msg;
        this.data = data;
    }

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
