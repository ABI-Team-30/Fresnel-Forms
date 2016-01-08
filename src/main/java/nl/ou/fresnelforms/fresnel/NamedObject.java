package nl.ou.fresnelforms.fresnel;

import nl.ou.fresnelforms.utils.UriUtils;

import org.semanticweb.owlapi.model.IRI; // use the owlapi IRI for resolving namespaces, fragments etc.

/**
 * The basic class for all named fresnel elements (lenses & properties).
 *
 */
public class NamedObject {
	private Fresnel fresnel;
	/**
	 * The URI of the element.
	 */
	private String uri;

	/**
	 * Creates a Named object of a Fresnel Model.
	 * 
	 * @param fresnel the Fresnel model to which this element belongs
	 * @param uri the URI of this object
	 */
	public NamedObject(Fresnel fresnel, String uri) {
		this.fresnel = fresnel;
		this.uri = uri;
	}

	/**
	 * @return the uri of the element
	 */
	public String getURI() {
		return uri;
	}

	/**
	 * @return the Fresnel model to which this element belongs
	 */
	public Fresnel getFresnel() {
		return fresnel;
	}

	/**
	 * Determines a prefixed version of a URI, if possible.
	 * 
	 * @return a prefixed version of the URI or the URI itself if it could determine a prefix
	 */
	public String getPrefixedURI() {
		String result = uri;
		if (fresnel.getOntology() != null) {
			result = fresnel.getOntology().getPrefixedURI(uri);
		}
		return result;
	}

	/**
	 * @return the name of the element
	 */
	public String getName() {
		String prefixedUri = getPrefixedURI();
		if (prefixedUri.startsWith(":")) {
			prefixedUri = prefixedUri.substring(1); // remove leading :, if any
		}
		return prefixedUri;
	}

	/**
	 * @return the namespace of the element
	 */
	public String getNameSpace() {
		return IRI.create(uri).getNamespace();
	}

	/**
	 * @return the local name of the element (everything after the namespace)
	 */
	public String getLocalName() {
		return IRI.create(uri).getFragment();
	}

	/**
	 * @return the uri-encoded name of the element
	 */
	public String getEncodedName() {
		return UriUtils.encodedURI(this);
	}

}
