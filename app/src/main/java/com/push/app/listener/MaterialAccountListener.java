package com.push.app.listener;


import com.push.app.NavigationDrawer.MaterialAccount;

/**
 * Created by neokree on 11/12/14.
 */
public interface MaterialAccountListener {

    public void onAccountOpening(MaterialAccount account);

    public void onChangeAccount(MaterialAccount newAccount);

}
