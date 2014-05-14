package dk.itu.groupe.loading;

import java.awt.*;
import javax.swing.*;

/**
 * This class objective is to display a loading screen for the user to see when
 * the program is starting.
 *
 * @author Peter Bindslev (plil@itu.dk), Rune Henriksen (ruju@itu.dk) &amp;
 * Mikael Jepsen (mlin@itu.dk)
 */
public class LoadingPanel extends JComponent
{

    private final Image image;
    private final double maximumNumberOfLoadedElements = 22.0;
    private int numberOfLoadedElements;
    private final Color color = Color.decode("#5B9EAA");

    public LoadingPanel()
    {
        numberOfLoadedElements = 0;
        image = new ImageIcon("./res/Loading.png").getImage();
    }

    public void elementLoaded()
    {
        numberOfLoadedElements++;
        repaint();
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
        g.setColor(color);
        g.drawRect(10, getHeight() - 20, getWidth() - 20, 10);
        g.fillRect(10, getHeight() - 20, (int) ((getWidth() - 40) * (numberOfLoadedElements / maximumNumberOfLoadedElements)), 10);
    }
}
