package dk.itu.groupe.loading;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) & Mikael
 * Jepsen (mlin@itu.dk)
 */
public class LoadingPanel extends JComponent
{

    private BufferedImage image;

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