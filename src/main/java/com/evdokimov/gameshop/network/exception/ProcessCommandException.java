package com.evdokimov.gameshop.network.exception;

public class ProcessCommandException extends Exception {

    public static final String EMPTY_LOGIN_MESSAGE = "Do not specify a required parameter 'login'.";
    public static final String NEED_TO_DO_LOGIN_MESSAGE = "Need to do 'login' before execute operation.";
    public static final String EMPTY_GAME_ITEM_NAME_MESSAGE = "Do not specify a required parameter 'item name'.";
    public static final String NOT_ENOUGH_MONEY_MESSAGE = "There is not enough money in your account.";

    public ProcessCommandException(String message) {
        super(message);
    }
}
