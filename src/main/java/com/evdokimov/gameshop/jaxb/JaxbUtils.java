package com.evdokimov.gameshop.jaxb;

import org.apache.ibatis.io.Resources;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;

public class JaxbUtils {

    public static GameItems loadGameItems() throws JAXBException, IOException {
        Unmarshaller un = JAXBContext.newInstance(GameItems.class).createUnmarshaller();

        return (GameItems) un.unmarshal(Resources.getResourceAsReader("items.xml"));
    }
}
