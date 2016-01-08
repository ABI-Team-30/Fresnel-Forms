package nl.ou.fresnelforms.view;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 * Menu displayed when user right-clicks on the diagram.
 */
public class LensDiagramRightClickMenu extends JPopupMenu {
	
	private static final long serialVersionUID = -1493883138050747022L;
	private LensDiagram lensdiagram;
	
	/**
	 * Constructor that initializes the menu.
	 * @param alensdiagram The diagram the user right-clicked on
	 */
	public LensDiagramRightClickMenu(LensDiagram alensdiagram) {
		super(alensdiagram.getName());
		this.lensdiagram = alensdiagram;


		JMenuItem expAll = createExpandAllMenuItem("Expand all");
		this.add(expAll);
		
		JMenuItem collAll = createCollapseAllMenuItem("Collapse all");
		this.add(collAll);

		JMenuItem showAll = createShowAllMenuItem("Show all");
		this.add(showAll);

		JMenuItem hideAll = createHideAllMenuItem("Hide all");
		this.add(hideAll);
		
		JMenuItem rearrange = createRearrangeMenuItem("Rearrange");
		this.add(rearrange);

		JMenuItem sortAlpha = createsortAlphaMenuItem("Sort all alphabetically");
		this.add(sortAlpha);
		
		JMenuItem showStats = createShowStatsMenuItem("Show ontology statistics");
		this.add(showStats);
		
	}

	
	
	
	
	/**
	 * Sort all alphabetically menuitem.
	 * @param menucaption the caption of the menuitem
	 * @return a JMenuItem 
	 */
	private JMenuItem createsortAlphaMenuItem(String menucaption){
		JMenuItem sortall = new JMenuItem(menucaption);
		sortall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setBusyCursor();
					lensdiagram.sortAllAlphabetically();
					doRepaint();
				} finally {
					setDefaultCursor(); 
				}
		    }
		});
	 return sortall;
	}
	
	
	/**
	 * Show all menuitem.
	 * @param menucaption the caption of the menuitem
	 * @return a JMenuItem 
	 */
	private JMenuItem createShowAllMenuItem(String menucaption){
		JMenuItem showall = new JMenuItem(menucaption);
		showall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setBusyCursor();
					lensdiagram.showAll();
					doRepaint();
				} finally {
					setDefaultCursor(); 
				}
		    }
		});
	 return showall;
	}
	
	/**
	 * Hide all menuitem.
	 * @param menucaption the caption of the menuitem
	 * @return a JMenuItem 
	 */
	private JMenuItem createHideAllMenuItem(String menucaption){
		JMenuItem hideall = new JMenuItem(menucaption);
		hideall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setBusyCursor();
					lensdiagram.hideAll();
					doRepaint();
				} finally {
					setDefaultCursor(); 
				}
		    }
		});
	 return hideall;
	}

	/**
	 * Expand all menuitem.
	 * @param menucaption the caption of the menuitem
	 * @return a JMenuItem 
	 */
	private JMenuItem createExpandAllMenuItem(String menucaption){
		JMenuItem expall = new JMenuItem(menucaption);
		expall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setBusyCursor();
					lensdiagram.expandAll();
					lensdiagram.rearrange();
					doRepaint();
				} finally {
					setDefaultCursor(); 
				}
		    }
		});
	 return expall;
	}
	
	/**
	 * Collapse all menuitem.
	 * @param menucaption the caption of the menuitem
	 * @return a JMenuItem 
	 */
	private JMenuItem createCollapseAllMenuItem(String menucaption){
		JMenuItem collall = new JMenuItem(menucaption);
		collall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setBusyCursor();
					lensdiagram.collapseAll();
					lensdiagram.rearrange();
					doRepaint();
				} finally {
					setDefaultCursor(); 
				}
		    }
		});
	 return collall;
	}
	
	/**
	 * Rearrange menuitem.
	 * @param menucaption the caption of the menuitem
	 * @return a JMenuItem 
	 */
	private JMenuItem createRearrangeMenuItem(String menucaption){
		JMenuItem rearr = new JMenuItem(menucaption);
		rearr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setBusyCursor();
					lensdiagram.rearrange();
					doRepaint();
				} finally {
					setDefaultCursor(); 
				}
		    }
		});
	 return rearr;
	}
	
	/**
	 * Show statistics menuitem
	 * @param menucaption the caption of the menu item
	 * @return a JMenuItem
	 */
	private JMenuItem createShowStatsMenuItem(String menucaption){
		JMenuItem showstat = new JMenuItem(menucaption);
		showstat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					lensdiagram.displayOntologyStatistics();
		    }
		});
	 return showstat;
		
	}
	
    /**
	 *  repaint the diagram.
	 */
	private void doRepaint() {
		try {
			setBusyCursor();
	        lensdiagram.getParent().repaint();
	        lensdiagram.draw();
		} finally {
			setDefaultCursor(); 
		}
	}
	
	/**
	 * this method sets a busy cursor.
	 */
	private void setBusyCursor(){
		lensdiagram.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * this method sets the default cursor.
	 */
	private void setDefaultCursor(){
		lensdiagram.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}







	

}
