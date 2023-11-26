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
 * @author Adam Pierce
 */
public class DeckManager {
    private DeckLoader deckLoader;
    private Scene deckEditScene;
    private Label LquantityLabel;
    private Button plusButton;
    private Button minusButton;
    //private Label quantityLabel;
    private Stage deckEditStage;
    private static final int MAX_DECKS = 10;

    public DeckManager(Scene mainScene, Stage primaryStage) {
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
    
   private void initEditorWindow(Deck selectedDeck) {
        deckEditStage = new Stage();
        Deck[] updatedDeckHolder = new Deck[1];

        initializeDeckEditor(deckEditStage, selectedDeck, updatedDeckHolder);

        ScrollPane scrollPane = createCardGrid(deckEditStage);
        
        ScrollPane deckScroll = createDeckList(deckEditStage);  // Adds in the list of cards that are in the deck

        addCardsToGrid((GridPane) scrollPane.getContent(), CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json"), updatedDeckHolder);

        // adds the cards to the list of cards in the deck
        addToList((GridPane) deckScroll.getContent(), CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json"), updatedDeckHolder);
        
        deckScroll.setMinWidth(170);
        scrollPane.setMinWidth(1130);
        
        HBox layout = new HBox(1);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(scrollPane,deckScroll);
        
        VBox whole = new VBox(10);
        whole.setAlignment(Pos.CENTER);
        whole.getChildren().addAll(layout,createSaveDeckButton(selectedDeck, updatedDeckHolder));
        
        deckEditScene = new Scene(whole, 1300, 900);
        deckEditStage.setScene(deckEditScene);
        deckEditStage.show();
    }
    
    private void initializeDeckEditor(Stage deckEditStage, Deck selectedDeck, Deck[] updatedDeckHolder) {
        deckEditStage.setTitle("Deck Edit Window");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json");

        String jsonFilePath = selectedDeck.getJsonFilePath();
        Deck updatedDeck = Deck.loadFromJson(jsonFilePath);

        updatedDeckHolder[0] = updatedDeck != null ? updatedDeck : selectedDeck;
    }
    
    private ScrollPane createCardGrid(Stage deckEditStage) {
        GridPane cardGrid = new GridPane();
        cardGrid.setHgap(5);
        cardGrid.setVgap(5);
        cardGrid.setAlignment(Pos.CENTER_LEFT);

        ScrollPane scrollPane = new ScrollPane(cardGrid);
        //scrollPane.setFitToWidth(true);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(scrollPane);

        adjustGridSizeBasedOnWindowSize(deckEditStage, scrollPane);

        return scrollPane;
    }
    
    private void adjustGridSizeBasedOnWindowSize(Stage deckEditStage, ScrollPane scrollPane) {
        // Width listener
        deckEditStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double leftMargin = newVal.doubleValue() * 0.01;
            double rightMargin = newVal.doubleValue() * 0.20;
            double width = newVal.doubleValue() - (leftMargin + rightMargin);
            scrollPane.setPrefWidth(width);
            AnchorPane.setLeftAnchor(scrollPane, leftMargin);
            AnchorPane.setRightAnchor(scrollPane, rightMargin);
        });

        // Height listener
        deckEditStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double topMargin = newVal.doubleValue() * 0.01;
            double bottomMargin = newVal.doubleValue() * 0.20;
            double height = newVal.doubleValue() - (topMargin + bottomMargin);
            scrollPane.setPrefHeight(height);
            AnchorPane.setTopAnchor(scrollPane, topMargin);
            AnchorPane.setBottomAnchor(scrollPane, bottomMargin);
        });
    }
    
    private void addCardsToGrid(GridPane cardGrid, List<Card> allCards, Deck[] updatedDeckHolder) {
        int col = 0;
        int row = 0;

        for (Card card : allCards) {
            VBox cardVBox = createCardBox(card, updatedDeckHolder);
            cardGrid.add(cardVBox, col, row);

            col++;
            if (col > 4) {
                col = 0;
                row++;
            }
        }
    }
    
    private VBox createCardBox(Card card, Deck[] updatedDeckHolder) {
        VBox cardVBox = new VBox(5);
        cardVBox.setAlignment(Pos.CENTER);

        Image cardImage = new Image("file:" + card.getImageFile());
        ImageView cardImageView = new ImageView(cardImage);
        cardImageView.setFitHeight(100);
        cardImageView.setFitWidth(70);

        HBox cardInfoHBox = new HBox(10);
        cardInfoHBox.setAlignment(Pos.CENTER);

        Label cardLabel = new Label(card.getName());
        plusButton = new Button("+");
        minusButton = new Button("-");

        CardWithQuantity cardWithQuantity = updatedDeckHolder[0].getCards().stream()
                .filter(cwq -> cwq.getCard().getCardID() == card.getCardID())
                .findFirst()
                .orElse(new CardWithQuantity(card, 0));

        Label quantityLabel = new Label(String.valueOf(cardWithQuantity.getQuantity()));

        plusButton.setOnAction(e -> {
            cardWithQuantity.incrementQuantity();
            quantityLabel.setText(String.valueOf(cardWithQuantity.getQuantity()));
            updateCardInDeck(updatedDeckHolder[0], cardWithQuantity);
        });

        minusButton.setOnAction(e -> {
            if (cardWithQuantity.getQuantity() > 0) {
                cardWithQuantity.decrementQuantity();
                quantityLabel.setText(String.valueOf(cardWithQuantity.getQuantity()));
                updateCardInDeck(updatedDeckHolder[0], cardWithQuantity);
            }
        });

        cardInfoHBox.getChildren().addAll(minusButton, cardLabel, plusButton);
        cardVBox.getChildren().addAll(cardImageView, cardInfoHBox, quantityLabel);
        
        return cardVBox;
    }
    
    private void updateCardInDeck(Deck deck, CardWithQuantity cardWithQuantity) {
        // Remove the card with the same ID if it exists.
        deck.getCards().removeIf(cwq -> cwq.getCard().getCardID() == cardWithQuantity.getCard().getCardID());

        // If the card has a quantity greater than 0, then add it to the deck.
        if (cardWithQuantity.getQuantity() > 0) {
            deck.getCards().add(cardWithQuantity);
        }
    }
    
    private Button createSaveDeckButton(Deck selectedDeck, Deck[] updatedDeckHolder) {
        Button saveDeckButton = new Button("Save Deck");
        saveDeckButton.setOnAction(e -> saveDeck(selectedDeck, updatedDeckHolder));

        return saveDeckButton;
    }
    
    private void saveDeck(Deck selectedDeck, Deck[] updatedDeckHolder) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);

        VBox popupVBox = new VBox();
        popupVBox.setAlignment(Pos.CENTER);

        Label savingLabel = new Label("Saving your changes...");
        popupVBox.getChildren().add(savingLabel);

        Scene popupScene = new Scene(popupVBox, 200, 100);
        popupStage.setScene(popupScene);
        popupStage.show();

        Thread saveThread = new Thread(() -> {
            selectedDeck.setCards(new ArrayList<>(updatedDeckHolder[0].getCards()));
            deckLoader.saveDeck(selectedDeck);

            Platform.runLater(() -> {
                popupStage.close();
            });
        });

        saveThread.start();
    }
    
    // New List creation
    private ScrollPane createDeckList(Stage deckEditStage) {
        GridPane cardGrid = new GridPane();
        cardGrid.setHgap(1);
        cardGrid.setVgap(1);
        cardGrid.setAlignment(Pos.CENTER_RIGHT);

        ScrollPane scrollPane = new ScrollPane(cardGrid);
        //scrollPane.setFitToWidth(true);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(scrollPane);

        adjust(deckEditStage, scrollPane);

        return scrollPane;
    }
    private void adjust(Stage deckEditStage, ScrollPane scrollPane) {
        // Width listener
        deckEditStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double leftMargin = newVal.doubleValue() * 0.01;
            double rightMargin = newVal.doubleValue() * 0.20;
            double width = newVal.doubleValue() - (leftMargin + rightMargin);
            scrollPane.setPrefWidth(width);
            AnchorPane.setLeftAnchor(scrollPane, leftMargin);
            AnchorPane.setRightAnchor(scrollPane, rightMargin);
        });

        // Height listener
        deckEditStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double topMargin = newVal.doubleValue() * 0.01;
            double bottomMargin = newVal.doubleValue() * 0.20;
            double height = newVal.doubleValue() - (topMargin + bottomMargin);
            scrollPane.setPrefHeight(height);
            AnchorPane.setTopAnchor(scrollPane, topMargin);
            AnchorPane.setBottomAnchor(scrollPane, bottomMargin);
        });
    }
    private HBox createList(Card card, Deck[] updatedDeckHolder) {
        HBox cardVBox = new HBox(2);
        cardVBox.setAlignment(Pos.CENTER_RIGHT);

        HBox cardInfoHBox = new HBox(1);
        cardInfoHBox.setAlignment(Pos.CENTER_RIGHT);

        Label cardLabel = new Label(card.getName());

        CardWithQuantity cardWithQuantity = updatedDeckHolder[0].getCards().stream()
                .filter(cwq -> cwq.getCard().getCardID() == card.getCardID())
                .findFirst()
                .orElse(new CardWithQuantity(card, 0));
        
        LquantityLabel = new Label(String.valueOf(cardWithQuantity.getQuantity()));
        
        cardInfoHBox.getChildren().addAll(cardLabel);
        cardVBox.getChildren().addAll(cardInfoHBox, LquantityLabel);

        return cardVBox;
    }
    private void addToList(GridPane cardGrid, List<Card> allCards, Deck[] updatedDeckHolder) {
        int col = 0;
        int row = 0;

        for (Card card : allCards) {
            HBox cardVBox = createList(card, updatedDeckHolder);
            if(!"0".equals(LquantityLabel.getText()))
            {
                cardGrid.add(cardVBox, col, row);
                col++;
                if (col > 0) 
                {
                    col = 0;
                    row++;
                }
            } 
        }
    }
}
