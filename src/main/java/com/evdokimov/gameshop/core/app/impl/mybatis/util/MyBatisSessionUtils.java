package com.evdokimov.gameshop.core.app.impl.mybatis.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

public class MyBatisSessionUtils {

    private static final String CONFIG_FILE_NAME = "mybatis-config.xml";

    private static SqlSessionFactory sqlSessionFactory;

    public static SqlSession openSession() {
        if (sqlSessionFactory == null) {
            try {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(CONFIG_FILE_NAME));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sqlSessionFactory.openSession();
    }
}
