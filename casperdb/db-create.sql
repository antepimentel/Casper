create table server (
	id varchar(20) not null,
	name varchar(40) not null,
	constraint server_pk
		primary key (id)
);

create table customcommand (
	serverid varchar(20) not null,
	name varchar(15) not null,
	command varchar(300) not null,
	constraint customcommand_pk
		primary key (name),
	constraint server_customcommand_fk
		foreign key (serverid) references server (id)
);

create table disabledcommand (
	serverid varchar(20) not null,
	name varchar(15) not null,
	constraint command_pk
		primary key (name),
	constraint server_command_fk
		foreign key (serverid) references server (id)
);

create table eventboard (
	serverid varchar(20) not null,
	channelid varchar(20) not null,
	sys_type varchar(4) not null,
	constraint channel_eb_pk
		primary key (channelid),
	constraint server_eb_fk
		foreign key (serverid) references server (id)
);

create table post (
	serverid varchar(20) not null,
	groupid varchar(15) not null, 
	name varchar(150) not null,
	groupdate varchar(30) not null,
	players varchar(120),
	subs varchar(80),
	sys_type varchar(4) not null,
	msg_id varchar(20) not null,
    owner_id varchar(20) not null,
    type_code varchar(6),
    rollcall_count varchar(2),
    timezone varchar(4),
	constraint server_post_fk
		foreign key (serverid) references server (id)
);

create table autochannel (
	serverid varchar(20) not null,
	channelid varchar(20) not null,
	channelname varchar(40),
	constraint ac_pk
		primary key (channelid),
	constraint server_ac_fk
		foreign key (serverid) references server (id)
);

create table autorole (
	serverid varchar(20) not null,
	rolename varchar(30) not null,
	messageid varchar(20),
	roleid varchar(20) not null,
	constraint ar_pk
		primary key (rolename),
	constraint server_ar_fk
		foreign key (serverid) references server (id)
);

create table link (
    discordid varchar(20) not null,
    destinyid varchar(20) not null,
    platform int not null,
    constraint links_pk
		primary key(discordid)
)


