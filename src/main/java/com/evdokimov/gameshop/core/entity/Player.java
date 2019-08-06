package com.evdokimov.gameshop.core.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity(name = "evd$Player")
@Table(name = "PLAYER")
public class Player {
    @Id
    @Column(name = "ID")
    private UUID id;
    @Column(name = "LOGIN", unique = true, nullable = false)
    private String login;
    @Column(name = "ACCOUNT", nullable = false)
    private BigDecimal account;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PLAYER_GAME_ITEM", joinColumns = @JoinColumn(name = "PLAYER_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "GAME_ITEM_ID", referencedColumnName = "ID"))
    private List<GameItem> gameItems;

    public List<GameItem> getGameItems() {
        return gameItems;
    }

    public void setGameItems(List<GameItem> gameItems) {
        this.gameItems = gameItems;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public BigDecimal getAccount() {
        return account;
    }

    public void setAccount(BigDecimal account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return String.format("Player info: login - %s, account - %s", login,
                account.toString());
    }
}
