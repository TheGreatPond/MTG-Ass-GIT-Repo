
package AppClasses;

//import T7DeckBuilder.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Adam
 */
public class MTGDeckBuilder{
    private Stage primaryStage;
    private BorderPane mainLayout;
    private Scene mainScene;
    private Stage helpStage;
    private DeckManager DM;
    private CardListViewer CLV;
    private AnalyzerWindow AW;
    
    public MTGDeckBuilder(){
        
    }
    
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MTG Deck Builder");

        // Initialize the main layout and scene
        initLayout();
        
        DM = new DeckManager(mainScene,primaryStage);
        CLV = new CardListViewer();
        AW = new AnalyzerWindow(mainScene,primaryStage);
        // Set and show the main scene
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
    
    private void initLayout() {
        mainLayout = new BorderPane();
        mainScene = new Scene(mainLayout, 300, 200);
        
        mainScene.getStylesheets().add("/styles/main_menu.css");
        
        mainLayout.setTop(createTopBox());
        mainLayout.setCenter(createMainGrid());
    }

    private HBox createTopBox() {
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.TOP_RIGHT);

        Button exitButton = createButton("Exit", e -> System.exit(0));
        //Button cardListButton = createButton("Card List", e -> showCardViewerScene());

        topBox.getChildren().addAll(exitButton);
        return topBox;
    }
    
    private GridPane createMainGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(15);
        grid.setHgap(15);
        
        
        grid.add(createButton("  Help  ", e -> showHelpWindow()), 1, 1);
        grid.add(createButton("Analyze", e -> showAnalyzeWindow()), 1, 0);
        grid.add(createButton(" Decklist ", e -> showDecklistWindow()), 0, 0);
        grid.add(createButton("Card List", e -> showCardViewerScene()), 0, 1);

        return grid;
    }
    
    private Button createButton(String text, EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setOnAction(handler);
        return button;
    }
    
    private void showDecklistWindow(){
        DM.showDecklistWindow(mainScene, primaryStage);
    }

    private void showHelpWindow() {
        
        String helpText = "This is the main menu of MTG Deck Builder.\n\nThe main menu has 3 other"
                + " buttons being: Card List, DeckList, and Analyze. \n\nEach button brings you to a corresponding menu\n\n"
                + " - Card List: This button opens up a window that lets you see all of the accessible cards in the program\n\n"
                + " - DeckList: This button opens up the deck menu, which lets you create and/or edit decks\n\n"
                + " - Analyze: This button opens up the deck analysis menu, allowing you to see stats about existing decks";
        
        helpStage = new Stage();
        helpStage.setTitle("Help");
        StackPane helpLayout = new StackPane();
        Scene helpScene = new Scene(helpLayout, 1000, 500);

        helpScene.getStylesheets().add("/styles/help_menu.css");
        
        // Add help text
        Label helpLabel = new Label(helpText);
        helpLayout.getChildren().add(helpLabel);

        // Set the help scene
        helpStage.setScene(helpScene);

        // Show the help window
        helpStage.show();
    }

    private void showAnalyzeWindow() {
        AW.showAnalyzeWindow(mainScene,primaryStage);
    }

    private void showCardViewerScene() {
        CLV.showCardViewerScene(mainScene,primaryStage);
    }
}
