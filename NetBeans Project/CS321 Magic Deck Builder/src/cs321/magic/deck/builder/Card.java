package cs321.magic.deck.builder;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Card {
    private static Map<Integer, Card> allCards = new HashMap<>(); // Map to store all card objects

    private final int cardID; // Represents the multiverseid
    private final String name;
    private final String cardType;  // e.g., creature, spell, land
    private final String manaCost;  // e.g., 1UU, 2R
    private final int totalMana;    // Previously named 'cmc'
    private final String description;  // Represents the text
    private final String imageFile;    // Previously named 'imageUrl'
    
    public Card(JSONObject jsonObject) {
        this.cardID = jsonObject.optInt("multiverseid", 0);
        this.name = jsonObject.getString("name");
        this.cardType = jsonObject.optString("type", "");
        this.manaCost = jsonObject.optString("manaCost", "");
        this.totalMana = jsonObject.optInt("cmc", 0);
        this.description = jsonObject.optString("text", "");
        this.imageFile = "src/mtg_data/image_data/" + cardID + ".jpg";

        allCards.put(cardID, this); // Add card to the map
    }

    // Static method to get a Card by its ID
    public static Card getCardFromID(int cardID) {
        return allCards.get(cardID);
        
    }    // Getter methods
    public int getCardID() {
        return cardID;
    }

    public String getName() {
        return name;
    }

    public String getCardType() {
        return cardType;
    }

    public String getManaCost() {
        return manaCost;
    }

    public int getTotalMana() {
        return totalMana;
    }

    public String getDescription() {
        return description;
    }

    public String getImageFile() {
        return imageFile;
    }
}

