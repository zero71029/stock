package com.jetec.zero.Tool;

public class ZeroFactory {

    public static ResultBean buildResultBean(Integer code) {
        return new ResultBean(code);
    }

    public static ResultBean buildResultBean(Integer code, String message) {
        return new ResultBean(code, message);
    }

    public static ResultBean buildResultBean(Integer code, String message, Object data) {
        return new ResultBean(code, message, data);
    }

    public static ResultBean fail(String message){
        return new ResultBean(500, message);
    }
    public static ResultBean success(String message, Object data){
        return new ResultBean(200, message ,data);
    }

}
