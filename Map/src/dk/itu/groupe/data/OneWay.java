/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.groupe.data;

/**
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) &amp;
 * Mikael Jepsen (mlin@itu.dk)
 */
public enum OneWay
{

    /**
     * States that the road is one-way from the first node to the second node.
     */
    FROM_TO(1),
    /**
     * No one-way restrictions.
     */
    NO(0),
    /**
     * States that the road is one-way from the second node to the first node.
     */
    TO_FROM(-1);

    private final int number;

    private OneWay(int number)
    {
        this.number = number;
    }

    public int getNumber()
    {
        return number;
    }
}
