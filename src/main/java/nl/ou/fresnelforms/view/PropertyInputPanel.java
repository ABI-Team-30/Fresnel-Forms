package nl.ou.fresnelforms.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Property input panel.
 *
 */
public class PropertyInputPanel extends JPanel {

	private static final long serialVersionUID = 4183348428102582031L;
	private static final int GRID_NRROWS = 1;
	private static final int NRCOLUMNS = 18;

	private JPanel messagePanel;
	private JPanel labelPanel;
	private JPanel fieldPanel;

	private JTextField uriField;
	private JTextField indexField;

	/**
	 * The constructor.
	 * 
	 * @param lensBox the lensbox
	 */
	public PropertyInputPanel(LensBox lensBox) {
		init(lensBox);
	}

	/**
	 * Init the form.
	 * 
	 * @param lensBox the lensbox
	 */
	private void init(LensBox lensBox) {
		this.setLayout(new BorderLayout());
		messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelPanel = new JPanel(new GridLayout(GRID_NRROWS, 1));
		fieldPanel = new JPanel(new GridLayout(GRID_NRROWS, 1));
		this.add(messagePanel, BorderLayout.NORTH);
		this.add(labelPanel, BorderLayout.WEST);
		this.add(fieldPanel, BorderLayout.CENTER);

		messagePanel.add(new JLabel("Add a new property to " + lensBox.getLens().getName() + ": "));

		uriField = new JTextField();
		uriField.setFont(LensDiagram.FONT_NORMAL);
		uriField.setColumns(NRCOLUMNS);
		indexField = new JTextField();
		indexField.setFont(LensDiagram.FONT_NORMAL);
		indexField.setColumns(NRCOLUMNS);

		JLabel uriLabel = new JLabel("URI: ", JLabel.RIGHT);
		uriLabel.setLabelFor(uriField);
		labelPanel.add(uriLabel);
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p2.add(uriField);
		fieldPanel.add(p2);

	}

	/**
	 * @return the urifield value
	 */
	public String getUriFieldText() {
		return this.uriField.getText();
	}

}
