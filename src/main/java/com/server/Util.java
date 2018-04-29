package com.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Random;

public class Util {
    private static final SessionFactory SESSION_FACTORY = new Configuration().configure().buildSessionFactory();
    private static final Random RANDOM = new Random();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Получить фабрику сессий
     * @return {@link SessionFactory}
     */
    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static Random getRandom() {
        return RANDOM;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}