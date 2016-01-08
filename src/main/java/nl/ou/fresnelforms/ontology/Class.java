package nl.ou.fresnelforms.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Class that represents an OWL-ontology class.
 * 
 * @author Tim Zwanenberg
 *
 */
public class Class extends Resource {
	private ArrayList<OWLClass> owlClasses;
	private ArrayList<Class> parents;
	private ArrayList<DatatypeProperty> dataProperties;
	private ArrayList<ObjectProperty> objectProperties;
	private ArrayList<ObjectProperty> rangeOfObjectProperties;
	private Map<Property,PropertyRestriction> propertyrestrictions;

	/**
	 * Constructor for Class-class.
	 * 
	 * @param owlClasses represented by class
	 */
	public Class(ArrayList<OWLClass> owlClasses) {
		super(owlClasses.get(0).getIRI().toString());
		this.owlClasses = owlClasses;
		this.parents = new ArrayList<Class>();
		this.dataProperties = new ArrayList<DatatypeProperty>();
		this.objectProperties = new ArrayList<ObjectProperty>();
		this.rangeOfObjectProperties = new ArrayList<ObjectProperty>();
		this.propertyrestrictions = new HashMap<Property,PropertyRestriction>();
	}

	/**
	 * Get first OWLClass represented by class.
	 * 
	 * @return first OWLClass represented by class
	 */
	public OWLClass getOWLClass() {
		return owlClasses.get(0);
	}

	/**
	 * Get OWLclasses represented by class.
	 * 
	 * @return OWLclasses represented by class
	 */
	public ArrayList<OWLClass> getOWLClasses() {
		return owlClasses;
	}

	/**
	 * Get class parents.
	 * 
	 * @return parents of class
	 */
	public ArrayList<Class> getParents() {
		return this.parents;
	}

	/**
	 * Get class data properties.
	 * 
	 * @return data properties of class
	 */
	public ArrayList<DatatypeProperty> getDataProperties() {
		return dataProperties;
		
	}

	/**
	 * Get class object properties.
	 * 
	 * @return object properties of class
	 */
	public ArrayList<ObjectProperty> getObjectProperties() {
		return objectProperties;
	}

	/**
	 * @return list of range object properties
	 */
	public ArrayList<ObjectProperty> getRangeOfObjectProperties() {
		return rangeOfObjectProperties;
	}

	
	/**
	 * @return true if there are restriction for this class otherwise false.
	 */
	public boolean hasRestrictions(){
		return !propertyrestrictions.isEmpty();
	}

	/**
	 * @return the propertyrestrictions
	 */
	public Map<Property, PropertyRestriction> getPropertyrestrictions() {
		return propertyrestrictions;
	}
	
	
	
	/**
	 * Add parent to class.
	 * 
	 * @param parent to be added
	 */
	public void addParent(Class parent) {
		parents.add(parent);
	}

	/**
	 * Add data property to class.
	 * 
	 * @param dataProperty property to be added
	 */
	public void addDataProperty(DatatypeProperty dataProperty) {
		dataProperties.add(dataProperty);
	}

	/**
	 * Add object property to class.
	 * 
	 * @param objectProperty property to be added
	 */
	public void addObjectProperty(ObjectProperty objectProperty) {
		objectProperties.add(objectProperty);
	}

	/**
	 * @param objectProperty the range property
	 */
	public void addRangeOfObjectProperty(ObjectProperty objectProperty) {
		rangeOfObjectProperties.add(objectProperty);
	}

	
	/**
	 * Set the maxcardinality restriction for this class.
	 * @param prop the property in the restriction
	 * @param maxcard the max cardinality
	 */
	public void setMaxCardinalityRestriction(Property prop, int maxcard ){
		if (!propertyrestrictions.containsKey(prop)){
			propertyrestrictions.put(prop, new PropertyRestriction());
		}
		propertyrestrictions.get(prop).setMaxCardinality(maxcard);
	}
	
	/**
	 * Set the mincardinality restriction for this class.
	 * @param prop the property in the restriction
	 * @param mincard the max cardinality
	 */
	public void setMinCardinalityRestriction(Property prop, int mincard ){
		if (!propertyrestrictions.containsKey(prop)){
			propertyrestrictions.put(prop, new PropertyRestriction());
		}	
		propertyrestrictions.get(prop).setMinCardinality(mincard);
	}
	
	
	/**
	 * Tests if class represents given OWLClass.
	 * 
	 * @param owlClass to be checked
	 * @return True if OWLClass is represented by class
	 */
	public boolean equalsOWLClass(OWLClass owlClass) {
		return owlClasses.contains(owlClass);
	}
	
	
	
	/**
	 * Finds the dataproperty with the owldataproperty.
	 * @param owldatprop the owldataproperty
	 * @return the index in the dataproperty array
	 */
	public int findOWLDataProperty(OWLDataProperty owldatprop){
		int propindex = -1;
		
		for (int i=0; i<dataProperties.size();i++){
			if (dataProperties.get(i).equalsOWLDataProperty(owldatprop)){
				propindex = i;
				break;
			}
		}
		return propindex;
	}

	/**
	 * Finds the objectproperty with the owlobjectproperty.
	 * @param owlobjprop the owlobjectproperty
	 * @return the index in the dataproperty array
	 */
	public int findOWLObjectProperty(OWLObjectProperty owlobjprop){
		int propindex = -1;
		for (int i=0; i<objectProperties.size();i++){
			if (objectProperties.get(i).equalsOWLObjectProperty(owlobjprop)){
				propindex =i;
				break;
			}
		}
		return propindex;
	}


	
	
}
