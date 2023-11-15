/**
 * File originally created and commented by Adam Pierce
 * 
 */
package T7DeckBuilder.DeckPackage;

import T7DeckBuilder.CardPackage.CardWithQuantity;
import T7DeckBuilder.CardPackage.Card;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Creates a Deck object that can hold a set max of cards
 * <p>
 * Each deck is created with a unique hash number.
 * Each deck stores a list containing CardWithQuantity objects.
 * Each deck is given a String name so the User can identify the decks
 * </p>
 * 
 */
public class Deck {
    /**
     * Uses MAX_SIZE to set the max numbers of card for each deck
     */
    private static final int MAX_SIZE = 100;
    
    /**
     * Stores the unique hash number the deck
     */
    private final long hashID;
    
    /**
     * Stores the CardWithQuantity objects in a list
     */
    private final List<CardWithQuantity> cardsWithQuantities;
    
    /**
     * Stores the name of the deck object
     */
    private String name; // Deck name
    /**
     * Stores the colorDistriburion of the deck object
     */
    private int[] colorDistribution = new int[7];// 5 colors, colorless, and multicolored are counted here 
    /**
     * Constructor for the deck object using a name
     * @param name 
     */
    public Deck(String name) {
        this.cardsWithQuantities = new ArrayList<>();
        this.hashID = generateHashID();
        this.name = name;
    }
    
    /**
     * Constructor for a deck object using a name and hash ID
     * @param name
     * @param hashID 
     */
     public Deck(String name, long hashID) {
        this.cardsWithQuantities = new ArrayList<>();
        this.hashID = hashID;
        this.name = name;
    }
    
    /**
     * Uses the system time to generate a new hash ID for the deck object
     * @return System.currentTimeMillis() + new Random().nextInt(1000);
     */
    public static long generateNewHashID() {
        return System.currentTimeMillis() + new Random().nextInt(1000);
    }
    
    /**
     * Uses the system time to generate a hash ID for the deck object when it is created
     * @return System.currentTimeMillis() + new Random().nextInt(1000);
     */
    private long generateHashID() {
        return System.currentTimeMillis() + new Random().nextInt(1000);
    }

    /**
     * Returns the hash ID of the deck object
     * @return hashID
     */
    public long getHashID() {
        return hashID;
    }

    /**
     * returns a cardWithQuantities object from the list
     * @return cardsWithQuantities
     */
    public List<CardWithQuantity> getCards() {
        return cardsWithQuantities;
    }

    /**
     * Returns the name of the deck object
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the deck object using the name parameter
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks to see if a card is allowed to be added to the list of card objects.
     * Succeeds if the list is under the max size given.
     * Fails if the list is equal to the max size already.
     * @param card
     * @param quantity
     * @return true or false
     */
    public boolean addCard(Card card, int quantity) {
        if (cardsWithQuantities.size() < MAX_SIZE) {
            cardsWithQuantities.add(new CardWithQuantity(card, quantity));
            return true;
        }
        return false;
    }
    
    /**
     * Updates the quantity of the card in list
     * @param newCardsWithQuantities 
     */
    public void setCards(List<CardWithQuantity> newCardsWithQuantities) {
        this.cardsWithQuantities.clear();
        this.cardsWithQuantities.addAll(newCardsWithQuantities);
    }
    
    /**
     * Checks to make sure the card object removed from the list matches the card object in the parameter
     * @param card
     * @return 
     */
    public boolean removeCard(Card card) {
        return cardsWithQuantities.removeIf(c -> c.getCard().equals(card));
    }
    
    /**
     * Returns the file path of JSON file generated from the deck object
     * @return Deck Object file path
     */
    public String getJsonFilePath() {
        // Assuming the naming convention for the file is 'deck_<hashID>.json' in 'src/mtg_data'
        return Paths.get("src", "mtg_data", "deck_" + this.hashID + ".json").toString();
    }
    
    /**
     * Returns a deck object from the JSON file generated from the deck object at the file path in the parameter
     * @param filePath
     * @return loadedDeck
     */
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
    
    /**
     * Saves the deck object to a JSON file using the name and hash ID as identifiers.
     * First load the existing JSON data from the file and then loop through each cardWithQuantity.
     * If the cardID is found in the existing data and the quantity has changed, we update it.
     * If it's not found, we add it as a new entry.
     */
    public void saveToJson() {
        JSONObject deckJson = new JSONObject();
        deckJson.put("hashID", hashID);
        deckJson.put("name", name);

        String filePath = Paths.get("src", "mtg_data", "deck_" + hashID + ".json").toString();

        // Load the existing deck from the JSON file
        JSONObject existingDeckJson = null;
        try {
            existingDeckJson = new JSONObject(new String(Files.readAllBytes(Paths.get(filePath))));
        } catch (Exception e) {
            // Handle exception (maybe the file doesn't exist or there's a read error)
            existingDeckJson = new JSONObject();  // Initialize an empty JSON object if there's an error
        }

        // Get the existing cards array from the loaded JSON data
        JSONArray existingCardsJson = existingDeckJson.optJSONArray("cards");
        if (existingCardsJson == null) {
            existingCardsJson = new JSONArray();
        }

        // Convert the existing cards JSON array to a Map for easier lookup
        Map<Integer, JSONObject> existingCardMap = new HashMap<>();
        for (int i = 0; i < existingCardsJson.length(); i++) {
            JSONObject cardJson = existingCardsJson.getJSONObject(i);
            existingCardMap.put(cardJson.getInt("cardID"), cardJson);
        }

        JSONArray cardsJson = new JSONArray();
        for (CardWithQuantity cardWithQuantity : cardsWithQuantities) {
            int cardID = cardWithQuantity.getCard().getCardID();

            // If card ID exists in the map, update its quantity
            if (existingCardMap.containsKey(cardID)) {
                existingCardMap.get(cardID).put("quantity", cardWithQuantity.getQuantity());
                cardsJson.put(existingCardMap.get(cardID));
            } else {
                // Card ID doesn't exist, so create a new entry
                JSONObject newCardJson = new JSONObject();
                newCardJson.put("cardID", cardID);
                newCardJson.put("quantity", cardWithQuantity.getQuantity());
                cardsJson.put(newCardJson);
            }
        }

        deckJson.put("cards", cardsJson);

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(deckJson.toString(4)); // Indented with 4 spaces for readability
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
 * Extracts the total mana cost for all the cards in the given deck.
 *
 * @param deck The deck to analyze.
 * @return An array of size 8 where each index (0-7) corresponds to mana costs
 * 1-8, respectively. The last index includes costs of 8 or more.
 */
    public int[] extractManaCosts(Deck deck) {
        int[] manaCostArray = new int[8];

        for (CardWithQuantity cardWithQuantity : deck.getCards()) {
            int cardManaCost = cardWithQuantity.getCard().getTotalMana();
            int quantity = cardWithQuantity.getQuantity();

            if (cardManaCost >= 8) {
                manaCostArray[7] += quantity;  // Add to the last index for mana costs 8 or more.
            } else if (cardManaCost >= 1) {
                manaCostArray[cardManaCost - 1] += quantity; // Indexing starts from 0, so we subtract 1.
            }
        }

        return manaCostArray;
    }
    public void setColorDistribution(Deck deck) {
        int[] colorDistributionArray = new int[7];
        
        Card blueCard = Card.getCardFromID(463840);
        String blueColor= blueCard.getColors();
        Card whiteCard = Card.getCardFromID(460952);
        String whiteColor= whiteCard.getColors();
        Card greenCard = Card.getCardFromID(461092);
        String greenColor= greenCard.getColors();
        Card redCard = Card.getCardFromID(461073);
        String redColor= redCard.getColors();
        Card blackCard = Card.getCardFromID(461021);
        String blackColor= blackCard.getColors();
        

        for (CardWithQuantity cardWithQuantity : deck.getCards()) {
            String cardColor = cardWithQuantity.getCard().getColors();
            int quantity = cardWithQuantity.getQuantity();
            //System.out.println(cardColor);
            if (cardColor== "") {
                colorDistribution[0] += quantity;  // Add to the last index for mana costs 8 or more.
            } else if (cardColor.equals(redColor)) {
                colorDistribution[1] += quantity;  // Add to the last index for mana costs 8 or more.
            } else if (cardColor.equals(blueColor)) {
                colorDistribution[2] += quantity;  // Add to the last index for mana costs 8 or more.
            } else if (cardColor.equals(greenColor)) {
                colorDistribution[3] += quantity;  // Add to the last index for mana costs 8 or more.
            } else if (cardColor.equals(whiteColor)) {
                colorDistribution[4] += quantity;  // Add to the last index for mana costs 8 or more.
            } else if (cardColor.equals(blackColor)) {
                colorDistribution[5] += quantity;  // Add to the last index for mana costs 8 or more.
            } else  {
                colorDistribution[6] += quantity;  // Add to the last index for mana costs 8 or more.
            }
        }
    }
    
    public int[] getColorDistribution(Deck deck) {
    return colorDistribution;
    }

}

    
