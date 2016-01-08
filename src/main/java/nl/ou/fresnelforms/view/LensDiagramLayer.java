package nl.ou.fresnelforms.view;

import java.util.ArrayList;

/**
 * Class that contains lens boxes with the same number of ancestors and therefore.
 * by default get displayed on the same y-position
 */
public class LensDiagramLayer {
	
	private int level = 0;
	private ArrayList<LensBox> lensBoxes = new ArrayList<LensBox>();
	
	/**
	 * Constructor.
	 * @param level level of the layer (number of ancestors each lens box in the layer has)
	 */
	public LensDiagramLayer(int level) {
		this.level = level;
	}
	
	/**
	 * @return level of the layer (number of ancestors each lens box in the layer has)
	 */
	public int getLevel() {
		return level;
	}
	
	/**
	 * @return lens boxes in layer
	 */
	public ArrayList<LensBox> getLensBoxes() {
		return lensBoxes;
	}
	
	/**
	 * Calculates and return the height of the heighest lens box and therefore the height of the layer.
	 * @return layer height
	 */
	public double getHeight() {
		double height = 0;
		for (LensBox l: lensBoxes) {
			if (l.getHeight() > height) {
				height = l.getHeight();
			}
		}
		return height;
	}
	
	/**
	 * @param lb lens box to be added to layer
	 */
	public void addLensBox(LensBox lb) {
		lensBoxes.add(lb);
	}

}
