
package AppClasses;

import T7DeckBuilder.CardPackage.CardLoader;
import T7DeckBuilder.DeckPackage.Deck;
import T7DeckBuilder.DeckPackage.DeckLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @authors Adam Pierce, Lake Sessions
 */
public class AnalyzerWindow {

    private final Stage primaryStage;
    private final Scene mainScene;
    private final DeckLoader deckLoader = new DeckLoader();

    /**
     * Creates the primaryStage and mainScene
     * @param mainScene
     * @param primaryStage 
     */
    public AnalyzerWindow(Scene mainScene, Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.mainScene = mainScene;
    }

    /**
     * Initiates the scene and layout
     * @param mainScene
     * @param primaryStage 
     */
    public void showAnalyzeWindow(Scene mainScene, Stage primaryStage) {
        VBox analyzeLayout = createAnalyzeLayout();
        Scene analyzeScene = new Scene(analyzeLayout, 500, 400);
        primaryStage.setScene(analyzeScene);
    }

    /**
     * Creates the list of decks to choose from
     * @return analyzeLayout
     */
    private VBox createAnalyzeLayout() {
        VBox analyzeLayout = new VBox(10);
        analyzeLayout.setAlignment(Pos.CENTER);

        HBox buttonBox = createButtonBox(analyzeLayout);
        ListView<String> deckListView = createDeckListView();

        analyzeLayout.getChildren().addAll(buttonBox, deckListView);
        return analyzeLayout;
    }

    /**
     * Creates the buttons for interacting with the window
     * @param analyzeLayout
     * @return buttonBox
     */
    private HBox createButtonBox(VBox analyzeLayout) {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.TOP_RIGHT);

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> primaryStage.setScene(mainScene));

        Button analyzeDeckButton = new Button("Mana Curve");
        analyzeDeckButton.setOnAction(e -> analyzeSelectedDeck(analyzeLayout));
        
        Button pieChartDeckButton = new Button("PieChart");
        pieChartDeckButton.setOnAction(e -> pieChartSelectedDeck(analyzeLayout));

        buttonBox.getChildren().addAll(analyzeDeckButton, pieChartDeckButton, backButton, exitButton);
        return buttonBox;
    }

    /**
     * Creates the list of decks
     * @return deckListView
     */
    private ListView<String> createDeckListView() {
        ListView<String> deckListView = new ListView<>();
        populateDeckListView(deckListView);
        return deckListView;
    }

    /**
     * Populates the deck list with the current decks in the deckLoader
     * @param deckListView 
     */
    private void populateDeckListView(ListView<String> deckListView) {
        // Populate the ListView with loaded decks
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
     * Checks to see if the currently selected deck is valid to be analyzed for the color chart
     * @param analyzeLayout 
     */
    private void pieChartSelectedDeck(VBox analyzeLayout) {
        ListView<String> deckListView = (ListView<String>) analyzeLayout.getChildren().get(1); // Assuming deckListView is the second child
        String selectedDeckName = deckListView.getSelectionModel().getSelectedItem();
        if (selectedDeckName != null && !selectedDeckName.equals("No decks available")) {
            Deck toAnalyze = deckLoader.getDeckByName(selectedDeckName);
            if (toAnalyze != null) {
                analyzePieChart(toAnalyze);
            } else {
                showAlert(Alert.AlertType.ERROR, "The selected deck was not found!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a deck to analyze!");
        }
    }
    
    /**
     * Checks to see if the currently selected deck is valid to be analyzed for the mana cost chart
     * @param analyzeLayout 
     */
    private void analyzeSelectedDeck(VBox analyzeLayout) {
        ListView<String> deckListView = (ListView<String>) analyzeLayout.getChildren().get(1); // Assuming deckListView is the second child
        String selectedDeckName = deckListView.getSelectionModel().getSelectedItem();
        if (selectedDeckName != null && !selectedDeckName.equals("No decks available")) {
            Deck toAnalyze = deckLoader.getDeckByName(selectedDeckName);
            if (toAnalyze != null) {
                analyzeManaCostCurve(toAnalyze);
            } else {
                showAlert(Alert.AlertType.ERROR, "The selected deck was not found!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select a deck to analyze!");
        }
    }

    /**
     * Creates an alert depending on the message in the argument
     * @param alertType
     * @param message 
     */
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType, message);
        alert.showAndWait();
    }

    /**
     * Loads the cards from the deck and checks how much mana they cost
     * @param selectedDeck 
     */
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
        scene.getStylesheets().add("/styles/mana_style.css");
        Stage newStage = new Stage();
        newStage.setTitle("MTG Deck Cost Curve");
        newStage.setScene(scene);
        newStage.show();
    }
    
    /**
     * Loads data from deck to see what color each card is.
     * @param selectedDeck 
     */
    private void analyzePieChart(Deck selectedDeck) {
        //loads card data into memory
        CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json");
        
        
        // Load the selected deck from the JSON file to get the updated quantities
        String jsonFilePath = selectedDeck.getJsonFilePath();
        Deck updatedDeck = Deck.loadFromJson(jsonFilePath);
        

        // If the loaded deck is null, use the provided deck object
        if (updatedDeck == null) {
            updatedDeck = selectedDeck;
        }
       
        updatedDeck.setColorDistribution(updatedDeck);
        int[] colorDistribuitionArray = updatedDeck.getColorDistribution(updatedDeck);
                
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Colorless", colorDistribuitionArray[0]),
                new PieChart.Data("Red", colorDistribuitionArray[1]),
                new PieChart.Data("Blue", colorDistribuitionArray[2]),
                new PieChart.Data("Green", colorDistribuitionArray[3]),
                new PieChart.Data("White", colorDistribuitionArray[4]),
                new PieChart.Data("Black", colorDistribuitionArray[5]),
                new PieChart.Data("Multicolor", colorDistribuitionArray[6]));
        
        final PieChart chart = new PieChart(pieChartData);
        
        Scene scene = new Scene(chart, 800, 600);
        scene.getStylesheets().add("/styles/pie_style.css");
        Stage newStage = new Stage();
        newStage.setTitle("MTG Deck Color Ratio");
        newStage.setScene(scene);
        newStage.show();
    }
}