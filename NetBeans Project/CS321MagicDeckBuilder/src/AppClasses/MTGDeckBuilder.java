
package AppClasses;

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
 * @author Adam Pierce
 * The Class for the main window.
 * Creates the objects for the Stage,Layout,Scene,DeckManager,CardListViewer,and AnalyzerWindow
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
    /**
     * @author Adam Pierce
     * Creates the primaryStage and call initLayout() to set up UI
     * Creates a DeckManager,CardListViewer,and AnalyzerWindow objects
     * @param primaryStage
     */
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
    
    /**
     * @author Adam Pierce
     * Creates a BorderPane and sets the scene variables for the mayLayout
     * Edits the mainLayout to create a topBox and a grid in the center of the window
     */
    private void initLayout() {
        mainLayout = new BorderPane();
        mainScene = new Scene(mainLayout, 300, 200);

        mainLayout.setTop(createTopBox());
        mainLayout.setCenter(createMainGrid());
    }
    
    /**
     * @author Adam Pierce
     * Creates a new horizontal box for the exit button
     * @return Exit button
     */
    private HBox createTopBox() {
        HBox topBox = new HBox();
        topBox.setAlignment(Pos.TOP_RIGHT);

        Button exitButton = createButton("Exit", e -> System.exit(0));
        //Button cardListButton = createButton("Card List", e -> showCardViewerScene());

        topBox.getChildren().addAll(exitButton);
        return topBox;
    }
    
    /**
     * @author Adam Pierce
     * Populates the grid of buttons for the main window
     * @return grid
     */
    private GridPane createMainGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(createButton("Help", e -> showHelpWindow()), 0, 0);
        grid.add(createButton("Analyze", e -> showAnalyzeWindow()), 1, 0);
        grid.add(createButton("Decklist", e -> showDecklistWindow()), 0, 1);
        grid.add(createButton("Card List", e -> showCardViewerScene()), 1, 1);

        return grid;
    }
    
    /**
     * @author Adam Pierce
     * Creates a button depending on the EventHandler passed as argument
     * @param text
     * @param handler
     * @return button
     */
    private Button createButton(String text, EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setOnAction(handler);
        return button;
    }
    
    /**
     * @author Adam Pierce
     * Shows the main decklist editor window
     */
    private void showDecklistWindow(){
        DM.showDecklistWindow(mainScene, primaryStage);
    }

    /**
     * @author Adam Pierce
     * Shows the help text window
     */
    private void showHelpWindow() {
        helpStage = new Stage();
        helpStage.setTitle("Help");
        StackPane helpLayout = new StackPane();
        Scene helpScene = new Scene(helpLayout, 700, 150);

        // Add help text
        Label helpLabel = new Label("Welcome to MTG Deck Builder. \n\n To analyze manacurve or color distribution of a deck, click the analyze button on the main menu \n\n To build or edit a deck please click the Decklist button from the main menu");
        helpLayout.getChildren().add(helpLabel);

        // Set the help scene
        helpStage.setScene(helpScene);

        // Show the help window
        helpStage.show();
    }

    /**
     * @author Adam Pierce
     * Shows the Analyzer Window
     */
    private void showAnalyzeWindow() {
        AW.showAnalyzeWindow(mainScene,primaryStage);
    }

    /**
     * @author Adam Pierce
     * Shows the Card List Window
     */
    private void showCardViewerScene() {
        CLV.showCardViewerScene(mainScene,primaryStage);
    }
}
