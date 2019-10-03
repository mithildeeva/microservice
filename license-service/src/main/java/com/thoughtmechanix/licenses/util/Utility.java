package com.thoughtmechanix.licenses.util;

import java.util.Random;

public class Utility {

    public static void randomlySleep(int seconds, int oneInNumChances) {
        int randomNum = (new Random()).nextInt((oneInNumChances)) + 1;
        if (randomNum == oneInNumChances) sleep(seconds);
    }

    private static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
