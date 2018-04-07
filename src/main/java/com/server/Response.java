package com.server;

public class Response {
    private String data;
    private Byte error;

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
