package com.evdokimov.gameshop.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "evd$GameItem")
@Table(name = "GAME_ITEM")
@XmlRootElement(name = "item")
public class GameItem implements Serializable {
    @Id
    @Column(name = "ID")
    private UUID id;
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;
    @Column(name = "PRICE", nullable = false)
    private BigDecimal price;

    @XmlTransient
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @XmlElement(name = "name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "price", required = true)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("Item name - %s, price - %s", name, price.toString());
    }
}
