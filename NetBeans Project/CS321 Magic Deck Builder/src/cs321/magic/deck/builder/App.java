
package cs321.magic.deck.builder;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

public class App extends Application {
    private Stage primaryStage;
    private Scene mainScene;
    private Stage helpStage;
    private BorderPane mainLayout;
    private Scene cardViewerScene;
    private static final int MAX_DECKS = 10;
    private DeckLoader deckLoader = new DeckLoader(); // Added the DeckLoader

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
    VBox decklistLayout = new VBox(10);
    decklistLayout.setAlignment(Pos.CENTER);
    Scene decklistScene = new Scene(decklistLayout, 500, 400);

    HBox buttonBox = new HBox(10);
    buttonBox.setAlignment(Pos.TOP_RIGHT);

    Button exitButton = new Button("Exit");
    Button backButton = new Button("Back");
    Button createDeckButton = new Button("Create Deck");
    Button deleteDeckButton = new Button("Delete Deck");
    Button editDeckButton = new Button("Edit Deck");

    ListView<String> deckListView = new ListView<>();

    // Ensure deckLoader is properly instantiated and has loaded decks
    deckLoader = new DeckLoader(); // Assuming deckLoader is a class field
    List<Deck> deckList = deckLoader.getDecks(); // Use getDecks from DeckLoader

    // Populate the ListView with loaded decks
    if (deckList.isEmpty()) {
        deckListView.getItems().add("No decks available");
    } else {
        for (Deck deck : deckList) {
            deckListView.getItems().add(deck.getName());
        }
    }

    exitButton.setOnAction(e -> System.exit(0));
    backButton.setOnAction(e -> primaryStage.setScene(mainScene));

createDeckButton.setOnAction(e -> {
    ObservableList<String> items = deckListView.getItems();
    
    if (items.size() < MAX_DECKS || items.contains("No decks available")) {
        TextInputDialog dialog = new TextInputDialog("Deck Name");
        dialog.setTitle("Create New Deck");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter the deck name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(deckName -> {
            String trimmedName = deckName.trim();
            if (!trimmedName.isEmpty()) {
                // Check for duplicate names
                if (items.stream().anyMatch(name -> name.equalsIgnoreCase(trimmedName))) {
                    Alert duplicateAlert = new Alert(Alert.AlertType.ERROR, "A deck with this name already exists!");
                    duplicateAlert.showAndWait();
                    return;
                }

                Deck newDeck = new Deck(trimmedName);
                deckLoader.saveDeck(newDeck); // Ensure this sets the hash ID in newDeck

                if (items.contains("No decks available")) {
                    items.clear();
                }
                // Add only the name to the ListView
                items.add(newDeck.getName());
            }
        });
    } else {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Maximum decks reached!");
        alert.showAndWait();
    }
});




    deleteDeckButton.setOnAction(e -> {
        String selectedDeckName = deckListView.getSelectionModel().getSelectedItem();
        if (selectedDeckName != null && !selectedDeckName.equals("No decks available")) {
            Deck toRemove = deckLoader.getDeckByName(selectedDeckName);
            if (toRemove != null) {
                deckLoader.deleteDeck(toRemove); // Delete using DeckLoader
                deckListView.getItems().remove(selectedDeckName);
            }
        }
    });

editDeckButton.setOnAction(e -> {
    String selectedDeckName = deckListView.getSelectionModel().getSelectedItem();
    if (selectedDeckName != null && !selectedDeckName.equals("No decks available")) {
        Deck toEdit = deckLoader.getDeckByName(selectedDeckName);

        if (toEdit != null) {
            showDeckEditWindow(toEdit); // Implement this method to show the deck editing interface
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The selected deck was not found!");
            alert.showAndWait();
        }
    } else {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a deck to edit!");
        alert.showAndWait();
    }
});


    buttonBox.getChildren().addAll(createDeckButton, deleteDeckButton, editDeckButton, exitButton, backButton);
    decklistLayout.getChildren().addAll(buttonBox, deckListView);
    primaryStage.setScene(decklistScene);
}




private void showDeckEditWindow(Deck selectedDeck) {
    Stage deckEditStage = new Stage();
    deckEditStage.setTitle("Deck Edit Window");

    VBox layout = new VBox(10);
    layout.setAlignment(Pos.CENTER);

    // Load card data from WAR_cards.json
    List<Card> allCards = CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json");

    // Load the selected deck from the JSON file to get the updated quantities
    String jsonFilePath = selectedDeck.getJsonFilePath();
    Deck updatedDeck = Deck.loadFromJson(jsonFilePath);
    
    if (updatedDeck == null) {
        updatedDeck = selectedDeck;
    }

    // Create a ListView to display the list of cards with '+' and '-' buttons
    ListView<HBox> cardListView = new ListView<>();
    cardListView.setPrefHeight(300);

    for (Card card : allCards) {
        HBox cardBox = new HBox(10);
        cardBox.setAlignment(Pos.CENTER_LEFT);

        // Assuming Card has a method getImagePath() that returns path to card's image
        Image cardImage = new Image("file:" + card.getImageFile());
        ImageView cardImageView = new ImageView(cardImage);
        cardImageView.setFitHeight(100);
        cardImageView.setFitWidth(70);

        Label cardLabel = new Label(card.getName());
        Button plusButton = new Button("+");
        Button minusButton = new Button("-");
        
        CardWithQuantity cardWithQuantity = updatedDeck.getCards().stream()
            .filter(cwq -> cwq.getCard().getCardID() == card.getCardID())
            .findFirst()
            .orElse(new CardWithQuantity(card, 0));

        Label quantityLabel = new Label(String.valueOf(cardWithQuantity.getQuantity()));

        plusButton.setOnAction(e -> {
            cardWithQuantity.incrementQuantity();
            quantityLabel.setText(String.valueOf(cardWithQuantity.getQuantity()));
            updateCardInDeck(selectedDeck, cardWithQuantity);
        });

        minusButton.setOnAction(e -> {
            if (cardWithQuantity.getQuantity() > 0) {
                cardWithQuantity.decrementQuantity();
                quantityLabel.setText(String.valueOf(cardWithQuantity.getQuantity()));
                updateCardInDeck(selectedDeck, cardWithQuantity);
            }
        });

        cardBox.getChildren().addAll(cardImageView, cardLabel, plusButton, minusButton, quantityLabel);
        cardListView.getItems().add(cardBox);
    }

    Button saveDeckButton = new Button("Save Deck");
    saveDeckButton.setOnAction(e -> {
        deckLoader.saveDeck(selectedDeck);
    });

    layout.getChildren().addAll(cardListView, saveDeckButton);
    Scene deckEditScene = new Scene(layout, 500, 400);
    deckEditStage.setScene(deckEditScene);
    deckEditStage.show();
}



private void updateCardInDeck(Deck deck, CardWithQuantity cardWithQuantity) {
    // Remove the card with the same ID if it exists.
    deck.getCards().removeIf(cwq -> cwq.getCard().getCardID() == cardWithQuantity.getCard().getCardID());
    
    // If the card has a quantity greater than 0, then add it to the deck.
    if (cardWithQuantity.getQuantity() > 0) {
        deck.getCards().add(cardWithQuantity);
    }
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
            cardListView.getItems().add(card.getName());
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
                if (card.getName().equals(newValue)) {
                    selectedCard = card;
                    break;
                }
            }

            // Display card information and image
            if (selectedCard != null) {
                cardInfoTextArea.setText("Name: " + selectedCard.getName() + "\n" +
                                          "Cost: " + selectedCard.getTotalMana() + "\n" +
                                          "Effect: " + selectedCard.getDescription());
                Image cardImage = new Image("file:" + selectedCard.getImageFile());
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
