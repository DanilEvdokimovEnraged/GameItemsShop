package com.evdokimov.gameshop.network.app.impl;

import com.evdokimov.gameshop.core.app.ShopService;
import com.evdokimov.gameshop.core.app.util.AppUtils;
import com.evdokimov.gameshop.network.util.CommandConstants;
import com.evdokimov.gameshop.network.app.ShopApi;
import com.evdokimov.gameshop.network.exception.ProcessCommandException;
import com.evdokimov.gameshop.core.entity.GameItem;
import com.evdokimov.gameshop.core.entity.Player;
import com.evdokimov.gameshop.network.util.PlayerConnectionUtils;
import javafx.util.Pair;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;

public class CommandHandler implements Runnable, ShopApi {

    protected UUID playerId;
    protected OutputStream clientOutputStream;
    protected BufferedReader clientBufferedReader;
    protected ShopService shopService;

    protected CommandHandler() {
    }

    public CommandHandler(OutputStream clientOutputStream, InputStream clientInputStream) {
        this.clientOutputStream = clientOutputStream;
        clientBufferedReader = new BufferedReader(new InputStreamReader(clientInputStream, StandardCharsets.UTF_8));

        shopService = AppUtils.getShopService();
    }

    protected void checkAuthorization() throws ProcessCommandException {
        if (!PlayerConnectionUtils.isConnected(playerId)) {
            throw new ProcessCommandException(ProcessCommandException.NEED_TO_DO_LOGIN_MESSAGE);
        }
    }

    protected Pair<String, String> parseCommand(String commandString) throws ProcessCommandException {
        Matcher matcher = CommandConstants.COMMAND_PATTERN.matcher(commandString);

        if (!matcher.find()) {
            throw new ProcessCommandException(String.format("Invalid command format '%s'.", commandString));
        }

        String command = matcher.group(1).toLowerCase();
        String param = matcher.group(2);

        if (StringUtils.isNotBlank(param)) {
            param = param.trim();
        }

        return new Pair<>(command, param);
    }

    protected void processCommand(String commandString) throws IOException {
        try {
            if (commandString == null) {
                throw new IOException("Client disconnected.");
            }

            Pair<String, String> commandParts = parseCommand(commandString);

            String command = commandParts.getKey();
            String param = commandParts.getValue();

            switch (command) {
                case CommandConstants.LOGIN_COMMAND:
                    login(param);
                    break;
                case CommandConstants.LOGOUT_COMMAND:
                    logout();
                    break;
                case CommandConstants.VIEW_SHOP_COMMAND:
                    viewShop();
                    break;
                case CommandConstants.MY_INFO_COMMAND:
                    myInfo();
                    break;
                case CommandConstants.BUY_COMMAND:
                    buy(param);
                    break;
                case CommandConstants.SELL_COMMAND:
                    sell(param);
                    break;
                default:
                    throw new ProcessCommandException(String.format("Unknown command '%s'.", command));
            }
        } catch (ProcessCommandException e) {
            writeln(e.getMessage());
        }
    }

    @Override
    public void login(String login) throws ProcessCommandException, IOException {
        if (StringUtils.isBlank(login)) {
            throw new ProcessCommandException(ProcessCommandException.EMPTY_LOGIN_MESSAGE);
        }

        UUID playerId = shopService.login(login);

        if (playerId == null) {
            throw new ProcessCommandException(String.format("Player with login '%s' not exists.", login));
        }

        if (PlayerConnectionUtils.isConnected(playerId)) {
            throw new ProcessCommandException(String.format("Player with login '%s' already connected.", login));
        }

        PlayerConnectionUtils.connectPlayer(playerId);

        if (this.playerId != null) {
            PlayerConnectionUtils.disconnectPlayer(this.playerId);
        }

        this.playerId = playerId;

        writeln(String.format("Success connected as '%s'.", login));
    }

    @Override
    public void logout() {
        PlayerConnectionUtils.disconnectPlayer(playerId);
    }

    @Override
    public void viewShop() throws ProcessCommandException, IOException {
        checkAuthorization();

        List<GameItem> gameItems = shopService.viewShop();

        writeln("Available game items:");

        for (GameItem gameItem : gameItems) {
            writeln(gameItem.toString());
        }
    }

    @Override
    public void myInfo() throws ProcessCommandException, IOException {
        checkAuthorization();

        Player player = shopService.myInfo(playerId);

        if (player == null) {
            throw new ProcessCommandException("Current player not exists.");
        }

        writeln(player.toString());
        writeln("Player's game items:");

        for (GameItem gameItem : player.getGameItems()) {
            writeln(gameItem.toString());
        }
    }

    @Override
    public void buy(String itemName) throws ProcessCommandException, IOException {
        checkAuthorization();

        if (StringUtils.isBlank(itemName)) {
            throw new ProcessCommandException(ProcessCommandException.EMPTY_GAME_ITEM_NAME_MESSAGE);
        }

        GameItem gameItem = shopService.getGameItemByName(itemName);

        if (gameItem == null) {
            throw new ProcessCommandException(String.format("Game item with name '%s' not exists.", itemName));
        }

        Player player = shopService.myInfo(playerId);

        if (CollectionUtils.exists(player.getGameItems(), (item) -> item.getId().equals(gameItem.getId()))) {
            throw new ProcessCommandException(String.format("Game item with name '%s' already bought.", gameItem.getName()));
        }

        if (player.getAccount().compareTo(gameItem.getPrice()) < 0) {
            throw new ProcessCommandException(ProcessCommandException.NOT_ENOUGH_MONEY_MESSAGE);
        }

        shopService.buy(player, gameItem);

        writeln("Purchase successful.");
    }

    @Override
    public void sell(String itemName) throws ProcessCommandException, IOException {
        checkAuthorization();

        if (StringUtils.isBlank(itemName)) {
            throw new ProcessCommandException(ProcessCommandException.EMPTY_GAME_ITEM_NAME_MESSAGE);
        }

        Player player = shopService.myInfo(playerId);

        GameItem sellGameItem = CollectionUtils.find(player.getGameItems(), (item) -> item.getName().equals(itemName));

        if (sellGameItem == null) {
            throw new ProcessCommandException(String.format("Game item with name '%s' not bought.", itemName));
        }

        shopService.sell(player, sellGameItem);

        writeln("Sale successful.");
    }

    @Override
    public void run() {
        try {
            welcomeMassage();

            while (true) {
                String command = clientBufferedReader.readLine();

                processCommand(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PlayerConnectionUtils.disconnectPlayer(playerId);
        }
    }

    protected void welcomeMassage() throws IOException {
        writeln("Welcome to Game Shop!\n\rPlease, press enter!");

        clientBufferedReader.readLine();
    }

    protected void writeln(String message) throws IOException {
        write(message + "\n\r");
    }

    protected void write(String message) throws IOException {
        IOUtils.write(message, clientOutputStream, StandardCharsets.UTF_8);
    }
}
