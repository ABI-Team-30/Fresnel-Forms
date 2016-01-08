package nl.ou.fresnelforms.ontology;


/**
 * The class representing a property.
 * 
 */
public class Property extends Resource {
	private String label="";
	private boolean functional = false;
	private boolean inversefunctional = false;

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the functional
	 */
	public boolean isFunctional() {
		return functional;
	}

	/**
	 * @param functional the functional to set
	 */
	public void setFunctional(boolean functional) {
		this.functional = functional;
	}

	/**
	 * @return the inversefunctional
	 */
	public boolean isInversefunctional() {
		return inversefunctional;
	}

	/**
	 * @param inversefunctional the inversefunctional to set
	 */
	public void setInversefunctional(boolean inversefunctional) {
		this.inversefunctional = inversefunctional;
	}

	/**
	 * Creates an abstract Property with a URI.
	 * 
	 * @param uri the URI to use
	 */
	public Property(String uri) {
		super(uri);
	}

	/**
	 * @return true if this property is an ObjectProperty, false otherwise
	 */
	public boolean isObjectProperty() {
		return false;
	}

	/**
	 * If this Property is an ObjectProperty, answer that ObjectProperty; otherwise throw an exception.
	 * @return this Property as an ObjectProperty if this Property is an ObjectProperty
	 */
	public ObjectProperty asObjectProperty() {
		if (isObjectProperty()) {
			return (ObjectProperty) this;
		} else {
			throw new ObjectPropertyRequiredException("Property " + getURI()
					+ " cannot be treated as an ObjectProperty.");
		}
	}

	/**
	 * @return true if this property is a DatatypeProperty, false otherwise
	 */
	public boolean isDatatypeProperty() {
		return false;
	}

	/**
	 * If this Property is an DatatypeProperty, answer that DatatypeProperty; otherwise throw an exception.
	 * @return this Property as an DatatypeProperty if this Property is an DatatypeProperty
	 */
	public DatatypeProperty asDatatypeProperty() {
		if (isDatatypeProperty()) {
			return (DatatypeProperty) this;
		} else {
			throw new DatatypePropertyRequiredException("Property " + getURI()
					+ " cannot be treated as an DataTypeProperty.");
		}
	}
	
}
