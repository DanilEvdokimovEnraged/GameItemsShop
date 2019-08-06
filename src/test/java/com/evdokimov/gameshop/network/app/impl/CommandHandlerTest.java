package com.evdokimov.gameshop.network.app.impl;

import com.evdokimov.gameshop.core.app.impl.hibernate.HibernateShopServiceImpl;
import com.evdokimov.gameshop.core.entity.GameItem;
import com.evdokimov.gameshop.core.entity.Player;
import com.evdokimov.gameshop.network.exception.ProcessCommandException;
import com.evdokimov.gameshop.network.util.PlayerConnectionUtils;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PlayerConnectionUtils.class)
public class CommandHandlerTest {

    private static final UUID CONNECTED_PLAYER_ID = UUID.randomUUID();
    private static final UUID NOT_CONNECTED_PLAYER_ID = UUID.randomUUID();

    private Player player = new Player();
    private List<GameItem> gameItems = new ArrayList<>();
    private GameItem first = new GameItem();
    private GameItem second = new GameItem();
    private GameItem third = new GameItem();

    @Before
    public void initObjects() {
        player.setId(CONNECTED_PLAYER_ID);
        player.setLogin("player");
        player.setAccount(new BigDecimal(1000));
        player.setGameItems(gameItems);

        first.setId(UUID.randomUUID());
        first.setName("first");
        first.setPrice(new BigDecimal(1));

        second.setId(UUID.randomUUID());
        second.setName("second");
        second.setPrice(new BigDecimal(2));

        third.setId(UUID.randomUUID());
        third.setName("third");
        third.setPrice(new BigDecimal(2000));

        gameItems.add(first);
        gameItems.add(second);
    }

    @Before
    public void initMock() {
        PowerMockito.mockStatic(PlayerConnectionUtils.class);

        PowerMockito.when(PlayerConnectionUtils.isConnected(CONNECTED_PLAYER_ID)).thenReturn(true);
        PowerMockito.when(PlayerConnectionUtils.isConnected(NOT_CONNECTED_PLAYER_ID)).thenReturn(false);
    }

    @Test
    public void successParseCommandTest() throws ProcessCommandException {
        CommandHandler commandHandler = new CommandHandler();

        Pair<String, String> result = commandHandler.parseCommand("login testLogin");

        Assert.assertEquals("login", result.getKey());
        Assert.assertEquals("testLogin", result.getValue());

        result = commandHandler.parseCommand("login");

        Assert.assertEquals("login", result.getKey());
        Assert.assertTrue(StringUtils.isBlank(result.getValue()));
    }

    @Test(expected = ProcessCommandException.class)
    public void failParseCommandTest() throws ProcessCommandException {
        CommandHandler commandHandler = new CommandHandler();

        commandHandler.parseCommand(" fdgdf ^$(^!*-");
    }

    @Test
    public void successCheckAuthorizationTest() throws ProcessCommandException {
        CommandHandler commandHandler = new CommandHandler();

        commandHandler.playerId = CONNECTED_PLAYER_ID;

        commandHandler.checkAuthorization();
    }

    @Test(expected = ProcessCommandException.class)
    public void failCheckAuthorizationTest() throws ProcessCommandException {
        CommandHandler commandHandler = new CommandHandler();

        commandHandler.playerId = NOT_CONNECTED_PLAYER_ID;

        commandHandler.checkAuthorization();
    }

    @Test
    public void loginTest() throws ProcessCommandException {
        CommandHandler commandHandler = new CommandHandler();

        try {
            commandHandler.login("");

            Assert.fail();
        } catch (ProcessCommandException e) {
            Assert.assertEquals(ProcessCommandException.EMPTY_LOGIN_MESSAGE, e.getMessage());
        } catch (IOException e) {
            Assert.fail();
        }

        try {
            commandHandler.shopService = new HibernateShopServiceImpl() {
                @Override
                public synchronized UUID login(String login) {
                    return null;
                }
            };

            commandHandler.login("notExists");

            Assert.fail();
        } catch (ProcessCommandException e) {
            Assert.assertEquals("Player with login 'notExists' not exists.", e.getMessage());
        } catch (IOException e) {
            Assert.fail();
        }

        try {
            commandHandler.shopService = new HibernateShopServiceImpl() {
                @Override
                public synchronized UUID login(String login) {
                    return CONNECTED_PLAYER_ID;
                }
            };

            commandHandler.login("connectedLogin");

            Assert.fail();
        } catch (ProcessCommandException e) {
            Assert.assertEquals("Player with login 'connectedLogin' already connected.", e.getMessage());
        } catch (IOException e) {
            Assert.fail();
        }

        commandHandler = new CommandHandler() {
            @Override
            protected void writeln(String message) {
                Assert.assertEquals("Success connected as 'notConnectedLogin'.", message);
            }
        };

        try {
            commandHandler.shopService = new HibernateShopServiceImpl() {
                @Override
                public synchronized UUID login(String login) {
                    return NOT_CONNECTED_PLAYER_ID;
                }
            };

            commandHandler.login("notConnectedLogin");

            Assert.assertEquals(commandHandler.playerId, NOT_CONNECTED_PLAYER_ID);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void viewShopTest() throws ProcessCommandException, IOException {
        StringBuffer testOutput = new StringBuffer();

        CommandHandler commandHandler = new CommandHandler() {
            @Override
            protected void writeln(String message) {
                testOutput.append(message + "\n");
            }
        };

        commandHandler.playerId = CONNECTED_PLAYER_ID;
        commandHandler.shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized List<GameItem> viewShop() {
                return gameItems;
            }
        };

        commandHandler.viewShop();

        Assert.assertEquals("Available game items:\nItem name - first, price - 1\n" +
                "Item name - second, price - 2\n", testOutput.toString());

        commandHandler.shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized List<GameItem> viewShop() {
                return new ArrayList<>();
            }
        };

        commandHandler.viewShop();

        Assert.assertEquals("Available game items:\nItem name - first, price - 1\n" +
                "Item name - second, price - 2\nAvailable game items:\n", testOutput.toString());
    }

    @Test(expected = ProcessCommandException.class)
    public void failMyInfoTest() throws ProcessCommandException, IOException {
        CommandHandler commandHandler = new CommandHandler();

        commandHandler.playerId = CONNECTED_PLAYER_ID;
        commandHandler.shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized Player myInfo(UUID playerId) {
                return null;
            }
        };

        commandHandler.myInfo();

        Assert.fail();
    }

    @Test
    public void successMyInfoTest() throws ProcessCommandException, IOException {
        StringBuffer testOutput = new StringBuffer();

        CommandHandler commandHandler = new CommandHandler() {
            @Override
            protected void writeln(String message) {
                testOutput.append(message + "\n");
            }
        };

        commandHandler.playerId = CONNECTED_PLAYER_ID;
        commandHandler.shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized Player myInfo(UUID playerId) {
                return player;
            }
        };

        commandHandler.myInfo();

        Assert.assertEquals("Player info: login - player, account - 1000\nPlayer's game items:\n" +
                "Item name - first, price - 1\nItem name - second, price - 2\n", testOutput.toString());
    }

    @Test
    public void buyTest() throws ProcessCommandException, IOException {
        StringBuffer testOutput = new StringBuffer();

        CommandHandler commandHandler = new CommandHandler() {

            @Override
            protected void writeln(String message) {
                testOutput.append(message + "\n");
            }
        };

        commandHandler.playerId = CONNECTED_PLAYER_ID;

        try {
            commandHandler.buy("");

            Assert.fail();
        } catch (ProcessCommandException e) {
            Assert.assertEquals(ProcessCommandException.EMPTY_GAME_ITEM_NAME_MESSAGE, e.getMessage());
        } catch (IOException e) {
            Assert.fail();
        }

        commandHandler.shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized GameItem getGameItemByName(String itemName) {
                return null;
            }
        };

        try {
            commandHandler.buy("notExistsItemName");

            Assert.fail();
        } catch (ProcessCommandException e) {
            Assert.assertEquals("Game item with name 'notExistsItemName' not exists.", e.getMessage());
        } catch (IOException e) {
            Assert.fail();
        }

        commandHandler.shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized GameItem getGameItemByName(String itemName) {
                return first;
            }

            @Override
            public synchronized Player myInfo(UUID playerId) {
                return player;
            }
        };

        try {
            commandHandler.buy(first.getName());

            Assert.fail();
        } catch (ProcessCommandException e) {
            Assert.assertEquals("Game item with name 'first' already bought.", e.getMessage());
        } catch (IOException e) {
            Assert.fail();
        }

        commandHandler.shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized GameItem getGameItemByName(String itemName) {
                return third;
            }

            @Override
            public synchronized Player myInfo(UUID playerId) {
                return player;
            }

            @Override
            public synchronized void buy(Player player, GameItem gameItem) {
                Assert.assertEquals(CommandHandlerTest.this.player, player);
                Assert.assertEquals(third, gameItem);
            }
        };

        try {
            commandHandler.buy(third.getName());

            Assert.fail();
        } catch (ProcessCommandException e) {
            Assert.assertEquals(ProcessCommandException.NOT_ENOUGH_MONEY_MESSAGE, e.getMessage());
        } catch (IOException e) {
            Assert.fail();
        }

        third.setPrice(new BigDecimal(500));

        commandHandler.buy(third.getName());

        Assert.assertEquals("Purchase successful.\n", testOutput.toString());
    }

    @Test
    public void sellTest() throws ProcessCommandException, IOException {
        StringBuffer testOutput = new StringBuffer();

        CommandHandler commandHandler = new CommandHandler() {

            @Override
            protected void writeln(String message) {
                testOutput.append(message + "\n");
            }
        };

        commandHandler.playerId = CONNECTED_PLAYER_ID;

        try {
            commandHandler.sell("");

            Assert.fail();
        } catch (ProcessCommandException e) {
            Assert.assertEquals(ProcessCommandException.EMPTY_GAME_ITEM_NAME_MESSAGE, e.getMessage());
        } catch (IOException e) {
            Assert.fail();
        }

        commandHandler.shopService = new HibernateShopServiceImpl() {
            @Override
            public synchronized Player myInfo(UUID playerId) {
                return player;
            }

            @Override
            public synchronized void sell(Player player, GameItem gameItem) {
                Assert.assertEquals(CommandHandlerTest.this.player, player);
                Assert.assertEquals(first, gameItem);
            }
        };

        try {
            commandHandler.sell(third.getName());

            Assert.fail();
        } catch (ProcessCommandException e) {
            Assert.assertEquals("Game item with name 'third' not bought.", e.getMessage());
        } catch (IOException e) {
            Assert.fail();
        }

        commandHandler.sell(first.getName());

        Assert.assertEquals("Sale successful.\n", testOutput.toString());
    }
}
