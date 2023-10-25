
package cs321.magic.deck.builder;

/**
 *
 * @author Michael
 */
public class Deck {
    
    // this should actually be a vector!
    private Card[] cardsInDeck;
    private String name;
    
    public Deck()
    {
        name = "default";
    }
    
    public Deck(String n)
    {
        name = n;
        // getDeck();
    }
    
    public void saveDeck()
    {
        // this should write the deck information to a file
    }
    
    public void getDeck()
    {
        // retrieves decks that are already made from a file
        // may want the parameter of name?
    }
    
    public boolean addCard()
    {
     return false;//default return value   
    }
}
