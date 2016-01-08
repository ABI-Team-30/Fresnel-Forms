package nl.ou.fresnelforms.view;

import java.util.Comparator;

/**
 * Comparator class for property labels that compares their labels alphabetically.
 */
public class PropertyLabelAlphabetComparator implements Comparator<PropertyLabel> {
	
	
	/** 
	 * comparator of two propertylabels by their labels.
	 * @param p1 propertylabel 1
	 * @param p2 propertylabel 2
	 * @return -1,0,1
	 */
	public int compare(PropertyLabel p1, PropertyLabel p2) {
		String label1 = p1.getPropertyBinding().getLabel();
		String label2 = p2.getPropertyBinding().getLabel();
        return (int) Math.signum(label1.compareToIgnoreCase(label2));
    }


}
