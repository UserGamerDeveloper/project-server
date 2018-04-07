package com.server;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sessions")
public class SessionInfo {
    @Id
    @Column(name="authKey")
    private String mKey;
    @Column(name="idPlayer")
    private int mIdPlayer;

    public SessionInfo(int idPlayer, String key) {
        mIdPlayer = idPlayer;
        mKey = key;
    }

    public SessionInfo() {
    }

    public String getKey() {
        return mKey;
    }
    public void setKey(String key) {
        mKey = key;
    }
    public int getIdPlayer() {
        return mIdPlayer;
    }
}
