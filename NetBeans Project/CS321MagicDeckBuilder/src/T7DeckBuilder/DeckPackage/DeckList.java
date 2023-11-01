/**
 * File originally created and commented by Adam Pierce
 * 
 */
package T7DeckBuilder.DeckPackage;

import org.json.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class is a list that contains all of the created decks
 * <p>
 * Creates a list to contain the deck objects.
 * The JSON files are stored in the mtg_data folder under the source directory.
 * </p>
 * 
 */
public class DeckList {
    private final List<Deck> decks;
    private static final String DECKS_DIR = "src/mtg_data/";

    /**
     * Constructor that creates the deck list object
     */
    public DeckList() {
        this.decks = new ArrayList<>();
        loadDecks();
    }

    /**
     * Searches through the directory for any JSON files of deck objects.
     * Loops through the files and creates a deck object using the information stored in the JSON file.
     * Adds the created deck object to the deck list.
     * 
     */
    private void loadDecks() {
        File directory = new File(DECKS_DIR);
        File[] files = directory.listFiles((dir, name) -> name.startsWith("deck_") && name.endsWith(".json"));

        if (files == null || files.length == 0) {
            // No decks available. The JavaFX object will need to handle this case.
            return;
        }

        for (File file : files) {
            //reads the directory for JSON objects to import data from
            try {
                JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(file.getPath()))));

                // Extract deck name and initialize the deck with its name
                String deckName = jsonObject.getString("name");
                Deck deck = new Deck(deckName); 
                
                //adds deck object to list
                decks.add(deck);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the list of decks
     * @return decks
     */
    public List<Deck> getDecks() {
        return decks;
    }

    /**
     * Checks if the deck list is empty
     * @return 
     */
    public boolean isEmpty() {
        return decks.isEmpty();
    }

    /**
     * Creates the JSON file using the deck object in the parameter
     * @param deck 
     */
    public void createDeck(Deck deck) {
        decks.add(deck);
        deck.saveToJson();
    }

    /**
     * Deletes the deck file from the directory and the list of decks if the deck object's hash ID matches a deck in the directory.
     * @param deck 
     */
    public void deleteDeck(Deck deck) {
        decks.remove(deck);
        File file = new File(DECKS_DIR + "deck_" + deck.getHashID() + ".json");
        if (file.exists()) {
            file.delete();
        }
    }
}
