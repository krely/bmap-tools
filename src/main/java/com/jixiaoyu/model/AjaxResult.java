package com.jixiaoyu.model;

public class AjaxResult {

    public int code;
    public String msg;
    public Object data;

    public AjaxResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static AjaxResult success(Object obj) {
        AjaxResult result = new AjaxResult(1, "", obj);
        return result;
    }

    public static AjaxResult error(String msg) {
        AjaxResult result = new AjaxResult(-1, msg, null);
        return result;
    }


}
