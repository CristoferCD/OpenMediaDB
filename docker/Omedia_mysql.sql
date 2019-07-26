create or replace schema omedia collate latin1_swedish_ci;

use omedia;

create or replace table ExternalIds
(
	id int auto_increment
		primary key,
	imdbId varchar(15) null,
	tmdbId int null,
	traktId int null,
	tvdbId int null
);

create or replace table FileInfo
(
	id int auto_increment
		primary key,
	path text not null,
	duration int null,
	resolution varchar(20) not null,
	bitrate varchar(20) not null,
	codec varchar(20) not null
);

create or replace table `Show`
(
	showId varchar(15) not null
		primary key,
	name varchar(255) not null,
	sinopsis text not null,
	totalSeasons int default 0 not null,
	totalEpisodes int default 0 not null,
	imgPoster text null,
	imgBackground text null,
	path text not null,
	externalIds int not null,
	constraint fk_Show_externalIds_id
		foreign key (externalIds) references ExternalIds (id)
			on update cascade on delete cascade
);

create or replace table User
(
	id int auto_increment
		primary key,
	name varchar(255) not null,
	password varchar(128) not null,
	constraint User_name_unique
		unique (name)
);

create or replace table Following
(
	id int auto_increment
		primary key,
	userId int not null,
	showId varchar(15) not null,
	following tinyint(1) default 0 not null,
	constraint fk_Following_showId_showId
		foreign key (showId) references `Show` (showId),
	constraint fk_Following_userId_id
		foreign key (userId) references User (id)
);

create or replace table NotificationQueue
(
	id int auto_increment
		primary key,
	userId int not null,
	content text not null,
	`read` tinyint(1) default 0 not null,
	constraint fk_NotificationQueue_userId_id
		foreign key (userId) references User (id)
);

create or replace table Video
(
	id int auto_increment
		primary key,
	fileId int null,
	showId varchar(15) not null,
	imdbId varchar(15) null,
	name varchar(255) not null,
	season int not null,
	airDate date default '1970-01-01' not null,
	episodeNumber int not null,
	sinopsis text not null,
	imgPoster text null,
	externalIds int not null,
	constraint Video_imdbId_unique
		unique (imdbId),
	constraint fk_Video_externalIds_id
		foreign key (externalIds) references ExternalIds (id)
			on update cascade on delete cascade,
	constraint fk_Video_fileId_id
		foreign key (fileId) references FileInfo (id),
	constraint fk_Video_showId_showId
		foreign key (showId) references `Show` (showId)
);

create or replace table Seen
(
	id int auto_increment
		primary key,
	userId int not null,
	videoId int not null,
	seen tinyint(1) default 0 not null,
	constraint fk_Seen_userId_id
		foreign key (userId) references User (id),
	constraint fk_Seen_videoId_id
		foreign key (videoId) references Video (id)
);

create or replace table VideoTokens
(
	id int auto_increment
		primary key,
	fileId int not null,
	token varchar(128) not null,
	expires date not null,
	constraint VideoTokens_token_unique
		unique (token),
	constraint fk_VideoTokens_fileId_id
		foreign key (fileId) references FileInfo (id)
);