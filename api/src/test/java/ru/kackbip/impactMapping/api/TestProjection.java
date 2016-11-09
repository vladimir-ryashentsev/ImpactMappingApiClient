package ru.kackbip.impactMapping.api;

/**
 * Created by ryashentsev on 07.11.2016.
 */

public class TestProjection {
    private String str;
    private int num;
    private TestProjection inner;

    public TestProjection(String str, int num, TestProjection inner){
        this.str = str;
        this.num = num;
        this.inner = inner;
    }

    public String getStr() {
        return str;
    }

    public int getNum() {
        return num;
    }

    public TestProjection getInner() {
        return inner;
    }
}
