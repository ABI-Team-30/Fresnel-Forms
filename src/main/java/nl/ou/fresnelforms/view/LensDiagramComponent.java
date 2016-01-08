package nl.ou.fresnelforms.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Generic class for all square components that get displayed in lens diagram.
 */
public class LensDiagramComponent extends Rectangle2D.Double {
	
	private static final long serialVersionUID = 6167983058485565537L;
	private double x, y;
	
	/**
	 * Move component to position.
	 * @param position the position to move the component to
	 */
	public void setPosition(Point2D position){
		this.x = position.getX();
		this.y = position.getY();		
	}
	
	/** 
	 * @return the x value
	 */
	public double getX() {
		return x;
	}
	
	/** 
	 * @return the y value
	 */
	public double getY() {
		return y;
	}

}
