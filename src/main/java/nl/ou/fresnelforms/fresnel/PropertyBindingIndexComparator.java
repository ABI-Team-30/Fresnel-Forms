package nl.ou.fresnelforms.fresnel;

import java.util.Comparator;

/**
 * Property binding comparator based on index values.
 *
 */
public class PropertyBindingIndexComparator implements Comparator<PropertyBinding> {
	

	/**
	 * compare to property binding objects.
	 * @param p1 propertybinding object 1
	 * @param p2 propertybinding object 1
	 * @return -1,0,1 depending on p1.index being smaller, equal or larger than p2.index
	 *
	 */
	public int compare(PropertyBinding p1, PropertyBinding p2) {
        if (p1.getIndex() < p2.getIndex()) {
        	return -1;
        }
        if (p1.getIndex() > p2.getIndex()) {
        	return 1;
        }
        return 0;
    }

}
