/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.groupe.parsing.coastline;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Mikael
 */
public class CoastlineParser
{

    public static void main(String[] args)
    {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("2229.dat")))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.endsWith(">")) {
                    continue;
                }
                String[] sub = line.split("	");
                System.out.println(sub[0] + ", " + sub[1]);
            }
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
