package nl.ou.fresnelforms.view;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

/**
 * Mouse listener for all actions performed on lens boxes and property labels.
 */
public class LensDiagramMouseAdapter extends MouseAdapter {
	
	private LensDiagram diagram;
	
	private int x = 0;
	private int y = 0;	
	private LensDiagramComponent selected = null;
	
	/**
	 * Constructor.
	 * @param diagram lens diagram to listen to
	 */
	public LensDiagramMouseAdapter(LensDiagram diagram) {
		this.diagram = diagram;
	}
	
	/**
	 * Checks for mouse overs on lens boxes and property labels and mouse exits from menus.
	 * @param e the mouseevent
	 */
	public void mouseMoved(MouseEvent e) {
    	double x = e.getX();
        double y = e.getY();
        Point2D p = new Point2D.Double(x, y);
        
        for (LensBox lb: diagram.getLensBoxes()) {
        	if (!lb.isMouseOver() && lb.contains(p)) {
        		lb.setMouseOver(true);
        		doRepaint();
        	} else if (lb.isMouseOver() && !lb.contains(p)){
        		lb.setMouseOver(false);
        		doRepaint();
        	}
        }
    }
	
	/**
	 * Checks for mouse clicks on menu options, lens boxes and property labels.
	 * @param e the mousevent
	 */
	public void mouseClicked(MouseEvent e) {
		x = e.getX();
        y = e.getY();
        Point2D p = new Point2D.Double(x, y);
        
        for (LensBox lb: diagram.getLensBoxes()) {
        	if (lb.contains(p)) {
        		//The click is on the lensbox
        		//Check for click on a property label
        		for (PropertyLabel pl: lb.getPropertyLabels()) {
        			if (pl.contains(p)) {
        				if (SwingUtilities.isLeftMouseButton(e)) {
        					pl.getPropertyBinding().setShown(!pl.getPropertyBinding().isShown());
        					doRepaint();
        					return;
        				} else if (SwingUtilities.isRightMouseButton(e)) {
        					PropertyLabelRightClickMenu menu = new PropertyLabelRightClickMenu(pl);
        					menu.show(e.getComponent(), x, y);
        					doRepaint();
        					return;
        				}
        			}
        		}
        		//The mouseclick is on the lens and not on a property label
        		if (SwingUtilities.isLeftMouseButton(e)) {
        			//With the left mouse button the lensbox is selected or deselected 
        			lb.setSelected(! lb.isSelected());
        			doRepaint();
        			return;
        		} else if (SwingUtilities.isRightMouseButton(e)) {
        			//With the right mouse button the popup menus for the lensbox is activated
	    			LensBoxRightClickMenu menu = new LensBoxRightClickMenu(lb);
	    			menu.show(e.getComponent(), x, y);
	    			doRepaint();
	    			return;
        		}
        	}
        }
        //No lensbox clicked so it must be the diagram.
        if (SwingUtilities.isRightMouseButton(e)) {
        	//Activate the right mousebutton popup menu for the diagram
			LensDiagramRightClickMenu menu = new LensDiagramRightClickMenu(diagram);
			menu.show(e.getComponent(), x, y);
			doRepaint();
		}
	}
	
    /** 
     * The mouse pressed event handler.
     * @param e the mouse event
     */
    public void mousePressed(MouseEvent e) { 
        x = e.getX();
        y = e.getY();
    }
    
    /**
     * Checks for dragging lens boxes and property labels.
     * @param e the mouse event
     */
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - x;
        int dy = e.getY() - y;
        
        if (selected == null) {
        	//selecteer een lensbox of property label
        	for (LensBox lb: diagram.getLensBoxes()) {
        		if (lb.contains(x, y)) {
        			//in ieder geval lensbox geselecteerd, maar misschien ook wel property label
        			selected = lb;
        			lb.setZIndex(diagram.getMaxZIndex()+1);
        			for (PropertyLabel pl: lb.getPropertyLabels()) {
        				if (pl.contains(x, y)) {
        					selected = pl;
        				}
        			}
        		}
        	}
        }
        
        if (selected != null) {
	        double newx = selected.getX() + dx;
	        double newy = selected.getY() + dy;
	        selected.setPosition(new Point2D.Double(newx, newy));
	        doRepaint();
        }
        x = e.getX();
        y = e.getY(); 
    }
    
    /**
     * Checks for dragging lens boxes and property labels.
     * @param e the mouse event
     */
    public void mouseReleased(MouseEvent e) { 
    	if (selected instanceof PropertyLabel) {
    		PropertyLabel pl = (PropertyLabel) selected;
    		pl.changeIndex();
    		doRepaint();
    	}
    	else if (selected instanceof LensBox){
    		LensBox lb = (LensBox) selected;
    		diagram.arrangeOverlap(lb);
    		doRepaint();
    	}
    	selected = null;
    	doRepaint();
    }

    /**
	 *  repaint the diagram.
	 */
	private void doRepaint() {
		try {
			setBusyCursor();
	        diagram.getParent().repaint();
	        diagram.draw();
		} finally {
			setDefaultCursor(); 
		}
	}
	
	
	/**
	 * this method sets a busy cursor
	 */
	private void setBusyCursor(){
		diagram.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * this method sets the default cursor
	 */
	private void setDefaultCursor(){
		diagram.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}


	
}
