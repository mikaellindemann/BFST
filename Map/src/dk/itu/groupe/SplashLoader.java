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

    private final SplashScreen splash = SplashScreen.getSplashScreen();
    private Graphics2D g;
    
    public SplashLoader()
    {
        if (splash != null) {
            g = splash.createGraphics();
        }
    }
    
    /**
     * Updates the Splash-screen on loading.
     *
     * @param percent The percentage of the program that is loaded.
     * @throws IllegalArgumentException If the percentage is not between 0 and
     * 100
     */
    public void updateSplash(int percent) throws IllegalArgumentException
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
