package com.evdokimov.gameshop.core.app.impl.mybatis.mapper;

import com.evdokimov.gameshop.core.entity.GameItem;
import com.evdokimov.gameshop.core.entity.Player;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PlayerMapper {

    @Select("select * from PLAYER where ID = #{id, jdbcType=OTHER}")
    Player getPlayerById(UUID id);

    @Results(id = "playerResult", value = {
            @Result(property = "id", column = "id", id = true, javaType = UUID.class),
            @Result(property = "login", column = "login"),
            @Result(property = "account", column = "account"),
            @Result(property = "gameItems", column = "id", javaType = List.class, many = @Many(select = "getPlayerGameItems"))
    })
    @Select("select * from PLAYER where ID = #{id, jdbcType=OTHER}")
    Player getPlayerWithGameItemsById(UUID id);

    @Select("select ID from PLAYER where LOGIN = #{login}")
    UUID getPlayerIdByLogin(String login);

    @Select("select GI.ID as ID, GI.NAME as NAME, GI.PRICE as PRICE from PLAYER_GAME_ITEM as PGI " +
            "join GAME_ITEM as GI on PGI.GAME_ITEM_ID = GI.ID where PGI.PLAYER_ID = #{playerId, jdbcType=OTHER}")
    List<GameItem> getPlayerGameItems(UUID playerId);

    @Update("update PLAYER set ACCOUNT = #{account} where ID = #{playerId, jdbcType=OTHER}")
    void updatePlayerAccount(@Param("playerId") UUID playerId, @Param("account") BigDecimal account);
}
