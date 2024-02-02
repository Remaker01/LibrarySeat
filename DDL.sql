create table if not exists users
(
    uid      int auto_increment
        primary key,
    username varchar(255)                 null,
    password varchar(255)                 not null,
    truename varchar(20)                  not null,
    gender   varchar(5)                   not null,
    phone    varchar(13) charset latin1   not null,
    role     tinyint unsigned default '2' null comment '0:超级管理员，1:图书室管理员，2:学生',
    valid    tinyint unsigned default '1' null,
    constraint username
        unique (username)
);

create table if not exists message
(
    uid     int                    not null,
    time    datetime               not null,
    title   varchar(60) default '' not null,
    content varchar(2000)          null,
    primary key (uid, time),
    constraint message_users_uid_fk
        foreign key (uid) references users (uid)
            on update cascade
);

create table if not exists room
(
    roomid   int auto_increment
        primary key,
    roomname varchar(255) not null,
    admin    int          not null,
    constraint room_users_uid_fk
        foreign key (admin) references users (uid)
            on update cascade
);

create table if not exists seat
(
    seatid int                          not null,
    roomid int                          not null,
    status tinyint unsigned default '0' not null comment '0：空闲，1：已预定未签到，2：已签到',
    primary key (seatid, roomid),
    constraint RID
        foreign key (roomid) references room (roomid)
            on update cascade on delete cascade
);

create table if not exists reservation
(
    seatid           int      not null,
    roomid           int      not null,
    uid              int      not null,
    reservation_time datetime not null,
    signin_time      datetime null,
    signout_time     datetime null comment 'signintime!=null时为退座时间，否则为放弃时间',
    primary key (roomid, seatid, uid, reservation_time),
    constraint reservation_seat_seatid_roomid_fk
        foreign key (seatid, roomid) references seat (seatid, roomid)
            on update cascade on delete cascade,
    constraint reservation_users_uid_fk
        foreign key (uid) references users (uid)
            on update cascade
);

create index users_phone_index
    on users (phone);