package nl.ou.fresnelforms.ontology;

import java.util.ArrayList;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;

/**
 * Class that represents an OWL-ontology data property.
 * 
 * @author Tim Zwanenberg
 *
 */
public class DatatypeProperty extends Property {

	private ArrayList<OWLDataProperty> owlDataProperties;
	private ArrayList<DataType> range;
	
	/**
	 * Constructor for DataProperty-class.
	 * 
	 * @param owlDataProperties represented by data property
	 */
	public DatatypeProperty(ArrayList<OWLDataProperty> owlDataProperties) {
		super(owlDataProperties.get(0).getIRI().toString());
		this.owlDataProperties = owlDataProperties;
		this.range = new ArrayList<DataType>();
	}

	/**
	 * Get first OWLdataproperty represented by class.
	 * 
	 * @return first OWLdataproperty represented by class
	 */
	public OWLDataProperty getOWLDataProperty() {
		return owlDataProperties.get(0);
	}

	/**
	 * Get OWLdataproperties represented by data property.
	 * 
	 * @return OWLdataproperties represented by data property
	 */
	public ArrayList<OWLDataProperty> getOWLDataProperties() {
		return owlDataProperties;
	}

	/**
	 * Get data property range.
	 * 
	 * @return range of data property
	 */
	public ArrayList<DataType> getRange() {
		return range;
	}

	
	/**
	 * Add data type to class range.
	 * 
	 * @param dt type to be added
	 */
	public void addRange(DataType dt) {
		range.add(dt);
	}


	/**
	 * @return The XSD datatype IRI of the datatype or null if it is not a datatype
	 */
	public IRI getDataTypeIRI(){
		IRI iriresult = null;
		if (this.range.size()>0){
			iriresult= this.range.get(0).getDataTypeIRI();
		}
		return iriresult;
	}

	
	/**
	 * Tests if data property represents given OWLDataProperty.
	 * 
	 * @param owlDataProperty to be checked
	 * @return True if OWLDataProperty is represented by data property
	 */
	public boolean equalsOWLDataProperty(OWLDataProperty owlDataProperty) {
		return owlDataProperties.contains(owlDataProperty);
	}

	/* (non-Javadoc)
	 * @see ontology.Property#isDataTypeProperty()
	 */
	@Override
	public boolean isDatatypeProperty() {
		return true;
	}

}
