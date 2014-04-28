/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dk.itu.groupe;

import java.util.Comparator;

/**
 *
 * @author Mikael
 */
public class LengthComparator implements Comparator<Edge>
{

    @Override
    public int compare(Edge o1, Edge o2)
    {
        return (int) (o1.getLength() - o2.getLength());
    }
}
