CREATE TABLE users (
    id INT IDENTITY PRIMARY KEY,
    username VARCHAR(100),
    email VARCHAR(100),
    password VARCHAR(100),
    dob DATE
);

CREATE TABLE artists (
    id INT IDENTITY PRIMARY KEY,
    userId INT,
    bio VARCHAR(500),
    followerCount INT,
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE genres (
    id INT IDENTITY PRIMARY KEY,
    name VARCHAR(50),
    description VARCHAR(255)
);

CREATE TABLE languages (
    id INT IDENTITY PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE albums (
    id INT IDENTITY PRIMARY KEY,
    name VARCHAR(100),
    releaseYear INT,
    artistId INT,
    FOREIGN KEY (artistId) REFERENCES artists(id)
);

CREATE TABLE songs (
    id INT IDENTITY PRIMARY KEY,
    title VARCHAR(100),
    duration INT,
    releaseDate DATE,
    audioFile VARBINARY(MAX),
    coverImage VARBINARY(MAX),
    artistId INT,
    albumId INT,
    genreId INT,
    languageId INT,
    FOREIGN KEY (artistId) REFERENCES artists(id),
    FOREIGN KEY (albumId) REFERENCES albums(id),
    FOREIGN KEY (genreId) REFERENCES genres(id),
    FOREIGN KEY (languageId) REFERENCES languages(id)
);

CREATE TABLE playlists (
    id INT IDENTITY PRIMARY KEY,
    name VARCHAR(100),
    createdOn DATE,
    userId INT,
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE playlist_songs (
    playlistId INT,
    songId INT,
    addedAt DATE,
    PRIMARY KEY (playlistId, songId),
    FOREIGN KEY (playlistId) REFERENCES playlists(id),
    FOREIGN KEY (songId) REFERENCES songs(id)
);

CREATE TABLE likes (
    userId INT,
    songId INT,
    likedOn DATE,
    PRIMARY KEY (userId, songId),
    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (songId) REFERENCES songs(id)
);

CREATE TABLE recently_played (
    id INT IDENTITY PRIMARY KEY,
    userId INT,
    songId INT,
    playedOn DATETIME,
    device VARCHAR(100),
    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (songId) REFERENCES songs(id)
);

CREATE TABLE follow_artist (
    userId INT,
    artistId INT,
    followedOn DATE,
    PRIMARY KEY (userId, artistId),
    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (artistId) REFERENCES artists(id)
);

CREATE TABLE user_devices (
    id INT IDENTITY PRIMARY KEY,
    userId INT,
    deviceName VARCHAR(100),
    lastUsedOn DATETIME,
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE listening_stats (
    userId INT,
    songId INT,
    totalPlays INT,
    totalDuration INT,
    PRIMARY KEY (userId, songId),
    FOREIGN KEY (userId) REFERENCES users(id),
    FOREIGN KEY (songId) REFERENCES songs(id)
);

CREATE TABLE search_history (
    id INT IDENTITY PRIMARY KEY,
    userId INT,
    searchTerm VARCHAR(255),
    searchedOn DATETIME,
    FOREIGN KEY (userId) REFERENCES users(id)
);
