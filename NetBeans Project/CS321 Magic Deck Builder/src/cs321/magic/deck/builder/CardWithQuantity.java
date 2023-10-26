
package cs321.magic.deck.builder;

/**
 *
 * @author Adam
 */
public class CardWithQuantity {
    private Card card;
    private int quantity;

    public CardWithQuantity(Card card, int quantity) {
    this.card = card;
    this.quantity = quantity;
}


    public Card getCard() {
        return card;
    }

    public int getQuantity() {
        return quantity;
    }

    public void incrementQuantity() {
        if (this.quantity < 4) {
            this.quantity++;
        }
    }

    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }
}
