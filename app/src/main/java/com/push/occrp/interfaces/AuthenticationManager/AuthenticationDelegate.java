package com.push.occrp.interfaces.AuthenticationManager;

public interface AuthenticationDelegate {
    void didLoginSuccessfully();
    void didReceiveErrorOnLogin();
}
