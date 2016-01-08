package nl.ou.fresnelforms.ontology;

/**
 * Exception to throw when a Property required to be an ObjectProperty isn't, or when a Property supposed to be an
 * ObjectProperty isn't.
 */
public class ObjectPropertyRequiredException extends RuntimeException {

	private static final long serialVersionUID = 2320156974836142364L;

	/**
	 * Constructs a new exception with {@code null} as its detail message.
	 */
	public ObjectPropertyRequiredException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
	 *            method.
	 */
	public ObjectPropertyRequiredException(String message) {
		super(message);
	}

}
