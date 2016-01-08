package nl.ou.fresnelforms.ontology;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataRange;

/**
 * Class that represents a Protege default data type.
 * @author Tim Zwanenberg
 *
 */
public class DataType extends Resource {
	
	private OWLDataRange owlDataRange;
	private String name;
	
	/**
	 * Constructor for DataType-class.
	 * @param owlDataRange name of data type
	 */
	public DataType(OWLDataRange owlDataRange) {
		super(owlDataRange.asOWLDatatype().getIRI().toString());
		this.owlDataRange = owlDataRange;
		this.name = owlDataRange.toString();
	}
	
	/**
	 * Get OWLDataRange represented by data type.
	 * @return OWLDataRange represented by data type
	 */
	public OWLDataRange getOWLDataRange() {
		return owlDataRange;
	}
	
	/**
	 * Get data type name.
	 * @return name of data type
	 */
	public String getName() {
		return name;
	}
	

	/**
	 * @return The XSD datatype IRI of the datatype or null if it is not a datatype
	 */
	public IRI getDataTypeIRI(){
		if (this.owlDataRange.isDatatype()){
			return  this.owlDataRange.asOWLDatatype().getIRI();
		} else {
			return null;
		}
	}
	
	
}
