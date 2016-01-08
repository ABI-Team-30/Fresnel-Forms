package nl.ou.fresnelforms.view;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import nl.ou.fresnelforms.fresnel.PropertyBinding;

/**
 * Property arrow display class.
 */
public class PropertyArrow extends Path2D.Double {
	
	private static final int QUARTER_RATIO = 4;
	private static final int ARROW_SIZE = 5;
	private static final long serialVersionUID = 7871068186969273097L;
	//private PropertyBinding propertyBinding;
	private LensBox domain;
	private LensBox range;
	private PropertyBinding propertyBinding;
	
	/**
	 * Constructor that initializes the property arrow.
	 * @param propertyBinding property binding displayed by arrow
	 * @param domain domain lens box
	 * @param range range lens box
	 */
	public PropertyArrow(PropertyBinding propertyBinding, LensBox domain, LensBox range) {
		this.propertyBinding = propertyBinding;
		this.domain = domain;
		this.range = range;
		this.refresh();
	
	}

	/**
	 * Redefines the path with the current coordinates of the child and parent lensboxes.
	 */
	public void refresh(){
		//check if a refresh is nessecary. lens is dirty when moved.
		if (domain.isLensdirty() || range.isLensdirty() ){
			//clear the path
			this.reset();
			//ObjectProperty objectProperty = (ObjectProperty) propertyBinding.getProperty().getProperty();
			
			Point2D.Double domainPoint = new Point2D.Double(domain.getX(), domain.getY());
			Point2D.Double rangePoint = new Point2D.Double(range.getX(), range.getY());
			
			double incomingWidth = 0;
			double outgoingWidth = domain.width;
			double arrowWidth = -ARROW_SIZE;
			if (domainPoint.x >= rangePoint.x+range.width) {
				incomingWidth = range.width;
				outgoingWidth = 0;
				arrowWidth = -arrowWidth;
			}
			
			int labelHeight = LensDiagram.getLabelHeigth();
			int propertyIndex = propertyBinding.getIndex();
			double relativeY = labelHeight*propertyIndex + LensDiagram.LENSBOX_HEADER_HEIGHT - labelHeight/QUARTER_RATIO;
			double incomingHeight = range.height/2;
			if (range.equals(domain)) {
				incomingHeight = relativeY;
			}
			
			this.moveTo(domainPoint.x+outgoingWidth, domainPoint.y+relativeY);
			if (range.equals(domain)) {
				this.lineTo(domainPoint.x+outgoingWidth+2*arrowWidth, domainPoint.y+relativeY);
			} else {
				this.lineTo(domainPoint.x+outgoingWidth-2*arrowWidth, domainPoint.y+relativeY);
			}
			this.lineTo(rangePoint.x+incomingWidth+2*arrowWidth, rangePoint.y+incomingHeight);
			this.lineTo(rangePoint.x+incomingWidth, rangePoint.y+incomingHeight);
			this.lineTo(rangePoint.x+incomingWidth+arrowWidth, rangePoint.y+incomingHeight-ARROW_SIZE);
			this.moveTo(rangePoint.x+incomingWidth, rangePoint.y+incomingHeight);
			this.lineTo(rangePoint.x+incomingWidth+arrowWidth, rangePoint.y+incomingHeight+ARROW_SIZE);
		}
	}
	
	
	
	/**
	 * @return the domain lensbox
	 */
	public LensBox getDomain() {
		return this.domain;
	}
	
	/**
	 * @return the range lensbox
	 */
	public LensBox getRange() {
		return this.range;
	}
	
}
