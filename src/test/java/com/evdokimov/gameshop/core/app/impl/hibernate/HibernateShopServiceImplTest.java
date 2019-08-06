package com.evdokimov.gameshop.core.app.impl.hibernate;

import com.evdokimov.gameshop.core.app.ShopService;
import com.evdokimov.gameshop.core.app.impl.hibernate.util.HibernateSessionUtils;
import com.evdokimov.gameshop.core.entity.GameItem;
import com.evdokimov.gameshop.core.entity.Player;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HibernateSessionUtils.class)
public class HibernateShopServiceImplTest {

    private static final UUID EXISTS_PLAYER_ID = UUID.randomUUID();
    private static final UUID NOT_EXISTS_PLAYER_ID = UUID.randomUUID();
    private static final String EXISTS_PLAYER_LOGIN = "existsLogin";
    private static final String NOT_EXISTS_PLAYER_LOGIN = "notExistsLogin";
    private static final String EXISTS_ITEM_NAME = "existsItem";
    private static final String NOT_EXISTS_ITEM_NAME = "notExistsItem";

    private static final String GET_PLAYER_BY_LOGIN_QUERY = "select p.id from evd$Player p where p.login = :login";
    private static final String GET_GAME_ITEMS_QUERY = "select g from evd$GameItem g";
    private static final String GET_GAME_ITEM_BY_NAME_QUERY = "select g from evd$GameItem g where g.name = :name";

    private ShopService testShopService = new HibernateShopServiceImpl();
    private List<GameItem> gameItems = new ArrayList<>();
    private GameItem expectedGameItem = new GameItem();
    private Player expectedPlayer = new Player();
    private Player sellTestPlayer = new Player();
    private GameItem sellTestGameItem = new GameItem();
    private Player buyTestPlayer = new Player();
    private GameItem buyTestGameItem = new GameItem();
    private GameItem newGameItem = new GameItem();
    private GameItem oldGameItem = new GameItem();
    private List<GameItem> oldGameItems = new ArrayList<>();

    @Before
    public void initTestObjects() {
        newGameItem.setId(UUID.randomUUID());
        newGameItem.setName("newGameItem");
        newGameItem.setPrice(new BigDecimal(100));

        oldGameItem.setId(UUID.randomUUID());
        oldGameItem.setName("oldGameItem");
        oldGameItem.setPrice(new BigDecimal(50));

        oldGameItems.add(oldGameItem);

        buyTestPlayer.setId(UUID.randomUUID());
        buyTestPlayer.setLogin("buyTestPlayer");
        buyTestPlayer.setAccount(new BigDecimal(50));
        buyTestPlayer.setGameItems(new ArrayList<>());

        buyTestGameItem.setId(UUID.randomUUID());
        buyTestGameItem.setName("buyTestGameItem");
        buyTestGameItem.setPrice(new BigDecimal(50));

        sellTestPlayer.setId(UUID.randomUUID());
        sellTestPlayer.setLogin("sellTestPlayer");
        sellTestPlayer.setAccount(new BigDecimal(50));
        sellTestPlayer.setGameItems(new ArrayList<>());

        sellTestGameItem.setId(UUID.randomUUID());
        sellTestGameItem.setName("sellTestGameItem");
        sellTestGameItem.setPrice(new BigDecimal(50));

        sellTestPlayer.getGameItems().add(sellTestGameItem);

        expectedPlayer.setId(EXISTS_PLAYER_ID);
        expectedPlayer.setLogin(EXISTS_PLAYER_LOGIN);
        expectedPlayer.setAccount(new BigDecimal(1));

        expectedGameItem.setId(UUID.randomUUID());
        expectedGameItem.setName("expectedGameItem");
        expectedGameItem.setPrice(new BigDecimal(1));

        GameItem first = new GameItem();

        first.setId(UUID.randomUUID());
        first.setName("first");
        first.setPrice(new BigDecimal(1));

        GameItem second = new GameItem();

        second.setId(UUID.randomUUID());
        second.setName("second");
        second.setPrice(new BigDecimal(2));

        gameItems.add(first);
        gameItems.add(second);
    }

    @Before
    public void initMocks() throws Exception {
        Session sessionMock = PowerMockito.mock(Session.class);
        Transaction transactionMock = PowerMockito.mock(Transaction.class);

        Query successLoginQueryMock = PowerMockito.mock(Query.class);
        Query failLoginQueryMock = PowerMockito.mock(Query.class);

        Query gameItemsQueryMock = PowerMockito.mock(Query.class);

        Query successItemQueryMock = PowerMockito.mock(Query.class);
        Query failItemQueryMock = PowerMockito.mock(Query.class);

        PowerMockito.when(successLoginQueryMock.setParameter("login", EXISTS_PLAYER_LOGIN))
                .thenReturn(successLoginQueryMock);
        PowerMockito.when(successLoginQueryMock.setParameter("login", NOT_EXISTS_PLAYER_LOGIN))
                .thenReturn(failLoginQueryMock);
        PowerMockito.when(successItemQueryMock.setParameter("name", EXISTS_ITEM_NAME))
                .thenReturn(successItemQueryMock);
        PowerMockito.when(successItemQueryMock.setParameter("name", NOT_EXISTS_ITEM_NAME))
                .thenReturn(failItemQueryMock);

        PowerMockito.when(successItemQueryMock.getSingleResult()).thenReturn(expectedGameItem);
        PowerMockito.when(failItemQueryMock.getSingleResult()).thenThrow(new NoResultException());
        PowerMockito.when(successLoginQueryMock.getSingleResult()).thenReturn(EXISTS_PLAYER_ID);
        PowerMockito.when(failLoginQueryMock.getSingleResult()).thenThrow(new NoResultException());
        PowerMockito.when(gameItemsQueryMock.getResultList()).thenReturn(gameItems);

        PowerMockito.when(sessionMock.getTransaction()).thenReturn(transactionMock);
        PowerMockito.doAnswer(invocationOnMock -> {
            oldGameItems.add(newGameItem);

            return null;
        }).when(sessionMock, "save", newGameItem);
        PowerMockito.doAnswer(invocationOnMock -> {
            oldGameItem.setPrice(newGameItem.getPrice());

            return null;
        }).when(sessionMock, "update", newGameItem);
        PowerMockito.doNothing().when(sessionMock, "update", buyTestPlayer);
        PowerMockito.doNothing().when(sessionMock, "update", sellTestPlayer);
        PowerMockito.when(sessionMock.load(Player.class, EXISTS_PLAYER_ID)).thenReturn(expectedPlayer);
        PowerMockito.when(sessionMock.load(Player.class, NOT_EXISTS_PLAYER_ID)).thenReturn(null);
        PowerMockito.when(sessionMock.createQuery(GET_GAME_ITEM_BY_NAME_QUERY, GameItem.class))
                .thenReturn(successItemQueryMock);
        PowerMockito.when(sessionMock.createQuery(GET_PLAYER_BY_LOGIN_QUERY, UUID.class))
                .thenReturn(successLoginQueryMock);
        PowerMockito.when(sessionMock.createQuery(GET_GAME_ITEMS_QUERY, GameItem.class))
                .thenReturn(gameItemsQueryMock);

        PowerMockito.mockStatic(HibernateSessionUtils.class);

        PowerMockito.when(HibernateSessionUtils.openSession()).thenReturn(sessionMock);
    }

    @Test
    public void successLoginTest() {
        Assert.assertEquals(EXISTS_PLAYER_ID, testShopService.login(EXISTS_PLAYER_LOGIN));
    }

    @Test
    public void failLoginTest() {
        Assert.assertNull(testShopService.login(NOT_EXISTS_PLAYER_LOGIN));
    }

    @Test
    public void successMyInfoTest() {
        Player actual = testShopService.myInfo(EXISTS_PLAYER_ID);

        Assert.assertEquals(expectedPlayer.getId(), actual.getId());
        Assert.assertEquals(expectedPlayer.toString(), actual.toString());
    }

    @Test
    public void failMyInfoTest() {
        Assert.assertNull(testShopService.myInfo(NOT_EXISTS_PLAYER_ID));
    }

    @Test
    public void viewShopTest() {
        List<GameItem> actual = testShopService.viewShop();

        Assert.assertEquals(gameItems.size(), actual.size());

        for (int i = 0; i < actual.size(); i++) {
            GameItem actualItem = actual.get(i);
            GameItem expectedItem = gameItems.get(i);

            Assert.assertEquals(expectedItem.getId(), actualItem.getId());
            Assert.assertEquals(expectedItem.toString(), actualItem.toString());
        }
    }

    @Test
    public void successGetGameItemByNameTest() {
        GameItem actual = testShopService.getGameItemByName(EXISTS_ITEM_NAME);

        Assert.assertEquals(expectedGameItem.getId(), actual.getId());
        Assert.assertEquals(expectedGameItem.toString(), actual.toString());
    }

    @Test
    public void failGetGameItemByNameTest() {
        Assert.assertNull(testShopService.getGameItemByName(NOT_EXISTS_ITEM_NAME));
    }

    @Test
    public void sellTest() {
        testShopService.sell(sellTestPlayer, sellTestGameItem);

        Assert.assertEquals(new BigDecimal(100), sellTestPlayer.getAccount());
        Assert.assertTrue(sellTestPlayer.getGameItems().isEmpty());
    }

    @Test
    public void buyTest() {
        testShopService.buy(buyTestPlayer, buyTestGameItem);

        Assert.assertEquals(new BigDecimal(0), buyTestPlayer.getAccount());
        Assert.assertFalse(buyTestPlayer.getGameItems().isEmpty());
    }

    @Test
    public void createGameItemTest() {
        ShopService shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized GameItem getGameItemByName(String itemName) {
                return null;
            }
        };

        shopService.createOrUpdateGameItem(newGameItem);

        Assert.assertEquals(2, oldGameItems.size());
        Assert.assertTrue(CollectionUtils.exists(oldGameItems, object -> object.equals(newGameItem)));
    }

    @Test
    public void updateGameItemTest() {
        ShopService shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized GameItem getGameItemByName(String itemName) {
                return oldGameItem;
            }
        };

        shopService.createOrUpdateGameItem(newGameItem);

        Assert.assertEquals(1, oldGameItems.size());
        Assert.assertEquals(oldGameItem.getPrice(), newGameItem.getPrice());
    }
}
