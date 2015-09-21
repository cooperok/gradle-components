package com.example.logger;

/**
 * Simplest logger
 */
public class Log {

    public void log(String e) {
        System.out.println(e);
    }

    public void log(Exception e) {
        System.out.println(e.getMessage());
    }

}