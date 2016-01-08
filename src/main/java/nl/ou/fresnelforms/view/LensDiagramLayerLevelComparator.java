package nl.ou.fresnelforms.view;

import java.util.Comparator;

/**
 * Comperator class for lens diagram layers that compares their levels.
 */
public class LensDiagramLayerLevelComparator implements Comparator<LensDiagramLayer> {

    /** 
     * compare two layers by level.
     * @param l1 Lensdiagram layer 1
     * @param l2 Lensdiagram layer 2
     * @return -1,0,1 
     */
    public int compare(LensDiagramLayer l1, LensDiagramLayer l2) {
        if (l1.getLevel() < l2.getLevel()) {
        	return -1;
        }
        if (l1.getLevel() > l2.getLevel()) {
        	return 1;
        }
        return 0;
    }
}