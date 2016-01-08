package nl.ou.fresnelforms.jena;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A Map of Property Resources to Lists of RDFNodes.
 *
 * This extends HashMap with helper methods for handling the lists of RDFNodes associated with a property.
 */
public class PropertyMap extends HashMap<Resource, List<RDFNode>> {
	private static final long serialVersionUID = 3165098499549583510L;

	/**
	 * Picks one RDFNode from the list of RDFNodes associated with a given Property. If no RDFNode could be found for
	 * the property, null will be returned.
	 * 
	 * @param property the property to pick an RDFNode For
	 * @return an arbitrary RDFNode from the list, or null if no RDFNode could be found
	 */
	public RDFNode pick(Resource property) {
		List<RDFNode> objects = this.get(property);
		if (objects == null || objects.isEmpty()) {
			return null;
		} else {
			return objects.get(0); // just return the first RDFNode
		}
	}
}
