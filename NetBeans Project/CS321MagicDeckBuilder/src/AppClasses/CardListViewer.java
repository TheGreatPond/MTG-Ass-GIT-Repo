package AppClasses;

import T7DeckBuilder.CardPackage.Card;
import T7DeckBuilder.CardPackage.CardLoader;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Adam
 */
public class CardListViewer{
    private Scene cardViewerScene;
    
    public CardListViewer(){
        
    }
    

    public void showCardViewerScene(Scene mainScene, Stage primaryStage) {
        Stage cardViewerStage = initCardViewerWindow();
        VBox cardViewerLayout = initCardViewerLayout();

        List<Card> cards = loadCardData();

        ListView<String> cardListView = setupCardListView(cards);
        cardViewerLayout.getChildren().add(cardListView);

        HBox cardInfoBox = setupCardInfoView(cards, cardListView);
        cardViewerLayout.getChildren().add(cardInfoBox);

        cardViewerStage.setScene(cardViewerScene);
        cardViewerStage.show();
    }

    private Stage initCardViewerWindow() {
        Stage cardViewerStage = new Stage();
        cardViewerStage.setTitle("Card Viewer");
        return cardViewerStage;
    }

    private VBox initCardViewerLayout() {
        VBox cardViewerLayout = new VBox(10);
        cardViewerLayout.setAlignment(Pos.CENTER);
        cardViewerScene = new Scene(cardViewerLayout, 1280, 720);
        return cardViewerLayout;
    }

    private List<Card> loadCardData() {
        return CardLoader.loadCardsFromJson("src/mtg_data/WAR_cards.json");
    }

    private ListView<String> setupCardListView(List<Card> cards) {
        ListView<String> cardListView = new ListView<>();
        for (Card card : cards) {
            cardListView.getItems().add(card.getName());
        }
        cardListView.prefHeightProperty().bind(cardViewerScene.heightProperty().multiply(0.9));
        return cardListView;
    }

    private HBox setupCardInfoView(List<Card> cards, ListView<String> cardListView) {
        TextArea cardInfoTextArea = new TextArea();
        cardInfoTextArea.setEditable(false);
        cardInfoTextArea.setWrapText(true);
        cardInfoTextArea.prefWidthProperty().bind(cardViewerScene.widthProperty().multiply(0.9));
        cardInfoTextArea.prefHeightProperty().bind(cardViewerScene.heightProperty().multiply(0.9));

        ImageView imageView = new ImageView();
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        cardListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Card selectedCard = findSelectedCard(cards, newValue);
            if (selectedCard != null) {
                updateCardInfo(cardInfoTextArea, imageView, selectedCard);
            }
        });

        HBox cardInfoBox = new HBox(10);
        cardInfoBox.getChildren().addAll(cardInfoTextArea, imageView);
        return cardInfoBox;
    }

    private Card findSelectedCard(List<Card> cards, String cardName) {
        for (Card card : cards) {
            if (card.getName().equals(cardName)) {
                return card;
            }
        }
        return null;
    }

    private void updateCardInfo(TextArea cardInfoTextArea, ImageView imageView, Card selectedCard) {
        cardInfoTextArea.setText("Name: " + selectedCard.getName() + "\n" +
                                 "Color: " + selectedCard.getColors() + "\n" +
                                 "Cost: " + selectedCard.getTotalMana() + "\n" +
                                 "Effect: " + selectedCard.getDescription());
        Image cardImage = new Image("file:" + selectedCard.getImageFile());
        imageView.setImage(cardImage);
    }

}
