# 🎵 Spotify Music Streaming Platform – DBMS Project

## 📘 Project Overview
This project is a simulation of a music streaming service similar to **Spotify**, built as part of a **Database Management Systems (DBMS)** course. It involves designing and implementing a **relational database** that supports a wide range of features such as user activity tracking, artist management, playlist creation, and analytics. A **JavaFX frontend** is integrated for demonstration.

---

## 🏗️ Features & Entities

- **Users**: Register, log in, play songs, create playlists, follow artists, like songs.
- **Artists**: Special users who upload songs, release albums.
- **Songs**: Metadata including genre, language, artist, and album.
- **Playlists**: User-created collections of songs.
- **Albums**: Artist-curated groups of songs.
- **Genres & Languages**: Classification tags for songs.
- **Listening Stats**: Track how many times each song is played by a user.
- **Recently Played**: Record of user's most recent streams.
- **Likes**: Tracks which songs a user has liked.
- **Follow Artist**: Maps user to favorite artists.
- **Search History**: Logs user searches with timestamps.
- **User Devices**: Logs which device the user accessed the app from.

---

## 🛠️ Technologies Used

| Component            | Tool                                      |
|----------------------|-------------------------------------------|
| **Backend RDBMS**    | Microsoft SQL Server Express              |
| **DB Tool**          | SQL Server Management Studio (SSMS)       |
| **Frontend**         | JavaFX (JDK 24 + OpenJFX 24.0.1)          |
| **IDE**              | IntelliJ IDEA                             |
| **Database Driver**  | JDBC (Java Database Connectivity)         |

---


---

## 🚀 How to Run

### 1. Database Setup
- Open **SSMS**
- Run scripts from `sql/` folder:
  1. `create_tables.sql`
  2. `insert_sample_data.sql`
  3. `stored_procedures.sql`
  4. `triggers.sql`

### 2. JavaFX Frontend
- Open the project in **IntelliJ IDEA**
- Ensure JDBC Driver is correctly added to project libraries
- Update `DBConnection.java` with your `SQL Server` credentials
- Run `Main.java`

---

## 💡 Sample Queries

```sql
-- Most liked songs
SELECT TOP 10 title FROM Songs ORDER BY likes_count DESC;

-- User’s recent streams
SELECT * FROM RecentlyPlayed WHERE user_id = 1 ORDER BY played_at DESC;

-- Total listening time per user
SELECT SUM(total_duration) FROM ListeningStats WHERE user_id = 1;

-- Songs filtered by genre or language
SELECT * FROM Songs WHERE genre_id = 2 OR language_id = 3;
