/**
 * 
 */
package nl.ou.fresnelforms.ontology;

/**
 * Exception to throw when a Property required to be a DatatypeProperty isn't, or when a Property supposed to be an
 * DatatypeProperty isn't.
 */
public class DatatypePropertyRequiredException extends RuntimeException {

	private static final long serialVersionUID = -2056859763777789245L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public DatatypePropertyRequiredException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
	 *            method.
	 */
	public DatatypePropertyRequiredException(String message) {
		super(message);
	}

}
