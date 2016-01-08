package nl.ou.fresnelforms.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import nl.ou.fresnelforms.fresnel.Lens;

/**
 * Lens box display class.
 */
public class LensBox extends LensDiagramComponent {
	
	private static final long serialVersionUID = -7304343999640070030L;
	private Lens lens;
	private int zIndex = 1;
	private ArrayList<PropertyLabel> propertyLabels;
	private boolean mouseOver = false;
	private LensBoxLabel label;
	private boolean showattribs = true;  //flags if the attributes are shown or not
	private boolean lensdirty = false;   //flags if the lens had changed
	private boolean selected = false;	 //flags if the lensbox is selected or nor
	private PropertyLabelHeuristicComparator propertylabelhcomparator; // the heuristic comparator for this lensbox.
	
	/**
	 * Constructor that initializes the lens box.
	 * @param lens displayed by the lens box
	 */
	public LensBox(Lens lens) {
		new LensBox(lens, true);
	}

	
	/**
	 * Constructor that initializes the lens box.
	 * @param lens displayed by the lens box
	 * @param ashowattribs true show the attributes of this lens, false do not show the attributes of this lens.
	 */
	public LensBox(Lens lens, boolean ashowattribs) {
		new LensBox(lens,ashowattribs,new PropertyLabelHeuristicComparator(null,null));
	}
	
	/**
	 * Constructor for the propertylabelheuristiccomparator.
	 * @param lens displayed by the lensbox
	 * @param heurcomparator the heuristic comparator
	 * @param ashowattribs true show the attributes of this lens, false do not show the attributes of this lens.
	 */
	public LensBox(Lens lens,boolean ashowattribs, PropertyLabelHeuristicComparator heurcomparator ){
		propertylabelhcomparator = heurcomparator;
		this.lens = lens;
		showattribs = ashowattribs;
		adjustHeight();
		this.width = LensDiagram.LENSBOX_WIDTH;
		this.propertyLabels = new ArrayList<PropertyLabel>();
		
		label = new LensBoxLabel(this);
		this.setPosition(lens.getSavedPosition());
		lens.setLensBox(this);
		
	}


	
	/**
	 * adjust height of the lensbox depending if the attributes are shown or not.
	 */
	private void adjustHeight(){
		if (this.showattribs) {
			this.height = getLens().getPropertyBindings().size()*LensDiagram.getLabelHeigth() + LensDiagram.LENSBOX_HEADER_HEIGHT;
		} else {
			this.height = LensDiagram.LENSBOX_HEADER_HEIGHT;
		}
		this.setLensdirty(true);
	}

	/**
	 * @return the lens
	 */
	public Lens getLens() {
		return lens;
	}
	
	/**
	 * Move LensBox to position.
	 * @param position the position to move the LensBox to
	 */
	public void setPosition(Point2D position){
		super.setPosition(position);
		if (getX() < 0){
			setPosition(new Point2D.Double(0, getY()));
		}
		if (getY() < 0){
			setPosition(new Point2D.Double(getX(), 0));
		}
		positionPropertyLabels();
		lens.setSavedPosition(position);
		lensdirty = true;
	}
	
	/**
	 * This flag indicates if the lens has changed/moved.
	 * @return the lensdirty
	 */
	public boolean isLensdirty() {
		return lensdirty;
	}


	/**
	 *  This flag sets if the lens has changed/moved.
	 * @param lensdirty the lensdirty to set
	 */
	public void setLensdirty(boolean lensdirty) {
		this.lensdirty = lensdirty;
	}


	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}


	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	/**
	 * positions the property labels.
	 */
	private void positionPropertyLabels() {
		int propertyIndex =0;
		for (PropertyLabel pl: getPropertyLabels()) {
			double x = this.getX() + LensDiagram.PROPERTY_ALIGNMENT_X;
			double y = this.getY() + LensDiagram.getLabelHeigth()*(propertyIndex-1) + LensDiagram.LENSBOX_HEADER_HEIGHT;
			pl.setPosition(new Point2D.Double(x, y));
			propertyIndex++;
		}
	}
	
	/**
	 * @return the z-index
	 */
	public int getZIndex() {
		return zIndex;
	}
	
	/**
	 * @param zIndex the z-index to set
	 */
	public void setZIndex(int zIndex) {
		this.zIndex = zIndex;
	}
	
	/**
	 * @return the number of ancestors of the lens box
	 */
	public int getLevel() {
		return lens.getNrOfAncestors();
	}
	
	/**
	 * @return true if mouse hovers lens box
	 */
	public boolean isMouseOver() {
		return this.mouseOver;
	}
	
	/**
	 * @param mouseOver true if mouse hovers lens box
	 */
	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}
	
	/**
	 * Add property label to lens box.
	 * @param pl property label to be added
	 */
	public void addPropertyLabel(PropertyLabel pl) {
		propertyLabels.add(pl);
		positionPropertyLabels();
	}
	
	/**
	 * @return property labels of lens box
	 */
	public ArrayList<PropertyLabel> getPropertyLabels() {
		return propertyLabels;
	}
	
	/**
	 * @return number of property labels of lens box
	 */
	public int getNrOfPropertyLabels() {
		return propertyLabels.size();
	}

	/**
	 * @return the showattribs
	 */
	public boolean isShowattribs() {
		return showattribs;
	}


	/**
	 * Sets the showattributes and ajust the height of the lensbox.
	 * @param showattribs the showattribs to set
	 */
	public void setShowattribs(boolean showattribs) {
		this.showattribs = showattribs;
		adjustHeight();
	}


	/**
	 * Sorts the propertylabel array alphabetically on the label.
	 */
	public void sortPropertyLabelAlphabetically(){
		Collections.sort(propertyLabels, new PropertyLabelAlphabetComparator());
		positionPropertyLabels();
		refreshIndexOfPropertyBinding();
		this.setLensdirty(true);
	}

	/**
	 * Sorts the propertylabel array heuristic on the label.
	 */
	public void sortPropertyLabelHeuristic(){
		Collections.sort(propertyLabels, propertylabelhcomparator);
		positionPropertyLabels();
		refreshIndexOfPropertyBinding();
		this.setLensdirty(true);
	}
	
	
	/**
	 * Change the index of a property label according to its y-position on screen.
	 * @param propertyLabel property label which index needs to be changed
	 */
	public void changeIndexOfPropertyLabel(PropertyLabel propertyLabel) {
		Collections.sort(propertyLabels, new LensDiagramComponentYComparator());
		refreshIndexOfPropertyBinding();
		positionPropertyLabels();
		this.setLensdirty(true);
	}


	/**
	 * Refreshes the indexes of the property binding object.
	 * neccesary when the order in the propertylabels array has changed.
	 */
	private void refreshIndexOfPropertyBinding() {
		int index = 0;
		for (PropertyLabel pl: propertyLabels) {
			pl.getPropertyBinding().setIndex(index);
			index++;
		}
	}
	
	/**
	 * @return the label of the lens box
	 */
	public LensBoxLabel getLensBoxLabel() {
		return this.label;
	}
	
	/**
	 * Draw the lens box.
	 * @param g2 the canvas
	 */
	public void draw(Graphics2D g2) {
		g2.setPaint(Color.WHITE);
	    g2.fill(this);
	    if (this.getLens().isDisplayed()) {
	    	if (this.isSelected()) {
		    	g2.setPaint(Color.RED); //a selected lens is red
	    	} else {
		    	g2.setPaint(Color.BLACK); // a shown lens is black 
	    	}
	    } else {
	    	g2.setPaint(Color.GRAY); //a hidden lens is grey
	    }
		g2.draw(this);
		label.draw(g2);
		//check if the attributes are visible or not.
		if (this.showattribs){
			for (PropertyLabel pl: getPropertyLabels()){
				pl.draw(g2);
			}
		}
	}
}
