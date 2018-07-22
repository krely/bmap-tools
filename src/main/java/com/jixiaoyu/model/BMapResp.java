package com.jixiaoyu.model;

import java.util.List;

public class BMapResp {
    private int status;
    private List<Point> result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Point> getResult() {
        return result;
    }

    public void setResult(List<Point> result) {
        this.result = result;
    }
}