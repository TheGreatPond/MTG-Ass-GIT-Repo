
package cs321.magic.deck.builder;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class app extends Application {
    private Stage primaryStage;
    private Scene mainScene;
    private Stage helpStage;
    private BorderPane mainLayout;
    private Scene cardViewerScene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("JavaFX Button Example");

    // Create the main layout
    mainLayout = new BorderPane();
    mainScene = new Scene(mainLayout, 300, 200);

    // Create a top HBox for the Exit button
    HBox topBox = new HBox();
    topBox.setAlignment(Pos.TOP_RIGHT);

    // Create the Exit button
    Button exitButton = new Button("Exit");
    exitButton.setOnAction(e -> System.exit(0));

    // Create the Card List button
    Button cardListButton = new Button("Card List");
    cardListButton.setOnAction(e -> showCardViewerScene());

    // Add the Exit and Card List buttons to the top HBox
    topBox.getChildren().addAll(exitButton, cardListButton);

    // Create the grid for other buttons
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setVgap(10);
    grid.setHgap(10);

    // Create buttons
    Button helpButton = new Button("Help");
    Button analyzeButton = new Button("Analyze");
    Button decklistButton = new Button("Decklist");

    // Add event handlers to the buttons
    helpButton.setOnAction(e -> showHelpWindow());
    analyzeButton.setOnAction(e -> showAnalyzeWindow());
    decklistButton.setOnAction(e -> showDecklistWindow());

    // Add buttons to the grid
    grid.add(helpButton, 0, 0);
    grid.add(analyzeButton, 1, 0);
    grid.add(decklistButton, 0, 1);

    // Set the top HBox and grid in the main layout
    mainLayout.setTop(topBox);
    mainLayout.setCenter(grid);

    // Set the main scene
    primaryStage.setScene(mainScene);

    // Show the main window
    primaryStage.show();
}


    private void showHelpWindow() {
        // Create a new help window
        helpStage = new Stage();
        helpStage.setTitle("Help");
        StackPane helpLayout = new StackPane();
        Scene helpScene = new Scene(helpLayout, 300, 150);

        // Add help text
        Label helpLabel = new Label("This is the help text.");
        helpLayout.getChildren().add(helpLabel);

        // Set the help scene
        helpStage.setScene(helpScene);

        // Show the help window
        helpStage.show();
    }

    private void showAnalyzeWindow() {
        // Create a new blank window
        Stage analyzeStage = new Stage();
        analyzeStage.setTitle("Analyze Window");
        StackPane blankLayout = new StackPane();
        Scene blankScene = new Scene(blankLayout, 300, 150);

        // Set the blank scene
        analyzeStage.setScene(blankScene);

        // Show the blank window
        analyzeStage.show();
    }

    private void showDecklistWindow() {
        // Create a new decklist window
        VBox decklistLayout = new VBox(10); // 10 is the spacing between buttons
        decklistLayout.setAlignment(Pos.CENTER);
        Scene decklistScene = new Scene(decklistLayout, 300, 200);

        // Create an HBox for button alignment
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.TOP_RIGHT);

        // Add an empty region to push the exit button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Create buttons for the decklist window
        Button exitButton = new Button("Exit");
        Button backButton = new Button("Back");

        // Add event handlers to the buttons
        exitButton.setOnAction(e -> System.exit(0));
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        // Add buttons to the buttonBox
        buttonBox.getChildren().addAll(spacer, exitButton);

        // Add buttons and buttonBox to the decklist layout
        decklistLayout.getChildren().addAll(buttonBox, backButton);

        // Set the decklist scene
        primaryStage.setScene(decklistScene);
    }

    // Function to show card viewer scene
    private void showCardViewerScene() {
    // Create a new card viewer window
    Stage cardViewerStage = new Stage();
    cardViewerStage.setTitle("Card Viewer");

    // Create a VBox to hold card information and picture
    VBox cardViewerLayout = new VBox(10);
    cardViewerLayout.setAlignment(Pos.CENTER);
    cardViewerScene = new Scene(cardViewerLayout, 600, 400);

    // Load card data from WAR_cards.json
    List<Card> cards = CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json"); // Replace with your logic

    // Create a ListView to display the list of cards
    ListView<String> cardListView = new ListView<>();
    cardListView.setPrefHeight(300);

    // Populate the cardListView with card names
    for (Card card : cards) {
        cardListView.getItems().add(card.GetName());
    }

    // Create a TextArea to display card information
    TextArea cardInfoTextArea = new TextArea();
    cardInfoTextArea.setEditable(false);
    cardInfoTextArea.setWrapText(true);
    cardInfoTextArea.setPrefWidth(300);
    cardInfoTextArea.setPrefHeight(300);

    // Create an ImageView to display the card image
    ImageView imageView = new ImageView();
    imageView.setFitHeight(300);
    imageView.setPreserveRatio(true);

    // Listen for selection changes in the ListView
    cardListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        // Find the selected card
        Card selectedCard = null;
        for (Card card : cards) {
            if (card.GetName().equals(newValue)) {
                selectedCard = card;
                break;
            }
        }

        // Display card information and image
        if (selectedCard != null) {
            cardInfoTextArea.setText("Name: " + selectedCard.GetName() + "\n" +
                                      "Cost: " + selectedCard.GetCmc() + "\n" +
                                      "Effect: " + selectedCard.GetText());
            Image cardImage = new Image("file:src/mtg_data/image_data/" + selectedCard.GetMultiverseId() + ".jpg");
            imageView.setImage(cardImage);
        }
    });

    // Add components to the cardViewerLayout
    HBox cardInfoBox = new HBox(10);
    cardInfoBox.getChildren().addAll(cardInfoTextArea, imageView);
    cardViewerLayout.getChildren().addAll(cardListView, cardInfoBox);

    // Set the card viewer scene
    cardViewerStage.setScene(cardViewerScene);

    // Show the card viewer window
    cardViewerStage.show();
    }
}
