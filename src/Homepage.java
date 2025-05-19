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
        VBox centerContent = new VBox();
        centerContent.setPadding(new Insets(15));
        centerContent.setStyle("-fx-background-color: #202020;");
        BorderPane.setMargin(centerContent, new Insets(5, 0, 0, 7));

        // Welcome message with user's name and type
        Label welcomeLabel = new Label("Welcome back, " + username);
        welcomeLabel.setTextFill(Color.WHITE);
        welcomeLabel.setFont(new Font(24));

        Label playlistsLabel = new Label("Playlists");
        playlistsLabel.setTextFill(Color.WHITE);
        playlistsLabel.setFont(new Font(18));

        HBox playlistsRow = new HBox(10);
        playlistsRow.setPadding(new Insets(10));
        for (int i = 1; i <= 5; i++) {
            VBox playlist = new VBox(5);
            playlist.setStyle("-fx-background-color: #333333; -fx-padding: 10;");
            Label pLabel = new Label("Playlist " + i);
            pLabel.setTextFill(Color.WHITE);
            playlist.getChildren().add(pLabel);
            playlistsRow.getChildren().add(playlist);
        }

        ScrollPane playlistScroll = new ScrollPane(playlistsRow);
        playlistScroll.setFitToHeight(true);
        playlistScroll.setFitToWidth(true);
        playlistScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        playlistScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        playlistScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        centerContent.getChildren().addAll(welcomeLabel, playlistsLabel, playlistScroll);

        // === Bottom Bar ===
        HBox bottomBar = new HBox();
        bottomBar.setStyle("-fx-background-color: #121212;");
        bottomBar.setPadding(new Insets(20));
        bottomBar.setAlignment(Pos.CENTER);

        Label nowPlaying = new Label("Now Playing: Song Name");
        nowPlaying.setTextFill(Color.WHITE);
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

        Scene scene = new Scene(root, 1000, 600);
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