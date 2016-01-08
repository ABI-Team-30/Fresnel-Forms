package nl.ou.fresnelforms.view;

import java.util.Comparator;

/**
 * Comperator class for lens diagram components that compares their y positions.
 */
public class LensDiagramComponentYComparator implements Comparator<LensDiagramComponent> {
	
	/** 
	 * compare y component of lens diagram components.
	 * @param c1 lensdiagram component 1
	 * @param c2 lensdiagram component 2
	 * @return -1,0,1
	 */
	public int compare(LensDiagramComponent c1, LensDiagramComponent c2) {
        if (c1.getY() < c2.getY()) {
        	return -1;
        }
        if (c1.getY() > c2.getY()) {
        	return 1;
        }
        return 0;
    }

}
