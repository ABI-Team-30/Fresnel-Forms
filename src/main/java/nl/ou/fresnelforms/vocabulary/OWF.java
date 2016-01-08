package nl.ou.fresnelforms.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Class for defining the presentation ontology's vocabulary.
 *
 */
public class OWF {
	/**
	 * The Presentation's URI.
	 */
	protected static final String URI = "http://is.cs.ou.nl/OWF/#";

	/**
	 * Disable construction of objects of this utility class.
	 */
	private OWF() {
	}

	/**
	 * returns the URI for this schema.
	 * 
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return URI;
	}

	/**
	 * Returns a RDF Property for this vocabulary.
	 * 
	 * @param local the local name of the property
	 * @return the RDF Property
	 */
	protected static final Property property(String local) {
		return ResourceFactory.createProperty(URI, local);
	}

	/**
	 * The property representing the X-part of the position of a Lens in the GUI.
	 */
	public static final Property XPOS = property("xpos");

	/**
	 * The property representing the Y-part of the position of a Lens in the GUI.
	 */
	public static final Property YPOS = property("ypos");
	
	/**
	 * A OWF's Format's autocompleteFromClass property.
	 */
	public static final Property AUTOCOMPLETEFROMCLASS = property("autocompleteFromClass");

	/**
	 * A OWF's Format's delimiter property.
	 */
	public static final Property DELIMITER = property("delimiter");

	/**
	 * A OWF's Format's datatype property.
	 */
	public static final Property DATATYPE = property("datatype");
	
	/**
	 * A OWF's Format's isList property.
	 */
	public static final Property ISLIST = property("isList");

	/**
	 * A OWF's Format's isMandatory property.
	 */
	public static final Property ISMANDATORY = property("isMandatory");
	
}
