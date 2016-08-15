package com.ityadi.app.tourmate.Response;

/**
 * Created by taslim on 8/12/2016.
 */
public class UserResponse {
    private String username,msg,err;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }
}
