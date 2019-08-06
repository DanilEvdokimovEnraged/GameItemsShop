create table PLAYER (
    ID uuid,
    LOGIN varchar(255) unique not null,
    ACCOUNT numeric(17,2) not null,

    primary key (ID)
);

create table GAME_ITEM (
    ID uuid,
    NAME varchar(255) unique not null,
    PRICE numeric(17,2) not null,

    primary key (ID)
);

create table PLAYER_GAME_ITEM (
    PLAYER_ID uuid,
    GAME_ITEM_ID uuid,

    primary key (PLAYER_ID, GAME_ITEM_ID),

    foreign key (PLAYER_ID) references PLAYER (ID),
    foreign key (GAME_ITEM_ID) references GAME_ITEM (ID)
);

create index IDX_PLAYER_GAME_ITEM_PLAYER_ID on PLAYER_GAME_ITEM (PLAYER_ID);
create index IDX_PLAYER_GAME_ITEM_GAME_ITEM_ID on PLAYER_GAME_ITEM (GAME_ITEM_ID);