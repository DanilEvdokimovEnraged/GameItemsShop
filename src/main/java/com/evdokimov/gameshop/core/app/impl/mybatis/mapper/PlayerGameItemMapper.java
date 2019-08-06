package com.evdokimov.gameshop.core.app.impl.mybatis.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

public interface PlayerGameItemMapper {
    @Insert("insert into PLAYER_GAME_ITEM values (#{playerId, jdbcType=OTHER}, #{itemId, jdbcType=OTHER})")
    void buyGameItem(@Param("playerId") UUID playerId, @Param("itemId") UUID itemId);

    @Delete("delete from PLAYER_GAME_ITEM where PLAYER_ID = #{playerId, jdbcType=OTHER} and GAME_ITEM_ID = #{itemId, jdbcType=OTHER}")
    void sellGameItem(@Param("playerId") UUID playerId, @Param("itemId") UUID itemId);
}
