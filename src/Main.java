import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create and show the login screen
        Login login = new Login(primaryStage);
        VBox loginView = login.createLoginView();

        StackPane root = new StackPane(loginView);
        root.setPrefSize(800, 600);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        primaryStage.setTitle("Spotify Login UI");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("Icons/logo.png"));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}