package com.dbkj.account.util;

import java.util.Random;

public class RandomUtil {

	/**
     * 获取一个随机的32位字符串
     * @return
     */
    public static String getSeqId() {
        return getRanCode(32);
    }

    /**
     * 获取一个指定长度的随机码
     * @param len
     * @return
     */
    public static String getRanCode(int len) {
        Random randGen = new Random();
        char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
                .toCharArray();
        char[] randBuffer = new char[len];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(35)];
        }
        return new String(randBuffer);
    }

}
