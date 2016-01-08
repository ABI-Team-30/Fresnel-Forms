package nl.ou.fresnelforms.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Class for defining Fresnel's vocabulary.
 *
 */
public class FRESNEL {

	/**
	 * Disable construction of objects of this utility class.
	 */
	private FRESNEL() {
	}

	/**
	 * Fresnel's URI.
	 */
	protected static final String IRI = "http://www.w3.org/2004/09/fresnel#";

	/**
	 * returns the URI for this schema.
	 * 
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return IRI;
	}

	/**
	 * Returns a Fresnel (RDF) Resource for this vocabulary.
	 * 
	 * @param local the local name of the resource
	 * @return the RDF Resource
	 */
	protected static final Resource resource(String local) {
		return ResourceFactory.createResource(IRI + local);
	}

	/**
	 * Returns a Fresnel (RDF) Property for this vocabulary.
	 * 
	 * @param local the local name of the property
	 * @return the RDF Property
	 */
	protected static final Property property(String local) {
		return ResourceFactory.createProperty(IRI, local);
	}

	/**
	 * A Fresnel Lens.
	 */
	public static final Resource LENS = resource("Lens");

	/**
	 * A Fresnel's Lens's showProperties property.
	 */
	public static final Property SHOWPROPERTIES = property("showProperties");

	/**
	 * A Fresnel's Lens's hideProperties property.
	 */
	public static final Property HIDEPROPERTIES = property("hideProperties");

	/**
	 * A Fresnel's Lens's resourceStyle property.
	 */
	public static final Property RESOURCESTYLE = property("resourceStyle");

	/**
	 * A Fresnel's Lens's show- or hideProperties item's property.
	 */
	public static final Property PROPERTY = property("property");

	/**
	 * A Fresnel's Lens's show- or hideProperties item's use.
	 */
	public static final Property USE = property("use");

	/**
	 * A Fresnel's Lens's purpose.
	 */
	public static final Property PURPOSE = property("purpose");

	/**
	 * A Fresnel's Lens's classLensDomain.
	 */
	public static final Property CLASSLENSDOMAIN = property("classLensDomain");

	/**
	 * A Fresnel's Lens's group.
	 */
	public static final Property GROUP = property("group");

	/**
	 * A Fresnel's Lens's group (The group type itself).
	 */
	public static final Property GROUP_TYPE = property("Group");

	/**
	 * A Fresnel's Lens's purpose defaultLens value.
	 */
	public static final Resource DEFAULTLENS = resource("defaultLens");

	/**
	 * A Fresnel's Lens's purpose.
	 */
	public static final Resource PROPERTYDESCRIPTION = resource("propertyDescription");

	/**
	 * A Fresnel propertyStyle property.
	 */
	public static final Resource STYLINGINSTRUCTIONS = resource("stylingInstructions");

	/**
	 * A Fresnel propertyStyle property.
	 */
	public static final Resource STYLECLASS = resource("styleClass");

	/**
	 * A Fresnel propertyStyle property.
	 */
	public static final Property PROPERTYSTYLE = property("propertyStyle");

	/**
	 * A Fresnel labelStyle property.
	 */
	public static final Property LABELSTYLE = property("labelStyle");

	/**
	 * A Fresnel valueStyle property.
	 */
	public static final Property VALUESTYLE = property("valueStyle");

	/**
	 * A Fresnel Format.
	 */
	public static final Resource FORMAT = resource("Format");

	/**
	 * A Fresnel's Format's propertyFormatDomain property.
	 */
	public static final Property PROPERTYFORMATDOMAIN = property("propertyFormatDomain");

	/**
	 * A Fresnel's Format's propertyFormatDomain property.
	 */
	public static final Property VALUE = property("value");

	/**
	 * A Fresnel's Format's propertyFormatDomain property.
	 */
	public static final Resource IMAGE = resource("image");

	/**
	 * A Fresnel's Format's propertyFormatDomain property.
	 */
	public static final Resource EXTERNALLINK = resource("externalLink");

	/**
	 * A Fresnel's Format's propertyFormatDomain property.
	 */
	public static final Resource URI = resource("uri");

	/**
	 * A Fresnel's Format's label property.
	 */
	public static final Property LABEL = property("label");

	/**
	 * Fresnel's way to define that a label mustn't be shown.
	 */
	public static final Resource NONE = resource("none");

	/**
	 * Fresnel's way to define that a label must be shown (the default).
	 */
	public static final Resource SHOW = resource("show");
	
}
