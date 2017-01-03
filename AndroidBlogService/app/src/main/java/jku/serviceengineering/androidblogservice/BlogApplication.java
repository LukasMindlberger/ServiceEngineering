package jku.serviceengineering.androidblogservice;

import android.app.Application;

/**
 * Created by Lukas Mindlberger on 03.01.2017.
 */

public class BlogApplication extends Application {
    private String user;
    private String ip = "192.168.1.105";
    private String port = "8081";

    public String getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void userSignOut() {
        this.user = null;
    }
}
