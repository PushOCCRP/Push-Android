package com.pushapp.press.interfaces.AuthenticationManager;

public interface AuthenticationDelegate {
    void didLoginSuccessfully();
    void didReceiveErrorOnLogin();
}
