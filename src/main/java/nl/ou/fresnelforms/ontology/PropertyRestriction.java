/**
 * 
 */
package nl.ou.fresnelforms.ontology;

/**
 * @author jn.theunissen@studie.ou.nl
 *
 */
public class PropertyRestriction {
	private int mincardinality = 0;
	private int maxcardinality = -1;

	/**
	 * @return the maxcardinality
	 */
	public int getMaxCardinality() {
		return maxcardinality;
	}

	/**
	 * @param maxcardinality the maxcardinality to set
	 */
	public void setMaxCardinality(int maxcardinality) {
		this.maxcardinality = maxcardinality;
	}

	/**
	 * @return the mincardinality
	 */
	public int getMinCardinality() {
		return mincardinality;
	}

	/**
	 * @param mincardinality the mincardinality to set
	 */
	public void setMinCardinality(int mincardinality) {
		this.mincardinality = mincardinality;
	}

	
}
