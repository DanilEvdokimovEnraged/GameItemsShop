package com.evdokimov.gameshop.network.app;

import com.evdokimov.gameshop.network.exception.ProcessCommandException;

import java.io.IOException;

public interface ShopApi {

    void login(String login) throws ProcessCommandException, IOException;

    void logout();

    void viewShop() throws ProcessCommandException, IOException;

    void myInfo() throws ProcessCommandException, IOException;

    void buy(String itemName) throws ProcessCommandException, IOException;

    void sell(String itemName) throws ProcessCommandException, IOException;
}
