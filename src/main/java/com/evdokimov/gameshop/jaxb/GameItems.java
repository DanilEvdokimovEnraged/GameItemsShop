package com.evdokimov.gameshop.jaxb;

import com.evdokimov.gameshop.core.entity.GameItem;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "items")
public class GameItems {

    private List<GameItem> gameItems;

    @XmlElement(name = "item")
    public List<GameItem> getGameItems() {
        return gameItems;
    }

    public void setGameItems(List<GameItem> gameItems) {
        this.gameItems = gameItems;
    }
}
