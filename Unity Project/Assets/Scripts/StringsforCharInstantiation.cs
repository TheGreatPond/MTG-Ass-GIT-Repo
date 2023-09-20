using UnityEngine;

public class StringsforCharInstantiation : MonoBehaviour
{


    public static string Player1;
    public static string Player2;


    void Awake()
    {
        DontDestroyOnLoad(gameObject); // Keeps the object and this script alive between scenes
    }
}