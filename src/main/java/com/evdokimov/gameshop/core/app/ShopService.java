package com.evdokimov.gameshop.core.app;

import com.evdokimov.gameshop.core.entity.GameItem;
import com.evdokimov.gameshop.core.entity.Player;

import java.util.List;
import java.util.UUID;

public interface ShopService {

    UUID login(String login);

    List<GameItem> viewShop();

    Player myInfo(UUID playerId);

    GameItem getGameItemByName(String itemName);

    void buy(Player player, GameItem gameItem);

    void sell(Player player, GameItem gameItem);

    void createOrUpdateGameItem(GameItem gameItem);
}
