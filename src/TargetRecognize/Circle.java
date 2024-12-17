package TargetRecognize;

import java.awt.Color;

public class Circle extends MyPoint{
	private int radius;
	private int thickness = 10;
	private Color color = Color.BLACK;
	private Boolean isClicked = false;
	
	public Circle(int x, int y, int radius) {
		super(x, y);
		this.radius = radius;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getThickness() {
		return thickness;
	}
	
	public void setThickness(int thick) {
		thickness = thick;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Boolean getIsClicked() {
		return isClicked;
	}

	public void setIsClicked(Boolean isClicked) {
		this.isClicked = isClicked;
	}
}
