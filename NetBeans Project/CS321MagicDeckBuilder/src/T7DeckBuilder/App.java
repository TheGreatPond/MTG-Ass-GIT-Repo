
package T7DeckBuilder;

import T7DeckBuilder.DeckPackage.*;
import T7DeckBuilder.CardPackage.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

public class App extends Application {
    private Stage primaryStage; //main menu
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
        primaryStage.setTitle("MTG Deck Builder");

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
        grid.add(cardListButton, 1, 1);

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
        VBox analyzeLayout = new VBox(10);
        analyzeLayout.setAlignment(Pos.CENTER);
        Scene analyzeScene = new Scene(analyzeLayout, 500, 400);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.TOP_RIGHT);

        Button exitButton = new Button("Exit");
        Button backButton = new Button("Back");
        Button analyzeDeckButton = new Button("Analyze");

        ListView<String> deckListView = new ListView<>();

        // Ensure deckLoader is properly instantiated and has loaded decks
        deckLoader = new DeckLoader();

        // Populate the ListView with loaded decks
        populateDeckListView(deckListView);

        exitButton.setOnAction(e -> System.exit(0));
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        analyzeDeckButton.setOnAction(e -> {
            String selectedDeckName = deckListView.getSelectionModel().getSelectedItem();
            if (selectedDeckName != null && !selectedDeckName.equals("No decks available")) {
                Deck toAnalyze = deckLoader.getDeckByName(selectedDeckName);
                if (toAnalyze != null) {
                    // Implement a function to analyze the selected deck and display a bar graph
                    analyzeManaCostCurve(toAnalyze);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The selected deck was not found!");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a deck to analyze!");
                alert.showAndWait();
            }
        });

        buttonBox.getChildren().addAll(analyzeDeckButton, backButton, exitButton);
        analyzeLayout.getChildren().addAll(buttonBox, deckListView);
        primaryStage.setScene(analyzeScene);
    }
    
    private void analyzeManaCostCurve(Deck selectedDeck) {
        //loads card data into memory
        CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json");
        
        
        // Load the selected deck from the JSON file to get the updated quantities
        String jsonFilePath = selectedDeck.getJsonFilePath();
        Deck updatedDeck = Deck.loadFromJson(jsonFilePath);
        

        // If the loaded deck is null, use the provided deck object
        if (updatedDeck == null) {
            updatedDeck = selectedDeck;
        }
       
        
        int[] manaCostArray = updatedDeck.extractManaCosts(updatedDeck);
        
        
        Map<String, Integer> manaCostCurveData = new HashMap<>();
        manaCostCurveData.put("1", manaCostArray[0]);
        manaCostCurveData.put("2", manaCostArray[1]);
        manaCostCurveData.put("3", manaCostArray[2]);
        manaCostCurveData.put("4", manaCostArray[3]);
        manaCostCurveData.put("5", manaCostArray[4]);
        manaCostCurveData.put("6", manaCostArray[5]);
        manaCostCurveData.put("7", manaCostArray[6]);
        manaCostCurveData.put("8+", manaCostArray[7]);

        // Set up the chart axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Mana Cost");
        yAxis.setLabel("Number of Cards");
        
        // Set the categories for the X-axis in the desired order
        xAxis.setCategories(FXCollections.<String>observableArrayList("1", "2", "3", "4", "5", "6", "7", "8+"));


        // Create the bar chart
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Cost Curve of MTG Deck");

        // Add data to the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(selectedDeck.getName());  // Use the deck's name as the series name

        for (Map.Entry<String, Integer> entry : manaCostCurveData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().addAll(series);

        // Display the scene
        Scene scene = new Scene(barChart, 800, 600);
        Stage newStage = new Stage();
        newStage.setTitle("MTG Deck Cost Curve");
        newStage.setScene(scene);
        newStage.show();
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
        deckLoader = new DeckLoader();

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


        layout.getChildren().addAll(anchorPane, saveDeckButton);
        Scene deckEditScene = new Scene(layout, 1280, 900);
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
            cardViewerScene = new Scene(cardViewerLayout, 1280, 720);

            // Load card data from WAR_cards.json
            List<Card> cards = CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json"); // Replace with your logic

            // Create a ListView to display the list of cards
            ListView<String> cardListView = new ListView<>();
            cardListView.prefHeightProperty().bind(cardViewerScene.heightProperty().multiply(0.9));

            // Populate the cardListView with card names
            for (Card card : cards) {
                cardListView.getItems().add(card.getName());
            }

            // Create a TextArea to display card information
            TextArea cardInfoTextArea = new TextArea();
            cardInfoTextArea.setEditable(false);
            cardInfoTextArea.setWrapText(true);
            cardInfoTextArea.prefWidthProperty().bind(cardViewerScene.widthProperty().multiply(0.9));
            cardInfoTextArea.prefHeightProperty().bind(cardViewerScene.heightProperty().multiply(0.9));

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
                                              "Color: " + selectedCard.getColors() + "\n" +
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
