package cs321.magic.deck.builder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeckLoader {
    private static final String DECKS_DIRECTORY = "src/mtg_data/";
    private final List<Deck> loadedDecks;

    public DeckLoader() {
        loadedDecks = new ArrayList<>();
        loadDecksFromDirectory();
    }

    private void loadDecksFromDirectory() {
    try {
        Files.newDirectoryStream(Paths.get(DECKS_DIRECTORY),
            path -> path.toString().endsWith(".json") && !path.getFileName().toString().equals("WAR_cards.json"))
            .forEach(filePath -> loadDeck(filePath.toFile()));
    } catch (Exception e) {
        e.printStackTrace();
    }
}

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

    public List<Deck> getDecks() {
        return new ArrayList<>(loadedDecks); // Return a copy to prevent external modifications
    }

    public Deck getDeckByName(String name) {
        return loadedDecks.stream()
                          .filter(deck -> deck.getName().equals(name))
                          .findFirst()
                          .orElse(null);
    }
    
     public Deck getDeckByHashID(long hashID) {
        return loadedDecks.stream()
                          .filter(deck -> deck.getHashID() == hashID)
                          .findFirst()
                          .orElse(null);
    }
    
    public void saveDeck(Deck deck) {
        deck.saveToJson(); // Let the Deck instance handle its own JSON serialization
    }

    public void deleteDeck(Deck deck) {
    String filename = "deck_" + deck.getHashID() + ".json";
    File file = new File(DECKS_DIRECTORY + filename);

    if (file.exists()) {
        file.delete();
        loadedDecks.remove(deck);
    }
}
}
