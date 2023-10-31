/**
 * File originally created and commented by Adam Pierce
 * 
 */

package T7DeckBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class adds cards from the JSON file to a list using the file path to the JSON object
 * 
 */
public class CardLoader {
    /**
     * Uses the file path of the JSON object to build the card list
     * @param jsonFilePath
     * @return cards
     */
    public static List<Card> loadCardsFromJson(String jsonFilePath) {
        List<Card> cards = new ArrayList<>();

        try {
            /**
             * Reads the JSON file at the given file path 
             */
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            
            /**
             * Creates a JSON array using the content of the JSON file
             */
            JSONArray jsonArray = new JSONArray(jsonContent);
            
            /**
             * loops through the JSON array
             * first creating a JSON object from the array list
             * second creating each card using the imported JSON object
             * third adding each card to the card list
             */
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Card card = new Card(jsonObject);
                cards.add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cards;
    }
}
