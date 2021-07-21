package com.ttz.kmystro.catchone;

import android.app.Application;

public class GlobalActivity extends Application {

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
