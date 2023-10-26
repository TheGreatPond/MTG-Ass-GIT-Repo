
package cs321.magic.deck.builder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CardLoader {
    public static List<Card> loadCardsFromJson(String jsonFilePath) {
        List<Card> cards = new ArrayList<>();

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONArray jsonArray = new JSONArray(jsonContent);

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
