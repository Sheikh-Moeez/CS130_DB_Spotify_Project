import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class Login {
    private Stage stage;
    private String loggedInUsername;
    private String loggedInEmail;

    public Login(Stage stage) {
        this.stage = stage;
    }

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

    // Sign-in method to handle login
    public void SignIN(VBox loginCard) {
        loginCard.getChildren().clear();

        // Heading and back button setup
        // Load the back button image safely
        ImageView backButton;
        try {
            Image backImage = new Image("Icons/back_Button.png");
            backButton = new ImageView(backImage);
        } catch (Exception e) {
            System.out.println("Could not load back button image: " + e.getMessage());
            backButton = new ImageView(); // Create empty ImageView as placeholder
        }

        backButton.setFitHeight(30);
        backButton.setFitWidth(30);
        backButton.setPreserveRatio(true);
        backButton.setOnMouseClicked(event -> showLoginCard(loginCard));

        // Heading text
        Text heading2 = new Text("Welcome back!");
        heading2.getStyleClass().add("heading");

        // BorderPane for top bar
        BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(30));
        topBar.setLeft(backButton);

        StackPane centerPane = new StackPane(heading2);
        centerPane.setPrefWidth(400); // Adjust as needed
        topBar.setCenter(centerPane);

        // Email and password form
        Label usernameLabel = new Label("Email:");
        TextField email = new TextField();
        email.setPromptText("Enter email");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("login-button");
        submitButton.setMaxWidth(Double.MAX_VALUE);

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("info-text");

        submitButton.setOnAction(event -> {
            String enteredEmail = email.getText().trim();
            String enteredPassword = passwordField.getText().trim();

            System.out.println("Login attempt for email: " + enteredEmail);

            if (enteredEmail.isEmpty() || enteredPassword.isEmpty()) {
                messageLabel.setText("Please enter both email and password.");
                return;
            }

            try (Connection conn = connectToDB()) {
                if (conn != null) {
                    String query = "SELECT username FROM Users WHERE email = ? AND password = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, enteredEmail);
                    stmt.setString(2, enteredPassword);

                    ResultSet resultSet = stmt.executeQuery();

                    if (resultSet.next()) {
                        loggedInUsername = resultSet.getString("username");
                        loggedInEmail = enteredEmail;

                        System.out.println("Login successful for user: " + loggedInUsername);
                        messageLabel.setText("Login successful. Welcome, " + loggedInUsername + "!");

                        // Close the result set and statement before navigating
                        resultSet.close();
                        stmt.close();

                        // Navigate to Homepage after successful login
                        try {
                            Homepage homepage = new Homepage(stage, loggedInUsername, loggedInEmail);
                            homepage.showHomepage();
                            System.out.println("Homepage should now be displayed");
                        } catch (Exception e) {
                            System.err.println("Error creating homepage: " + e.getMessage());
                            e.printStackTrace();
                            messageLabel.setText("Error loading homepage: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Invalid credentials for email: " + enteredEmail);
                        messageLabel.setText("Invalid email or password.");
                    }
                } else {
                    System.out.println("Database connection failed");
                    messageLabel.setText("Database connection failed.");
                }
            } catch (SQLException e) {
                System.err.println("SQL Error during login: " + e.getMessage());
                e.printStackTrace();
                messageLabel.setText("SQL Error: " + e.getMessage());
            }
        });

        VBox formBox = new VBox(10,
                usernameLabel, email,
                passwordLabel, passwordField,
                submitButton, messageLabel);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setMaxWidth(300);

        loginCard.getChildren().addAll(topBar, formBox);
    }

    public void SignUP(VBox loginCard) {
        loginCard.setMaxHeight(400);
        loginCard.getChildren().clear();

        // Load the back button image safely
        ImageView backButton;
        try {
            Image backImage = new Image("Icons/back_Button.png");
            backButton = new ImageView(backImage);
        } catch (Exception e) {
            System.out.println("Could not load back button image: " + e.getMessage());
            backButton = new ImageView(); // Create empty ImageView as placeholder
        }

        backButton.setFitHeight(30);
        backButton.setFitWidth(30);
        backButton.setPreserveRatio(true);
        backButton.setOnMouseClicked(event -> showLoginCard(loginCard));

        Text heading2 = new Text("Sign up for Spotify");
        heading2.getStyleClass().add("heading");

        // Top bar with back button and heading
        BorderPane topBar = new BorderPane();
        topBar.setPadding(new Insets(30));
        topBar.setLeft(backButton);

        StackPane centerPane = new StackPane(heading2);
        centerPane.setPrefWidth(400); // Adjust as needed
        topBar.setCenter(centerPane);

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Label dobLabel = new Label("Date of Birth:");
        DatePicker dobPicker = new DatePicker();

        Button submitButton = new Button("Submit");
        submitButton.getStyleClass().add("login-button");
        submitButton.setMaxWidth(Double.MAX_VALUE);

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("info-text");

        submitButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            LocalDate dob = dobPicker.getValue();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || dob == null) {
                messageLabel.setText("Please fill in all fields.");
                return;
            }

            System.out.println("Sign up attempt for: " + username + " (" + email + ")");

            try (Connection conn = connectToDB()) {
                if (conn != null) {
                    // Check if email already exists
                    String checkEmailSQL = "SELECT COUNT(*) FROM Users WHERE email = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkEmailSQL);
                    checkStmt.setString(1, email);
                    ResultSet checkResult = checkStmt.executeQuery();

                    if (checkResult.next() && checkResult.getInt(1) > 0) {
                        messageLabel.setText("Email already exists. Please use a different email.");
                        checkResult.close();
                        checkStmt.close();
                        return;
                    }
                    checkResult.close();
                    checkStmt.close();

                    String userId = UUID.randomUUID().toString();

                    String insertUserSQL = "INSERT INTO Users (id, username, email, password, dob) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(insertUserSQL);
                    stmt.setString(1, userId);
                    stmt.setString(2, username);
                    stmt.setString(3, email);
                    stmt.setString(4, password);
                    stmt.setDate(5, Date.valueOf(dob));

                    int rows = stmt.executeUpdate();

                    if (rows > 0) {
                        System.out.println("Sign up successful for: " + username);
                        messageLabel.setText("Sign up successful! Please log in.");

                        // Automatically switch to login view after a brief delay
                        new Thread(() -> {
                            try {
                                Thread.sleep(1500); // Wait 1.5 seconds
                                javafx.application.Platform.runLater(() -> showLoginCard(loginCard));
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }).start();
                    } else {
                        System.out.println("Sign up failed for: " + username);
                        messageLabel.setText("Failed to sign up. Try again.");
                    }

                    stmt.close();
                } else {
                    System.out.println("Database connection failed during sign up");
                    messageLabel.setText("Database connection failed.");
                }
            } catch (SQLException ex) {
                System.err.println("SQL Error during sign up: " + ex.getMessage());
                ex.printStackTrace();
                messageLabel.setText("SQL Error: " + ex.getMessage());
            }
        });

        VBox formBox = new VBox(10,
                usernameLabel, usernameField,
                emailLabel, emailField,
                passwordLabel, passwordField,
                dobLabel, dobPicker,
                submitButton, messageLabel);
        formBox.setAlignment(Pos.CENTER_LEFT);
        formBox.setMaxWidth(300);

        loginCard.getChildren().addAll(topBar, formBox);
    }

    public void showLoginCard(VBox loginCard) {
        loginCard.getChildren().clear();

        ImageView logoImage;
        try {
            logoImage = new ImageView(new Image("Icons/logo.png"));
        } catch (Exception e) {
            System.out.println("Could not load logo image: " + e.getMessage());
            logoImage = new ImageView(); // Create empty ImageView as placeholder
        }

        logoImage.setFitWidth(80);
        logoImage.setFitHeight(80);
        logoImage.setPreserveRatio(true);
        logoImage.setSmooth(true);
        logoImage.setCache(true);

        Text heading = new Text("Millions of songs.\nFree on Spotify.");
        heading.getStyleClass().add("heading");

        Button loginButton = new Button("Log in");
        loginButton.getStyleClass().add("login-button");

        Text newText = new Text("New to Spotify?");
        newText.getStyleClass().add("info-text");

        Hyperlink signUp = new Hyperlink("Sign up free");
        signUp.getStyleClass().add("signup-link");

        HBox signupBox = new HBox(5, newText, signUp);
        signupBox.setAlignment(Pos.CENTER);

        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(30));
        loginCard.setMaxWidth(350);
        loginCard.setMinWidth(350);
        loginCard.setMinHeight(350);
        loginCard.setMaxHeight(350);
        loginCard.getStyleClass().add("login-card");
        loginCard.getChildren().addAll(logoImage, heading, loginButton, signupBox);

        // Attach event handlers here to always be current
        loginButton.setOnAction(e -> SignIN(loginCard));
        signUp.setOnAction(e -> SignUP(loginCard));
    }

    public VBox createLoginView() {
        VBox loginCard = new VBox();
        showLoginCard(loginCard);
        return loginCard;
    }

    // Getters for user information
    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    public String getLoggedInEmail() {
        return loggedInEmail;
    }
}