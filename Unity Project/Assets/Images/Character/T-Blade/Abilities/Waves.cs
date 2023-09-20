using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Waves : MonoBehaviour
{
    public float speed = 10f;
    public string targetTag = "Player";
    Animator animator;

    private Rigidbody2D rb;

    void Start()
    {
        rb = GetComponent<Rigidbody2D>();
        this.animator = GetComponent<Animator>();
        // Set the velocity of the rigid body to be in the direction of the transform's right vector
        rb.velocity = transform.right * speed;
        animator.Play("jump");
    }

    void OnTriggerEnter2D(Collider2D other)
    {
        // Check if the collided object has the target tag
        if (other.CompareTag(targetTag))
        {
            // Do something if the collided object has the target tag
            Debug.Log("Player hit!");
        }
        // Destroy the projectile
        Destroy(gameObject);
    }
}
