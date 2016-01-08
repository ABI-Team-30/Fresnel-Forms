package nl.ou.fresnelforms.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 * Lens box label display class.
 */
public class LensBoxLabel extends LensDiagramComponent {

	private static final long serialVersionUID = 1L;
	private LensBox lensBox;
	private Color color = Color.black;

	/**
	 * Constructor that initializes the lens box label.
	 * 
	 * @param lensBox lens box of the lens box label
	 */
	public LensBoxLabel(LensBox lensBox) {
		this.lensBox = lensBox;
	}

	/**
	 * draws the lensbox label.
	 * 
	 * @param g the canvas
	 */
	public void draw(Graphics2D g) {
		g.setFont(LensDiagram.FONT_BOLD);
		if (lensBox.getLens().isDisplayed()) {
			this.color = Color.black;
		} else {
			this.color = Color.gray;
		}

		Dimension dim = new Dimension(g.getFontMetrics().stringWidth(lensBox.getLens().getName()), g.getFontMetrics()
				.getHeight());
		this.width = dim.getWidth();
		this.height = dim.height;
		Point2D pos = new Point2D.Double(lensBox.getX() + (lensBox.getWidth() - dim.getWidth()) / 2, lensBox.getY()
				+ dim.getHeight());
		this.setPosition(pos);
		g.setColor(color);
		g.drawString(lensBox.getLens().getName(), (int) pos.getX(), (int) pos.getY() + LensDiagram.getLabelHeigth());
		g.setFont(LensDiagram.FONT_NORMAL);
	}

}
