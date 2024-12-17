package TargetGenerator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.server.Operation;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalBorders.Flush3DBorder;

import TargetRecognize.Circle;

public class TargetMain {
	static int rMax;
	static int rCurrent;
	static int x;
	static int y;
	static int betweenR = 40;
	static ArrayList<Circle> circles = new ArrayList<Circle>();
	static JSlider radiusSlider = new JSlider();
//	static JTextField radiusValue = new JTextField("");
	static JLabel radiusValue = new JLabel("");
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGui();
			}
		});
//		createGui();
	}

	private static BufferedImage createImage(Component panel) {
		int w = panel.getWidth();
		int h = panel.getHeight();
		System.out.println("in saving image size = " + w + " x " + h);
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		panel.paint(g);
		return bi;
	}

	private static void createGui() {
		JFrame targetWindow = new JFrame("Target creation");
		targetWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MyTargetLabel targetLabel = new MyTargetLabel();

		
		targetWindow.setSize(900, 700);
		targetWindow.setLocationRelativeTo(null);
		targetWindow.add(targetLabel, BorderLayout.CENTER);
		targetWindow.setVisible(true);
		System.out.println("targetLabel width = " + targetLabel.getWidth());
		rMax = (targetLabel.getWidth() > targetLabel.getHeight()) ? targetLabel.getHeight() / 2
				: targetLabel.getWidth() / 2;
		System.out.println("rMax = " + rMax);
		rCurrent = rMax - 20;
		System.out.println("rCurrent = " + rCurrent);
		radiusSlider.getModel().setMaximum(rCurrent);
		radiusValue.setText(""+rCurrent);
		x = targetLabel.getWidth() / 2;
		y = targetLabel.getHeight() / 2;
		circles.add(new Circle(x, y, rCurrent));
		targetLabel.drawTarget(circles);
//		targetLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));

		targetWindow.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				System.out.println("targetLabel width = " + targetLabel.getWidth());
				rMax = (targetLabel.getWidth() > targetLabel.getHeight()) ? targetLabel.getHeight() / 2
						: targetLabel.getWidth() / 2;
				System.out.println("rMax = " + rMax);
				rCurrent = rMax - 20;
				radiusSlider.getModel().setMaximum(rCurrent);
				System.out.println("rCurrent in resize = " + rCurrent);
				radiusValue.setText(""+rCurrent);
				x = targetLabel.getWidth() / 2;
				y = targetLabel.getHeight() / 2;
				circles.clear();
				circles.add(new Circle(x, y, rCurrent));
				targetLabel.drawTarget(circles);
			}
		});

		targetLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				System.out.println("---!! image clicked at x = "+e.getX()+" y="+e.getY()+ " !!---");
				//TODO сделать определение, куда ткнули мышкой - какой круг
				int circleIndex = getCircleIndexByXY(e.getX(), e.getY(), circles);
				if (circleIndex>=0) {
					circles.get(circleIndex).setIsClicked(true);
					circles.forEach(circle->{
						if (circle!=circles.get(circleIndex)) circle.setIsClicked(false);
					});
				} else circles.forEach(circle-> circle.setIsClicked(false));
				targetLabel.drawTarget(circles);
				System.out.println("circle index = "+circleIndex);
			}
		});
		
		JButton addCircleBtn = new JButton("circle +");
		addCircleBtn.addActionListener(e -> {
			circles.add(new Circle(x, y, circles.get(circles.size()-1).getRadius() - betweenR));
			System.out.println("circle added with parameters:  " + x + " " + y + " " + circles.get(circles.size()-1).getRadius());
			targetLabel.drawTarget(circles);
		});
		JButton delCircleBtn = new JButton("circle -");
		delCircleBtn.addActionListener(e -> {
			if (circles.size() > 1) {
				circles.remove(circles.size()-1);
				targetLabel.drawTarget(circles);
			}
		});
		JButton saveImageBtn = new JButton("Save image");
		saveImageBtn.addActionListener(e -> {
			String ext = "png";
			JFileChooser fileDialog = new JFileChooser();
			fileDialog.setAcceptAllFileFilterUsed(false);
	        FileNameExtensionFilter filter = new FileNameExtensionFilter(ext+" files", ext);
	        fileDialog.addChoosableFileFilter(filter);
			
			File workingDirectory = new File(System.getProperty("user.dir"));
			fileDialog.setCurrentDirectory(workingDirectory);
			int v = fileDialog.showSaveDialog(targetWindow);
			if (v == JFileChooser.APPROVE_OPTION) {
				BufferedImage bImage = createImage(targetLabel);
				System.out.println("image size = " + bImage.getWidth() + " x " + bImage.getHeight());
				File outputfile = fileDialog.getSelectedFile();
				if (!outputfile.getPath().contains(ext)) outputfile = new File(outputfile.getPath()+"."+ext);
				try {
					ImageIO.write(bImage, ext, outputfile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		var upperBox = new Box(BoxLayout.Y_AXIS);
		upperBox.setBorder(BorderFactory.createLineBorder(Color.gray, 5));
		Box upperBoxForCircles = new Box(BoxLayout.X_AXIS);
		upperBoxForCircles.add(addCircleBtn);
		upperBoxForCircles.add(Box.createHorizontalGlue());
		upperBoxForCircles.add(saveImageBtn);
		upperBoxForCircles.add(Box.createHorizontalGlue());
		upperBoxForCircles.add(delCircleBtn);
		upperBox.add(upperBoxForCircles);
		
		Box upperBoxForBackColor = createSliderBox(targetLabel, "Back color in gray:", 0, 255, 255, JSlider.HORIZONTAL);
		
		upperBox.add(upperBoxForBackColor);
		Box upperBoxForCircleColor = createSliderBox(targetLabel, "Circle color in gray:", 0, 255, 0, JSlider.HORIZONTAL);
		upperBox.add(upperBoxForCircleColor);
		
		Box buttomBox = createSliderBox(targetLabel, "Radius:", 20, rCurrent, rCurrent, JSlider.HORIZONTAL);
		Box eastBox = createSliderBox(targetLabel, "Thick: ", 1, 100, 10, JSlider.VERTICAL);
		
		//TODO добавить генерацию нужного кол-ва мишеней с разными параметрами в отдельную папку
		
		targetWindow.add(upperBox, BorderLayout.NORTH);
		targetWindow.add(buttomBox, BorderLayout.SOUTH);
		targetWindow.add(eastBox, BorderLayout.EAST);
		targetWindow.validate();
	}

	protected static int getCircleIndexByXY(int x, int y, ArrayList<Circle> circles2) {
		int i = 0;
		for (Circle circle : circles2) {
			System.out.println(""+i+" circle: x="+circle.getX()+", y="+circle.getY()+", rad = "+circle.getRadius());
			System.out.println("centerY-radius = "+(circle.getY()-circle.getRadius()));
			if (circleIsOnXY(circle, x, y)) return i;
			i++;
		}
		return -1;
	}

	private static boolean circleIsOnXY(Circle circle, int x, int y) {
//		(x – a)2 + (y – b)2 = R2
		int circleR = circle.getRadius();
		int thick = circle.getThickness();
		double leftPart = Math.pow(x-circle.getX(), 2) + Math.pow(y-circle.getY(), 2);
		if (leftPart>=((circleR-thick)*(circleR-thick)) && leftPart<=((circleR+thick)*(circleR+thick)))
				return true;
		return false;
	}
	
	private static Box createSliderBox(MyTargetLabel targetLabel, String labelText, int min, int max, int curValue, int orientation) {
		Box boxWithSlider = (orientation == JSlider.HORIZONTAL) ? new Box(BoxLayout.X_AXIS) : new Box(BoxLayout.Y_AXIS) ;
		boxWithSlider.add(new JLabel(labelText));
		JSlider slider = new JSlider(orientation, min, max, curValue);
		slider.setPaintTrack(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(5);
 
//		JTextField sliderValue = new JTextField(""+curValue);
		JLabel sliderValue = new JLabel(" "+curValue+" ");
		sliderValue.setBorder(BorderFactory.createLineBorder(Color.gray, 2));
		slider.addChangeListener(e->{
			System.out.println(labelText+" = "+slider.getValue());
			if (labelText.contains("Back")) targetLabel.setBackColor(slider.getValue());
			if (labelText.contains("Circle")) targetLabel.setCircleColor(slider.getValue());
			if (labelText.contains("Radius")) {
				radiusSlider = slider;
				slider.getModel().setMaximum(rCurrent);
				radiusValue = sliderValue;
				radiusValue.setText(""+rCurrent);
				circles.get(0).setRadius(slider.getValue());
				targetLabel.drawTarget(circles);
			}
			if (labelText.contains("Thick")) targetLabel.setThickness(slider.getValue());
			
			sliderValue.setText(String.valueOf(slider.getValue()));
			targetLabel.repaint();
			
		});
		boxWithSlider.add(sliderValue);
	//	sliderValue.setMaximumSize(new Dimension(10, 20));
		boxWithSlider.add(slider);
		return boxWithSlider;
	}

}
