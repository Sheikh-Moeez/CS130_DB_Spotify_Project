import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

// Song data class to hold song information
class Song {
    private String id;
    private String title;
    private int duration;
    private String releaseDate;
    private String audioFile;
    private String coverImage;
    private String artistId;
    private String albumId;
    private String genreId;
    private String languageId;

    public Song(String id, String title, int duration, String releaseDate,
                String audioFile, String coverImage, String artistId,
                String albumId, String genreId, String languageId) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.audioFile = audioFile;
        this.coverImage = coverImage;
        this.artistId = artistId;
        this.albumId = albumId;
        this.genreId = genreId;
        this.languageId = languageId;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public int getDuration() { return duration; }
    public String getReleaseDate() { return releaseDate; }
    public String getAudioFile() { return audioFile; }
    public String getCoverImage() { return coverImage; }
    public String getArtistId() { return artistId; }
    public String getAlbumId() { return albumId; }
    public String getGenreId() { return genreId; }
    public String getLanguageId() { return languageId; }

    // Helper method to format duration from seconds to mm:ss
    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}

public class Homepage
{
    private VBox settingsMenu; // globally accessible to toggle or add items
    private StackPane root;    // root for layered layout
    private Stage stage;
    private String username;
    private String email;
    private String userType; // "USER", "ARTIST"
    private String userId;    // Store the user ID for database operations

    // Database connection method
    public Connection connectToDB() {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=Spotify;encrypt=true;trustServerCertificate=true";
        String user = "Admiral";  // Replace with your DB username
        String password = "295336";  // Replace with your DB password

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Homepage(Stage stage, String username, String email) {
        this.stage = stage;
        this.username = username;
        this.email = email;

        // Determine user type and get user ID
        determineUserType();

        // Debug prints
        System.out.println("Homepage created for: " + username + " (" + email + ")");
        System.out.println("User ID: " + userId);
        System.out.println("User Type: " + userType);
    }

    private void determineUserType() {
        Connection conn = connectToDB();
        if (conn == null) {
            System.out.println("Database connection failed, defaulting to USER");
            userType = "USER"; // Default to USER if database connection fails
            return;
        }

        try {
            // First, get the user ID from the Users table
            String getUserSql = "SELECT id FROM Users WHERE email = ?";
            PreparedStatement getUserStmt = conn.prepareStatement(getUserSql);
            getUserStmt.setString(1, email);
            ResultSet userResult = getUserStmt.executeQuery();

            if (userResult.next()) {
                userId = userResult.getString("id");
                System.out.println("Found user with ID: " + userId);

                // Check if user is an artist
                String artistSql = "SELECT COUNT(*) as count FROM Artists WHERE userId = ?";
                PreparedStatement artistStmt = conn.prepareStatement(artistSql);
                artistStmt.setString(1, userId);
                ResultSet artistResult = artistStmt.executeQuery();

                if (artistResult.next() && artistResult.getInt("count") > 0) {
                    userType = "ARTIST";
                    System.out.println("User is an ARTIST");
                } else {
                    userType = "USER";
                    System.out.println("User is a regular USER");
                }

                artistResult.close();
                artistStmt.close();
            } else {
                System.out.println("User not found in database for email: " + email);
                userType = "USER"; // Default if user not found
                userId = null; // Make sure userId is null if user not found
            }

            userResult.close();
            getUserStmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            userType = "USER"; // Default to USER if query fails
            System.out.println("SQL Error in determineUserType: " + e.getMessage());
        }
    }

    // Method to fetch songs from database
    private List<Song> fetchSongsFromDatabase() {
        List<Song> songs = new ArrayList<>();
        Connection conn = connectToDB();

        if (conn == null) {
            System.out.println("Database connection failed, no songs to display");
            return songs;
        }

        try {
            String songSql = "SELECT id, title, duration, releaseDate, audioFile, coverImage, " +
                    "artistId, albumId, genreId, languageId FROM Songs ORDER BY releaseDate DESC";
            PreparedStatement stmt = conn.prepareStatement(songSql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Song song = new Song(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getInt("duration"),
                        rs.getString("releaseDate"),
                        rs.getString("audioFile"),
                        rs.getString("coverImage"),
                        rs.getString("artistId"),
                        rs.getString("albumId"),
                        rs.getString("genreId"),
                        rs.getString("languageId")
                );
                songs.add(song);
            }

            rs.close();
            stmt.close();
            conn.close();

            System.out.println("Fetched " + songs.size() + " songs from database");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching songs: " + e.getMessage());
        }

        return songs;
    }

    // Method to get artist name by artist ID
    private String getArtistNameById(String artistId) {
        Connection conn = connectToDB();
        if (conn == null) return "Unknown Artist";

        try {
            String sql = "SELECT u.username FROM Artists a JOIN Users u ON a.userId = u.id WHERE a.id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, artistId);
            ResultSet rs = stmt.executeQuery();

            String artistName = "Unknown Artist";
            if (rs.next()) {
                artistName = rs.getString("username");
            }

            rs.close();
            stmt.close();
            conn.close();
            return artistName;

        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown Artist";
        }
    }

    // Method to create a song row UI component (list style)
    private HBox createSongRow(Song song, int index) {
        HBox songRow = new HBox(15);
        songRow.setStyle("-fx-background-color: transparent; -fx-padding: 8 20 8 20;");
        songRow.setAlignment(Pos.CENTER_LEFT);
        songRow.setPrefHeight(64);
        songRow.setCursor(javafx.scene.Cursor.HAND);

        // Add hover effect
        songRow.setOnMouseEntered(e ->
                songRow.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 8 20 8 20;"));
        songRow.setOnMouseExited(e ->
                songRow.setStyle("-fx-background-color: transparent; -fx-padding: 8 20 8 20;"));

        // Index/Number
        Label indexLabel = new Label(String.valueOf(index));
        indexLabel.setTextFill(Color.web("#b3b3b3"));
        indexLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));
        indexLabel.setPrefWidth(30);
        indexLabel.setAlignment(Pos.CENTER);

        // Cover image (smaller for list view)
        ImageView coverImageView = createCoverImage(song.getCoverImage(), 48, 48);
        coverImageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 5, 0, 0, 0);");

        // Song info container (title and artist)
        VBox songInfo = new VBox(2);
        songInfo.setAlignment(Pos.CENTER_LEFT);

        // Song title
        Label titleLabel = new Label(song.getTitle());
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));

        // Artist name
        String artistName = getArtistNameById(song.getArtistId());
        Label artistLabel = new Label(artistName);
        artistLabel.setTextFill(Color.web("#b3b3b3"));
        artistLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

        songInfo.getChildren().addAll(titleLabel, artistLabel);

        // Song title (center column)
        Label songTitleCenter = new Label(song.getTitle());
        songTitleCenter.setTextFill(Color.WHITE);
        songTitleCenter.setFont(Font.font("System", FontWeight.NORMAL, 16));

        // Release date
        Label releaseDateLabel = new Label(formatReleaseDate(song.getReleaseDate()));
        releaseDateLabel.setTextFill(Color.web("#b3b3b3"));
        releaseDateLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

        // Duration
        Label durationLabel = new Label(song.getFormattedDuration());
        durationLabel.setTextFill(Color.web("#b3b3b3"));
        durationLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));
        durationLabel.setPrefWidth(60);
        durationLabel.setAlignment(Pos.CENTER_RIGHT);

        // Spacers for proper column alignment
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        // Add click handler for song playback
        songRow.setOnMouseClicked(e -> playSong(song));

        songRow.getChildren().addAll(
                indexLabel,
                coverImageView,
                songInfo,
                spacer1,
                songTitleCenter,
                spacer2,
                releaseDateLabel,
                durationLabel
        );

        return songRow;
    }

    // Helper method to format release date
    private String formatReleaseDate(String releaseDate) {
        if (releaseDate == null || releaseDate.trim().isEmpty()) {
            return "Unknown";
        }
        try {
            // Assuming date format is YYYY-MM-DD, extract month and year
            String[] parts = releaseDate.split("-");
            if (parts.length >= 2) {
                String year = parts[0];
                String month = parts[1];

                String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                int monthInt = Integer.parseInt(month);
                if (monthInt >= 1 && monthInt <= 12) {
                    return monthNames[monthInt] + " " + parts[2] + ", " + year;
                }
            }
            return releaseDate;
        } catch (Exception e) {
            return releaseDate;
        }
    }

    // Method to create cover image with error handling
    private ImageView createCoverImage(String imagePath, int width, int height) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                // Try to load the image from file path
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    FileInputStream fis = new FileInputStream(imageFile);
                    Image image = new Image(fis);
                    imageView.setImage(image);
                    fis.close();
                } else {
                    // If file doesn't exist, try loading from resources
                    try {
                        Image image = new Image(getClass().getResourceAsStream(imagePath));
                        imageView.setImage(image);
                    } catch (Exception ex) {
                        // Use default cover if image not found
                        setDefaultCoverImage(imageView);
                    }
                }
            } catch (Exception e) {
                System.out.println("Could not load cover image: " + imagePath + " - " + e.getMessage());
                setDefaultCoverImage(imageView);
            }
        } else {
            setDefaultCoverImage(imageView);
        }

        return imageView;
    }

    // Set a default cover image or create a placeholder
    private void setDefaultCoverImage(ImageView imageView) {
        // Create a simple colored rectangle as placeholder
        imageView.setStyle("-fx-background-color: #404040;");
        // You could also try to load a default image from resources here
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/Icons/default_cover.png"));
            imageView.setImage(defaultImage);
        } catch (Exception e) {
            // If no default image available, the styled background will show
            System.out.println("No default cover image available");
        }
    }

    // Method to handle song playback (placeholder for now)
    private void playSong(Song song) {
        System.out.println("Playing song: " + song.getTitle() + " (" + song.getAudioFile() + ")");
        // TODO: Implement actual audio playback functionality
        // You could use JavaFX MediaPlayer here to play the audio file
    }

    public void showHomepage() {
        System.out.println("Showing homepage for user: " + username + " (Type: " + userType + ")");

        BorderPane mainLayout = new BorderPane();
        root = new StackPane(mainLayout); // to allow dropdown over the layout

        // === Top Bar ===
        HBox topBar = new HBox();
        topBar.setStyle("-fx-background-color: #121212;");
        topBar.setPadding(new Insets(10));
        topBar.setSpacing(10);
        topBar.setAlignment(Pos.CENTER);

        // Use a placeholder image if the icon file is not found
        ImageView homeIcon = createImageView("/Icons/home_icon.png", 20, 20);

        TextField searchField = new TextField();
        searchField.setPromptText("What do you want to play?");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-radius: 20; -fx-background-color: #2a2a2a; -fx-text-fill: white; -fx-prompt-text-fill: #bbbbbb;");

        ImageView settingsicon = createImageView("/Icons/settings.png", 20, 20);

        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        topBar.getChildren().addAll(homeIcon, spacerLeft, searchField, spacerRight, settingsicon);

        // === Left Sidebar ===
        VBox librarySidebar = new VBox(10);
        librarySidebar.setPrefWidth(200);
        librarySidebar.setPadding(new Insets(15));
        librarySidebar.setStyle("-fx-background-color: #181818;");
        BorderPane.setMargin(librarySidebar, new Insets(5, 0, 0, 0));

        Label libraryLabel = new Label("Your Library");
        libraryLabel.setTextFill(Color.WHITE);

        ImageView addPlaylistIcon = createImageView("Icons/plus_icon.png", 20, 20);

        HBox libraryTitle = new HBox(5);
        libraryTitle.setAlignment(Pos.CENTER_LEFT);
        libraryTitle.getChildren().addAll(libraryLabel, addPlaylistIcon);

        librarySidebar.getChildren().add(libraryTitle);

        // === Center Content ===
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(20));
        centerContent.setStyle("-fx-background-color: #121212;");
        BorderPane.setMargin(centerContent, new Insets(5, 0, 0, 7));

        // Welcome message with user's name and type
        Label welcomeLabel = new Label("Welcome , " + username);
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setFont(Font.font("System", FontWeight.BOLD, 28));

        // Songs section
        Label songsLabel = new Label("Recently Added Songs");
        songsLabel.setTextFill(Color.WHITE);
        songsLabel.setFont(Font.font("System", FontWeight.BOLD, 22));

        // Fetch songs from database
        List<Song> songs = fetchSongsFromDatabase();

        // Create songs table header
        HBox tableHeader = new HBox(15);
        tableHeader.setStyle("-fx-background-color: transparent; -fx-padding: 8 20 8 20; -fx-border-color: #333; -fx-border-width: 0 0 1 0;");
        tableHeader.setAlignment(Pos.CENTER_LEFT);
        tableHeader.setPrefHeight(40);

        Label headerIndex = new Label("#");
        headerIndex.setTextFill(Color.web("#b3b3b3"));
        headerIndex.setFont(Font.font("System", FontWeight.BOLD, 14));
        headerIndex.setPrefWidth(30);
        headerIndex.setAlignment(Pos.CENTER);

        Label headerTitle = new Label("Title");
        headerTitle.setTextFill(Color.web("#b3b3b3"));
        headerTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        headerTitle.setPrefWidth(300);

        Label headerAlbum = new Label("Album");
        headerAlbum.setTextFill(Color.web("#b3b3b3"));
        headerAlbum.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label headerDate = new Label("Date added");
        headerDate.setTextFill(Color.web("#b3b3b3"));
        headerDate.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label headerDuration = new Label("⏱");
        headerDuration.setTextFill(Color.web("#b3b3b3"));
        headerDuration.setFont(Font.font("System", FontWeight.BOLD, 14));
        headerDuration.setPrefWidth(60);
        headerDuration.setAlignment(Pos.CENTER_RIGHT);

        Region headerSpacer1 = new Region();
        HBox.setHgrow(headerSpacer1, Priority.ALWAYS);

        Region headerSpacer2 = new Region();
        HBox.setHgrow(headerSpacer2, Priority.ALWAYS);

        tableHeader.getChildren().addAll(
                headerIndex,
                new Label("   "), // Space for cover image
                headerTitle,
                headerSpacer1,
                headerAlbum,
                headerSpacer2,
                headerDate,
                headerDuration
        );

        // Create songs list
        VBox songsList = new VBox();
        songsList.setStyle("-fx-background-color: transparent;");

        if (songs.isEmpty()) {
            Label noSongsLabel = new Label("No songs available");
            noSongsLabel.setTextFill(Color.web("#b3b3b3"));
            noSongsLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));
            noSongsLabel.setStyle("-fx-padding: 20;");
            songsList.getChildren().add(noSongsLabel);
        } else {
            // Display all songs in list format
            for (int i = 0; i < songs.size(); i++) {
                HBox songRow = createSongRow(songs.get(i), i + 1);
                songsList.getChildren().add(songRow);
            }
        }

        ScrollPane songsScroll = new ScrollPane(songsList);
        songsScroll.setFitToWidth(true);
        songsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        songsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        songsScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Container for table header and songs list
        VBox songsTable = new VBox();
        songsTable.getChildren().addAll(tableHeader, songsScroll);

        centerContent.getChildren().addAll(welcomeLabel, songsLabel, songsTable);

        // === Bottom Bar ===
        HBox bottomBar = new HBox();
        bottomBar.setStyle("-fx-background-color: #181818;");
        bottomBar.setPadding(new Insets(15));
        bottomBar.setAlignment(Pos.CENTER);

        Label nowPlaying = new Label("♪ Ready to play your music");
        nowPlaying.setTextFill(Color.web("#b3b3b3"));
        nowPlaying.setFont(Font.font("System", FontWeight.NORMAL, 14));
        bottomBar.getChildren().add(nowPlaying);

        // === Layout Placement ===
        mainLayout.setTop(topBar);
        mainLayout.setLeft(librarySidebar);
        mainLayout.setCenter(centerContent);
        mainLayout.setBottom(bottomBar);

        // === Create and add settings menu ===
        createSettingsMenu();
        root.getChildren().add(settingsMenu);

        // === Toggle on icon click using lambda ===
        settingsicon.setOnMouseClicked(e -> toggleSettingsMenu());

        Scene scene = new Scene(root, 1200, 700);
        stage.setScene(scene);

        // Handle missing CSS file gracefully
        try {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Could not load style.css: " + e.getMessage());
        }

        // Handle missing icon gracefully
        try {
            stage.getIcons().add(new Image("Icons/logo.png"));
        } catch (Exception e) {
            System.out.println("Could not load logo icon: " + e.getMessage());
        }

        stage.setTitle("Spotify-Like App - " + username);
        stage.show(); // Make sure to show the stage

        System.out.println("Homepage displayed successfully");
    }

    // Helper method to create ImageView with error handling
    private ImageView createImageView(String imagePath, int width, int height) {
        try {
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            return imageView;
        } catch (Exception e) {
            System.out.println("Could not load image: " + imagePath + " - " + e.getMessage());
            // Create a placeholder rectangle instead
            ImageView placeholder = new ImageView();
            placeholder.setFitWidth(width);
            placeholder.setFitHeight(height);
            return placeholder;
        }
    }

    private void createSettingsMenu() {
        settingsMenu = new VBox(5);
        settingsMenu.setStyle("-fx-background-color: #1c1b1b; -fx-border-color: #444; -fx-border-width: 1;");
        settingsMenu.setPadding(new Insets(10));
        settingsMenu.setMaxWidth(150);
        settingsMenu.setVisible(false);
        StackPane.setAlignment(settingsMenu, Pos.TOP_RIGHT);
        settingsMenu.setTranslateY(50);  // Position dropdown below top bar

        // Create menu items based on user type
        System.out.println("Creating settings menu for user type: " + userType);
        switch (userType) {
            case "ARTIST":
                createArtistMenu();
                break;

            case "USER":
            default:
                createUserMenu();
                break;
        }

        // Always add logout option
        Label logoutLabel = new Label("Logout ↗");
        logoutLabel.setStyle("-fx-text-fill: #ea2121; -fx-cursor: hand;");
        logoutLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Add logout functionality
        logoutLabel.setOnMouseClicked(e -> {
            System.out.println("Logout clicked");
            // Return to login screen
            Login login = new Login(stage);
            VBox loginView = login.createLoginView();

            StackPane loginRoot = new StackPane(loginView);
            loginRoot.setPrefSize(800, 600);
            loginRoot.setPadding(new Insets(20));

            Scene loginScene = new Scene(loginRoot);
            try {
                loginScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            } catch (Exception ex) {
                System.out.println("Could not load style.css for login: " + ex.getMessage());
            }

            stage.setScene(loginScene);
            stage.setTitle("Spotify Login UI");
        });

        settingsMenu.getChildren().add(logoutLabel);
    }

    private void createArtistMenu() {
        System.out.println("Creating artist menu");
        Label addSongLabel = new Label("Add Song 🎵");
        addSongLabel.setStyle("-fx-text-fill: #1db954; -fx-cursor: hand;");
        addSongLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

        addSongLabel.setOnMouseClicked(e -> {
            // TODO: Implement add song functionality
            System.out.println("Add Song clicked - Artist: " + username);
            // You can open a new window/scene for adding songs here
            // Example: openAddSongWindow();
        });

        Label manageAlbumsLabel = new Label("Manage Albums 💿");
        manageAlbumsLabel.setStyle("-fx-text-fill: #1db954; -fx-cursor: hand;");
        manageAlbumsLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

        manageAlbumsLabel.setOnMouseClicked(e -> {
            // TODO: Implement manage albums functionality
            System.out.println("Manage Albums clicked - Artist: " + username);
        });

        settingsMenu.getChildren().addAll(addSongLabel, manageAlbumsLabel);
    }

    private void createUserMenu() {
        System.out.println("Creating user menu");
        Label becomeArtistLabel = new Label("Become Artist 🎤");
        becomeArtistLabel.setStyle("-fx-text-fill: #1db954; -fx-cursor: hand;");
        becomeArtistLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

        becomeArtistLabel.setOnMouseClicked(e -> becomeArtist());

        Label profileLabel = new Label("Profile ⚙️");
        profileLabel.setStyle("-fx-text-fill: #bbbbbb; -fx-cursor: hand;");
        profileLabel.setFont(Font.font("System", FontWeight.NORMAL, 14));

        profileLabel.setOnMouseClicked(e -> {
            // TODO: Implement profile functionality
            System.out.println("Profile clicked - User: " + username);
        });

        settingsMenu.getChildren().addAll(becomeArtistLabel, profileLabel);
    }

    private void becomeArtist() {
        System.out.println("Attempting to become artist for user: " + username);

        if (userId == null) {
            System.out.println("Cannot become artist: userId is null");
            return;
        }

        Connection conn = connectToDB();
        if (conn == null) {
            System.out.println("Failed to connect to database");
            return;
        }

        try {
            // Check if the user is already an artist (avoid duplicate insert)
            String checkSql = "SELECT COUNT(*) as count FROM Artists WHERE userId = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, userId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt("count") > 0) {
                System.out.println("User is already an artist.");
                rs.close();
                checkStmt.close();
                conn.close();
                return;
            }

            rs.close();
            checkStmt.close();

            // Insert new artist
            String insertArtistSql = "INSERT INTO Artists (id, userId, bio, followerCount) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertArtistSql);

            // Generate a new artist ID (you might want to use UUID)
            String artistId = "ART" + System.currentTimeMillis(); // Simple ID generation
            stmt.setString(1, artistId);
            stmt.setString(2, userId);
            stmt.setString(3, "New artist - " + username); // Default bio
            stmt.setInt(4, 0); // Initial follower count

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Successfully became an artist!");
                // Update user type and refresh the settings menu
                userType = "ARTIST";

                // Clear existing settings menu and recreate
                settingsMenu.getChildren().clear();
                createSettingsMenu();

                // Refresh the homepage to show artist options
                showHomepage();
            } else {
                System.out.println("Failed to become an artist");
            }

            stmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error becoming an artist: " + e.getMessage());
        }
    }

    private void toggleSettingsMenu() {
        boolean wasVisible = settingsMenu.isVisible();
        settingsMenu.setVisible(!wasVisible);
        System.out.println("Settings menu toggled: " + !wasVisible);
    }

    // Getters for user information
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserId() {
        return userId;
    }
}