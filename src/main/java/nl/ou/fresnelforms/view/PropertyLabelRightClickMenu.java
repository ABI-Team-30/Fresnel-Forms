package nl.ou.fresnelforms.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import nl.ou.fresnelforms.fresnel.FresnelStyleClass;
import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.fresnel.PropertyType;

/**
 * Menu displayed when user right-clicks on property label.
 */
public class PropertyLabelRightClickMenu extends JPopupMenu {

	private static final long serialVersionUID = -8237047320853765651L;
	private PropertyLabel propertyLabel;

	/**
	 * Constructor that initializes the menu.
	 * 
	 * @param propertyLabel property label user right-clicked on
	 */
	public PropertyLabelRightClickMenu(final PropertyLabel propertyLabel) {
		super(propertyLabel.getPropertyBinding().getLabel());
		this.propertyLabel = propertyLabel;

		String labelShowHideString = getLabelShowHideCaption(propertyLabel);
		JMenuItem labelShowHide = createMenuItemLabelShowHide(labelShowHideString);
		this.add(labelShowHide);

		JMenuItem relabel = createMenuItemRelabel(propertyLabel);
		this.add(relabel);

		JMenuItem changeType = createMenuItemChangeType(propertyLabel);
		this.add(changeType);

		String showHideString = getShowHideCaption(propertyLabel);
		JMenuItem showHide = createMenuItemShowHide(showHideString);
		this.add(showHide);

		JMenuItem editCSS = createMenuItemCSS();
		this.add(editCSS);

		JMenuItem delimiter = createMenuItemDelimiter(propertyLabel);
		this.add(delimiter);
		
		JMenuItem autoCompletion = createMenuItemAutoCompletion();
		if (autoCompletion != null) {
			this.add(autoCompletion);
		}
		
		String labelisList = getLabelisListCaption(propertyLabel);
		JMenuItem islist = createMenuItemisList(labelisList);
		this.add(islist);

		String labelMandatory = getLabelMandatoryCaption(propertyLabel);
		JMenuItem mandatory = createMenuItemMandatory(labelMandatory);
		this.add(mandatory);

	}

	
	/**
	 * get the menu caption multivalue/singlevalue label for the property
	 * 
	 * @param propertyLabel the propertyLabel
	 * @return the string with the right caption
	 */
	private String getLabelisListCaption(final PropertyLabel propertyLabel) {
		String isListString = "Multi value";
		if (propertyLabel.getPropertyBinding().isList() ) {
			isListString = "Single value";
		}
		return isListString;
	}
	
	/**
	 * get the menu caption mandatory/optional label for the property
	 * 
	 * @param propertyLabel the propertyLabel
	 * @return the string with the right caption
	 */
	private String getLabelMandatoryCaption(final PropertyLabel propertyLabel) {
		String mandatoryString = "Mandatory";
		if (propertyLabel.getPropertyBinding().isMandatory() ) {
			mandatoryString = "Optional";
		}
		return mandatoryString;
	}
	
	/**
	 * get the menu caption show/hide label for the property
	 * 
	 * @param propertyLabel the propertyLabel
	 * @return the string with the right caption
	 */
	private String getLabelShowHideCaption(final PropertyLabel propertyLabel) {
		String showHideString = "Show label";
		if (propertyLabel.getPropertyBinding().isLabelEnabled()) {
			showHideString = "Hide label";
		}
		return showHideString;
	}

	/**
	 * get the menu caption show/hide for the lensbox
	 * 
	 * @param lensBox the lensbox
	 * @return the string with the right caption
	 */
	private String getShowHideCaption(final PropertyLabel propertyLabel) {
		String showHideString = "Show";
		if (propertyLabel.getPropertyBinding().isShown()) {
			showHideString = "Hide";
		}
		return showHideString;
	}

	/**
	 * @return the relabel jmenuitem
	 */
	private JMenuItem createMenuItemRelabel(final PropertyLabel propertyLabel) {
		JMenuItem relabel = new JMenuItem("Relabel");
		relabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String label = (String) JOptionPane.showInputDialog(null, "Please enter a new label:", "Relabel "
						+ propertyLabel.getPropertyBinding().getLabel(), JOptionPane.PLAIN_MESSAGE, null, null,
						propertyLabel.getPropertyBinding().getLabel());
				if (label != null) {
					// User at least didn't press Cancel
					if (label.isEmpty()) {
						// Don't allow empty labels
						propertyLabel.getPropertyBinding().setLabel(
								propertyLabel.getPropertyBinding().getDefaultLabel());
					} else {
						// User provided a new label
						propertyLabel.getPropertyBinding().setLabel(label);
					}
				}
			}
		});
		return relabel;
	}

	/**
	 * @return the delimiter jmenuitem
	 */
	private JMenuItem createMenuItemDelimiter(final PropertyLabel propertyLabel) {
		JMenuItem delimiter = new JMenuItem("Delimiter");
		delimiter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String delimiter = (String) JOptionPane.showInputDialog(null,
						"Please enter the delimiter for multiple values:", "Delimiter "
								+ propertyLabel.getPropertyBinding().getDelimiter(), JOptionPane.PLAIN_MESSAGE, null,
						null, propertyLabel.getPropertyBinding().getDelimiter());
				if (delimiter != null) {
					// User at least didn't press Cancel
					if (delimiter.isEmpty()) {
						// Don't allow empty delimiters
						propertyLabel.getPropertyBinding().setDelimiter(PropertyBinding.DEFAULT_DELIMITER);
					} else {
						// User provided a new delimiter
						propertyLabel.getPropertyBinding().setDelimiter(delimiter);
					}
				}
			}
		});
		return delimiter;
	}

	/**
	 * @return
	 */
	private JMenuItem createMenuItemChangeType(final PropertyLabel propertyLabel) {
		JMenuItem changeType = new JMenuItem("Change type");
		changeType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] options = { PropertyType.URI, PropertyType.IMAGE, PropertyType.EXTERNAL_LINK };
				PropertyType type = (PropertyType) JOptionPane.showInputDialog(null,
						"Select the value type of property:", "Change type of "
								+ propertyLabel.getPropertyBinding().getLabel(), JOptionPane.PLAIN_MESSAGE, null,
						options, propertyLabel.getPropertyBinding().getPropertyType());
				
				if (type != null) {
					// User at least didn't press Cancel
					propertyLabel.getPropertyBinding().setPropertyType(type);
				}
			}
		});
		return changeType;
	}

	/**
	 * @param showHideString the caption string
	 * @return a JMenuItem
	 */
	private JMenuItem createMenuItemLabelShowHide(String showHideString) {
		JMenuItem showHide = new JMenuItem(showHideString);
		showHide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				propertyLabel.getPropertyBinding()
						.setLabelEnabled(!propertyLabel.getPropertyBinding().isLabelEnabled());
			}
		});
		return showHide;
	}

	/**
	 * @param showHideString the caption string
	 * @return a JMenuItem
	 */
	private JMenuItem createMenuItemAutoCompletion() {
		if (propertyLabel.getPropertyBinding().isAutoCompletionAvailable()) {
			String menuString = "Auto completion";
			if (propertyLabel.getPropertyBinding().isAutoCompletionEnabled()) {
				menuString = "No " + menuString;
			}
			JMenuItem autoCompletion = new JMenuItem(menuString);
			autoCompletion.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (propertyLabel.getPropertyBinding().isAutoCompletionAvailable()) {
						propertyLabel.getPropertyBinding().setAutoCompletionEnabled(
								!propertyLabel.getPropertyBinding().isAutoCompletionEnabled());
					}
				}
			});
			return autoCompletion;
		} else {
			return null;
		}
	}

	/**
	 * @param showHideString the caption string
	 * @return a JMenuItem
	 */
	private JMenuItem createMenuItemShowHide(String showHideString) {
		JMenuItem showHide = new JMenuItem(showHideString);
		showHide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				propertyLabel.getPropertyBinding().setShown(!propertyLabel.getPropertyBinding().isShown());
			}
		});
		return showHide;
	}

	/**
	 * @param isListString the caption string
	 * @return a JMenuItem
	 */
	private JMenuItem createMenuItemisList(String isListString) {
		JMenuItem islist = new JMenuItem(isListString);
		islist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				propertyLabel.getPropertyBinding().setIsList(!propertyLabel.getPropertyBinding().isList()); 
			}
		});
		return islist;
	}
	
	/**
	 * @param mandatoryString the caption string
	 * @return a JMenuItem
	 */
	private JMenuItem createMenuItemMandatory(String mandatoryString) {
		JMenuItem mandatory = new JMenuItem(mandatoryString);
		mandatory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				propertyLabel.getPropertyBinding().setMandatory(!propertyLabel.getPropertyBinding().isMandatory() ); 
			}
		});
		return mandatory;
	}

	
	/**
	 * @return a JMenuItem
	 */
	private JMenuItem createMenuItemCSS() {
		JMenuItem editCSS = new JMenuItem("Edit CSS");
		editCSS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				CSSInputPanel panel = new CSSInputPanel(propertyLabel);

				int result = JOptionPane.showConfirmDialog(null, panel, "Edit css of "
						+ propertyLabel.getPropertyBinding().getLabel(), JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					propertyLabel.getPropertyBinding().getFresnelStyle()
							.setFresnelStyle(FresnelStyleClass.PROPERTY_STYLE, panel.getPropertyInput());
					propertyLabel.getPropertyBinding().getFresnelStyle()
							.setFresnelStyle(FresnelStyleClass.LABEL_STYLE, panel.getLabelInput());
					propertyLabel.getPropertyBinding().getFresnelStyle()
							.setFresnelStyle(FresnelStyleClass.VALUE_STYLE, panel.getValueInput());
				}
			}
		});
		return editCSS;
	}

	/**
	 * @return the property label
	 */
	public PropertyLabel getPropertyLabel() {
		return propertyLabel;
	}
}
