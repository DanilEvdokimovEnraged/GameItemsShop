package com.evdokimov.gameshop.core.app.impl.mybatis;

import com.evdokimov.gameshop.core.app.ShopService;
import com.evdokimov.gameshop.core.entity.GameItem;
import com.evdokimov.gameshop.core.entity.Player;
import com.evdokimov.gameshop.core.app.impl.mybatis.mapper.GameItemMapper;
import com.evdokimov.gameshop.core.app.impl.mybatis.mapper.PlayerGameItemMapper;
import com.evdokimov.gameshop.core.app.impl.mybatis.mapper.PlayerMapper;
import com.evdokimov.gameshop.core.app.impl.mybatis.util.MyBatisSessionUtils;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.UUID;

public class MyBatisShopServiceImpl implements ShopService {
    @Override
    public synchronized UUID login(String login) {
        try (SqlSession sqlSession = MyBatisSessionUtils.openSession()) {
            PlayerMapper playerMapper = sqlSession.getMapper(PlayerMapper.class);

            return playerMapper.getPlayerIdByLogin(login);
        }
    }

    @Override
    public synchronized List<GameItem> viewShop() {
        try (SqlSession sqlSession = MyBatisSessionUtils.openSession()) {
            GameItemMapper gameItemMapper = sqlSession.getMapper(GameItemMapper.class);

            return gameItemMapper.getGameItems();
        }
    }

    @Override
    public synchronized Player myInfo(UUID playerId) {
        try (SqlSession sqlSession = MyBatisSessionUtils.openSession()) {
            PlayerMapper playerMapper = sqlSession.getMapper(PlayerMapper.class);

            return playerMapper.getPlayerWithGameItemsById(playerId);
        }
    }

    @Override
    public synchronized GameItem getGameItemByName(String itemName) {
        try (SqlSession sqlSession = MyBatisSessionUtils.openSession()) {
            GameItemMapper gameItemMapper = sqlSession.getMapper(GameItemMapper.class);

            return gameItemMapper.getGameItemByName(itemName);
        }
    }

    @Override
    public synchronized void buy(Player player, GameItem gameItem) {
        try (SqlSession sqlSession = MyBatisSessionUtils.openSession()) {
            PlayerGameItemMapper playerGameItemMapper = sqlSession.getMapper(PlayerGameItemMapper.class);
            PlayerMapper playerMapper = sqlSession.getMapper(PlayerMapper.class);

            playerMapper.updatePlayerAccount(player.getId(), player.getAccount().subtract(gameItem.getPrice()));
            playerGameItemMapper.buyGameItem(player.getId(), gameItem.getId());

            sqlSession.commit();
        }
    }

    @Override
    public synchronized void sell(Player player, GameItem gameItem) {
        try (SqlSession sqlSession = MyBatisSessionUtils.openSession()) {
            PlayerGameItemMapper playerGameItemMapper = sqlSession.getMapper(PlayerGameItemMapper.class);
            PlayerMapper playerMapper = sqlSession.getMapper(PlayerMapper.class);

            playerMapper.updatePlayerAccount(player.getId(), player.getAccount().add(gameItem.getPrice()));
            playerGameItemMapper.sellGameItem(player.getId(), gameItem.getId());

            sqlSession.commit();
        }
    }

    @Override
    public synchronized void createOrUpdateGameItem(GameItem gameItem) {
        try (SqlSession sqlSession = MyBatisSessionUtils.openSession()) {
            GameItemMapper gameItemMapper = sqlSession.getMapper(GameItemMapper.class);

            GameItem exists = gameItemMapper.getGameItemByName(gameItem.getName());

            if (exists == null) {
                gameItem.setId(UUID.randomUUID());

                gameItemMapper.insertGameItem(gameItem);
            } else {
                gameItem.setId(exists.getId());

                gameItemMapper.updateGameItem(gameItem);
            }
        }
    }
}
