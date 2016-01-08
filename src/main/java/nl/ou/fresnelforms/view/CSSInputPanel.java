package nl.ou.fresnelforms.view;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.ou.fresnelforms.fresnel.FresnelStyleClass;

/**
 * CSS input panel.
 *
 */
public class CSSInputPanel extends JPanel {

	private static final long serialVersionUID = 2010696885803142477L;
	private static final int STYLEFIELD_NRCOLUMNS = 10;
	private static final int TXTFIELD_NRCOLUMNS = 10;
	private static final int STRUTWIDTH = 15;

	private JTextField resourceInput;
	private JTextField propertyInput;
	private JTextField labelInput;
	private JTextField valueInput;

	/**
	 * The constructor.
	 * 
	 * @param lensBox the lensbox
	 */
	public CSSInputPanel(LensBox lensBox) {
		init(lensBox);
	}

	/**
	 * The constructor.
	 * 
	 * @param propertyLabel the property label
	 */
	public CSSInputPanel(PropertyLabel propertyLabel) {
		init(propertyLabel);
	}

	/**
	 * Init the form.
	 * 
	 * @param lensBox the lensbox
	 */
	private void init(PropertyLabel propertyLabel) {
		propertyInput = new JTextField(propertyLabel.getPropertyBinding().getFresnelStyle()
				.getFresnelStyle(FresnelStyleClass.PROPERTY_STYLE), TXTFIELD_NRCOLUMNS);
		labelInput = new JTextField(propertyLabel.getPropertyBinding().getFresnelStyle()
				.getFresnelStyle(FresnelStyleClass.LABEL_STYLE), TXTFIELD_NRCOLUMNS);
		valueInput = new JTextField(propertyLabel.getPropertyBinding().getFresnelStyle()
				.getFresnelStyle(FresnelStyleClass.VALUE_STYLE), TXTFIELD_NRCOLUMNS);

		this.add(new JLabel("Property style:"));
		this.add(propertyInput);
		this.add(Box.createHorizontalStrut(STRUTWIDTH)); // a spacer
		this.add(new JLabel("Label style:"));
		this.add(labelInput);
		this.add(Box.createHorizontalStrut(STRUTWIDTH)); // a spacer
		this.add(new JLabel("Value style:"));
		this.add(valueInput);
	}

	/**
	 * Init the form.
	 * 
	 * @param lensBox the lensbox
	 */
	private void init(LensBox lensBox) {
		resourceInput = new JTextField(lensBox.getLens().getFresnelStyle()
				.getFresnelStyle(FresnelStyleClass.RESOURCE_STYLE), STYLEFIELD_NRCOLUMNS);
		propertyInput = new JTextField(lensBox.getLens().getFresnelStyle()
				.getFresnelStyle(FresnelStyleClass.PROPERTY_STYLE), STYLEFIELD_NRCOLUMNS);
		labelInput = new JTextField(lensBox.getLens().getFresnelStyle().getFresnelStyle(FresnelStyleClass.LABEL_STYLE),
				STYLEFIELD_NRCOLUMNS);
		valueInput = new JTextField(lensBox.getLens().getFresnelStyle().getFresnelStyle(FresnelStyleClass.VALUE_STYLE),
				STYLEFIELD_NRCOLUMNS);

		this.add(new JLabel("Resource style:"));
		this.add(resourceInput);
		this.add(Box.createHorizontalStrut(STRUTWIDTH)); // a spacer
		this.add(new JLabel("Property style:"));
		this.add(propertyInput);
		this.add(Box.createHorizontalStrut(STRUTWIDTH)); // a spacer
		this.add(new JLabel("Label style:"));
		this.add(labelInput);
		this.add(Box.createHorizontalStrut(STRUTWIDTH)); // a spacer
		this.add(new JLabel("Value style:"));
		this.add(valueInput);
	}

	/**
	 * @return resource input text
	 */
	public String getResourceInput() {
		return resourceInput.getText();
	}

	/**
	 * @return property input text
	 */
	public String getPropertyInput() {
		return propertyInput.getText();
	}

	/**
	 * @return Label input text
	 */
	public String getLabelInput() {
		return labelInput.getText();
	}

	/**
	 * @return value input text
	 */
	public String getValueInput() {
		return valueInput.getText();
	}

}
