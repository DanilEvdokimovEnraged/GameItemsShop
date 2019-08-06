package com.evdokimov.gameshop;

import com.evdokimov.gameshop.core.app.ShopService;
import com.evdokimov.gameshop.core.app.util.AppUtils;
import com.evdokimov.gameshop.core.entity.GameItem;
import com.evdokimov.gameshop.jaxb.GameItems;
import com.evdokimov.gameshop.jaxb.JaxbUtils;
import com.evdokimov.gameshop.network.app.impl.CommandHandler;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static void loadGameItems() {
        try {
            ShopService shopService = AppUtils.getShopService();

            GameItems gameItems = JaxbUtils.loadGameItems();

            for (GameItem gameItem : gameItems.getGameItems()) {
                shopService.createOrUpdateGameItem(gameItem);
            }
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        loadGameItems();

        ServerSocket serverSocket = new ServerSocket(AppUtils.getServerPort());

        while (true) {
            Socket clientSocket = serverSocket.accept();

            new Thread(new CommandHandler(clientSocket.getOutputStream(), clientSocket.getInputStream())).start();
        }
    }
}
