package nl.ou.fresnelforms.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresnel.Lens;
import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.ontology.Class;
import nl.ou.fresnelforms.ontology.ObjectProperty;
import nl.ou.fresnelforms.ontology.Ontology;
import nl.ou.fresnelforms.ontology.Property;

/**
 * Lens diagram display class responsible for drawing lens boxes, property
 * labels and property arrows.
 */
public class LensDiagram extends JPanel {

	private static final int TRANSLATE_FACTOR = 3;
	private static final long serialVersionUID = 1L;
	private static final double START_Y = 50;
	private static final double DEFAULT_LENSBOX_X_DISTANCE = 80;
	private static final double DEFAULT_LENSBOX_Y_DISTANCE = 100;
	private static final double MIN_LENSBOX_X_DISTANCE = 20;
	private static final double MIN_LENSBOX_Y_DISTANCE = 40;
	/** default lensbox width. */
	protected static final double LENSBOX_WIDTH = 200;
	/** default object property spacing. */
	protected static final double OBJECT_PROPERTY_SPACING = 20;
	/** default property alligment X. */
	protected static final double PROPERTY_ALIGNMENT_X = 10;
	/** default lesnbox header height. */
	protected static final double LENSBOX_HEADER_HEIGHT = 50;
	/** default normal font. */
	protected static final Font FONT_NORMAL = new Font("default", Font.PLAIN, 12);
	/** default bold font. */
	protected static final Font FONT_BOLD = new Font("default", Font.BOLD, 12);
	private static final int STATISTICSBOXWIDTH =208;
	private static final int STATISTICSBOXHEIGHT = 300;

	/** default label height. */
	private static int labelHeight = 0;
	private double width;

	private double height;
	private Fresnel fresnel;
	private Graphics2D graphics;
	private boolean init = true;
	private List<LensBox> lensBoxes;
	private List<LensGeneralizationArrow> lensGeneralizationArrows;
	private List<PropertyArrow> propertyArrows;
	private LensDiagramMouseAdapter ldma;
	private PropertyLabelHeuristicComparator proplabelheurcomparator;

	/**
	 * Constructor that initializes the lens diagram.
	 * 
	 * @param fresnel fresnel object that gets displayed by lens diagram
	 * @param dim  dimensions of lens diagram
	 */
	public LensDiagram(Fresnel fresnel, Dimension dim) {
		super();

		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.fresnel = fresnel;
		this.width = dim.width;
		this.height = dim.height;
		lensBoxes = new ArrayList<LensBox>();
		lensGeneralizationArrows = new ArrayList<LensGeneralizationArrow>();
		propertyArrows = new ArrayList<PropertyArrow>();
		ldma = new LensDiagramMouseAdapter(this);
		addMouseMotionListener(ldma);
		addMouseListener(ldma);
	}

	/**
	 * @return the label height
	 */
	protected static int getLabelHeigth() {
		return labelHeight;
	}

	/**
	 * Sorts all the lensboxes alphabetically.
	 */
	public void sortAllAlphabetically() {
		for (LensBox lb : lensBoxes) {
			//lens its dirty bit is set.
			lb.sortPropertyLabelAlphabetically();
		}
	}
	
	/**
	 * Shows all the lensboxes by setting displayed true.
	 */
	public void showAll() {
		for (LensBox lb : lensBoxes) {
			lb.getLens().setDisplayed(true);
			lb.setLensdirty(true);
		}
	}

	/**
	 * Hides all the lensboxes by setting displayed false.
	 */
	public void hideAll() {
		for (LensBox lb : lensBoxes) {
			lb.getLens().setDisplayed(false);
			lb.setLensdirty(true);
		}
	}

	/**
	 * Expands all the lensboxes by setting the showattrib true.
	 */
	public void expandAll() {
		for (LensBox lb : lensBoxes) {
			lb.setShowattribs(true);
			lb.setLensdirty(true);
		}
	}

	/**
	 * Collapses all the lensboxes by setting the showattrib false.
	 */
	public void collapseAll() {
		for (LensBox lb : lensBoxes) {
			lb.setShowattribs(false);
			lb.setLensdirty(true);
		}
	}

	/**
	 * Rearrange the lensboxes, adjusting for size.
	 */
	public void rearrange() {
		this.resetLensPositions();
	}

	/**
	 * Draw the lens diagram.
	 */
	public void draw() {
		getGraphics().setFont(LensDiagram.FONT_NORMAL);
		labelHeight = getGraphics().getFontMetrics().getHeight();
		if (init) {
			initLensBoxes();
			initLensGeneralizationArrows();
			initPropertyArrows();
			init = false;
		}
		drawLensGeneralizationArrows();
		drawPropertyArrows();
		drawLensBoxes();
	}

	/**
	 * Draw lens boxes.
	 */
	private void drawLensBoxes() {
		for (LensBox lb : lensBoxes) {
			Graphics2D g2 = getGraphics();
			lb.draw(g2);
			lb.setLensdirty(false);
		}
	}

	/**
	 * Draw generalization arrows.
	 */
	private void drawLensGeneralizationArrows() {
		Graphics2D g2 = getGraphics();
		for (LensGeneralizationArrow lga : this.lensGeneralizationArrows) {
			// check if child or parent lensbox has moved, ifso adjust path.
			lga.refresh();
			// draw the arrow in red for a selected parent or child lensbox
			if (lga.getChild().isSelected() || lga.getParent().isSelected()) {
				g2.setColor(Color.RED);
			} else {
				g2.setColor(Color.BLACK);
			}
			g2.draw(lga);
		}
	}

	/**
	 * Draw property arrows for lens box which user hovers.
	 */
	private void drawPropertyArrows() {
		Graphics2D g2 = getGraphics();
		LensBox lbdomain = null;
		for (PropertyArrow pa : propertyArrows) {
			lbdomain = pa.getDomain();
			if (lbdomain.isMouseOver() || lbdomain.isSelected()) {
				// show attrib arrow only if the atributes are show.
				if (pa.getDomain().isShowattribs()) {
					// show attribute arrow in green for a selected lensbox
					if (lbdomain.isSelected()) {
						g2.setColor(Color.GREEN);
					}
					// show attribute arrows in blue for the active lensbox
					if (lbdomain.isMouseOver()) {
						g2.setColor(Color.BLUE);
					}
					// check if domain or range lensbox has moved.if so adjust
					// path
					pa.refresh();
					g2.draw(pa);
				}
			}
		}
	}

	/**
	 * Initialises lens boxes from the fresnel lens definitions.
	 */
	private void initLensBoxes() {
		this.lensBoxes = new ArrayList<LensBox>();
		//init the comparator
		Ontology ontology = this.fresnel.getOntology();
		proplabelheurcomparator = new PropertyLabelHeuristicComparator(ontology.getRangeClassFrequenties(), ontology.getPropertyFrequenties());
		if (fresnel != null) {
			for (Lens lens : fresnel.getLenses()) {
				LensBox lb = new LensBox(lens, false, proplabelheurcomparator);
				// add property labels to the lensbox
				// why not in the lensbox constructor?
				for (PropertyBinding p : lens.getPropertyBindings()) {
					PropertyLabel pl = new PropertyLabel(p, lb);
					lb.addPropertyLabel(pl);
				}
				lensBoxes.add(lb);
			}
		}
	}

	/**
	 * Reset the lens positions.
	 */
	public void resetLensPositions() {
		List<LensDiagramLayer> layers = new ArrayList<LensDiagramLayer>();

		// add the lensboxes with equal level value to the same layer
		layers = initLayers(layers);

		Collections.sort(layers, new LensDiagramLayerLevelComparator());
		// draw the lensboxes ordered by level.
		// adjust the size of the lensdiagram in the proces.
		resetPositionOfLensBoxes(layers);
		
		//sort the lensboxes for drawing acording to the Zindex.
		Collections.sort(lensBoxes, new LensBoxZIndexComperator());


		Dimension dim = new Dimension();
		dim.width = (int) Math.round(this.width);
		dim.height = (int) Math.round(this.height);
		this.setPreferredSize(dim);

		// reinit the arrows
		initLensGeneralizationArrows();
		initPropertyArrows();

	}

	/**
	 * Reset the postions of the lensboxes from the layers collection.
	 * 
	 * @param layers the layers ordered by level
	 */
	private void resetPositionOfLensBoxes(List<LensDiagramLayer> layers) {
		double currentY = START_Y;
		double layerWidth = 0;
		double currentX = 0;
		// loop the layer collection
		for (LensDiagramLayer layer : layers) {
			// get the lensboxes for this layer
			List<LensBox> lensBoxes = layer.getLensBoxes();
			// determine the total layer width
			layerWidth = 0;
			for (LensBox lb : lensBoxes) {
				layerWidth += lb.getWidth() + DEFAULT_LENSBOX_X_DISTANCE;
			}
			// adjust layerwidth if lensboxes are present
			if (layerWidth > 0) {
				layerWidth -= DEFAULT_LENSBOX_X_DISTANCE;
			}
			// set width of the diagram if layerwidth is larger.
			this.width = Math.max(this.width, layerWidth);

			// calc the left most position of the first lensbox
			currentX = (this.width / 2) - layerWidth / 2;
			// reset the lensboxes postions of this layer
			for (LensBox lb : lensBoxes) {
				lb.setPosition(new Point2D.Double(currentX, currentY));
				currentX += lb.getWidth() + DEFAULT_LENSBOX_X_DISTANCE;
			}
			// adjust y for next level
			currentY += layer.getHeight() + DEFAULT_LENSBOX_Y_DISTANCE;

			// set height of the diagram if layerheight is larger.
			this.height = Math.max(this.height, currentY);
		}
	}

	/**
	 * Init layers based on the level of the lensboxes. add the lensboxes with
	 * equal level value to the same layer
	 * 
	 * @param layers the layers collection
	 */
	private List<LensDiagramLayer> initLayers(List<LensDiagramLayer> layers) {
		// loop the lensboxes
		for (LensBox lb : lensBoxes) {
			int level = lb.getLevel();
			LensDiagramLayer layer = null;
			// loop the layers, searching for the lensbox level
			for (LensDiagramLayer l : layers) {
				if (l.getLevel() == level) {
					layer = l;
				}
			}
			// if no layer is found add a new layer for this lensbox
			if (layer == null) {
				layer = new LensDiagramLayer(level);
				layers.add(layer);
			}
			// add the lensbox to the layer
			layer.addLensBox(lb);
			//set the Zindex as a reverse of the level.
			lb.setZIndex(-1*level);
		}
		return layers;
	}

	/**
	 * set the fresnel model for the diagram.
	 * 
	 * @param fresnel the fresnel model
	 */
	public void setFresnel(Fresnel fresnel) {
		this.fresnel = fresnel;
	}

	/**
	 * Initializes property arrows.
	 */
	private void initPropertyArrows() {
		this.propertyArrows = new ArrayList<PropertyArrow>();
		for (LensBox domainLensBox : lensBoxes) {
			List<PropertyBinding> properties = domainLensBox.getLens().getPropertyBindings();
			for (PropertyBinding propertyBinding : properties) {
				nl.ou.fresnelforms.ontology.Property owlProperty = propertyBinding.getProperty().getProperty();
				if (owlProperty instanceof ObjectProperty) {
					ObjectProperty objectProperty = (ObjectProperty) owlProperty;
					List<Class> owlRangeClasses = objectProperty.getRange();
					for (Class owlRangeClass : owlRangeClasses) {
						for (LensBox rangeLensBox : lensBoxes) {
							if (rangeLensBox.getLens().getClassLensDomain() == owlRangeClass) {
								propertyArrows.add(new PropertyArrow(propertyBinding, domainLensBox, rangeLensBox));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Initializes generalization arrows.
	 */
	private void initLensGeneralizationArrows() {
		this.lensGeneralizationArrows = new ArrayList<LensGeneralizationArrow>();
		for (LensBox childLensBox : lensBoxes) {
			for (Lens parentLens : childLensBox.getLens().getParents()) {
				for (LensBox parentLensBox : lensBoxes) {
					if (parentLensBox.getLens().equals(parentLens)) {
						this.lensGeneralizationArrows.add(new LensGeneralizationArrow(childLensBox, parentLensBox));
					}
				}
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.graphics = (Graphics2D) g;
		draw();
	}

	/**
	 * @return the 2D graphics
	 */
	public Graphics2D getGraphics() {
		return this.graphics;
	}

	/**
	 * @return the z-index of the lens box with the largest z-index
	 */
	public int getMaxZIndex() {
		int maxZIndex = 0;
		for (LensBox lb : lensBoxes) {
			if (lb.getZIndex() > maxZIndex) {
				maxZIndex = lb.getZIndex();
			}
		}
		return maxZIndex;
	}

	/**
	 * @return lens boxes in lens diagram
	 */
	public List<LensBox> getLensBoxes() {
		return this.lensBoxes;
	}

	/**
	 * @param init sets the init value
	 */
	public void setInit(boolean init) {
		this.init = init;
	}

	/**
	 * Recursive check if lens box overlaps other lens boxes and moves lens box
	 * if needed.
	 * 
	 * @param lb lens box to be checked
	 */
	public void arrangeOverlap(LensBox lb) {
		LensBox overlap;
		while ((overlap = checkOverlap(lb)) != null) {
			translateBox(lb, overlap);
			LensBox newOverlap;
			while ((newOverlap = checkOverlap(lb)) != null) {
				arrangeOverlap(newOverlap);
			}
		}
	}

	/**
	 * Checks is lens box overlaps other lens boxes.
	 * 
	 * @param lensBox lens box to be checked
	 * @return true if lens box overlaps other lens boxes
	 */
	private LensBox checkOverlap(LensBox lensBox) {
		List<LensBox> sortedLensBoxes = new ArrayList<LensBox>(lensBoxes);
		Collections.sort(sortedLensBoxes, new LensBoxDistanceComparator(lensBox));
		// loop the sorted listboxes and check for intersection
		for (LensBox lb : sortedLensBoxes) {
			if (lb.intersects(lensBox) && !lb.equals(lensBox)) {
				return lb;
			}
		}
		return null;
	}

	/**
	 * Set new position for lens box that overlaps another lens box so they they
	 * won't overlap anymore.
	 * 
	 * @param lb lens box that overlaps
	 * @param overlap lens box that gets overlapped
	 */
	private void translateBox(LensBox lb, LensBox overlap) {
		Point2D position;
		Rectangle2D intersect = lb.createIntersection(overlap);
		if (intersect.getWidth() < intersect.getHeight()) {
			position = translateHorizontal(lb, overlap.getCenterX());
		} else {
			position = translateVertical(lb, overlap);
		}
		lb.setPosition(position);
	}

	/**
	 * Calculates the new horizontal position of the lensbox.
	 * 
	 * @param lb lensbox
	 * @param center the centre
	 * @return a point, the new position of the lensbox
	 */
	private Point2D translateHorizontal(LensBox lb, double center) {
		double newx;
		if (lb.getCenterX() < center) {
			newx = center - (TRANSLATE_FACTOR * LENSBOX_WIDTH / 2 + MIN_LENSBOX_X_DISTANCE);
		} else {
			newx = center + (LENSBOX_WIDTH / 2 + MIN_LENSBOX_X_DISTANCE);
		}
		return new Point2D.Double(newx, lb.getY());
	}

	/**
	 * Calculates the new vertical position of the lensbox.
	 * 
	 * @param lb lensbox
	 * @param overlap the lensbox that overlaps
	 * @return een punt
	 */
	private Point2D translateVertical(LensBox lb, LensBox overlap) {
		double newy;
		double center = overlap.getCenterY();
		if (lb.getCenterY() < center) {
			newy = center - (overlap.getHeight() / 2 + MIN_LENSBOX_Y_DISTANCE + lb.getHeight());
		} else {
			newy = center + (overlap.getHeight() / 2 + MIN_LENSBOX_Y_DISTANCE);
		}
		return new Point2D.Double(lb.getX(), newy);
	}
	
	
	/**
	 * Shows the ontology statistics in a scrollable message box.
	 */
	public void displayOntologyStatistics(){
		JLabel  labelcomponent;
		StringBuilder msg = new StringBuilder();
		
		//layout definition format strings
		String tableheader = "<html><table style='text-align: left; width: 100%%;' border='1' cellpadding='0' cellspacing='0'><tbody>";
		String generalinfoheader = "<tr align='center:'><td colspan='2' rowspan='1'><h3>General info</h3></td></tr>";
		String inforow = "<tr style='background-color: white;'><td style='width: 75%%;' >%s</td><td style='text-align: center; width: 25%%;'><b>%d</b></td></tr>";
		String rangeclassfrequentieHeader = "<tr align='center:'><td colspan='2' rowspan='1'><h3>Rangeclass frequency list</h3></td></tr>";
		String propertyfrequentieHeader = "<tr align='center:'><td colspan='2' rowspan='1'><h3>Property frequency list</h3></td></tr>";
		String tablefooter = "</tbody></table></html>";

		//proces the format strings
		Ontology ont = fresnel.getOntology();
		msg.append(tableheader);
		//proces the general info datapart
		msg.append(generalinfoheader);
		msg.append(String.format(inforow,"Nr. of classes",ont.getNrOfClasses()));
		msg.append(String.format(inforow,"Nr. of properties",ont.getNrOfProperties()));
		msg.append(String.format(inforow,"Nr. of dataproperties",ont.getNrOfDataProperties()));
		msg.append(String.format(inforow,"Nr. of objectproperties",ont.getNrOfObjectProperties()));
		msg.append(String.format(inforow,"Nr. of dataproperties with ranges",ont.getNrOfDataPropWithRanges()));
		msg.append(String.format(inforow,"Nr. of objectproperties with ranges",ont.getNrOfObjectPropWithRanges()));
		msg.append(String.format(inforow,"Rangeclass frequentielist size",ont.getRangeClassFrequenties().size()));
		msg.append(String.format(inforow,"Property frequentielist size",ont.getPropertyFrequenties().size()));
		//proces the rangeclass frequenty list datapart
		msg.append(rangeclassfrequentieHeader);
		for (Map.Entry<Class, Integer> entry: ont.getRangeClassFrequenties().entrySet() ){
			msg.append(String.format(inforow,entry.getKey().getResourceName() ,entry.getValue()));
		}
		//proces the property frequenty list datapart
		msg.append(propertyfrequentieHeader);
		for (Map.Entry<Property, Integer> entry: ont.getPropertyFrequenties().entrySet() ){
			msg.append(String.format(inforow,entry.getKey().getResourceName(),entry.getValue()));
		}
		//process the footer
		msg.append(tablefooter);
		
		//setup the scrollable messagebox
		labelcomponent = new JLabel(msg.toString());
		JScrollPane view = new JScrollPane(labelcomponent);
		view.setPreferredSize(new Dimension(STATISTICSBOXWIDTH,STATISTICSBOXHEIGHT)); 
		view.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		JOptionPane.showMessageDialog(this, view , "Ontology statistics", JOptionPane.INFORMATION_MESSAGE);
	}

}
