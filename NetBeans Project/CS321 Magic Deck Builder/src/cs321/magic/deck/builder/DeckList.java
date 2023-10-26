package cs321.magic.deck.builder;

import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DeckList {
    private final List<Deck> decks;
    private static final String DECKS_DIR = "src/mtg_data/";

    public DeckList() {
        this.decks = new ArrayList<>();
        loadDecks();
    }

    private void loadDecks() {
        File directory = new File(DECKS_DIR);
        File[] files = directory.listFiles((dir, name) -> name.startsWith("deck_") && name.endsWith(".json"));

        if (files == null || files.length == 0) {
            // No decks available. The JavaFX object will need to handle this case.
            return;
        }

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                JSONObject jsonObject = new JSONObject(new String(Files.readAllBytes(Paths.get(file.getPath()))));

                // Extract deck name and initialize the deck with its name
                String deckName = jsonObject.getString("name");
                Deck deck = new Deck(deckName); 

                // Here, you'd also want to reconstruct the deck's cards from the JSON 
                // (I'm assuming this is handled in your Card class or somewhere else).
                // e.g., JSONArray cardIds = jsonObject.getJSONArray("cards");
                // and then add these cardIds to the deck object as needed.

                decks.add(deck);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public boolean isEmpty() {
        return decks.isEmpty();
    }

    public void createDeck(Deck deck) {
        decks.add(deck);
        deck.saveToJson();
    }

    public void deleteDeck(Deck deck) {
        decks.remove(deck);
        File file = new File(DECKS_DIR + "deck_" + deck.getHashID() + ".json");
        if (file.exists()) {
            file.delete();
        }
    }
}
