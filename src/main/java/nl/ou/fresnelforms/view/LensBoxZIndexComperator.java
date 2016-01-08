package nl.ou.fresnelforms.view;

import java.util.Comparator;
/**
 * Comperator class for lens boxes that compares their z-index.
 */
public class LensBoxZIndexComperator implements Comparator<LensBox> {

	/** 
	 * compare two lensboxes.
	 * @param l1 lensbox 1
	 * @param l2 lensbox 2
	 * @return -1,0,1 depending on the zindex
	 */
	public int compare(LensBox l1, LensBox l2) {
        if (l1.getZIndex() < l2.getZIndex()) {
        	return -1;
        }
        if (l1.getZIndex() > l2.getZIndex()) {
        	return 1;
        }
        return 0;
    }
}