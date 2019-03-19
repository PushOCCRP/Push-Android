package com.push.occrpnews.interfaces.AuthenticationManager;

public interface AuthenticationDelegate {
    void didLoginSuccessfully();
    void didReceiveErrorOnLogin();
}
