package com.jstarcraft.core.orm.mongo.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.jstarcraft.core.orm.mongo.schema.MongoXmlParser.ElementDefinition;

/**
 * MongoXML处理器
 * 
 * @author Birdy
 */
public class MongoXmlHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(ElementDefinition.CONFIGURATION.getName(), new MongoXmlParser());
    }

}
