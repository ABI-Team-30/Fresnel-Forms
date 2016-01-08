package nl.ou.fresnelforms.view;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * Generalization arrow display class.
 */
public class LensGeneralizationArrow extends Path2D.Double {
	
	private static final int ARROW_LENGTH = 10;
	private static final long serialVersionUID = 9087299337430863776L;
	private LensBox child;
	private LensBox parent;
	
	/**
	 * Constructor that initializes the generalization arrow.
	 * @param child lens box
	 * @param parent lens box
	 */
	public LensGeneralizationArrow(LensBox child, LensBox parent) {
		super();
		this.child = child;
		this.parent = parent;
		this.refresh();
	}
	
	
	/**
	 * Redefines the path with the current coordinates of the child and parent lensboxes.
	 */
	public void refresh(){
		//check if a refresh is nessecary. lens is dirty when moved.
		if (child.isLensdirty() || parent.isLensdirty()){
			//clear the path
			this.reset();
			//redefine the path with the current coordinates of the child and parent lensboxes.
			Point2D.Double childPoint = new Point2D.Double(child.getX() + child.width/2, child.getY());
			Point2D.Double parentPoint = new Point2D.Double(parent.getX() + parent.width/2, parent.getY() + parent.height);
			
			this.moveTo(childPoint.x, childPoint.y);
			this.lineTo(childPoint.x, childPoint.y-ARROW_LENGTH);
			this.lineTo(parentPoint.x, parentPoint.y+2*ARROW_LENGTH );
			this.lineTo(parentPoint.x, parentPoint.y+ARROW_LENGTH);
			this.lineTo(parentPoint.x+ARROW_LENGTH, parentPoint.y+ARROW_LENGTH);
			this.lineTo(parentPoint.x, parentPoint.y);
			this.lineTo(parentPoint.x-ARROW_LENGTH, parentPoint.y+ARROW_LENGTH);
			this.lineTo(parentPoint.x, parentPoint.y+ARROW_LENGTH);
		}
	}
	
	/**
	 * @return the child lensbox
	 */
	public LensBox getChild() {
		return child;
	}
	
	/**
	 * @return the parent lensbox
	 */
	public LensBox getParent() {
		return parent;
	}

}
