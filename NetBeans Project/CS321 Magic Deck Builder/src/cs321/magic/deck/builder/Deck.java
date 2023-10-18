
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
    
    public void SaveDeck()
    {
        // this should write the deck information to a file
    }
    
    public void GetDeck()
    {
        // retrieves decks that are already made from a file
        // may want the parameter of name?
    }
    
    public boolean AddCard()
    {
        
    }
}
