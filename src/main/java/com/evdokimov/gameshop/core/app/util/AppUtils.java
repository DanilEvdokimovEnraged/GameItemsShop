package com.evdokimov.gameshop.core.app.util;

import com.evdokimov.gameshop.core.app.ShopService;
import com.evdokimov.gameshop.core.app.impl.hibernate.HibernateShopServiceImpl;
import com.evdokimov.gameshop.core.app.impl.mybatis.MyBatisShopServiceImpl;
import org.apache.ibatis.io.Resources;

import java.io.IOException;
import java.util.Properties;

public class AppUtils {

    private static final String DB_CONNECTION_MODE_PROPERTY_NAME = "db.connectionMode";
    private static final String HIBERNATE_MODE = "hibernate";
    private static final String MY_BATIS_MODE = "mybatis";
    private static final String SERVER_WORK_PORT_PROPERTY_NAME = "server.workPort";
    private static final String DEFAULT_SERVER_WORK_PORT = "7777";
    private static final String CONFIG_FILE_NAME = "config.properties";

    private static ShopService shopService;
    private static Properties properties;

    public static synchronized ShopService getShopService() {
        if (shopService == null) {
            switch (getProperties().getProperty(DB_CONNECTION_MODE_PROPERTY_NAME, HIBERNATE_MODE)) {
                case MY_BATIS_MODE:
                    shopService = new MyBatisShopServiceImpl();
                    break;
                case HIBERNATE_MODE:
                default:
                    shopService = new HibernateShopServiceImpl();
            }
        }

        return shopService;
    }

    public static synchronized Properties getProperties() {
        if (properties == null) {
            try {
                properties = new Properties();

                properties.load(Resources.getResourceAsReader(CONFIG_FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return properties;
    }

    public static synchronized int getServerPort() {
        return Integer.parseInt(getProperties().getProperty(SERVER_WORK_PORT_PROPERTY_NAME, DEFAULT_SERVER_WORK_PORT));
    }
}
