package com.jetec.zero.Tool;

public class ResultBean {

    public Integer code;
    public String message;
    public Object data;

    public ResultBean() {
    }

    public ResultBean(Integer code) {
        this.code = code;
    }

    public ResultBean(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultBean(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
