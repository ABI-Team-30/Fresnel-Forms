package nl.ou.fresnelforms.fresneltowiki;

/**
 * The types that are supported by Semantic MediaWiki.
 *
 */
public enum SMWType {
	/**
	 * A Page.
	 */
	PAGE ("Page"),
	/**
	 * A String.
	 */
	STRING ("String"),
	/**
	 * A Date.
	 */
	DATE ("Date"),
	/**
	 * A Number.
	 */
	NUMBER("Number"),
	/**
	 * A URL.
	 */
	URL("URL"),
	/**
	 * A Boolean.
	 */
	BOOLEAN("Boolean");
	
	private final String annotation;
	
	/**
	 * Constructs a Semantic MediaWiki type.
	 * 
	 * @param annotation Semantic MediaWiki's annotation of this type (may contain spaces)
	 */
	SMWType(String annotation) {
		this.annotation = annotation;
	}
	
	/**
	 * Returns the annotation of a SMWtype.
	 * @return the annotation
	 */
	public String getAnnotation() {
		return annotation;
	}
}
