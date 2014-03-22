package dk.itu.groupe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SplashScreen;

/**
 *
 * @author Mikael
 */
public class SplashLoader
{

    private static int nodes = 0, edges = 0;
    private static final SplashScreen splash = SplashScreen.getSplashScreen();
    private static Graphics2D g = splash.createGraphics();

    ;
    
    private SplashLoader()
    {
    }

    public static void countNode()
    {
        nodes++;
        if (nodes % 6759 == 0) {
            updateSplash((int)((double)(nodes * 20 / 675902)));
        }
    }

    public static void countEdge()
    {
        edges++;
        if (edges % 8123 == 0 || edges == 812301) {
            updateSplash((int)(20 + 80 * ((double) edges / 812301)));
        }
    }

    /**
     * Updates the Splash-screen on loading.
     *
     * @param percent The percentage of the program that is loaded.
     * @throws IllegalArgumentException If the percentage is not between 0 and
     * 100
     */
    public static void updateSplash(int percent) throws IllegalArgumentException
    {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("A percentage is between 0 and 100");
        }
        if (g != null) {
            double splashWidth = splash.getSize().width;
            double splashHeight = splash.getSize().height;

            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(10, (int) splashHeight - 10, (int) splashWidth - 20, 4);
            g.fillRect(10, (int) splashHeight - 10, (int) (percent * ((splashWidth - 20) / 100.0)), 5);
            splash.update();
        }
    }
}
