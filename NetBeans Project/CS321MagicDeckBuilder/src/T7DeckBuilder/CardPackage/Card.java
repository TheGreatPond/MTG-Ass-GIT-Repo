/**
 * File originally created and commented by Adam Pierce
 * 
 */

package T7DeckBuilder.CardPackage;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Card Class stores cardID, name, card type, mana cost, total Mana cost, description, and file path to the image file
 *
 */
public class Card {
    /**
     * Creates a hash map to store the card objects
     */
    private static Map<Integer, Card> allCards = new HashMap<>(); // Map to store all card objects
    
    /**
     * uses the multiverse ID of each card as an unique identifier
     */
    private final int cardID;
    
    /**
     * name of card
     */
    private final String name;
    
    /**
     * color identity of the card symbolized by the first letter of the color
     */
    private final String colorIdentity;
            
    /**
     * type of the card, e.g. creature, spell, land 
     */
    private final String cardType;
    
    /**
     * mana cost of the card, e.g. 1UU, 2R
     */
    private final String manaCost;
    
    /**
     * total mana of the card or 'cmc' for converted mana cost
     */
    private final int totalMana;
    
    /**
     * the card description
     */
    private final String description;
    
    /**
     * stores the location of the image file for the card
     */
    private final String imageFile;
    
    /**
     * This function builds the card parameters using the object imported by the jsonjava library
     * @param jsonObject 
     */
    public Card(JSONObject jsonObject) {
        this.cardID = jsonObject.optInt("multiverseid", 0);
        this.name = jsonObject.getString("name");
        this.colorIdentity = jsonObject.optString("colorIdentity", "");
        this.cardType = jsonObject.optString("type", "");
        this.manaCost = jsonObject.optString("manaCost", "");
        this.totalMana = jsonObject.optInt("cmc", 0);
        this.description = jsonObject.optString("text", "");
        this.imageFile = "src/mtg_data/image_data/" + cardID + ".jpg";

        allCards.put(cardID, this); // Add card to the map
    }

    /**
     * Static method to return a card by it's ID
     * @param cardID
     * @return allCards.get(cardID)
     */
    public static Card getCardFromID(int cardID) {
        return allCards.get(cardID);
        
    }
    /**
     * returns the card ID
     * @return cardID
     */
    public int getCardID() {
        return cardID;
    }
    
    /**
     * Returns the name of the card
     * @return name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the color identity of the card
     * @return colorIdentity
     */
    public String getColorIdentity(){
        return colorIdentity;
    }
    
    /**
     * Returns the card type
     * @return cardType
     */
    public String getCardType() {
        return cardType;
    }
    
    /**
     * Returns the Mana cost of the card
     * @return manaCost
     */
    public String getManaCost() {
        return manaCost;
    }
    
    /**
     * returns the total mana of the card
     * @return totalMana
     */
    public int getTotalMana() {
        return totalMana;
    }
    
    /**
     * returns the description of the card
     * @return description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * returns the location of the image file of the card
     * @return imageFile
     */
    public String getImageFile() {
        return imageFile;
    }
    
    
}

