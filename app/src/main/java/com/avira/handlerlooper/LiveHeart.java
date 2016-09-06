package com.avira.handlerlooper;

import com.google.gson.annotations.SerializedName;


public class LiveHeart {



    @SerializedName("result")
    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
