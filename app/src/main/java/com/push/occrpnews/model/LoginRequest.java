package com.push.occrpnews.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("api_key")
    @Expose
    private String apiKey;
    @SerializedName("username")
    @Expose
    private String username;

    /**
     *
     * @return
     * The code
     */
    public Integer getCode() {
        return code;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @return
     * The status
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     *
     * @return
     * The status
     */
    public String getUsername() {
        return username;
    }

}
