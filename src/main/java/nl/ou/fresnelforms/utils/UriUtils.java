package nl.ou.fresnelforms.utils;

import nl.ou.fresnelforms.fresnel.NamedObject;
import nl.ou.fresnelforms.jena.JenaUtils;

import org.apache.jena.riot.system.IRILib;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Abstract Class with some static helper methods which generically handle URIs of different objects.
 * 
 */
public abstract class UriUtils {

	/**
	 * Encodes a URI for a NamedObject.
	 * 
	 * @param namedObject the namedObject to encode the URI for
	 * @return the encoded URI
	 */
	public static String encodedURI(NamedObject namedObject) {
		String result = null;
		if (namedObject != null) {
			return encodedURI(namedObject.getURI(), namedObject.getPrefixedURI());
		}
		return result;
	}

	/**
	 * Encodes a URI for a Jena Resource.
	 * 
	 * @param resource the Resource to encode the URI for
	 * @return the encoded URI
	 */
	public static String encodedURI(Resource resource) {
		String result = null;
		if (resource != null) {
			result = encodedURI(resource.getURI(), JenaUtils.prefixedURI(resource));
		}
		return result;
	}

	/**
	 * Encodes a URI, according to its decoded form, which is implementation-specific. Basically, it first duplicates
	 * all dashes and then replaces all colons, which are the result of prefixing, with dashes to guarantee unity.
	 * 
	 * @param uri the uri to encode
	 * @param decodedUri the uri in its implementation-specific decoded form
	 * @return the encoded URI
	 */
	public static String encodedURI(String uri, String decodedUri) {
		final String charToReplace = ":";
		final String replacementChar = "-";
		if (decodedUri.startsWith(charToReplace)) {
			decodedUri = decodedUri.substring(1); // remove leading char to replace, if any
		}
		String result = "";
		if (!uri.equals(decodedUri)) { // was the uri prefixable?
			// first, duplicate every pre-existing replacementChar to guarantee unity
			result = decodedUri.replace(replacementChar, replacementChar + replacementChar);
			// replace the character to replace with a replacement character to make it a valid fragment
			result = decodedUri.replace(charToReplace, replacementChar);
		} else {
			result = IRILib.encodeUriComponent(decodedUri); // for non-prefixable uri's, just take the encoded form
		}
		return result;
	}

}
