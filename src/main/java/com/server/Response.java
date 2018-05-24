package com.server;

public class Response {
    private String data;
    private Byte error;
    private Integer mGearScore;

    public Integer getGearScore() {
        return mGearScore;
    }
    public void setGearScore(Integer gearScore) {
        mGearScore = gearScore;
    }

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

    public Byte getError() {
        return error;
    }
    public void setError(Byte error) {
        this.error = error;
    }
}
