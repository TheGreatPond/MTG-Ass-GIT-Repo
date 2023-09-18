using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using TMPro;

public class Buttons_Menu1 : MonoBehaviour
{
    public TextMeshProUGUI Select;
    public int charc;

    public void Start()
    {
        charc = 0;
        Select.text = "Player One Select Your Character";
    }
    public void Char1()
    {
        Debug.Log("TBlade selected");
        GameObject charNames = GameObject.Find("CharNames"); // Find the GameObject named "CharNames"
        if (charNames != null)
        {
            if(charc == 0)
            {
                StringsforCharInstantiation.Player1 = "TBlade"; // Change the value of the Player1 string field
                Debug.Log(StringsforCharInstantiation.Player1);
                Select.text = "Player Two Select Your Character";

                charc++;
            } else
            {
                StringsforCharInstantiation.Player2 = "TBlade"; // Change the value of the Player2 string field
                Debug.Log(StringsforCharInstantiation.Player2);
                SceneManager.LoadScene("testLevel");
            }
            
        }
        else
        {
            Debug.LogError("CharNames GameObject not found");

        }
        //SceneManager.LoadScene("Level_One"); // Future use will be a save option to reference to
    }

    public void Char2()
    {
        Debug.Log("Matt selected");
        GameObject charNames = GameObject.Find("CharNames"); // Find the GameObject named "CharNames"
        if (charNames != null)
        {
            if (charc == 0)
            {
                StringsforCharInstantiation.Player1 = "Matt"; // Change the value of the Player1 string field
                Debug.Log(StringsforCharInstantiation.Player1);
                Select.text = "Player Two Select Your Character";

                charc++;
            }
            else
            {
                StringsforCharInstantiation.Player2 = "Matt"; // Change the value of the Player2 string field
                Debug.Log(StringsforCharInstantiation.Player2);
                SceneManager.LoadScene("testLevel");
            }
        }
        else
        {
            Debug.LogError("CharNames GameObject not found");
        }
        //SceneManager.LoadScene("Local_Selection");
    }

    public void Char3()
    {
        Debug.Log("Unfinished Character selected");
        GameObject charNames = GameObject.Find("CharNames"); // Find the GameObject named "CharNames"
        if (charNames != null)
        {
           StringsforCharInstantiation.Player1 = "Unfinished Character"; // Change the value of the Player1 string field
            Debug.Log(StringsforCharInstantiation.Player1);
            //SceneManager.LoadScene("Main_Menu");
        }
        else
        {
            Debug.LogError("CharNames GameObject not found");
        }
        //SceneManager.LoadScene("Settings");
    }
    public void Quit()
    {
        Debug.Log("quitter detected???");
        SceneManager.LoadScene("Main_Menu");
    }
}
