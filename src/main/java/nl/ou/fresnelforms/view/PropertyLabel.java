package nl.ou.fresnelforms.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.ontology.Property;

/**
 * Property label display class.
 */
public class PropertyLabel extends LensDiagramComponent {
	private static final long serialVersionUID = 2989914497671680332L;
	private PropertyBinding propertyBinding;
	private LensBox domainLensBox;
	private Color color = Color.black;
	private static final int ACTIVELABELCOLOR = 0x9696FF; // TT 26112014 rgb(150,150,255)

	/**
	 * Constructor that initializes the property label.
	 * 
	 * @param propertyBinding property binding displayed by the property label
	 * @param domainLensBox lens box which is the domain of the property binding
	 */
	public PropertyLabel(PropertyBinding propertyBinding, LensBox domainLensBox) {
		this.propertyBinding = propertyBinding;
		this.domainLensBox = domainLensBox;
	}

	/**
	 * draws the property label.
	 * 
	 * @param g the canvas
	 */
	public void draw(Graphics g) {
		boolean isObjectProperty = propertyBinding.getProperty().getProperty().isObjectProperty();
		boolean inActive = !propertyBinding.isShown() || !domainLensBox.getLens().isDisplayed();
		if (isObjectProperty) {
			if (inActive) {
				this.color = new Color(ACTIVELABELCOLOR);
			} else {
				this.color = Color.blue;
			}
		} else {
			if (inActive) {
				this.color = Color.gray;
			} else {
				this.color = Color.black;
			}
		}
		g.setColor(color);
		Font defaultfont = g.getFont();
		if (!propertyBinding.isLabelEnabled()) {
			Font font = defaultfont.deriveFont(Font.ITALIC);
			g.setFont(font);
		}
		String text = getLabelText(g);
		g.drawString(text, (int) this.getX(), (int) this.getY() + LensDiagram.getLabelHeigth());
		g.setFont(defaultfont); // reset font to the default
	}

	/**
	 * Returns the label text to display, taking the maximum width into account
	 * 
	 * @param g the canvas to draw on
	 * @return the text to display
	 */
	private String getLabelText(Graphics g) {
		String label = propertyBinding.getLabel();
		String name = propertyBinding.getProperty().getName();
		String text = name;
		String suffix = "";
		if (!label.equals(propertyBinding.getDefaultLabel())) {
			text = label + " (" + text; // use the label as prefix
			suffix += ")";
		}
		suffix += getLabelTags();

		// Determine a fitting label, taking the suffixes into account
		FontMetrics fontMetrics = g.getFontMetrics();
		double maxWidth = LensDiagram.LENSBOX_WIDTH - 2 * LensDiagram.PROPERTY_ALIGNMENT_X;
		double suffixWidth = fontMetrics.stringWidth(suffix);
		double maxTextWidth = maxWidth;
		if (suffixWidth < maxWidth) {
			maxTextWidth = maxWidth - suffixWidth; // Take the suffix into account if it fits
		}
		double textWidth = fontMetrics.stringWidth(text);
		if (textWidth > maxTextWidth) {
			// the text is too long to fit
			String ellipsis = "..."; // so, we mark it by using an ellipsis as suffix
			double ellipsisWidth = fontMetrics.stringWidth(ellipsis);
			// The maximum length will now even be shorter due to this marking
			if (ellipsisWidth < maxTextWidth) {
				maxTextWidth = maxTextWidth - ellipsisWidth; // Take the ellipsis into account if it fits
			}
			while (textWidth > maxTextWidth) { // shorten the string by guessing if needed
				// We can take a guess at the number of characters which will fit
				int upperIndexGuess = (int) ((maxTextWidth / textWidth) * text.length());
				text = text.substring(0, upperIndexGuess); // shorten the text
				textWidth = fontMetrics.stringWidth(text); // determine the shortened width
			}
			if (ellipsisWidth < maxTextWidth) {
				text += ellipsis; // add the ellipsis (if it fits)
			}
		}
		if (suffixWidth < maxWidth) {
			text += suffix; // add the suffixes (if it fits)
		}

		this.width = textWidth;
		this.height = LensDiagram.getLabelHeigth();
		return text;
	}

	/**
	 * Returns the tags to add to a label to indicate property settings.
	 * 
	 * @return The tags to add to a label
	 */
	private String getLabelTags() {
		String tokens = "";
		if (propertyBinding.isAutoCompletionEnabled()) {
			tokens += "~";
		}
		if (!propertyBinding.isList()) {
			tokens += "_";
		}
		if (propertyBinding.isMandatory()) {
			tokens += "+";
		}
		return tokens;
	}

	/**
	 * @return the property binding
	 */
	public PropertyBinding getPropertyBinding() {
		return this.propertyBinding;
	}

	
	/**
	 * @return the ontology property of this propertylabel
	 */
	public Property getProperty(){
		return this.getPropertyBinding().getProperty().getProperty();
	}
	/**
	 * Change index of property label within domain lens box.
	 */
	public void changeIndex() {
		domainLensBox.changeIndexOfPropertyLabel(this);
	}

}
