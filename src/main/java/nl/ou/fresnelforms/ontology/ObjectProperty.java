package nl.ou.fresnelforms.ontology;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Class that represents an OWL-ontology object property.
 * 
 * @author Tim Zwanenberg
 *
 */
public class ObjectProperty extends Property {

	private List<OWLObjectProperty> owlObjectProperties;
	private List<Class> range;
	/**
	 * Constructor for ObjectProperty-class.
	 * 
	 * @param owlObjectProperties represented by object property
	 */
	public ObjectProperty(List<OWLObjectProperty> owlObjectProperties) {
		super(owlObjectProperties.get(0).getIRI().toString());
		this.owlObjectProperties = owlObjectProperties;
		this.range = new ArrayList<Class>();
	}

	
	/**
	 * Get first OWLobjectproperty represented by object property.
	 * 
	 * @return first OWLobjectproperty represented by object property
	 */
	public OWLObjectProperty getOWLObjectProperty() {
		return owlObjectProperties.get(0);
	}

	/**
	 * Get OWLobjectproperties represented by object property.
	 * 
	 * @return OWLobjectproperties represented by object property
	 */
	public List<OWLObjectProperty> getOWLObjectProperties() {
		return owlObjectProperties;
	}

	/**
	 * Get object property range.
	 * 
	 * @return range of object property
	 */
	public List<Class> getRange() {
		return range;
	}

	/**
	 * Add class to object property range.
	 * 
	 * @param c class to be added
	 */
	public void addRange(Class c) {
		range.add(c);
	}

	/**
	 * Tests if object property represents given OWLObjectProperty.
	 * 
	 * @param owlObjectProperty to be checked
	 * @return True if OWLObjectProperty is represented by object property
	 */
	public boolean equalsOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
		return owlObjectProperties.contains(owlObjectProperty);
	}

	/* (non-Javadoc)
	 * @see ontology.Property#isObjectProperty()
	 */
	@Override
	public boolean isObjectProperty() {
		return true;
	}

}
