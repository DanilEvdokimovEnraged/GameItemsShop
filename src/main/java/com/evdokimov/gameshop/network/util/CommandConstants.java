package com.evdokimov.gameshop.network.util;

import java.util.regex.Pattern;

public class CommandConstants {

    public static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\w+)(\\s+.*)?$");
    public static final String LOGIN_COMMAND = "login";
    public static final String LOGOUT_COMMAND = "logout";
    public static final String VIEW_SHOP_COMMAND = "viewshop";
    public static final String MY_INFO_COMMAND = "myinfo";
    public static final String BUY_COMMAND = "buy";
    public static final String SELL_COMMAND = "sell";
}
