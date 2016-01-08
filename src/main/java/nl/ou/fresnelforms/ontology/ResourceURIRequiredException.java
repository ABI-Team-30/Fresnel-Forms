/**
 * 
 */
package nl.ou.fresnelforms.ontology;

/**
 * Exception to throw when a URI required for a resource isn't a correct URI.
 */
public class ResourceURIRequiredException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1057211961443426315L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public ResourceURIRequiredException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
	 *            method.
	 */
	public ResourceURIRequiredException(String message) {
		super(message);
	}

}
