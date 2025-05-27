import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;

public class Audio extends Application {

    @Override
    public void start(Stage primaryStage) {
    String audioFilePath = new File("D:\\Repos\\Spotify Project\\Assets\\Songs\\Jhol.mp3").toURI().toString();
    
    Media media = new Media(audioFilePath);
    MediaPlayer mediaPlayer = new MediaPlayer(media);

    Button playButton = new Button("Play Audio");
    playButton.setOnAction(e -> {
        mediaPlayer.stop(); // Restart if already playing
        mediaPlayer.play();
    });

    VBox root = new VBox(10, playButton);
    Scene scene = new Scene(root, 300, 200);

    primaryStage.setTitle("JavaFX Audio Player");
    primaryStage.setScene(scene);
    primaryStage.show();
}

    public static void main(String[] args) {
        launch(args);
    }
}