package com.dbkj.account.util;

import java.util.Random;
import java.util.UUID;

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
    
    /**
     * 获取一个指定长度的随机字符串
     * @param len
     * @return
     */
    public static String GetRandomString(int len) {
        
        String[] baseString={"0","1","2","3",
                "4","5","6","7","8","9",
                "a","b","c","d","e",
                "f","g","h","i","j",
                "k","l","m","n","o",
                "p","q","r","s","t",
                "u","v","w","x","y",
                "z","A","B","C","D",
                "E","F","G","H","I",
                "J","K","L","M","N",
                "O","P","Q","R","S",
                "T","U","V","W","X","Y","Z"};
        Random random = new Random();
        int length=baseString.length;
        StringBuilder sb=new StringBuilder(length);
        for(int i=0;i<length;i++){
            sb.append(baseString[random.nextInt(length)]);
        }
        String randomString=sb.toString();
        random = new Random(System.currentTimeMillis());
        sb.delete(0, sb.length());
        for (int i = 0; i < len; i++) {
            sb.append(randomString.charAt(random.nextInt(randomString.length()-1)));
        }
        return sb.toString();
    }

    /**
     * 获取32位UUID随机字符串
     * @return
     */
    public static String getUUIDString(){
    	String uuid = UUID.randomUUID().toString();
		uuid=uuid.replace("-", "");
		return uuid;
    }
}
