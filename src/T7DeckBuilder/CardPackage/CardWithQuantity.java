/**
 * File originally created and commented by Adam Pierce
 * 
 */
package T7DeckBuilder.CardPackage;

/**
 * This class keeps track of the of the quantity of each card in the decks
 */
public class CardWithQuantity {
    private Card card;
    private int quantity;
    
    /**
     * Constructor for the CardWithQuantity class
     * @param card
     * @param quantity 
     */
    public CardWithQuantity(Card card, int quantity) {
    this.card = card;
    this.quantity = quantity;
}

    /**
     * Returns the card object
     * @return card
     */
    public Card getCard() {
        return card;
    }
    
    /**
     * Returns the quantity of the card object
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }
    
    /**
     * Increases the quantity of the card being stored by 1 as long as it is less than 3
     */
    public void incrementQuantity() {
        if (this.quantity < 4) {
            this.quantity++;
        }
    }
    
    /**
     * Decrements the quantity of the card by 1 as long as the quantity is 1 or more
     */
    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }
}
