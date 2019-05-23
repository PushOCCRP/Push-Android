package com.push.krik.interfaces.AuthenticationManager;

public interface AuthenticationDelegate {
    void didLoginSuccessfully();
    void didReceiveErrorOnLogin();
}
