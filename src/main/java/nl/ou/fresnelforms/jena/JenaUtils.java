/**
 * 
 */
package nl.ou.fresnelforms.jena;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Abstract class with static Jena-specific utilities.
 *
 */
public abstract class JenaUtils {

	/**
	 * Replaces all statements with Properties p of Resource r with statements saying Resource r has Property p with
	 * objects from os. So, it deletes all existing properties p of Resource r, if any, and adds one Property p of
	 * Resource r with Object o for every object in os. If os equals null or is empty, all statements with Properties p
	 * of Resource r will be removed.
	 * 
	 * @param r The Resource to replace the properties of
	 * @param p The Property to be replaced
	 * @param os The RDFNodes to use as replacement Objects
	 * @return The resource, to permit cascading
	 */
	public static Resource replaceProperty(Resource r, Property p, List<? extends RDFNode> os) {
		removePropertyValues(r, p);
		if (os != null && !os.isEmpty()) {
			for (RDFNode o : os) {
				r.addProperty(p, o); // Add the property which replaces all old values
			}
		}
		return r;
	}

	/**
	 * Replaces all statements with Properties p of Resource r with statements saying Resource r has Property p with
	 * object o. So, it deletes all existing properties p of Resource r, if any, and adds one Property p of Resource r
	 * with Object o. If o equals null, all statements with Properties p of Resource r will be removed.
	 * 
	 * @param r The Resource to replace the properties of
	 * @param p The Property to be replaced
	 * @param o The RDFNode to use as replacement Objects
	 * @return The resource, to permit cascading
	 */
	public static Resource replaceProperty(Resource r, Property p, RDFNode o) {
		removePropertyValues(r, p);
		if (o != null) {
			r.addProperty(p, o); // Add the property which replaces all old values
		}
		return r;
	}

	/**
	 * Removes all existing Properties p of Resource r, including complete RDFList- and anonymous node objects.
	 * 
	 * @param r The Resource to remove the properties from
	 * @param p The Property to be removed
	 */
	private static void removePropertyValues(Resource r, Property p) {
		List<RDFNode> blankNodes = new ArrayList<RDFNode>();
		Model model = r.getModel();
		for (Statement statement : r.listProperties(p).toList()) {
			RDFNode node = statement.getObject();
			model.remove(r, p, node); // Remove the statement from the resource's model
			if (node.canAs(RDFList.class)) {
				// Remove residual RDFLists, for they consist of several anomymous Resources
				RDFList rdfList = node.as(RDFList.class);
				// Remember the blank nodes in the list
				for (Iterator<RDFNode> rdfListIterator = rdfList.iterator(); rdfListIterator.hasNext();) {
					RDFNode item = rdfListIterator.next();
					if (item.isAnon()) {
						blankNodes.add(item);
					}
				}
				// Remove the list
				if (!rdfList.isEmpty()) {
					rdfList.removeList();
				}
			} else if (node.isAnon()) {
				// Remember the blank node
				blankNodes.add(node);
			}
		}
		// Last, but not least, remove all dangling blank nodes
		for (RDFNode blankNode : blankNodes) {
			model.removeAll(blankNode.asResource(), null, null);
		}
	}

	/**
	 * Returns a list of Resources out of an RDFNode. When the RDFNode can be treated as an RDFList, return its members
	 * as Resources (this can always be done because list members must be resources @See
	 * http://www.w3.org/TR/rdf-schema/#ch_list). Else, when the RDFNode can be treated as a Resource, return it as the
	 * single Resource in the List.
	 * 
	 * @param node the RDFNode to get the resource(s) from
	 * @return the resources to treat as hideProperties's objects.
	 */
	public static List<Resource> getRDFResources(RDFNode node) {
		List<Resource> resources = new ArrayList<Resource>();
		if (node != null) {
			if (node.canAs(RDFList.class)) {
				for (Iterator<RDFNode> iterator = node.as(RDFList.class).iterator(); iterator.hasNext();) {
					resources.add(iterator.next().asResource());
				}
			} else {
				if (node.isResource()) {
					resources.add(node.asResource());
				}
			}
		}
		return resources;
	}

	/**
	 * Returns a prefixed version of a URI, if possible.
	 * 
	 * @param resource the resource of which to return the prefixed URI
	 * @return the prefixed URI or the URI itself
	 */
	public static String prefixedURI(Resource resource) {
		String result = null;
		if (resource != null) {
			result = resource.getModel().shortForm(resource.getURI());
		}
		return result;
	}

	/**
	 * Returns a prefix of a URI or the namespace if no prefix was found.
	 * 
	 * @param resource the resource of which to return the prefix
	 * @return the prefix or the namespace
	 */
	public static String prefix(Resource resource) {
		String result = null;
		if (resource != null) {
			String prefixedUri = prefixedURI(resource);
			if (prefixedUri.equals(resource.getURI())) {
				result = resource.getNameSpace();
			} else {
				result = prefixedUri.substring(0, prefixedUri.indexOf(":"));
			}
		}
		return result;
	}

}
