package cs321.magic.deck.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Deck {
    private static final int MAX_SIZE = 100;
    private final long hashID; // Unique identifier for the deck
    private final List<CardWithQuantity> cardsWithQuantities;
    private String name; // Deck name

    public Deck(String name) {
        this.cardsWithQuantities = new ArrayList<>();
        this.hashID = generateHashID();
        this.name = name;
    }
    
     public Deck(String name, long hashID) {
        this.cardsWithQuantities = new ArrayList<>();
        this.hashID = hashID;
        this.name = name;
    }
    
        // Static method for generating a new hash ID
    public static long generateNewHashID() {
        return System.currentTimeMillis() + new Random().nextInt(1000);
    }
     
    private long generateHashID() {
        return System.currentTimeMillis() + new Random().nextInt(1000);
    }

    public long getHashID() {
        return hashID;
    }

    public List<CardWithQuantity> getCards() {
        return cardsWithQuantities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean addCard(Card card, int quantity) {
        if (cardsWithQuantities.size() < MAX_SIZE) {
            cardsWithQuantities.add(new CardWithQuantity(card, quantity));
            return true;
        }
        return false;
    }
    
    public void setCards(List<CardWithQuantity> newCardsWithQuantities) {
        this.cardsWithQuantities.clear();
        this.cardsWithQuantities.addAll(newCardsWithQuantities);
    }
    
    public boolean removeCard(Card card) {
        return cardsWithQuantities.removeIf(c -> c.getCard().equals(card));
    }
    
    public String getJsonFilePath() {
        // Assuming the naming convention for the file is 'deck_<hashID>.json' in 'src/mtg_data'
        return Paths.get("src", "mtg_data", "deck_" + this.hashID + ".json").toString();
    }
    
    
    public static Deck loadFromJson(String filePath) {
        try {
            // Read JSON file content
            String content = new String(Files.readAllBytes(Path.of(filePath)));
            JSONObject deckJson = new JSONObject(content);

            // Extract deck information
            long hashID = deckJson.getLong("hashID");
            String name = deckJson.getString("name");
            Deck loadedDeck = new Deck(name, hashID);

            // Extract cards and quantities
            JSONArray cardsJson = deckJson.getJSONArray("cards");
            for (int i = 0; i < cardsJson.length(); i++) {
                JSONObject cardJson = cardsJson.getJSONObject(i);
                int cardID = cardJson.getInt("cardID");
                int quantity = cardJson.getInt("quantity");

                // Use the static method from Card class to get a Card object by its ID
                Card card = Card.getCardFromID(cardID);
                if (card != null) {
                    loadedDeck.addCard(card, quantity);
                }
            }

            return loadedDeck;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void saveToJson() {
        JSONObject deckJson = new JSONObject();
        deckJson.put("hashID", hashID);
        deckJson.put("name", name);

        JSONArray cardsJson = new JSONArray();
        for (CardWithQuantity cardWithQuantity : cardsWithQuantities) {
            JSONObject cardJson = new JSONObject();
            cardJson.put("cardID", cardWithQuantity.getCard().getCardID());
            cardJson.put("quantity", cardWithQuantity.getQuantity());
            cardsJson.put(cardJson);
        }
        deckJson.put("cards", cardsJson);

        String filePath = Paths.get("src", "mtg_data", "deck_" + hashID + ".json").toString();
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(deckJson.toString(4)); // Indented with 4 spaces for readability
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
