package com.dbkj.account.config;

import com.dbkj.account.dic.Constant;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.token.ITokenCache;
import com.jfinal.token.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2017/09/20.
 */
public class FromTokenByEhcache implements ITokenCache {

    public void put(Token token) {
        CacheKit.put(Constant.TOKEN_CACHE_KEY,token.getId(),token);
    }

    public void remove(Token token) {
        CacheKit.remove(Constant.TOKEN_CACHE_KEY,token.getId());
    }

    public boolean contains(Token token) {
        return CacheKit.get(Constant.TOKEN_CACHE_KEY,token.getId())!=null;
    }

    public List<Token> getAll() {
        List<String> keys=CacheKit.getKeys(Constant.TOKEN_CACHE_KEY);
        List<Token> list=new ArrayList<Token>(keys.size());
        for(String key:keys){
            list.add((Token) CacheKit.get(Constant.TOKEN_CACHE_KEY,key));
        }
        return list;
    }
}
