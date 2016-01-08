package nl.ou.fresnelforms.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import nl.ou.fresnelforms.fresnel.FresnelStyleClass;
import nl.ou.fresnelforms.fresnel.PropertyBinding;

/**
 * Menu displayed when user right-clicks on lens box.
 */
public class LensBoxRightClickMenu extends JPopupMenu {
	
	private static final long serialVersionUID = -1493883138050747022L;
	private LensBox lensBox;

	/**
	 * Constructor that initializes the menu.
	 * @param lensBox lens box user right-clicked on
	 */
	public LensBoxRightClickMenu(final LensBox lensBox) {
		super(lensBox.getLens().getName());
		this.lensBox = lensBox;
		
		String expcolcaption = getExpColCaption(lensBox);

		JMenuItem expCol = createExpColMenuItem(expcolcaption);
		this.add(expCol);
		
		// refactored code fragment to method getShowHideCaption - TT27112014
		String showHideString = getShowHideCaption(lensBox);
		
		JMenuItem showHide = createHideShowMenuItem(showHideString);
		this.add(showHide);

		JMenuItem addProperty = createPropertyMenuItem();
		this.add(addProperty);
		
		JMenuItem editCSS = createCSSMenuItem();
		this.add(editCSS);
		
		JMenuItem sortAlphabetically = createSortAlphaMenuItem();
		this.add(sortAlphabetically);

		JMenuItem sortHeuristic = createSortHeuristicMenuItem();
		this.add(sortHeuristic);
	}


	/**
	 * creates the sort alphabetically menuitem.
	 * @return a JMenuitem
	 */
	private JMenuItem createSortAlphaMenuItem(){
		JMenuItem sortAlphabetically = new JMenuItem("Sort alphabetically");
		sortAlphabetically.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lensBox.sortPropertyLabelAlphabetically();
		        repaintDiagram((JPopupMenu)((JMenuItem)e.getSource()).getParent());
				}
			});
		return sortAlphabetically;
	}
	
	/**
	 * creates the sort heuristic menuitem.
	 * @return the sort heuristic menuitem
	 */
	private JMenuItem createSortHeuristicMenuItem(){
		JMenuItem sortHeuristic = new JMenuItem("Sort heuristic");
		sortHeuristic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lensBox.sortPropertyLabelHeuristic();
		        repaintDiagram((JPopupMenu)((JMenuItem)e.getSource()).getParent());
				}
			});
		return sortHeuristic;
	}
	
	/**
	 * Create the Expand/Collapse menuitem.
	 * @param expcolstring the caption of the menuitem
	 * @return a JMenuItem 
	 */
	private JMenuItem createExpColMenuItem(String expcolstring){
		JMenuItem expcol = new JMenuItem(expcolstring);
		expcol.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lensBox.setShowattribs(! lensBox.isShowattribs());
		        repaintDiagram((JPopupMenu)((JMenuItem)e.getSource()).getParent());
		    }
		});
	 return expcol;
	}
	
	private void repaintDiagram(JPopupMenu jpm){
		LensDiagram diagram = (LensDiagram)jpm.getInvoker();
        diagram.getParent().repaint();
        diagram.draw();
	}
	
/**
 * Create the Hide/Show menuitem.
 * @param showHideString the caption of the menuitem
 * @return a JMenuItem 
 */
private JMenuItem createHideShowMenuItem(String showHideString){
	JMenuItem showHide = new JMenuItem(showHideString);
	showHide.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			lensBox.getLens().setDisplayed(!lensBox.getLens().isDisplayed());
			JPopupMenu jpm = (JPopupMenu)((JMenuItem)e.getSource()).getParent();
			LensDiagram diagram = (LensDiagram)jpm.getInvoker();
			diagram.getParent().repaint();
	    }
	});
 return showHide;
}
	
/**
 * Create the add property menuitem.
 * create the property menuitem.
 * @return a JMenuItem 
 */
private JMenuItem createPropertyMenuItem(){
	JMenuItem addProperty = new JMenuItem("Add Property");
	addProperty.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			
			//code fragment refactored to class
			//Create the property input panel
			PropertyInputPanel inputPanel = new PropertyInputPanel(lensBox);
			
			int result = JOptionPane.showConfirmDialog(null, inputPanel, "Add Property", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION){
				nl.ou.fresnelforms.ontology.Property property = new nl.ou.fresnelforms.ontology.Property(inputPanel.getUriFieldText());
				nl.ou.fresnelforms.fresnel.Property fProperty = new nl.ou.fresnelforms.fresnel.Property(lensBox.getLens().getFresnel(), property);
				PropertyBinding pBinding = new PropertyBinding(fProperty, lensBox.getLens(), lensBox.getPropertyLabels().size());
				lensBox.getLens().addPropertyBinding(pBinding);
				JPopupMenu jpm = (JPopupMenu)((JMenuItem)arg0.getSource()).getParent();
				LensDiagram diagram = (LensDiagram)jpm.getInvoker();
				diagram.setInit(true);
				diagram.getParent().repaint();
			}
		}
	});
  return addProperty;
}


/**
 *  Create the CSS style input menuitem.
 * @return a JMenuItem 
 */
private JMenuItem createCSSMenuItem(){
	JMenuItem editCSS = new JMenuItem("Edit CSS");
	editCSS.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {


			CSSInputPanel panel = new CSSInputPanel(lensBox);
			
			int result = JOptionPane.showConfirmDialog(
							null,
							panel,
							"Edit css of " +lensBox.getLens().getURI(),
							JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				lensBox.getLens().getFresnelStyle().setFresnelStyle(FresnelStyleClass.RESOURCE_STYLE, panel.getResourceInput());
				lensBox.getLens().getFresnelStyle().setFresnelStyle(FresnelStyleClass.PROPERTY_STYLE, panel.getPropertyInput());
				lensBox.getLens().getFresnelStyle().setFresnelStyle(FresnelStyleClass.LABEL_STYLE, panel.getLabelInput());
				lensBox.getLens().getFresnelStyle().setFresnelStyle(FresnelStyleClass.VALUE_STYLE, panel.getValueInput());
		    }
			
	    }
	});
  return editCSS;
}


	/**
	 * Get the menu caption Collapse/Expand for the lensbox.
	 * @param lensBox the lensbox
	 * @return the string with the right caption
	 */
	private String getExpColCaption(final LensBox lensbox){
		String expcolString = "Expand";
		if (lensBox.isShowattribs()) {
			expcolString = "Collapse";
		}
		return expcolString;
	}



	/**
	 * Get the menu caption show/hide for the lensbox.
	 * @param lensBox the lensbox
	 * @return the string with the right caption
	 */
	private String getShowHideCaption(final LensBox lensBox) {
		String showHideString = "Show";
		if (lensBox.getLens().isDisplayed()) {
			showHideString = "Hide";
		}
		return showHideString;
	}
	
	/**
	 * @return the lensbox
	 */
	public LensBox getLensBox() {
		return lensBox;
	}


	
}
