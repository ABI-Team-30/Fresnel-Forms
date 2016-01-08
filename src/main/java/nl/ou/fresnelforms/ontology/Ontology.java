package nl.ou.fresnelforms.ontology;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.prefix.PrefixUtilities;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * Class that represents an OWL-ontology.
 * 
 * @author Tim Zwanenberg
 *
 */
public class Ontology {

	private OWLOntology owlOntology;
	private String name;
	private ArrayList<Class> classes;

	private static Logger log = LogManager.getLogger(Ontology.class);

	/**
	 * Constructor for Ontology-class.
	 * 
	 * @param owlOntology represented by ontology
	 */
	public Ontology(OWLOntology owlOntology) {
		this.owlOntology = owlOntology;
		this.classes = new ArrayList<Class>();
		this.name = ""; // reset the name

		// try to determine a reasonable ontology name
		IRI ontologyIRI = owlOntology.getOntologyID().getOntologyIRI();
		String ontologyNamespace = ontologyIRI.toString();
		PrefixManager prefixManager = PrefixUtilities.getPrefixOWLOntologyFormat(owlOntology);
		for (Entry<String, String> entry : prefixManager.getPrefixName2PrefixMap().entrySet()) {
			String key = entry.getKey();
			String namespace = entry.getValue();
			// for the prefix, remove the last character (:), for technically, it isn't part of the prefix itself
			String prefix = key.substring(0, key.length() - 1);
			// if the name hasn't been determined yet, the prefix isn't the default one and the found namespace is equal
			// to the ontology's one, the name of this ontology will be the found prefix
			if ("".equals(name) && !"".equals(prefix) && ontologyNamespace.equals(namespace)) {
				name = prefix;
			}
		}

		// if the name couldn't be determined from the prefixes, take the first part of the fragment of the ontology's
		// IRI (before the first dot)
		if ("".equals(name)) {
			String fragment = ontologyIRI.getFragment();
			if (fragment != null && !fragment.isEmpty()) {
				int dotLocation = fragment.indexOf(".");
				if (dotLocation < 0) {
					name = fragment;
				} else {
					name = fragment.substring(0, dotLocation);
				}
			} else {
				// As a last resort: take and encode the IRI itself as name for the ontology
				name = ontologyIRI.toString().replaceAll("[^a-zA-Z_]", "_");
			}
		}
		log.debug("Name of ontology: " + name);
	}

	/**
	 * Get OWLontology represented by ontology.
	 * 
	 * @return OWLontology represented by ontology
	 */
	public OWLOntology getOWLOntology() {
		return owlOntology;
	}

	/**
	 * Get ontology name.
	 * 
	 * @return name of ontology
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get ontology URI.
	 * 
	 * @return URI of ontology
	 */
	public URI getURI() {
		return owlOntology.getOntologyID().getOntologyIRI().toURI();
	}

	/**
	 * Gets a prefix for a namespace.
	 * 
	 * @param uri the uri to get the prefix version for
	 * @return prefixed version of the provided URI, or the URI itself when a prefix could not be found
	 */
	public String getPrefixedURI(String uri) {
		String prefixedURI = PrefixUtilities.getPrefixOWLOntologyFormat(owlOntology).getPrefixIRI(IRI.create(uri));
		if (prefixedURI == null) {
			prefixedURI = uri;
		}
		return prefixedURI;
	}

	/**
	 * Get ontology classes.
	 * 
	 * @return classes of ontology
	 */
	public ArrayList<Class> getClasses() {
		return classes;
	}

	/**
	 * @return the number of classes in this ontology.
	 */
	public int getNrOfClasses(){
		return classes.size();
	}
	
	
	/**
	 * @return the number of properties in this ontology.
	 */
	public int getNrOfProperties(){
		int nrprop=0;
		nrprop = getNrOfDataProperties() + getNrOfObjectProperties();
		return nrprop;
	}
	

	/**
	 * @return the number of distinct dataproperties in this ontology.
	 */
	public int getNrOfDataProperties(){
		Set<Property> props = new HashSet<Property>();
		for (int i=0;i<classes.size();i++){
			props.addAll(classes.get(i).getDataProperties());
		}
		return props.size();
	}
	
	/**
	 * @return the number of distcinct objectproperties in this ontology.
	 */
	public int getNrOfObjectProperties(){
		Set<Property> props = new HashSet<Property>();
		for (int i=0;i<classes.size();i++){
			props.addAll(classes.get(i).getObjectProperties());
		}
		return props.size();
	}
	
	/**
	 * @return the number of distinct dataproperties with ranges in this ontology.
	 */
	public int getNrOfDataPropWithRanges(){
		int nrprop=0;
		//get all the distinct datatype properties
		Set<DatatypeProperty> dtprops = new HashSet<DatatypeProperty>();
		for (int i=0;i<classes.size();i++){
			dtprops.addAll(classes.get(i).getDataProperties());
		}
		
		//loop the properties to count the properties with ranges.
		for (DatatypeProperty dtprp : dtprops){
			if (dtprp.getRange().size()>0){
					nrprop++;
				}
		}
		return nrprop;
	}
	
	/**
	 * @return the number of distinct objectproperties with ranges in this ontology.
	 * an objectproperty with no range definitions is coupled to Thing.
	 * so these ranges are excluded from the count.
	 */
	public int getNrOfObjectPropWithRanges(){
		int nrprop=0;
		List<Class> range;

		//get all the distinct object properties
		Set<ObjectProperty> obprops = new HashSet<ObjectProperty>();
		for (int i=0;i<classes.size();i++){
			obprops.addAll(classes.get(i).getObjectProperties());
		}
		
		//loop the properties to count the ranges.
			for (ObjectProperty op: obprops ){
				
				range = op.getRange();
				//if 2 or more there is a range definition
				if (range.size()>1){
					nrprop++;
				} else {
					//there is always 1 class as range, test if it is Thing
					 if (!range.get(0).getURI().endsWith("Thing")){
						 nrprop++;
					 }
				}
			}
		return nrprop;
	}

	
	/**
	 * Counts the times a class is named in a range definition of a property.
	 * @return the rangeclass frequentie list
	 */
	public HashMap<Class,Integer> getRangeClassFrequenties(){
		HashMap<Class,Integer> classfrequenties = new HashMap<Class,Integer>() ;
		List<Class> ranges;
		List<Property> props;
		int freqcount;
		//list for saving the handled properties
		props = new ArrayList<Property>();
		
		//loop the classes to get the properties.
		for (Class cls : classes){
			for (ObjectProperty op: cls.getObjectProperties() ){
				//handle the distinct properties
				if (!props.contains(op)){
					//loop the properties to get the range classes
					ranges = op.getRange();
					for (Class range: ranges){
						if (!classfrequenties.containsKey(range)){
							classfrequenties.put(range, 1);
						} else {
							freqcount = classfrequenties.get(range);
							classfrequenties.put(range,++freqcount);
						}
					}
					//add the checked object property to the list. 
					props.add(op);
				}
			}
		}
		return classfrequenties;
	}


	/**
	 * Counts the use of the properties by the classes in this ontology.
	 * @return the property frequentie list
	 */
	public HashMap<Property,Integer> getPropertyFrequenties(){
		HashMap<Property,Integer> propertyfrequenties = new HashMap<Property,Integer>() ;
		int freqcount;
		
		//loop the classes to get the properties.
		for (Class cls : classes){
			//handle the object properties
			for (ObjectProperty op: cls.getObjectProperties() ){
				//count the properties 
				if (!propertyfrequenties.containsKey(op)){
					propertyfrequenties.put(op, 1);
				} else {
					freqcount = propertyfrequenties.get(op);
					propertyfrequenties.put(op,++freqcount);
				}
			}
			
			//handle the data properties
			for (DatatypeProperty dtp: cls.getDataProperties() ){
				//count the properties 
				if (!propertyfrequenties.containsKey(dtp)){
					propertyfrequenties.put(dtp, 1);
				} else {
					freqcount = propertyfrequenties.get(dtp);
					propertyfrequenties.put(dtp,++freqcount);
				}
			}
		}
		return propertyfrequenties;
	}
	
	
	
	/**
	 * Add class to ontology.
	 * 
	 * @param c class to be added
	 */
	public void addClass(Class c) {
		classes.add(c);
	}

	/**
	 * Set classes of ontology.
	 * 
	 * @param classes of ontology
	 */
	public void setClasses(ArrayList<Class> classes) {
		this.classes = classes;
	}
	
	/**
	 * Finds the dataproperty with the owldataproperty.
	 * @param uri the uri of the class
	 * @return the class
	 */
	public Class findClass(String uri){
		int classindex = -1;
		for (int i=0; i<classes.size();i++){
			if (classes.get(i).getURI().equals(uri)){
				classindex = i;
				break;
			}
		}
		return classes.get(classindex);
	}

	
}
