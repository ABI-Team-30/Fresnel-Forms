package nl.ou.fresnelforms.view;

import java.util.Comparator;

/**
 * Comperator class for property labels that compares their property binding indexes.
 */
public class PropertyLabelIndexComparator implements Comparator<PropertyLabel> {
	
	/** 
	 * comparator of two propertylabels by their index.
	 * @param p1 propertylabel 1
	 * @param p2 propertylabel 2
	 * @return -1,0,1
	 */
	public int compare(PropertyLabel p1, PropertyLabel p2) {
        if (p1.getPropertyBinding().getIndex() < p2.getPropertyBinding().getIndex()) {
        	return -1;
        }
        if (p1.getPropertyBinding().getIndex() > p2.getPropertyBinding().getIndex()) {
        	return 1;
        }
        return 0;
    }

}
