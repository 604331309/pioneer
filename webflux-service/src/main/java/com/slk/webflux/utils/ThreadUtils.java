package com.slk.webflux.utils;

public class ThreadUtils {

    public static void println(Object object) {
        String threadName = Thread.currentThread().getName();
        System.out.println("[线程：" + threadName + "]" + object);
    }
}
