package nl.ou.fresnelforms.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nl.ou.fresnelforms.ontology.DataType;
import nl.ou.fresnelforms.ontology.DatatypeProperty;
import nl.ou.fresnelforms.ontology.ObjectProperty;
import nl.ou.fresnelforms.ontology.Ontology;
import nl.ou.fresnelforms.ontology.Class;
import nl.ou.fresnelforms.ontology.Property;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Class to import the active ontology.
 * 
 * @author Tim Zwanenberg
 *
 */
public class OWLImport {

	private static Ontology ontology;
	private static Logger log = LogManager.getLogger(OWLImport.class);

	/**
	 * private construcor to hide the default constructor.
	 */
	private OWLImport() {
	}

	/**
	 * Loads the active ontology.
	 * 
	 * @param manager the owl model manager
	 * @return the active ontology
	 */
	public static Ontology getActiveOntology(OWLModelManager manager) {
		OWLOntology ontology = manager.getActiveOntology(); // Get the active ontology from Protégé
		return initializeOntology(ontology); // Return our own kind of ontology
	}

	/**
	 * Initializes an Ontology from a OWLOntology.
	 * 
	 * @param source the source OWLOntology
	 * @return an initialized Ontology
	 */
	public static Ontology initializeOntology(OWLOntology source) {
		ontology = new Ontology(source);
		Map<OWLClass,Set<OWLClassExpression>> restrictions = null;

		// Load classes from ontology
		ArrayList<Class> classes = findClasses(ontology);
		
		//Retrieve the restrictions for the classes
		restrictions = retrieveClassRestrictions(classes);

		// Load data properties from ontology
		ArrayList<DatatypeProperty> dataProperties = findDataProperties(ontology);

		// Match data properties with classes (domain and ranges)
		matchDataPropertiesWithClasses(dataProperties, classes);

		// Load object properties from ontology
		ArrayList<ObjectProperty> objectProperties = findObjectProperties(ontology);

		// Match object properties with classes
		matchObjectPropertiesWithClasses(objectProperties, classes);

		//check for restrictions on the dataproperties
		checkRestrictionsOnClasses(restrictions,classes);

		// Match classes with parents
		matchClassesWithParents(classes);

		
		// Add classes to ontology
		ontology.setClasses(classes);

		return ontology;

	}

	/**
	 * Finds all classes in given ontology
	 * 
	 * @param ontology
	 * @return list of classes in ontology
	 */
	private static ArrayList<Class> findClasses(Ontology ontology) {
		// Get all OWLclasses from ontology
		Set<OWLClass> owlClassSet = ontology.getOWLOntology().getClassesInSignature();
		Iterator<OWLClass> allOwlClasses = owlClassSet.iterator();
		ArrayList<OWLClass> coveredOwlClasses = new ArrayList<OWLClass>();
		ArrayList<Class> classes = new ArrayList<Class>();
		boolean owlThingFound = false;
		// Convert each OWLclass to Ontology.Class
		while (allOwlClasses.hasNext()) {
			// Find all equivalent OWL-classes and handle them as one
			OWLClass owlClass = allOwlClasses.next();
			if (owlClass.isOWLThing()) {
				owlThingFound = true;
			}
			ArrayList<OWLClass> equivalentOwlClasses;
			// code part refactored to the method retrieveEquivalentOwlClasse. TT-27112014
			equivalentOwlClasses = retrieveEquivalentOwlClasses(owlClass);

			// Check if one of the equivalent OWL-classes has been covered before
			// 'checkstyle message:empty if' changed the condition to not and removed the else part TT-27-11-2014
			if (!coveredOwlClasses.containsAll(equivalentOwlClasses)) {
				// Has not been covered before. Add to classes.
				coveredOwlClasses.addAll(equivalentOwlClasses);
				classes.add(new Class(equivalentOwlClasses));
			}
			
			
		}
		if (!owlThingFound) {
			OWLThing owlThing = new OWLThing();
			ArrayList<OWLClass> owlThings = new ArrayList<OWLClass>();
			owlThings.add(owlThing);
			classes.add(new Class(owlThings));
		}
		return classes;
	}

	
	/**
	 * Retrieve the restrictions for all classes.
	 * all equivalent and super classes are examined for restriction classes.
	 * @param classes All the classes to check for restrictions
	 * @return the mapping from owlclass to restriction expressions.
	 */
	private static Map<OWLClass,Set<OWLClassExpression>> retrieveClassRestrictions(ArrayList<Class> classes){
		Map<OWLClass,Set<OWLClassExpression>> restrictions = new HashMap<OWLClass,Set<OWLClassExpression>>();
		Set<OWLClassExpression> owlClassExpressionSet;
		//retrieve the equivalent and super restrictionclasses
		for (Class cls: classes){
			owlClassExpressionSet = retrieveClassRestrictions(cls.getOWLClass());
			restrictions.put(cls.getOWLClass(), owlClassExpressionSet );
		}
		return restrictions;
	}
	
	
	/**
	 * retrieve the equivalent classes of the given owl class.
	 * 
	 * @param owlClass the owl class
	 * @return the array of equivalent classes.
	 */
	private static ArrayList<OWLClass> retrieveEquivalentOwlClasses(OWLClass owlClass) {
		ArrayList<OWLClass> equivalentOwlClasses = new ArrayList<OWLClass>();

		equivalentOwlClasses.add(owlClass);
		Set<OWLClassExpression> equivalantOWLClassExpressionSet = owlClass.getEquivalentClasses(ontology
				.getOWLOntology());
		Iterator<OWLClassExpression> equivalantOWLClassExpressions = equivalantOWLClassExpressionSet.iterator();
		while (equivalantOWLClassExpressions.hasNext()) {
			OWLClassExpression equivalantOWLClassExpression = equivalantOWLClassExpressions.next();
			if (!equivalantOWLClassExpression.isAnonymous()) {
				OWLClass owlEquivalentClass = equivalantOWLClassExpression.asOWLClass();
				equivalentOwlClasses.add(owlEquivalentClass);
			}
		}
		return equivalentOwlClasses;
	}

	/**
	 * Retrieve the class expressions that are restrictions of the given owl class.
	 * 
	 * @param owlClass The owl class
	 * @return The set of class expressions.
	 */
	private static Set<OWLClassExpression> retrieveClassRestrictions(OWLClass owlClass) {
		Set<OWLClassExpression> owlClassExpressionSet;
		Set<OWLClassExpression> restrictionExpressionSet = new HashSet<OWLClassExpression>();
		
		//Get the equivalent class expressions
		owlClassExpressionSet = owlClass.getEquivalentClasses(ontology.getOWLOntology());
		//check if there are any equivalent classes
		if (owlClassExpressionSet != null && !owlClassExpressionSet.isEmpty()){
			//prepare to loop 
			Iterator<OWLClassExpression> equivalantOWLClassExpressions = owlClassExpressionSet.iterator();
			//loop the equivalent class expressions
			while (equivalantOWLClassExpressions.hasNext()) {
				OWLClassExpression equivalantOWLClassExpression = equivalantOWLClassExpressions.next();
				if (equivalantOWLClassExpression.getClassExpressionType() != ClassExpressionType.OWL_CLASS) {
					restrictionExpressionSet.add(equivalantOWLClassExpression);
				}
			}
		}
		
		//get the superclass class-expressions
		owlClassExpressionSet = owlClass.getSuperClasses(ontology.getOWLOntology());
		//check if there are any superclasses
		if (owlClassExpressionSet != null && !owlClassExpressionSet.isEmpty()){
			//prepare to loop 
			Iterator<OWLClassExpression> superOWLClassExpressions = owlClassExpressionSet.iterator();
			//loop the equivalent class expressions
			while (superOWLClassExpressions.hasNext()) {
				OWLClassExpression superOWLClassExpression = superOWLClassExpressions.next();
				if (superOWLClassExpression.getClassExpressionType() != ClassExpressionType.OWL_CLASS) {
					restrictionExpressionSet.add(superOWLClassExpression);
				}
			}
		}
		return restrictionExpressionSet;
	}
	
	
	
	
	/**
	 * Finds all data properties in given ontology
	 * 
	 * @param ontology
	 * @return list of data properties in ontology
	 */
	private static ArrayList<DatatypeProperty> findDataProperties(Ontology ontology) {
		String label;
		DatatypeProperty dtp;
		
		// Get all OWLdataproperties from ontology
		Set<OWLDataProperty> owlDataPropertySet = ontology.getOWLOntology().getDataPropertiesInSignature();
		Iterator<OWLDataProperty> owlDataProperties = owlDataPropertySet.iterator();
		ArrayList<DatatypeProperty> dataProperties = new ArrayList<DatatypeProperty>();
		// Convert each OWLdataproperty to Ontology.DataProperty
		while (owlDataProperties.hasNext()) {
			OWLDataProperty owlDataProperty = owlDataProperties.next();
			// check if data property is equivalent to other data property
			boolean equivalent = false;
			for (DatatypeProperty d : dataProperties) {
				if (d.equalsOWLDataProperty(owlDataProperty)) {
					equivalent = true;
				}
			}
			if (equivalent) {
				// If data property is equivalent to other data property, ignore data property
				owlDataProperties.remove();
			} else {
				// Otherwise check for equivalent data properties and add those to array of equivalent data properties
				ArrayList<OWLDataProperty> equalDataProperties = new ArrayList<OWLDataProperty>();
				equalDataProperties.add(owlDataProperty);
				Set<OWLDataPropertyExpression> equivalantOWLDataPropertyExpressionSet = owlDataProperty
						.getEquivalentProperties(ontology.getOWLOntology());
				Iterator<OWLDataPropertyExpression> equivalantOWLDataPropertyExpressions = equivalantOWLDataPropertyExpressionSet
						.iterator();
				while (equivalantOWLDataPropertyExpressions.hasNext()) {
					OWLDataPropertyExpression equivalantOWLDataPropertyExpression = equivalantOWLDataPropertyExpressions
							.next();
					if (!equivalantOWLDataPropertyExpression.isAnonymous()) {
						OWLDataProperty owlEquivalentDataProperty = equivalantOWLDataPropertyExpression
								.asOWLDataProperty();
						equalDataProperties.add(owlEquivalentDataProperty);
					}
				}
				label = findPropertyLabel(equalDataProperties.get(0),ontology.getOWLOntology() );
				dtp = new DatatypeProperty(equalDataProperties);
				dtp.setFunctional(  dtp.getOWLDataProperty().isFunctional(ontology.getOWLOntology()));
				dtp.setLabel(label);
				// And add to array of data properties
				dataProperties.add(dtp);
			}
		}
		// Return array of data properties
		return dataProperties;
	}

	
	
	
	
	/**
	 * Finds all object properties in given ontology
	 * 
	 * @param ontology
	 * @return list of object properties in ontology
	 */
	private static ArrayList<ObjectProperty> findObjectProperties(Ontology ontology) {
		String label;
		ObjectProperty objp;

		// Get all OWLobjectproperties from ontology
		Set<OWLObjectProperty> owlObjectPropertySet = ontology.getOWLOntology().getObjectPropertiesInSignature();
		Iterator<OWLObjectProperty> owlObjectProperties = owlObjectPropertySet.iterator();
		ArrayList<ObjectProperty> objectProperties = new ArrayList<ObjectProperty>();
		// Convert each OWLobjectproperty to Ontology.ObjectProperty
		while (owlObjectProperties.hasNext()) {
			OWLObjectProperty owlObjectProperty = owlObjectProperties.next();
			boolean equivalent = false;
			for (ObjectProperty o : objectProperties) {
				if (o.equalsOWLObjectProperty(owlObjectProperty)) {
					equivalent = true;
				}
			}
			if (equivalent) {
				// If object property is equivalent to other object property, ignore object property
				owlObjectProperties.remove();
			} else {
				// Otherwise check for equivalent object properties and add those to array of equivalent object
				// properties
				ArrayList<OWLObjectProperty> equalObjectProperties = new ArrayList<OWLObjectProperty>();
				equalObjectProperties.add(owlObjectProperty);
				Set<OWLObjectPropertyExpression> equivalantOWLObjectPropertyExpressionSet = owlObjectProperty
						.getEquivalentProperties(ontology.getOWLOntology());
				Iterator<OWLObjectPropertyExpression> equivalantOWLObjectPropertyExpressions = equivalantOWLObjectPropertyExpressionSet
						.iterator();
				while (equivalantOWLObjectPropertyExpressions.hasNext()) {
					OWLObjectPropertyExpression equivalantOWLObjectPropertyExpression = equivalantOWLObjectPropertyExpressions
							.next();
					if (!equivalantOWLObjectPropertyExpression.isAnonymous()) {
						OWLObjectProperty owlEquivalentObjectProperty = equivalantOWLObjectPropertyExpression
								.asOWLObjectProperty();
						equalObjectProperties.add(owlEquivalentObjectProperty);
					}
				}
				label = findPropertyLabel(equalObjectProperties.get(0),ontology.getOWLOntology() );
				objp = new ObjectProperty(equalObjectProperties);
				objp.setLabel(label);
				objp.setFunctional( objp.getOWLObjectProperty().isFunctional(ontology.getOWLOntology()));
				objp.setInversefunctional(objp.getOWLObjectProperty().isInverseFunctional(ontology.getOWLOntology()));
				// And add to array of object properties
				objectProperties.add(objp);
			}
		}
		// Return array of object properties
		return objectProperties;
	}

	/**
	 * Find the label in the annotation set of the given property.
	 * @param owlprop The property
	 * @param owlontology The owlontology
	 * @return The label or "" if there is none.
	 */
	private static String findPropertyLabel(OWLEntity owlprop, OWLOntology owlontology){
	String label="";
	OWLLiteral owllit;
	
	Set<OWLAnnotation> annotationset = owlprop.getAnnotations(owlontology);
	OWLAnnotation annotation;
	
	Iterator<OWLAnnotation> it = annotationset.iterator();
	while (it.hasNext()){
		annotation = it.next();
		if (annotation.getProperty().isLabel()){
			owllit = (OWLLiteral) annotation.getValue();
			label =  owllit.getLiteral();
			break;
		}
	}
	return label;
	}
	
	/**
	 * Matches data properties with classes in the ontology
	 * 
	 * @param data properties
	 * @param classes
	 */
	private static void matchDataPropertiesWithClasses(ArrayList<DatatypeProperty> dataProperties, ArrayList<Class> classes) {
		Class thingClass = null;
		thingClass = findOWLThing(classes);

		// Get the range of each data property
		for (DatatypeProperty d : dataProperties) {
			// Get the range of each data property
			Set<OWLDataRange> owlDataRangeSet = d.getOWLDataProperty().getRanges(ontology.getOWLOntology());
			Iterator<OWLDataRange> owlDataRanges = owlDataRangeSet.iterator();
			// Add the range data types to the data property
			while (owlDataRanges.hasNext()) {
				OWLDataRange owlDataRange = owlDataRanges.next();
				switch (owlDataRange.getDataRangeType()) {
				case DATATYPE:
					DataType dataType = new DataType(owlDataRange);
					d.addRange(dataType);
					break;
				case DATA_ONE_OF:
					// stuff to do in case of owl:oneOf
					break;
				default:
					break;
				}
			}
			// Get the domain for each data property
			Set<OWLClassExpression> domainClassExpressionSet = d.getOWLDataProperty().getDomains(
					ontology.getOWLOntology());
			Iterator<OWLClassExpression> domainClassExpressions = domainClassExpressionSet.iterator();
			// Add this data property to each of its domain classes
			if (!domainClassExpressions.hasNext()) {
				thingClass.addDataProperty(d);
			} else {
				while (domainClassExpressions.hasNext()) {
					OWLClassExpression domainClassExpression = domainClassExpressions.next();
					if (!domainClassExpression.isAnonymous()) {
						OWLClass domainOWLClass = domainClassExpression.asOWLClass();
						for (Class c : classes) {
							if (c.getOWLClasses().contains(domainOWLClass)) {
								c.addDataProperty(d);
							}
						}
					}
				}
			}
		}
	}

	
	/**
	 * Check for restrictions on all classes and handle them
	 * @param restrictions the mapping from classes to classexpressions
	 * @param classes the classes in the ontology
	 */
	private static void  checkRestrictionsOnClasses( Map<OWLClass,Set<OWLClassExpression>> restrictions,ArrayList<Class> classes){
		Set<OWLClassExpression> restrictionExpressionSet;
		OWLClassExpression restrictionExpression;
		//loop all the classes
		for ( Class cls : classes){
			//check if there are restrictions for this class
			if (restrictions.containsKey(cls.getOWLClass())){
				//get the restrictions
				restrictionExpressionSet = restrictions.get(cls.getOWLClass());
				Iterator<OWLClassExpression> resIterator = restrictionExpressionSet.iterator();
				//loop the restrictions and handle them.
				while (resIterator.hasNext()){
					//handle the restriction for this class
					restrictionExpression = resIterator.next();
					handleRestrictionForClass(restrictionExpression, cls);
					
				}
			}
		}
	}

	
	/**
	 * Handle different kind of restriction expressions.
	 * @param restrictionExpression The restriction expressie
	 * @param cls the class for which the restrictions are defined.
	 */
	private static void handleRestrictionForClass(OWLClassExpression restrictionExpression, Class cls){
		switch (restrictionExpression.getClassExpressionType()) {
			case DATA_MAX_CARDINALITY:
				OWLDataMaxCardinality dmaxc = (OWLDataMaxCardinality) restrictionExpression;
				handleDataMaxCardinalityForClass(dmaxc, cls);
				break;
			case DATA_MIN_CARDINALITY: 
				OWLDataMinCardinality dminc = (OWLDataMinCardinality) restrictionExpression;
				handleDataMinCardinalityForClass(dminc, cls);
				break;
			case OBJECT_MAX_CARDINALITY: 
				OWLObjectMaxCardinality omaxc = (OWLObjectMaxCardinality) restrictionExpression;
				handleObjectMaxCardinalityForClass(omaxc, cls);
				break;
			case OBJECT_MIN_CARDINALITY: 
				OWLObjectMinCardinality ominc = (OWLObjectMinCardinality) restrictionExpression;
				handleObjectMinCardinalityForClass(ominc, cls);
				break;
			default:
		}
	}

	
	/**
	 * Handle the dataproperty maxcardinality restriction 
	 * @param dmc The owlapi maxcardinality restriction
	 * @param cls The class
	 */
	private static void handleDataMaxCardinalityForClass(OWLDataMaxCardinality dmc, Class cls){
		Property ontprop;
		
		try {
			//get the cardinality and property
			OWLDataProperty owldp =  dmc.getProperty().asOWLDataProperty() ;
			int maxcard = dmc.getCardinality();
			
			//get the index of the property in the property array
			int dpindex = cls.findOWLDataProperty(owldp);
			//check if property index is found
			if (dpindex >=0){
				ontprop = cls.getDataProperties().get(dpindex);
				cls.setMaxCardinalityRestriction(ontprop, maxcard);
			}
			
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	/**
	 * Handle the dataproperty mincardinality restriction 
	 * @param dmc The owlapi mincardinality restriction
	 * @param cls The class
	 */
	private static void handleDataMinCardinalityForClass(OWLDataMinCardinality dmc, Class cls){
		Property ontprop;
		try {
			//get the cardinality and property
			OWLDataProperty owldp =  dmc.getProperty().asOWLDataProperty() ;
			int mincard = dmc.getCardinality();
			//get the index of the property in the property array
			int dpindex = cls.findOWLDataProperty(owldp);
			//check if property index is found
			if (dpindex >=0){
				ontprop = cls.getDataProperties().get(dpindex);
				cls.setMinCardinalityRestriction(ontprop, mincard);
			}

		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	/**
	 * Handle the objectproperty maxcardinality restriction 
	 * @param dmc The owlapi maxcardinality restriction
	 * @param cls The class
	 */
	private static void handleObjectMaxCardinalityForClass(OWLObjectMaxCardinality dmc, Class cls){
		Property ontprop;
		try {
			//get the cardinality and property
			OWLObjectProperty owldp =  dmc.getProperty().asOWLObjectProperty() ;
			int maxcard = dmc.getCardinality();
			//get the index of the property in the property array
			int opindex = cls.findOWLObjectProperty(owldp);
			//check if property index is found
			if (opindex >=0){
				ontprop = cls.getObjectProperties().get(opindex);
				cls.setMaxCardinalityRestriction(ontprop, maxcard);
			}

		} catch (Exception e) {
			log.error(e.toString());
		}
	}

	/**
	 * Handle the objectproperty mincardinality restriction 
	 * @param dmc The owlapi mincardinality restriction
	 * @param cls The class
	 */
	private static void handleObjectMinCardinalityForClass(OWLObjectMinCardinality dmc, Class cls){
		Property ontprop;
		try {
			//get the cardinality and property
			OWLObjectProperty owldp =  dmc.getProperty().asOWLObjectProperty() ;
			int mincard = dmc.getCardinality();
			//get the index of the property in the property array
			int opindex = cls.findOWLObjectProperty(owldp);
			//check if property index is found
			if (opindex >=0){
				ontprop = cls.getObjectProperties().get(opindex);
				cls.setMinCardinalityRestriction(ontprop, mincard);
			}
			
		} catch (Exception e) {
			log.error(e.toString());
		}
	}
	
	
	/**
	 * Matches object properties with classes in the ontology
	 * 
	 * @param object properties
	 * @param classes
	 */
	private static void matchObjectPropertiesWithClasses(ArrayList<ObjectProperty> objectProperties,
			ArrayList<Class> classes) {
		Class thingClass = null;
		thingClass = findOWLThing(classes);

		for (ObjectProperty o : objectProperties) {

			// Get the range of each object property
			Set<OWLClassExpression> rangeClassExpressionSet = o.getOWLObjectProperty().getRanges(
					ontology.getOWLOntology());
			addRangeClasses(classes, o, rangeClassExpressionSet);

			// If size of object property range is smaller than 1, no range classes were found.
			// Therefore add owl:Thing as range class.
			if (o.getRange().size() < 1) {
				o.addRange(thingClass);
			}

			// Get the domain of each object property
			Set<OWLClassExpression> domainClassExpressionSet = o.getOWLObjectProperty().getDomains(
					ontology.getOWLOntology());
			addProperty2DomainClasses(classes, thingClass, o, domainClassExpressionSet);
		}
	}

	/**
	 * Add this object property to each of its domain classes.
	 * 
	 * @param classes the array of owl classes
	 * @param thingClass the thing class
	 * @param o the object property
	 * @param domainClassExpressionSet the domain class expression set
	 */
	private static void addProperty2DomainClasses(ArrayList<Class> classes, Class thingClass, ObjectProperty o,
			Set<OWLClassExpression> domainClassExpressionSet) {
		Iterator<OWLClassExpression> domainClassExpressions = domainClassExpressionSet.iterator();
		// Add this object property to each of its domain classes
		if (!domainClassExpressions.hasNext()) {
			thingClass.addObjectProperty(o);
		} else {
			while (domainClassExpressions.hasNext()) {
				OWLClassExpression domainClassExpression = domainClassExpressions.next();
				if (!domainClassExpression.isAnonymous()) {
					OWLClass domainOWLClass = domainClassExpression.asOWLClass();
					for (Class c : classes) {
						if (c.getOWLClasses().contains(domainOWLClass)) {
							c.addObjectProperty(o);
						}
					}
				}
			}
		}
	}

	/**
	 * Add the range classes to the object property.
	 * 
	 * @param classes the array of owl classes
	 * @param o the object property
	 * @param rangeClassExpressionSet the range class expression set
	 */
	private static void addRangeClasses(ArrayList<Class> classes, ObjectProperty o,
			Set<OWLClassExpression> rangeClassExpressionSet) {
		Iterator<OWLClassExpression> rangeClassExpressions = rangeClassExpressionSet.iterator();
		// Add the range classes to the object property
		while (rangeClassExpressions.hasNext()) {
			OWLClassExpression rangeClassExpression = rangeClassExpressions.next();
			if (!rangeClassExpression.isAnonymous()) {
				OWLClass rangeOWLClass = rangeClassExpression.asOWLClass();
				for (Class c : classes) {
					if (c.getOWLClasses().contains(rangeOWLClass)) {
						o.addRange(c);
						c.addRangeOfObjectProperty(o);
					}
				}
			}
		}
	}

	/**
	 * tries to find the Thing class.
	 * 
	 * @param classes an array with classes
	 * @return null or the Thing class
	 */
	private static Class findOWLThing(ArrayList<Class> classes) {
		Class thingClass = null;
		for (Class c : classes) {
			for (OWLClass owlc : c.getOWLClasses()) {
				if (owlc.isOWLThing()) {
					thingClass = c;
				}
			}
		}
		return thingClass;
	}

	/**
	 * Matches classes with parent classes in the ontology.
	 * 
	 * @param classes
	 */
	private static void matchClassesWithParents(ArrayList<Class> classes) {
		Class thingClass = null;

		thingClass = findOWLThing(classes);
		for (Class c : classes) {
			if (!c.equals(thingClass)) {
				// Get the parents for each class, except for the first class which is owl:Thing
				Set<OWLClassExpression> parentClassExpressionSet = c.getOWLClass().getSuperClasses(
						ontology.getOWLOntology());
				Iterator<OWLClassExpression> parentClassExpressions = parentClassExpressionSet.iterator();
				// Add the parent classes to the class
				while (parentClassExpressions.hasNext()) {
					OWLClassExpression parentClassExpression = parentClassExpressions.next();

					if (!parentClassExpression.isAnonymous()) {
						// code fragment refactored to the method addParent - TT27112014
						addParent(classes, c, parentClassExpression);

					} else {

						// code fragment refactored to the method retrieveParentByDomain - TT27112014
						retrieveParentByDomain(classes, c, parentClassExpression);

					}
				}
				// If class still hasn't got any parents, make owl:Thing its parent
				if (c.getParents().isEmpty()) {
					c.addParent(thingClass);
				}
			}
		}
	}

	/**
	 * retrieve parent classes for a anonymous parent by domain classes.
	 * 
	 * @param classes the classes
	 * @param c the class
	 * @param parentClassExpression the parent class expression
	 */
	private static void retrieveParentByDomain(ArrayList<Class> classes, Class c,
			OWLClassExpression parentClassExpression) {

		ArrayList<OWLClass> owlDomainClasses = new ArrayList<OWLClass>();
		Iterator<OWLDataProperty> dataProperties = parentClassExpression.getDataPropertiesInSignature().iterator();

		while (dataProperties.hasNext()) {
			OWLDataProperty dataProperty = dataProperties.next();
			Iterator<OWLClassExpression> dataPropertyDomains = dataProperty.getDomains(ontology.getOWLOntology())
					.iterator();
			while (dataPropertyDomains.hasNext()) {
				OWLClassExpression dataPropertyDomain = dataPropertyDomains.next();
				// the asOWLClass needs a check for anonymous classes!
				if (!dataPropertyDomain.isAnonymous()) {
					owlDomainClasses.add(dataPropertyDomain.asOWLClass());
				}
			}
		}

		Iterator<OWLObjectProperty> objectProperties = parentClassExpression.getObjectPropertiesInSignature()
				.iterator();
		while (objectProperties.hasNext()) {
			OWLObjectProperty objectProperty = objectProperties.next();
			Iterator<OWLClassExpression> objectPropertyDomains = objectProperty.getDomains(ontology.getOWLOntology())
					.iterator();
			while (objectPropertyDomains.hasNext()) {
				OWLClassExpression objectPropertyDomain = objectPropertyDomains.next();
				// the asOWLClass needs a check for anonymous classes!
				if (!objectPropertyDomain.isAnonymous()) {
					owlDomainClasses.add(objectPropertyDomain.asOWLClass());
				}
			}
		}

		for (OWLClass owlDomainClass : owlDomainClasses) {
			for (Class parentClass : classes) {
				if (parentClass.getOWLClasses().contains(owlDomainClass)) {
					if (!c.equals(parentClass)) {
						c.addParent(parentClass);
					}
				}
			}
		}

	}

	/**
	 * add the parentclasses to a non anonymous class.
	 * 
	 * @param classes the classes
	 * @param c the class
	 * @param parentClassExpression the parent class expression
	 */
	private static void addParent(ArrayList<Class> classes, Class c, OWLClassExpression parentClassExpression) {
		if (!parentClassExpression.isAnonymous()) {
			OWLClass owlParentClass = parentClassExpression.asOWLClass();
			for (Class parentClass : classes) {
				if (parentClass.getOWLClasses().contains(owlParentClass)) {
					if (!c.equals(parentClass)) {
						c.addParent(parentClass);
					}
				}
			}
		}
	}

}
