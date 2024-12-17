package TargetRecognize;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JLabel;

public class MyLabel extends JLabel{
	int circleR;
	int circleX;
	int circleY;
	boolean paintCircle = false;
	boolean paintCircles = false;
	ArrayList<Circle> circlesList = null;
	float dHeight = 1;
	Circle circle;
	boolean paintRedPoint = false;
	
	public void drawCircle(int x, int y, int r, float dHeight) {
		this.dHeight = dHeight;
		circleR = (int) (r*dHeight);
		circleX = (int) (x*dHeight);
		circleY = (int) (y*dHeight);
		paintCircle = true;
		repaint();
	}
	
	// если добавить в параметры еще float dHeight, то можно не рисовать отдельно первый круг
	public void drawCircles(ArrayList<Circle> circlesList) { //для рисования всех кругов
		paintCircles = true;
		this.circlesList = circlesList;
		repaint();
	}	
	// Метод добавленный из IDEA:
		public void drawPoint(Circle circle, float dHeight2) {
			this.circle = circle;
			this.dHeight = dHeight2;
			paintRedPoint = true;
//			paintCircle = false;
			repaint();
		}
	// ---------------------------------------------------------------------------------
// Метод добавленный из IDEA:
	@Override
	
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D gr2D = (Graphics2D) g;
		Graphics2D grDot = (Graphics2D) g;
		BasicStroke pen;

		if (paintCircle) {
			float[] dash = { 20, 20 };
			gr2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			pen = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dash, 1);
			gr2D.setStroke(pen);
			gr2D.setColor(Color.GREEN);
			for (Circle circle : circlesList) {
				int x = circle.getX();
				int y = circle.getY();
				int r = circle.getRadius();
				circleR = (int) (r * dHeight);
				circleX = (int) (x * dHeight);
				circleY = (int) (y * dHeight);
				gr2D.drawOval(circleX - circleR, circleY - circleR, 2 * circleR, 2 * circleR);
			}
		}
		if (paintRedPoint) {
			System.out.println("Drawing blue");
			grDot.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			pen = new BasicStroke(5);
			grDot.setStroke(pen);
			grDot.setColor(Color.BLUE);
			circleX = (int) (circle.getX() * dHeight);
			circleY =  (int) (circle.getY()* dHeight);
			circleR =  (int) (circle.getRadius()* dHeight);
			grDot.drawRect(circleX-circleR-1, circleY-circleR-1, circleR*2+1, circleR*2+1);
		}
	}
	
	
//	public void paint(Graphics g) {
//		super.paint(g);
//		Graphics2D gr2D = (Graphics2D)g;
//		BasicStroke pen;
//		if (paintCircle) {
//			float[] dash = {20, 20};
//			gr2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			pen=new BasicStroke(10,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND, 10, dash, 1);
//			gr2D.setStroke(pen);
//			gr2D.setColor(Color.GREEN);
//			gr2D.drawOval(circleX-circleR, circleY-circleR, 2*circleR, 2*circleR);
//		}
//		if (paintCircles) {
//			float[] dash = {20, 20};
//			gr2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			pen=new BasicStroke(10,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND, 10, dash, 1);
//			gr2D.setStroke(pen);
//			gr2D.setColor(Color.YELLOW);
//			for (Circle circle : circlesList) {
//				int d = (int) (circle.getRadius()*2*dHeight);
//				int R = (int) (circle.getRadius()*dHeight);
//				int X = (int) (circle.getX()*dHeight);
//				int Y = (int) (circle.getY()*dHeight);
//				gr2D.drawOval(X-R, Y-R, d, d);
//			}
//		}
//	}

}
