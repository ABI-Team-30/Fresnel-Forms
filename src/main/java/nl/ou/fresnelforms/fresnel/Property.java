package nl.ou.fresnelforms.fresnel;

/**
 * The fresnel property class.
 *
 */
public class Property extends NamedObject {

	private nl.ou.fresnelforms.ontology.Property property;

	/**
	 * The constructor.
	 * 
	 * @param fresnel the Fresnel model to which this property belongs
	 * @param property the property to clone.
	 */
	public Property(Fresnel fresnel, nl.ou.fresnelforms.ontology.Property property) {
		super(fresnel, property.getURI());
		this.property = property;
	}

	/**
	 * @param fresnel the Fresnel model to which the property belongs
	 * @param uri the uri of the property
	 */
	public Property(Fresnel fresnel, String uri) {
		super(fresnel, uri);
		property = null;
	}

	/**
	 * @return this property
	 */
	public nl.ou.fresnelforms.ontology.Property getProperty() {
		return property;
	}

	/**
	 * Added this hashcode function to prefend checkstyle message.
	 * 
	 * @author eclipse 26-11-2014
	 * @return an integer hashcode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if (getURI() != null) {
			result += getURI().hashCode();
		}
		return result;
	}

	/**
	 * @return true if this object is a property and equals this property.
	 * @param obj the object to compare
	 * @throws NullPointerException if parameter obj has the value null.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Property) {
			Property property = (Property) obj;
			if (property.getURI().equals(this.getURI())) {
				return true;
			}
		}
		return false;
	}

}
