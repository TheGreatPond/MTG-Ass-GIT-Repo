/**
 * File originally created and commented by Adam Pierce
 * 
 */
package T7DeckBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class contains utility functions needed for loading and saving data to the JSON file
 * 
 */
public class DeckLoader {
    private static final String DECKS_DIRECTORY = "src/mtg_data/";
    private final List<Deck> loadedDecks;

    /**
     * Constructor for the DeckLoader class.
     * <p>
     * Creates a new array to store which decks have been loaded
     * Calls a function to load the decks saved in the provided directory
     * </p>
     */
    public DeckLoader() {
        loadedDecks = new ArrayList<>();
        loadDecksFromDirectory();
    }

    /**
     * Uses file stream to load all off the JSON files in the mtg_data folder that store deck data.
     * <p>
     * Calls the loadDeck(File file) function to load each deck found in directory.
     * Skips any decks that already exits and updates the list with any new decks that were created.
     * </p>
     */
    private void loadDecksFromDirectory() {
        Set<String> existingDeckFileNames = loadedDecks.stream().map(deck -> "deck_" + deck.getHashID()).collect(Collectors.toSet());

        try {
            Files.newDirectoryStream(Paths.get(DECKS_DIRECTORY),
                path -> path.toString().endsWith(".json") && !path.getFileName().toString().equals("WAR_cards.json"))
                .forEach(filePath -> {
                    // Extract the deck name from the file path (without the .json extension)
                    String deckName = filePath.getFileName().toString().replace(".json", "");

                    // Only load the deck if it's not already in the loadedDecks list
                    if (!existingDeckFileNames.contains(deckName)) {
                        loadDeck(filePath.toFile());
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    

    /**
     * Uses the file parameter to create a JSON object 
     * @param file 
     */
    private void loadDeck(File file) {
        try {
            String jsonContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonContent);
            Deck deck = parseDeck(jsonObject);
            loadedDecks.add(deck);
        } catch (JSONException je) {
            System.err.println("Error parsing JSON from file: " + file.getName());
            je.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Analyzes the JSON object in the parameter to get the ID and quantity of the cards stored in the JSON file.
     * @param jsonObject
     * @return deck
     */
    private Deck parseDeck(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        long hashID = jsonObject.getLong("hashID");

        Deck deck = new Deck(name, hashID);
        JSONArray cardsArray = jsonObject.getJSONArray("cards");
        for (int i = 0; i < cardsArray.length(); i++) {
            JSONObject cardObj = cardsArray.getJSONObject(i);
            int cardID = cardObj.getInt("cardID");
            int quantity = cardObj.getInt("quantity");

            // Assuming you have a way to get a Card object from cardID
            Card card = Card.getCardFromID(cardID);
            if (card != null) {
                deck.addCard(card, quantity);
            }
        }
        return deck;
    }

    /**
     * Returns a copy of the current list of deck objects to prevent external modifications
     * @return ArrayList(loadedDecks)
     */
    public List<Deck> getDecks() {
        loadDecksFromDirectory();
        return new ArrayList<>(loadedDecks);
    }

    /**
     * Filters and returns the deck that matches the name in the parameter
     * @param name
     * @return deck object
     */
    public Deck getDeckByName(String name) {
        loadDecksFromDirectory();
        return loadedDecks.stream()
                          .filter(deck -> deck.getName().equals(name))
                          .findFirst()
                          .orElse(null);
    }
    
    /**
     * Filters and returns the deck that matches the hash ID in the parameter
     * @param hashID
     * @return deck object
     */
     public Deck getDeckByHashID(long hashID) {
        loadDecksFromDirectory();
        return loadedDecks.stream()
                          .filter(deck -> deck.getHashID() == hashID)
                          .findFirst()
                          .orElse(null);
    }
    
    /**
     * Calls the saveToJson() function from the deck class to update the JSON files with the applied changes
     * @param deck 
     */
    public void saveDeck(Deck deck) {
        deck.saveToJson(); // Let the Deck instance handle its own JSON serialization
    }

    /**
     * Removes the deck object from the list and the directory
     * @param deck 
     */
    public void deleteDeck(Deck deck) {

        String filename = "deck_" + deck.getHashID() + ".json";
        File file = new File(DECKS_DIRECTORY + filename);

        if (file.exists()) {
            file.delete();
            loadedDecks.remove(deck);
        }
    }
}
