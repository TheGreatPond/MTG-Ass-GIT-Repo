package AppClasses;

import T7DeckBuilder.CardPackage.Card;
import T7DeckBuilder.CardPackage.CardLoader;
import T7DeckBuilder.CardPackage.CardWithQuantity;
import T7DeckBuilder.DeckPackage.Deck;
import T7DeckBuilder.DeckPackage.DeckLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import javafx.scene.layout.StackPane;
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
    private Stage deckEditStage;
    private static final int MAX_DECKS = 10;

    /**
     * Creates the deckLoader
     * @param mainScene
     * @param primaryStage 
     */
    public DeckManager(Scene mainScene, Stage primaryStage) {
        this.deckLoader = new DeckLoader();
    }

    /**
     * @author Adam Pierce
     * Creates a window for creating, editing,and deleting decks
     * @param mainScene
     * @param primaryStage 
     */
    public void showDecklistWindow(Scene mainScene, Stage primaryStage) {     
        VBox decklistLayout = new VBox(10);
        decklistLayout.setAlignment(Pos.CENTER);
        Scene decklistScene = new Scene(decklistLayout, 500, 400);
        
        decklistScene.getStylesheets().add("/styles/main_menu.css");

        // Creates button box
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        
        // Creates buttons
        Button exitButton = new Button("Exit");
        Button backButton = new Button("Back");
        Button createDeckButton = new Button("Create Deck");
        Button deleteDeckButton = new Button("Delete Deck");
        Button editDeckButton = new Button("Edit Deck");
        Button helpButton = new Button("Help");
        
        ListView<String> deckListView = new ListView<>();

        // Populate the ListView with loaded decks
        populateDeckListView(deckListView);

        exitButton.setOnAction(e -> System.exit(0));
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        createDeckButton.setOnAction(e -> {
            ObservableList<String> items = deckListView.getItems();

            /**
             * Checks if the current number of decks in the list is less than the MAX_DECKS variable.
             * If less, then the user is prompted to create a new deck that does not share a name with an already created deck.
             */
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
        
        /**
         * Checks to see if there is a selected deck.
         * If a deck is selected, prompts user to see if they intent to delete the currently selected deck.
         */
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

        /**
         * Checks to see if a deck is selected.
         * If a deck is selected, then it loads the deck into the deck editor window.
         */
        editDeckButton.setOnAction(e -> {
            String selectedDeckName = deckListView.getSelectionModel().getSelectedItem();
            if (selectedDeckName != null && !selectedDeckName.equals("No decks available")) {
                Deck toEdit = deckLoader.getDeckByName(selectedDeckName);

                if (toEdit != null) {
                    // Uses selected deck as argument for Deck Edit Window function
                    showDeckEditWindow(toEdit);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The selected deck was not found!");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a deck to edit!");
                alert.showAndWait();
            }
        });
        
        helpButton.setOnAction(e -> {
            String helpText = "This is the deck builder menu, where you can create and/or edit decks as needed.\n\n"
                    + "This menu has the following buttons: Help, Create Deck, Delete Deck, Edit Deck, Back, and Exit.\n\n"
                    + "Create Deck: This button prompts you for a name for your deck. Once you confirm the deck is saved to memory.\n\n"
                    + "Deletee Deck: This lets you delete any deck you've made. To delete a deck first select it in the menu, then click this button.\n\n"
                    + "Edit Deck: This lets you open the deck editor menu. To dit a deck first select it in the menu, then click this button.\n"
                    + "This opens up another window that lets you access all cards found on the card list page from the main menu. To add a card\n"
                    + "click the + under an image, and click - to remove it. You can have a max of 4 for each card. Once finished, click the \"Save Deck\" button.\n\n"
                    + "Back: This brings you to the main menu.\n\nExit: This exits the program.";
            
            Stage helpStage;
            
            helpStage = new Stage();
            helpStage.setTitle("Help");
            StackPane helpLayout = new StackPane();
            Scene helpScene = new Scene(helpLayout, 1300, 500);

            helpScene.getStylesheets().add("/styles/help_menu.css");
            
            // Add help text
            Label helpLabel = new Label(helpText);
            helpLayout.getChildren().add(helpLabel);

            // Set the help scene
            helpStage.setScene(helpScene);

            // Show the help window
            helpStage.show();
        });
        
        
        buttonBox.getChildren().addAll(helpButton, createDeckButton, deleteDeckButton, editDeckButton, backButton, exitButton);
        decklistLayout.getChildren().addAll(buttonBox, deckListView);
        primaryStage.setScene(decklistScene);
    }
    
    /**
     * @author Adam Pierce
     * Populates the deck list with all available cards in the selected deck
     * @param deckListView 
     */
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
    
    /**
     * @author Adam Pierce
     * Calls the init function to create the Editor Window
     * @param selectedDeck 
     */
    private void showDeckEditWindow(Deck selectedDeck){
        initEditorWindow(selectedDeck);
    }
    
    /**
     * @author Adam Pierce
     * Initializes the Editor Window with the layout.
     * Creates Scroll Planes and adds in the grid of cards in the scroll planes
     * @param selectedDeck 
     */
   private void initEditorWindow(Deck selectedDeck) {
        deckEditStage = new Stage();
        Deck[] updatedDeckHolder = new Deck[1];

        initializeDeckEditor(deckEditStage, selectedDeck, updatedDeckHolder);

        ScrollPane scrollPane = createCardGrid(deckEditStage);
        
        ScrollPane deckScroll = createDeckList(deckEditStage);  // Adds in the list of cards that are in the deck

        addCardsToGrid((GridPane) scrollPane.getContent(), CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json"), updatedDeckHolder, deckScroll);

        // adds the cards to the list of cards in the deck
        updateEditorCardList((GridPane) deckScroll.getContent(), CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json"), updatedDeckHolder);
        
        deckScroll.setMinWidth(170);
        scrollPane.setMinWidth(1130);
        
        HBox layout = new HBox(1);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(scrollPane,deckScroll);
        
        VBox whole = new VBox(10);
        whole.setAlignment(Pos.CENTER);
        whole.getChildren().addAll(layout,createSaveDeckButton(selectedDeck, updatedDeckHolder, deckScroll));
        
        
        deckEditScene = new Scene(whole, 1300, 900);
        deckEditScene.getStylesheets().add("/styles/deck_edit_menu.css");
        deckEditStage.setScene(deckEditScene);
        deckEditStage.show();
    }
    
   /**
    * @author Adam Pierce
    * Creates the Window and loads card data into the updatedDeck object
    * @param deckEditStage
    * @param selectedDeck
    * @param updatedDeckHolder 
    */
    private void initializeDeckEditor(Stage deckEditStage, Deck selectedDeck, Deck[] updatedDeckHolder) {
        deckEditStage.setTitle("Deck Edit Window");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json");

        String jsonFilePath = selectedDeck.getJsonFilePath();
        Deck updatedDeck = Deck.loadFromJson(jsonFilePath);

        updatedDeckHolder[0] = updatedDeck != null ? updatedDeck : selectedDeck;
    }
    
    /**
     * @author Adam Pierce
     * Creates the grid of cards
     * @param deckEditStage
     * @return scrollPane
     */
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
    
    /**
     * Uses listeners to scale the grid based on the current measurement of the window
     * @param deckEditStage
     * @param scrollPane 
     */
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
    
    /**
     * @author Adam Pierce
     * Adds all the cards in the current list of allCards to the grid
     * @param cardGrid
     * @param allCards
     * @param updatedDeckHolder
     * @param deckScroll 
     */
    private void addCardsToGrid(GridPane cardGrid, List<Card> allCards, Deck[] updatedDeckHolder, ScrollPane deckScroll) {
        int col = 0;
        int row = 0;

        for (Card card : allCards) {
            VBox cardVBox = createCardBox(card, updatedDeckHolder, deckScroll);
            cardGrid.add(cardVBox, col, row);

            col++;
            if (col > 4) {
                col = 0;
                row++;
            }
        }
    }
    
    /**
     * @author Adam Pierce
     * Creates the box for each card in the grid to sit in.
     * Also creates the plus and minus buttons to add the cards to the deck object.
     * @param card
     * @param updatedDeckHolder
     * @param deckScroll
     * @return cardVBox
     */
    private VBox createCardBox(Card card, Deck[] updatedDeckHolder, ScrollPane deckScroll) {
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
            updateEditorCardList((GridPane) deckScroll.getContent(), CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json"), updatedDeckHolder);
        });

        minusButton.setOnAction(e -> {
            if (cardWithQuantity.getQuantity() > 0) {
                cardWithQuantity.decrementQuantity();
                quantityLabel.setText(String.valueOf(cardWithQuantity.getQuantity()));
                updateCardInDeck(updatedDeckHolder[0], cardWithQuantity);
            }
            updateEditorCardList((GridPane) deckScroll.getContent(), CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json"), updatedDeckHolder);
        });

        cardInfoHBox.getChildren().addAll(minusButton, cardLabel, plusButton);
        cardVBox.getChildren().addAll(cardImageView, cardInfoHBox, quantityLabel);
        
        return cardVBox;
    }
    
    /**
     * @author Adam Pierce
     * Updates the deck when card quantity is changed
     * @param deck
     * @param cardWithQuantity 
     */
    private void updateCardInDeck(Deck deck, CardWithQuantity cardWithQuantity) {
        // Remove the card with the same ID if it exists.
        deck.getCards().removeIf(cwq -> cwq.getCard().getCardID() == cardWithQuantity.getCard().getCardID());

        // If the card has a quantity greater than 0, then add it to the deck.
        if (cardWithQuantity.getQuantity() > 0) {
            deck.getCards().add(cardWithQuantity);
        }
    }
    
    /**
     * @author Adam Pierce
     * Creates a button to save the current status of the deck in the editor
     * @param selectedDeck
     * @param updatedDeckHolder
     * @param deckScroll
     * @return saveDeckBUtton
     */
    private Button createSaveDeckButton(Deck selectedDeck, Deck[] updatedDeckHolder, ScrollPane deckScroll) {
        Button saveDeckButton = new Button("Save Deck");
        saveDeckButton.setOnAction(e -> saveDeck(selectedDeck, updatedDeckHolder, deckScroll));
        
        return saveDeckButton;
    }
    
    /**
     * @author Adam Pierce
     * Saves the Deck.
     * Creates a popup window while the deck is being saved to make sure the user doesn't close the window before the save is complete.
     * @param selectedDeck
     * @param updatedDeckHolder
     * @param deckScroll 
     */
    private void saveDeck(Deck selectedDeck, Deck[] updatedDeckHolder, ScrollPane deckScroll) {
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
        updateEditorCardList((GridPane) deckScroll.getContent(), CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json"), updatedDeckHolder);
    }
    
    
    // New List creation
    /**
     * @author Sarah Haddin
     * creates a single 1xN grid to create a list of all cards within the deck
     * @param deckEditStage
     * @return scrollPane
     */
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
    
    /**
     * @author Sarah Haddin
     * Changes the height and width of the planes to fit within the window
     * @param deckEditStage
     * @param scrollPane 
     */
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
    
    /**
     * @author Adam Pierce, Lake Sessions, and Sarah Haddin
     * adds in a new HBox for each new card added to the deck
     * @param card
     * @param updatedDeckHolder
     * @return cardVBox
     */
    private HBox createList(Card card, Deck[] updatedDeckHolder) {
        HBox cardVBox = new HBox(2);
        cardVBox.setAlignment(Pos.CENTER_LEFT);

        HBox cardInfoHBox = new HBox(1);
        cardInfoHBox.setAlignment(Pos.CENTER_LEFT);

        var cardLabel = new Label(card.getName());

        CardWithQuantity cardWithQuantity = updatedDeckHolder[0].getCards().stream()
                .filter(cwq -> cwq.getCard().getCardID() == card.getCardID())
                .findFirst()
                .orElse(new CardWithQuantity(card, 0));

        LquantityLabel = new Label(String.valueOf(cardWithQuantity.getQuantity()));

        ExecutorService executor = Executors.newCachedThreadPool();
        final int taskCount = 5; // Number of parallel tasks
        CountDownLatch latch = new CountDownLatch(taskCount);
        List<Future<String>> colorFutures = new ArrayList<>();

        // Submit tasks to fetch colors for each card in parallel
        colorFutures.add(executor.submit(() -> {
            try {
                return Card.getCardFromID(463840).getColors(); // blue
            } finally {
                latch.countDown();
            }
        }));
        colorFutures.add(executor.submit(() -> {
            try {
                return Card.getCardFromID(460952).getColors(); // white
            } finally {
                latch.countDown();
            }
        }));
        colorFutures.add(executor.submit(() -> {
            try {
                return Card.getCardFromID(461092).getColors(); // green
            } finally {
                latch.countDown();
            }
        }));
        colorFutures.add(executor.submit(() -> {
            try {
                return Card.getCardFromID(461073).getColors(); // red
            } finally {
                latch.countDown();
            }
        }));
        colorFutures.add(executor.submit(() -> {
            try {
                return Card.getCardFromID(461021).getColors(); // black
            } finally {
                latch.countDown();
            }
        }));

        // Wait for all tasks to complete
        new Thread(() -> {
            try {
                latch.await();
                String blueColor = colorFutures.get(0).get();
                String whiteColor = colorFutures.get(1).get();
                String greenColor = colorFutures.get(2).get();
                String redColor = colorFutures.get(3).get();
                String blackColor = colorFutures.get(4).get();

                // Processing after all colors are fetched
                Platform.runLater(() -> {
                    Image cardImage;
                    ImageView cardImageView;
                    String cardColor = card.getColors();

                    if (cardColor.equals("")) {
                        cardImage = new Image("file:" + "src/mtg_data/image_data/C.jpg"); // colorless
                    } else if (cardColor.equals(redColor)) {
                        cardImage = new Image("file:" + "src/mtg_data/image_data/R.jpg"); // red
                    } else if (cardColor.equals(blueColor)) {
                        cardImage = new Image("file:" + "src/mtg_data/image_data/U.jpg"); // blue
                    } else if (cardColor.equals(greenColor)) {
                        cardImage = new Image("file:" + "src/mtg_data/image_data/G.jpg"); // green
                    } else if (cardColor.equals(whiteColor)) {
                        cardImage = new Image("file:" + "src/mtg_data/image_data/W.jpg"); // white
                    } else if (cardColor.equals(blackColor)) {
                        cardImage = new Image("file:" + "src/mtg_data/image_data/B.jpg"); // black
                    } else {
                        cardImage = new Image("file:" + "src/mtg_data/image_data/M.jpg"); // multicolor
                    }

                    cardImageView = new ImageView(cardImage);
                    cardImageView.setFitHeight(20);
                    cardImageView.setFitWidth(20);

                    cardInfoHBox.getChildren().addAll(cardImageView);
                });
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }).start();

    executor.shutdown();
    
    cardInfoHBox.getChildren().addAll(cardLabel);
    cardVBox.getChildren().addAll(cardInfoHBox, LquantityLabel);
    
    return cardVBox;
}
    
    /**
     * @author Sarah Haddin
     * 
     * @param cardGrid
     * @param allCards
     * @param updatedDeckHolder 
     */
    private void updateEditorCardList(GridPane cardGrid, List<Card> allCards, Deck[] updatedDeckHolder) {
        int col = 0;
        int row = 0;
        cardGrid.getChildren().clear();
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
