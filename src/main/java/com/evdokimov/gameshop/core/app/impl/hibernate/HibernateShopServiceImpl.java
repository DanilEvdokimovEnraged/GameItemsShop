package com.evdokimov.gameshop.core.app.impl.hibernate;

import com.evdokimov.gameshop.core.app.ShopService;
import com.evdokimov.gameshop.core.entity.GameItem;
import com.evdokimov.gameshop.core.entity.Player;
import com.evdokimov.gameshop.core.app.impl.hibernate.util.HibernateSessionUtils;
import org.hibernate.Session;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.UUID;

public class HibernateShopServiceImpl implements ShopService {

    private static final String GET_PLAYER_BY_LOGIN_QUERY = "select p.id from evd$Player p where p.login = :login";
    private static final String GET_GAME_ITEMS_QUERY = "select g from evd$GameItem g";
    private static final String GET_GAME_ITEM_BY_NAME_QUERY = "select g from evd$GameItem g where g.name = :name";

    @Override
    public synchronized UUID login(String login) {
        try (Session session = HibernateSessionUtils.openSession()) {
            return session.createQuery(GET_PLAYER_BY_LOGIN_QUERY, UUID.class)
                    .setParameter("login", login).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public synchronized List<GameItem> viewShop() {
        try (Session session = HibernateSessionUtils.openSession()) {
            return session.createQuery(GET_GAME_ITEMS_QUERY, GameItem.class).getResultList();
        }
    }

    @Override
    public synchronized Player myInfo(UUID playerId) {
        try (Session session = HibernateSessionUtils.openSession()) {
            return session.load(Player.class, playerId);
        }
    }

    @Override
    public synchronized GameItem getGameItemByName(String itemName) {
        try (Session session = HibernateSessionUtils.openSession()) {
            return session.createQuery(GET_GAME_ITEM_BY_NAME_QUERY, GameItem.class)
                    .setParameter("name", itemName).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public synchronized void buy(Player player, GameItem gameItem) {
        try (Session session = HibernateSessionUtils.openSession()) {
            session.beginTransaction();

            player.setAccount(player.getAccount().subtract(gameItem.getPrice()));
            player.getGameItems().add(gameItem);

            session.saveOrUpdate(player);

            session.getTransaction().commit();
        }
    }

    @Override
    public synchronized void sell(Player player, GameItem gameItem) {
        try (Session session = HibernateSessionUtils.openSession()) {
            session.beginTransaction();

            player.setAccount(player.getAccount().add(gameItem.getPrice()));
            player.getGameItems().remove(gameItem);

            session.update(player);

            session.getTransaction().commit();
        }
    }

    @Override
    public synchronized void createOrUpdateGameItem(GameItem gameItem) {
        try (Session session = HibernateSessionUtils.openSession()) {
            session.beginTransaction();

            GameItem exists = getGameItemByName(gameItem.getName());

            if (exists == null) {
                gameItem.setId(UUID.randomUUID());

                session.save(gameItem);
            } else {
                gameItem.setId(exists.getId());

                session.update(gameItem);
            }

            session.getTransaction().commit();
        }
    }
}
