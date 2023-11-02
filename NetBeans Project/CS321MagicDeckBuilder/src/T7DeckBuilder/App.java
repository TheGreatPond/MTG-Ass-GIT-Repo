
package T7DeckBuilder;

import AppClasses.MTGDeckBuilder;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MTGDeckBuilder application = new MTGDeckBuilder();
        application.start(primaryStage);
    }
}