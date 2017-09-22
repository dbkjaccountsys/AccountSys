package com.dbkj.account.config.sql;

import com.dbkj.account.config.SqlContext;

import java.io.IOException;
import java.util.Map;

/**
 * Created by DELL on 2017/09/21.
 */
public interface ISqlRender {

    /**
     * 判断是否是相同类型
     * @param renderType
     * @return
     */
    boolean isMatch(SqlContext.RenderType renderType);

    /**
     * 执行解析
     * @param templateContent
     * @param paraMap
     * @return
     */
    String render(String templateContent, Map<String,Object> paraMap) throws IOException;
}
