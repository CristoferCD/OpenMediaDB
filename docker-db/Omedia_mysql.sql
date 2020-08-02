create or replace schema omedia collate 'utf8mb4_general_ci';

use omedia;

create or replace table ExternalIds
(
	id int auto_increment
		primary key,
	imdbId varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci null,
	tmdbId int null,
	traktId int null,
	tvdbId int null
) character set 'utf8mb4' collate 'utf8mb4_general_ci';

create or replace table FileInfo
(
	id int auto_increment
		primary key,
	path text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	duration int null,
	resolution varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	bitrate varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	codec varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null
) character set 'utf8mb4' collate 'utf8mb4_general_ci';

create or replace table `Show`
(
	showId varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null
		primary key,
	name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	sinopsis text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	totalSeasons int default 0 not null,
	totalEpisodes int default 0 not null,
	imgPoster text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci null,
	imgBackground text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci null,
	path text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	externalIds int not null,
	constraint fk_Show_externalIds_id
		foreign key (externalIds) references ExternalIds (id)
			on update cascade on delete cascade
) character set 'utf8mb4' collate 'utf8mb4_general_ci';

create or replace table User
(
	id int auto_increment
		primary key,
	name varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	password varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	constraint User_name_unique
		unique (name)
) character set 'utf8mb4' collate 'utf8mb4_general_ci';

create or replace table Following
(
	id int auto_increment
		primary key,
	userId int not null,
	showId varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	following tinyint(1) default 0 not null,
	constraint fk_Following_showId_showId
		foreign key (showId) references `Show` (showId),
	constraint fk_Following_userId_id
		foreign key (userId) references User (id)
) character set 'utf8mb4' collate 'utf8mb4_general_ci';

create or replace table NotificationQueue
(
	id int auto_increment
		primary key,
	userId int not null,
	content text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	`read` tinyint(1) default 0 not null,
	constraint fk_NotificationQueue_userId_id
		foreign key (userId) references User (id)
) character set 'utf8mb4' collate 'utf8mb4_general_ci';

create or replace table Video
(
	id int auto_increment
		primary key,
	fileId int null,
	showId varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	imdbId varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci null,
	name varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	season int not null,
	airDate date default '1970-01-01' not null,
	episodeNumber int not null,
	sinopsis text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	imgPoster text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci null,
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
) character set 'utf8mb4' collate 'utf8mb4_general_ci';

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
) character set 'utf8mb4' collate 'utf8mb4_general_ci';

create or replace table VideoTokens
(
	id int auto_increment
		primary key,
	fileId int not null,
	token varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci not null,
	expires date not null,
	constraint VideoTokens_token_unique
		unique (token),
	constraint fk_VideoTokens_fileId_id
		foreign key (fileId) references FileInfo (id)
) character set 'utf8mb4' collate 'utf8mb4_general_ci';