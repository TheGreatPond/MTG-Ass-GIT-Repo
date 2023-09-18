using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class Buttons_Menu : MonoBehaviour
{
    public void PlayStory(){
        SceneManager.LoadScene("Level_One"); // Future use will be a save option to reference to
    }

    public void PlayBuild(){
        SceneManager.LoadScene("Build Select");
    }

    public void PlayAnalyze(){
        SceneManager.LoadScene("Analyze Select");
    }

    public void PlayHelp()
    {
        SceneManager.LoadScene("Help");
    }

    public void PlayMain()
    {
        SceneManager.LoadScene("Main_Menu");
    }

    public void PlayDeck1()
    {
        SceneManager.LoadScene("Deck 1");
    }

    public void PlayDeck2()
    {
        SceneManager.LoadScene("Deck 2");
    }

    public void PlayDeck1Analyze()
    {
        SceneManager.LoadScene("Deck 1 Analyze");
    }

    public void PlayDeck2Analyze()
    {
        SceneManager.LoadScene("Deck 2 Analyze");
    }

    public void PlayDeckNew()
    {
        SceneManager.LoadScene("New Deck");
    }

    public void Quit(){
        Application.Quit();
    }
}
