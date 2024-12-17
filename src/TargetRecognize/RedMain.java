package TargetRecognize;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RedMain {
	private static JFrame mainFrame;
	static BufferedImage myPicture = null;
	 private static Timer timer;
	 static String fileName = "";
	 static ArrayList<Circle> circlesList;
	 
	public static void main(String[] args) {
//		consoleTest();
		guiTest();
	}


	public static List<String> listFilesUsingJavaIO(String dir) { //для получения списка файлов .png и .jpg в каталоге
	    return Stream.of(new File(dir).listFiles())
	      .filter(file -> !file.isDirectory() && (file.getPath().contains(".png") || file.getPath().contains(".jpg")))
	      .map(File::getName)
	      .collect(Collectors.toList());
	}
	
	private static JList<String> getListWithFiles() {
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		listModel.addAll(listFilesUsingJavaIO(System.getProperty("user.dir")));
		System.out.println("listModel = "+listModel);
		JList<String> filesList = new JList<>(listModel);
		return filesList;
	}
	
	private static void guiTest() {

		mainFrame = new JFrame("RedTargetTest");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MyLabel imageLabel = new MyLabel();
		JList<String> filesList = getListWithFiles();
		fileName = filesList.getModel().getElementAt(0);   
		try {
			myPicture = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageIcon imgIcon = new ImageIcon(myPicture);
		imageLabel.setIcon(imgIcon);
		mainFrame.add(imageLabel, BorderLayout.CENTER);
		mainFrame.add(filesList, BorderLayout.EAST);
		mainFrame.setSize(800, 600);
		mainFrame.setVisible(true);
		resizeImage(imageLabel, myPicture, imgIcon);
		mainFrame.setLocationRelativeTo(null);
		filesList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				JList tempList = (JList) e.getSource();
				fileName = (String) tempList.getSelectedValue();
				System.out.println("fileName = "+ fileName);
				try {
					myPicture = ImageIO.read(new File(fileName));
				} catch (IOException err) {
					err.printStackTrace();
				}
				resizeImage(imageLabel, myPicture, imgIcon);
				mainFrame.invalidate();
			}
		});
		timer = new Timer(50, e -> {
			System.out.println("Resize action performed!");
			resizeImage(imageLabel, myPicture, imgIcon);
		});
		timer.setRepeats(false); // Only execute once after resizing stops

//		mainFrame.addComponentListener(new ComponentAdapter() {
//			@Override
//			public void componentResized(ComponentEvent e) {
//				timer.restart();
////				System.out.println("--!!resized!!--");
//			}
//		});
		imageLabel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				timer.restart();
//				resizeImage(imageLabel, myPicture, imgIcon);
			}
		});

		 var myCircle = detectedRedPointOnTarget(imageLabel, myPicture, imgIcon);
	        checkTarget(circlesList, myCircle);
		
	}

	private static void resizeImage(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon) {
		float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
		int newWidth = (int) (myPicture.getWidth() * dHeight);
		Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
		imgIcon.setImage(dimg);
		RedSearch redSearch = new RedSearch(myPicture);
		redSearch.findRedPoints(); //Находим координаты красной точки
		Circle circle = redSearch.getCircle(); //находим внешний круг
		if (circle==null) {
			System.out.println("--!! No circle !!--");
		} else {
			
			//red point detected:
			 Circle myCircle = detectedRedPointOnTarget(imageLabel, myPicture, imgIcon);
		        imageLabel.drawPoint(myCircle, dHeight);
			//--------------------------------------------------------------------
			imageLabel.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), dHeight);
			System.out.println("--- inner circles search ---");
			ArrayList<Circle> circlesList = redSearch.getCircles(circle); //находим все внутренние круги
			imageLabel.drawCircles(circlesList);
			circlesList.add(circle); //если нужен список со всеми кругами
			

			
		}
	}
// Метод добaвленный из IDEA:
	// metod for detected red poins on target:
    private static Circle detectedRedPointOnTarget(MyLabel imageLabel, BufferedImage myPicture, ImageIcon imgIcon) {
        float dHeight = imageLabel.getHeight() / (float) myPicture.getHeight();
        int newWidth = (int) (myPicture.getWidth() * dHeight);
        Image dimg = myPicture.getScaledInstance(newWidth, imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imgIcon.setImage(dimg);
        
        RedSearch redSearch = new RedSearch(myPicture);
      //  Circle circle = redSearch.getCircle(); //находим внешний круг
       // ArrayList<Circle> circlesList = redSearch.getCircles(circle);
       // System.out.println("\ncircleList size = " + circleList.size() + "\n");
        Circle myCircle = redSearch.findRedPointsAsCircle(); // это наша красная точка
        
        imageLabel.drawPoint(myCircle, dHeight);//, обведенная окружностью
        
        System.out.println("red dot circle = " + myCircle.getX() + " " + myCircle.getY() + " " + myCircle.getRadius());
        return myCircle;


    }
// ---------------------------------------------------------------------------------
    
 // Методs для определения между какими кругами находится красная точка:
    private static void checkTarget(ArrayList<Circle> circlesList, Circle myCircle) {
        System.out.println("Going through the target: ");
        System.out.println("circles = "+circlesList.size());

        boolean miss = false, between = false;
        for (Circle circle0 : circlesList) {
            int i = circlesList.indexOf(circle0);

            Circle circle1 = circlesList.get(i + 1);

            int Cx = circle0.getX();
            int Cy = circle0.getY();
            int R = circle0.getRadius();


            // Проверяем на промах:
            if (i < circlesList.size() - 1 && checkRedPointAndCircleOut(myCircle, circle0)) {
                System.out.println("miss");
                miss = true;
                break;
            }

//            // Проверяем на попадание во внутрению окружность:
//            if (i == circleList.size() - 1 && checkRedPointAndCircleCentre(myCircle, circle1)) {
//                System.out.println("in center");
//                break;
//            }

//           //  Проверяем на попадание во внутрению окружность:
//            if (i == circleList.size() - 1 && (!miss && !center)) {
//                System.out.println("in center");
//                break;
//            }

            // Проверяем окружности на попадания без внутренего круга и промаха:
            if ( i < circlesList.size() - 1 && checkRedPointAndCircle(myCircle, circle0, circle1)) {
                System.out.println("between " + i + " & " + (i + 1));
                between = true;
                break;
            }
//  Проверяем на попадание во внутрению окружность:
            if (circle1 == circlesList.get(2) && (!miss && !between)) {
                System.out.println("in center");
                break;
            }

        }


    }
    
    
    private static double getRadius(Circle myCircle, Circle circle0) {
        return Math.pow((myCircle.getX() - circle0.getX()), 2) + Math.pow((myCircle.getY() - circle0.getY()), 2);
    }

    private static boolean checkRedPointAndCircleOut(Circle myCircle, Circle circle) {
        if (getRadius(myCircle, circle) > Math.pow(circle.getRadius(), 2)) return true;
        return false;
    }

    private static boolean checkRedPointAndCircleCentre(Circle myCircle, Circle circle) {
        if (getRadius(myCircle, circle) < Math.pow(circle.getRadius(), 2)) return true;
        return false;
    }

    private static boolean checkRedPointAndCircle(Circle myCircle, Circle circle0, Circle circle1) {
        if (getRadius(myCircle, circle0) < Math.pow(circle0.getRadius(), 2)
                && getRadius(myCircle, circle1) > Math.pow(circle1.getRadius(), 2))
            return true;

        return false;
    }
 	//---------------------------------------------------------------------------------
    
    
    
    
	private static void consoleTest() {
		RedSearch redSearch = new RedSearch("img.png");
		redSearch.findRedPoints();
		redSearch = new RedSearch("img2.png");
		redSearch.findRedPoints();
		redSearch = new RedSearch("target.png");
		redSearch.boundCircleSearch();
	}
}
