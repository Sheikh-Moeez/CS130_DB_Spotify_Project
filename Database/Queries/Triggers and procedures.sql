
-- PROCEDURE: Add Artist By Username
CREATE OR ALTER PROCEDURE AddArtistByUsername
    @Username VARCHAR(255),
    @Bio VARCHAR(1000)
AS
BEGIN
    DECLARE @UserId INT;
    SELECT @UserId = id FROM users WHERE username = @Username;
    IF @UserId IS NOT NULL
    BEGIN
        INSERT INTO artists (userId, bio, followerCount)
        VALUES (@UserId, @Bio, 0);
    END
    ELSE
    BEGIN
        PRINT 'Username not found.';
    END
END;
GO

-- TRIGGER: Initialize followerCount
CREATE OR ALTER TRIGGER trg_InitFollowerCount
ON artists
AFTER INSERT
AS
BEGIN
    UPDATE artists
    SET followerCount = 0
    WHERE followerCount IS NULL;
END;
GO

-- PROCEDURE: Add Song to Playlist
CREATE OR ALTER PROCEDURE AddSongToPlaylist
    @PlaylistId INT,
    @SongId INT
AS
BEGIN
    INSERT INTO playlist_songs (playlistId, songId, addedAt)
    VALUES (@PlaylistId, @SongId, GETDATE());
END;
GO

-- TRIGGER: Default device in recently_played
CREATE OR ALTER TRIGGER trg_DefaultDevice
ON recently_played
AFTER INSERT
AS
BEGIN
    UPDATE recently_played
    SET device = ISNULL(device, 'Unknown Device')
    WHERE device IS NULL;
END;
GO

-- PROCEDURE: Like a song
CREATE OR ALTER PROCEDURE LikeSong
    @UserId INT,
    @SongId INT
AS
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM likes
        WHERE userId = @UserId AND songId = @SongId
    )
    BEGIN
        INSERT INTO likes (userId, songId, likedOn)
        VALUES (@UserId, @SongId, GETDATE());
    END
    ELSE
    BEGIN
        PRINT 'Song already liked.';
    END
END;
GO

-- TRIGGER: Initialize listening stats
CREATE OR ALTER TRIGGER trg_InitListeningStats
ON listening_stats
AFTER INSERT
AS
BEGIN
    UPDATE listening_stats
    SET totalPlays = ISNULL(totalPlays, 0),
        totalDuration = ISNULL(totalDuration, 0)
    WHERE totalPlays IS NULL OR totalDuration IS NULL;
END;
GO
