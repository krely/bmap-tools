package com.jixiaoyu.model;

public class Point {
    private String x;
    private String y;

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public Point(){}

    public Point(String x, String y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

    public String ToStringT() {
        return x + "\t" + y;
    }
}