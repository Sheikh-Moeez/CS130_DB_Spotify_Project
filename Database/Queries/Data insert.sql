
-- Insert User
INSERT INTO users (username, email, password, dob)
VALUES ('Moeez', 'moeez@example.com', 'password123', '2000-01-01');
GO

-- Assume the user's ID is 1 for simplicity
-- Insert Artists
INSERT INTO artists (userId, bio, followerCount)
VALUES (1, 'Bayaan is a Pakistani alternative rock band.', 0),
       (1, 'Mannu is a rising indie artist.', 0);
GO

-- Insert Genres
INSERT INTO genres (name, description)
VALUES ('Rock', 'Rock music genre'),
       ('Indie', 'Indie music genre');
GO

-- Insert Languages
INSERT INTO languages (name)
VALUES ('Urdu'),
       ('Punjabi');
GO

-- Insert Albums
INSERT INTO albums (name, releaseYear, artistId)
VALUES ('Bayaan Album', 2023, 1),
       ('Mannu Debut', 2024, 2);
GO

-- Insert Songs
INSERT INTO songs (title, duration, releaseDate, audioFile, coverImage, artistId, albumId, genreId, languageId)
VALUES 
('Tere Naal', 210, '2024-01-10', 'Assets/Songs/Tere_Naal.mp3', 'Assets/Songs_Cover/Bayaan.png', 1, 1, 1, 1),

('Jhol', 195, '2023-12-25', 'Assets/Songs/Jhol.mp3', 'Assets/Songs_Cover/Jhol.png', 2, 2, 2, 2);

