
package cs321.magic.deck.builder;

/**
 *
 * @author Michael
 */

// not sure if this should be public actually
public class Card 
{
    // need to figure out what info we need for analysis/general use, as the card image be used for
    // everything else including deck building
    
    // making cost a string as there are many possibilites of what cost could be
    // may be easier to use a code like 3B 2G and then decode that as needed
    private String name, costIncludingColor, type, text, multiverseId;
    private int health, damage, totalCost, cmc; // do we need these?
    // private Image cardImage; we need an image library
    
    // make a dictionary with the name (might be called pair in java)
    // use ID to group data
    private int id;
    
    public Card()
    {
        name = "";
        costIncludingColor = "";
        type = "";
        health = 1;
        damage = 1;
        totalCost = 0;
    }
    
    // for general use
    public Card(String n, String cic, String t, int h, int d, int tc)
    {
        name = n;
        costIncludingColor = cic;
        type = t;
        health = h;
        damage = d;
        totalCost = tc;
    }
    
    // not all cards have health and damage
    public Card(String n, String cic, String t, int tc)
    {
        name = n;
        costIncludingColor = cic;
        type = t;
        totalCost = tc;
    }
    
    // this one should be used when the database is working
    // should put in all the needed info just based off of the name put in
    public Card(String n)
    {
        // GetCardInfo(n);
    }
    
    // shouldn't need set methods as these should all be immutable
    
    public String GetName()
    {
        return name;
    }
    
    public String GetCostIncludingColor()
    {
        return costIncludingColor;
    }
    
    public String GetType()
    {
        return type;
    }
    
    public int GetHealth()
    {
        return health;
    }
    
    public int GetDamage()
    {
        return damage;
    }
    
    public int GetTotalCost()
    {
        return totalCost;
    }
    
    public int GetCmc()
    {
        return cmc;
    }
    
    public String GetText()
    {
        return text;
    }
    
    public String GetMultiverseId()
    {
        return multiverseId;
    }
    
    
}
