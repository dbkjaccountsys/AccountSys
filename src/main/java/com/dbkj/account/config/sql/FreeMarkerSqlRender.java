package com.dbkj.account.config.sql;

import com.dbkj.account.config.SqlContext;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * freemarker 解析sql文件
 * Created by DELL on 2017/09/21.
 */
public class FreeMarkerSqlRender implements ISqlRender {

    @Override
    public boolean isMatch(SqlContext.RenderType renderType) {
        return SqlContext.RenderType.FreeMarker.equals(renderType);
    }

    @Override
    public String render(String templateContent, Map<String, Object> paraMap) throws IOException {
        StringWriter writer=new StringWriter();
        Configuration configuration=new Configuration();
        StringTemplateLoader templateLoader=new StringTemplateLoader();
        templateLoader.putTemplate(templateContent,templateContent,System.currentTimeMillis());
        configuration.setTemplateLoader(templateLoader);
        configuration.setDefaultEncoding("UTF-8");

        Template template = configuration.getTemplate(templateContent);
        try {
            template.process(paraMap,writer);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }
}
