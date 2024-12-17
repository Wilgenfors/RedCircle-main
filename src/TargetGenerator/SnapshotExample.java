package TargetGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class SnapshotExample
{
//    private JPanel contentPane;

    private void displayGUI()
    {
        JFrame frame = new JFrame("Snapshot Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new BorderLayout());
//        contentPane.setOpaque(true);
//        contentPane.setBackground(Color.WHITE);
        JLabel label = new JLabel("This JLabel will display"
                        + " itself on the SNAPSHOT", JLabel.CENTER);
        contentPane.add(label, BorderLayout.CENTER);
        JButton saveImageBtn = new JButton("Save");
        saveImageBtn.addActionListener(e->{
        	makePanelImage(contentPane);
        });
        frame.add(contentPane, BorderLayout.CENTER);
        frame.add(saveImageBtn, BorderLayout.NORTH);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

//        makePanelImage(contentPane);
    }

    private void makePanelImage(Component panel)
    {
        Dimension size = panel.getSize();
        BufferedImage image = new BufferedImage(
                    size.width, size.height 
                              , BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        panel.paint(g2);
        try
        {
            ImageIO.write(image, "png", new File("snapshot3.png"));
            System.out.println("Panel saved as Image.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String... args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {           
                new SnapshotExample().displayGUI();
            }
        });
    }
}
