package dk.itu.groupe.loading;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * This class objective is to display a loading screen for the 
 * user to see when the program is starting.
 * 
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class LoadingPanel extends JComponent
{

    private BufferedImage image;
    
    // Loads the image for the panel.
    public LoadingPanel()
    {
        try {
            image = ImageIO.read(new File("res/Loading.png"));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(480, 288);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(image, 0, 0, null);
    }
}