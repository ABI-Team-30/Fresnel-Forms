package nl.ou.fresnelforms.view;

import java.awt.geom.Point2D;
import java.util.Comparator;

/**
 * Comperator class for lens boxes that compares their distance to a given lens box.
 */
public class LensBoxDistanceComparator implements Comparator<LensBox> {
	
	private Point2D center;
	
	/**
	 * @param lb lens box that serves as the point measured from
	 */
	public LensBoxDistanceComparator(LensBox lb){
		this.center = new Point2D.Double(lb.getCenterX(), lb.getCenterY());
	}

	/** 
	 * comparator of two lensboxes.
	 * @param lb1 the first lensbox
	 * @param lb2 the second lensbox
	 * @return -1 when lb1 is nearer to the centre as lb2, 0 when the distance is equal, 1 if lb2 is nearer to the centre
	 */
	public int compare(LensBox lb1, LensBox lb2) {
		Point2D p1, p2;
		p1 = new Point2D.Double(lb1.getCenterX(), lb1.getCenterY());
		p2 = new Point2D.Double(lb2.getCenterX(), lb2.getCenterY());
		
		if (p1.distance(center) < p2.distance(center)){
			return -1;
		}
		if (p1.distance(center) > p2.distance(center)){
			return 1;
		}
		return 0;
	}

}
