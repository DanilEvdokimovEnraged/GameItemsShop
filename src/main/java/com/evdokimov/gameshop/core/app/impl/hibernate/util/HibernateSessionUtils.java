package com.evdokimov.gameshop.core.app.impl.hibernate.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionUtils {

    private static SessionFactory sessionFactory;

    public static Session openSession() {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration().configure();

            sessionFactory = configuration.buildSessionFactory();
        }

        return sessionFactory.openSession();
    }
}
