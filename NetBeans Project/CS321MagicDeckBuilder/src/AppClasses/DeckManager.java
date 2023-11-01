package AppClasses;

import T7DeckBuilder.CardPackage.Card;
import T7DeckBuilder.CardPackage.CardLoader;
import T7DeckBuilder.CardPackage.CardWithQuantity;
import T7DeckBuilder.DeckPackage.Deck;
import T7DeckBuilder.DeckPackage.DeckLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Adam
 */
public class DeckManager {
    private final Scene mainScene;   // Store the reference to the main scene.
    private final Stage primaryStage; // Store the reference to the primary stage
    private DeckLoader deckLoader;// DeckLoader instance
    private static final int MAX_DECKS = 10;

    public DeckManager(Scene mainScene, Stage primaryStage) {
        this.mainScene = mainScene;
        this.primaryStage = primaryStage;
        this.deckLoader = new DeckLoader();
    }

    public void showDecklistWindow(Scene mainScene, Stage primaryStage) {     
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

        // Populate the ListView with loaded decks
        populateDeckListView(deckListView);

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
                        populateDeckListView(deckListView);
                        //items.add(trimmedName);
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
                    // Create an alert with confirmation type
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setTitle("Confirm Deletion");
                confirmationAlert.setHeaderText(null);
                confirmationAlert.setContentText("Are you sure you want to delete this deck?");

                // Customize the button text
                ButtonType yesButton = new ButtonType("Yes");
                ButtonType noButton = new ButtonType("No");
                confirmationAlert.getButtonTypes().setAll(yesButton, noButton);

                Optional<ButtonType> result = confirmationAlert.showAndWait();
                if (result.get() == yesButton) {
                    Deck toRemove = deckLoader.getDeckByName(selectedDeckName);
                    if (toRemove != null) {
                        deckLoader.deleteDeck(toRemove); // Delete using DeckLoader
                        deckListView.getItems().remove(selectedDeckName);
                        populateDeckListView(deckListView);
                    }
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
        
        buttonBox.getChildren().addAll(createDeckButton, deleteDeckButton, editDeckButton, backButton, exitButton);
        decklistLayout.getChildren().addAll(buttonBox, deckListView);
        primaryStage.setScene(decklistScene);
    }
    
    private void populateDeckListView(ListView<String> deckListView) {
        // Clear existing items
        deckListView.getItems().clear();

        // Reload decks from DeckLoader
        List<Deck> deckList = deckLoader.getDecks();

        if (deckList.isEmpty()) {
            deckListView.getItems().add("No decks available");
        } else {
            for (Deck deck : deckList) {
                deckListView.getItems().add(deck.getName());
            }
        }
    }
    
    private void showDeckEditWindow(Deck selectedDeck){
        initEditorWindow(selectedDeck);
    }
    
    private void initEditorWindow(Deck selectedDeck){
        Stage deckEditStage = new Stage();
        deckEditStage.setTitle("Deck Edit Window");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);


        // Load card data from WAR_cards.json
        List<Card> allCards = CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json");

        // Load the selected deck from the JSON file to get the updated quantities
        String jsonFilePath = selectedDeck.getJsonFilePath();
        Deck updatedDeck = Deck.loadFromJson(jsonFilePath);

        //puts the deck in an array so that it can be edited by a lambda expression
        Deck[] updatedDeckHolder = { Deck.loadFromJson(jsonFilePath) };
            if (updatedDeckHolder[0] == null) {
                updatedDeckHolder[0] = selectedDeck;
            }

        if (updatedDeck == null) {
            updatedDeck = selectedDeck;
        }

        // Creates a grid for the cards
        GridPane cardGrid = new GridPane();
        cardGrid.setHgap(10);
        cardGrid.setVgap(10);
        cardGrid.setAlignment(Pos.CENTER);
        
        ScrollPane scrollPane = new ScrollPane(cardGrid); // Wrap the cardGrid inside the ScrollPane
        scrollPane.setFitToWidth(true); // This makes the ScrollPane expand horizontally with the cardGrid

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(scrollPane); // Add ScrollPane to the AnchorPane instead of cardGrid

        
        // Adjust the card grid positioning based on the window size
        deckEditStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // 10% of the window width for left and right margins
            double horizontalMargin = newVal.doubleValue() * 0.01;
            double width = newVal.doubleValue() * 0.80; // 80% of the total width
            scrollPane.setPrefWidth(width);
            AnchorPane.setLeftAnchor(scrollPane, horizontalMargin);
            AnchorPane.setRightAnchor(scrollPane, horizontalMargin);
        });

        deckEditStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            // 10% of the window height for top margin and 20% for bottom margin
            double topMargin = newVal.doubleValue() * 0.01;
            double bottomMargin = newVal.doubleValue() * 0.20;
            double height = newVal.doubleValue() - (topMargin + bottomMargin);
            scrollPane.setPrefHeight(height);
            AnchorPane.setTopAnchor(scrollPane, topMargin);
            AnchorPane.setBottomAnchor(scrollPane, bottomMargin);
        });
        
        //keeps track of rows and colums for the grid logic
        int col = 0;
        int row = 0;

        for (Card card : allCards) {
            VBox cardVBox = new VBox(5); // Vertical box for each card and its details
            cardVBox.setAlignment(Pos.CENTER);

            // Assuming Card has a method getImagePath() that returns path to card's image
            Image cardImage = new Image("file:" + card.getImageFile());
            ImageView cardImageView = new ImageView(cardImage);
            cardImageView.setFitHeight(100);
            cardImageView.setFitWidth(70);
            
            HBox cardInfoHBox = new HBox(10); // Horizontal box for card name, plus and minus buttons
            cardInfoHBox.setAlignment(Pos.CENTER);
            
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
                updateCardInDeck(updatedDeckHolder[0], cardWithQuantity); // Use updatedDeckHolder[0]
            });

            minusButton.setOnAction(e -> {
                if (cardWithQuantity.getQuantity() > 0) {
                    cardWithQuantity.decrementQuantity();
                    quantityLabel.setText(String.valueOf(cardWithQuantity.getQuantity()));
                    updateCardInDeck(updatedDeckHolder[0], cardWithQuantity); // Use updatedDeckHolder[0]
                }
            });

            cardInfoHBox.getChildren().addAll(minusButton, cardLabel, plusButton);
            cardVBox.getChildren().addAll(cardImageView, cardInfoHBox, quantityLabel);
            
            cardGrid.add(cardVBox, col, row); // Add to grid
            

            col++;
            if (col > 4) { // If more than 5 columns, go to the next row
                col = 0;
                row++;
            }
        }

        Button saveDeckButton = new Button("Save Deck");
        saveDeckButton.setOnAction(e -> {
                // Create a new stage for the popup
                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL); // Make it block other user input

                // Create a VBox layout for the popup contents
                VBox popupVBox = new VBox();
                popupVBox.setAlignment(Pos.CENTER);

                // Add a label to show a message to the user
                Label savingLabel = new Label("Saving your changes...");
                popupVBox.getChildren().add(savingLabel);

                // Set the scene for the popup
                Scene popupScene = new Scene(popupVBox, 200, 100);
                popupStage.setScene(popupScene);

                // Show the popup
                popupStage.show();

                // Run the saving operation on a new thread to avoid blocking the UI thread
                Thread saveThread = new Thread(() -> {
                    selectedDeck.setCards(new ArrayList<>(updatedDeckHolder[0].getCards())); // Update selectedDeck to match updatedDeck
                    deckLoader.saveDeck(selectedDeck);

                    // Close the popup on the JavaFX Application thread once the save operation completes
                    Platform.runLater(() -> {
                        popupStage.close();
                    });
                });

                // Start the save thread
                saveThread.start();
            });
    }
    
    private void updateCardInDeck(Deck deck, CardWithQuantity cardWithQuantity) {
        // Remove the card with the same ID if it exists.
        deck.getCards().removeIf(cwq -> cwq.getCard().getCardID() == cardWithQuantity.getCard().getCardID());

        // If the card has a quantity greater than 0, then add it to the deck.
        if (cardWithQuantity.getQuantity() > 0) {
            deck.getCards().add(cardWithQuantity);
        }
    }
}
