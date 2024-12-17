package TargetRecognize;
import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ResizeEndSwingExample {

    private Timer timer;

    public ResizeEndSwingExample() {
        JFrame frame = new JFrame("Resize End Example");
        frame.setSize(300, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        timer = new Timer(500, e -> performAction());
        timer.setRepeats(false); // Only execute once after resizing stops

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                timer.restart(); // Restart timer on resize
            }
        });

        frame.setVisible(true);
    }

    private void performAction() {
        System.out.println("Resize action performed!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ResizeEndSwingExample::new);
    }
}