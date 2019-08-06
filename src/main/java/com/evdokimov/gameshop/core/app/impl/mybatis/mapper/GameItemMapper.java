package com.evdokimov.gameshop.core.app.impl.mybatis.mapper;

import com.evdokimov.gameshop.core.entity.GameItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GameItemMapper {

    @Select("select * from GAME_ITEM")
    List<GameItem> getGameItems();

    @Select("select * from GAME_ITEM where NAME = #{name}")
    GameItem getGameItemByName(String name);

    @Insert("insert into GAME_ITEM values (#{id, jdbcType=OTHER}, #{name}, #{price})")
    void insertGameItem(GameItem gameItem);

    @Insert("update GAME_ITEM set PRICE = #{price} where ID = #{id, jdbcType=OTHER}")
    void updateGameItem(GameItem gameItem);
}
