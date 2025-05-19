CREATE TABLE users (
    id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    dob DATE
);

CREATE TABLE artists (
    id VARCHAR(50) PRIMARY KEY,
    userId VARCHAR(50) NOT NULL,
    bio VARCHAR(1000),
    followerCount INT DEFAULT 0,
    CONSTRAINT fk_artists_user FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE genres (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE languages (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE albums (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    releaseYear INT,
    artistId VARCHAR(50) NOT NULL,
    CONSTRAINT fk_albums_artist FOREIGN KEY (artistId) REFERENCES artists(id)
);

CREATE TABLE songs (
    id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration INT,
    releaseDate DATE,
    audioFile VARBINARY(MAX),
    coverImage VARBINARY(MAX),
    artistId VARCHAR(50) NOT NULL,
    albumId VARCHAR(50) NULL,
    genreId VARCHAR(50) NULL,
    languageId VARCHAR(50) NULL,
    CONSTRAINT fk_songs_artist FOREIGN KEY (artistId) REFERENCES artists(id),
    CONSTRAINT fk_songs_album FOREIGN KEY (albumId) REFERENCES albums(id) ON DELETE SET NULL,
    CONSTRAINT fk_songs_genre FOREIGN KEY (genreId) REFERENCES genres(id) ON DELETE SET NULL,
    CONSTRAINT fk_songs_language FOREIGN KEY (languageId) REFERENCES languages(id) ON DELETE SET NULL
);

CREATE TABLE playlists (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    createdOn DATE,
    userId VARCHAR(50) NOT NULL,
    CONSTRAINT fk_playlists_user FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE playlist_songs (
    playlistId VARCHAR(50) NOT NULL,
    songId VARCHAR(50) NOT NULL,
    addedAt DATE,
    PRIMARY KEY (playlistId, songId),
    CONSTRAINT fk_playlist_songs_playlist FOREIGN KEY (playlistId) REFERENCES playlists(id),
    CONSTRAINT fk_playlist_songs_song FOREIGN KEY (songId) REFERENCES songs(id)
);

CREATE TABLE likes (
    userId VARCHAR(50) NOT NULL,
    songId VARCHAR(50) NOT NULL,
    likedOn DATE,
    PRIMARY KEY (userId, songId),
    CONSTRAINT fk_likes_user FOREIGN KEY (userId) REFERENCES users(id),
    CONSTRAINT fk_likes_song FOREIGN KEY (songId) REFERENCES songs(id)
);

CREATE TABLE recently_played (
    id VARCHAR(50) PRIMARY KEY,
    userId VARCHAR(50) NOT NULL,
    songId VARCHAR(50) NOT NULL,
    playedOn DATETIME,
    device VARCHAR(255),
    CONSTRAINT fk_recently_played_user FOREIGN KEY (userId) REFERENCES users(id),
    CONSTRAINT fk_recently_played_song FOREIGN KEY (songId) REFERENCES songs(id)
);

CREATE TABLE follow_artist (
    userId VARCHAR(50) NOT NULL,
    artistId VARCHAR(50) NOT NULL,
    followedOn DATE,
    PRIMARY KEY (userId, artistId),
    CONSTRAINT fk_follow_user FOREIGN KEY (userId) REFERENCES users(id),
    CONSTRAINT fk_follow_artist FOREIGN KEY (artistId) REFERENCES artists(id)
);

CREATE TABLE user_devices (
    id VARCHAR(50) PRIMARY KEY,
    userId VARCHAR(50) NOT NULL,
    deviceName VARCHAR(255),
    lastUsedOn DATETIME,
    CONSTRAINT fk_user_devices_user FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE listening_stats (
    userId VARCHAR(50) NOT NULL,
    songId VARCHAR(50) NOT NULL,
    totalPlays INT DEFAULT 0,
    totalDuration INT DEFAULT 0,
    PRIMARY KEY (userId, songId),
    CONSTRAINT fk_listening_stats_user FOREIGN KEY (userId) REFERENCES users(id),
    CONSTRAINT fk_listening_stats_song FOREIGN KEY (songId) REFERENCES songs(id)
);

CREATE TABLE search_history (
    id VARCHAR(50) PRIMARY KEY,
    userId VARCHAR(50) NOT NULL,
    searchTerm VARCHAR(255),
    searchedOn DATETIME,
    CONSTRAINT fk_search_history_user FOREIGN KEY (userId) REFERENCES users(id)
);
