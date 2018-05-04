package com.server;

public class BalanceRequest {

    String base;
    String id;
    String param;
    String value;

    public BalanceRequest(){ }
    public BalanceRequest(String base, String id, String param, String value) {
        this.base = base;
        this.id = id;
        this.param = param;
        this.value = value;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}