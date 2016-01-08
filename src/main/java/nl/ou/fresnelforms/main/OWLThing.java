package nl.ou.fresnelforms.main;

import java.util.Set;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitorEx;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitor;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * OWLThing class.
 */
public class OWLThing implements OWLClass {

	private static final long serialVersionUID = -7049816376336349722L;

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLClassExpressionVisitor 
	 */
	public void accept(OWLClassExpressionVisitor arg0) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLClassExpressionVisitorEx<O> 
	 * @param <O> 
	 * @return <O>
	 */
	public <O> O accept(OWLClassExpressionVisitorEx<O> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return ???
	 */
	public Set<OWLClassExpression> asConjunctSet() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return ???
	 */
	public Set<OWLClassExpression> asDisjunctSet() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * default-Javadoc.
	 * @return OWLClass
	 */
	public OWLClass asOWLClass() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLClassExpression
	 * @return boolean
	 */
	public boolean containsConjunct(OWLClassExpression arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * default-Javadoc.
	 * @return ClassExpressionType
	 */
	public ClassExpressionType getClassExpressionType() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return OWLClassExpression
	 */
	public OWLClassExpression getComplementNNF() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return OWLClassExpression
	 */
	public OWLClassExpression getNNF() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return OWLClassExpression
	 */
	public OWLClassExpression getObjectComplementOf() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isAnonymous() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isClassExpressionLiteral() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isOWLNothing() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isOWLThing() {
		// TODO Auto-generated method stub
		return true;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLObjectVisitor 
	 */
	public void accept(OWLObjectVisitor arg0) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLObjectVisitorEx<O> 
	 * @param <O> 
	 * @return <O>
	 */
	public <O> O accept(OWLObjectVisitorEx<O> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return Set<OWLAnonymousIndividual>
	 */
	public Set<OWLAnonymousIndividual> getAnonymousIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return null
	 */
	public Set<OWLClass> getClassesInSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return Set<OWLDataProperty>
	 */
	public Set<OWLDataProperty> getDataPropertiesInSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return Set<OWLDatatype>
	 */
	public Set<OWLDatatype> getDatatypesInSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return Set<OWLNamedIndividual>
	 */
	public Set<OWLNamedIndividual> getIndividualsInSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return Set<OWLClassExpression>
	 */
	public Set<OWLClassExpression> getNestedClassExpressions() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return Set<OWLObjectProperty>
	 */
	public Set<OWLObjectProperty> getObjectPropertiesInSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return Set<OWLEntity>
	 */
	public Set<OWLEntity> getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isBottomEntity() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isTopEntity() {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * default-Javadoc.
	 * @param o OWLObject
	 * @return int
	 */
	public int compareTo(OWLObject o) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLEntityVisitor 
	 */
	public void accept(OWLEntityVisitor arg0) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLEntityVisitorEx<O> 
	 * @param <O> 
	 * @return <O>
	 */
	public <O> O accept(OWLEntityVisitorEx<O> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	 /**
	 * default-Javadoc.
	 * @return OWLAnnotationProperty
	 */
	public OWLAnnotationProperty asOWLAnnotationProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	
	 /**
	 * default-Javadoc.
	 * @return OWLDataProperty
	 */
	public OWLDataProperty asOWLDataProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	
	 /**
	 * default-Javadoc.
	 * @return OWLDatatype
	 */
	public OWLDatatype asOWLDatatype() {
		// TODO Auto-generated method stub
		return null;
	}

	
	 /**
	 * default-Javadoc.
	 * @return OWLNamedIndividual
	 */
	public OWLNamedIndividual asOWLNamedIndividual() {
		// TODO Auto-generated method stub
		return null;
	}

	
	 /**
	 * default-Javadoc.
	 * @return OWLObjectProperty
	 */
	public OWLObjectProperty asOWLObjectProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	
	 /**
	 * default-Javadoc.
	 * @param arg0 OWLOntology 
	 * @return Set<OWLAnnotationAssertionAxiom>
	 */
	public Set<OWLAnnotationAssertionAxiom> getAnnotationAssertionAxioms(
			OWLOntology arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology 
	 * @return Set<OWLAnnotation>
	 */
	public Set<OWLAnnotation> getAnnotations(OWLOntology arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology 
	 * @param arg1 OWLAnnotationProperty 
	 * @return Set<OWLAnnotation>
	 */
	public Set<OWLAnnotation> getAnnotations(OWLOntology arg0,
			OWLAnnotationProperty arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	 /**
	 * default-Javadoc.
	 * @return EntityType<?>
	 */
	public EntityType<?> getEntityType() {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 EntityType<E>
	 * @param <E> 
	 * @return <E extends OWLEntity>
	 */
	public <E extends OWLEntity> E getOWLEntity(EntityType<E> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology 
	 * @return Set<OWLAxiom>
	 */
	public Set<OWLAxiom> getReferencingAxioms(OWLOntology arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology 
	 * @param arg1 boolean
	 * @return Set<OWLAxiom>
	 */
	public Set<OWLAxiom> getReferencingAxioms(OWLOntology arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	 /**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isBuiltIn() {
		// TODO Auto-generated method stub
		return false;
	}

	
	 /**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isOWLAnnotationProperty() {
		// TODO Auto-generated method stub
		return false;
	}

	
	 /**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isOWLClass() {
		// TODO Auto-generated method stub
		return false;
	}

	
	 /**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isOWLDataProperty() {
		// TODO Auto-generated method stub
		return false;
	}

	
	 /**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isOWLDatatype() {
		// TODO Auto-generated method stub
		return false;
	}

	
	 /**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isOWLNamedIndividual() {
		// TODO Auto-generated method stub
		return false;
	}

	
	 /**
	 * default-Javadoc.
	 * @return boolean
	 */
	public boolean isOWLObjectProperty() {
		// TODO Auto-generated method stub
		return false;
	}

	
	 /**
	 * default-Javadoc.
	 * @param arg0 EntityType<?>
	 * @return boolean
	 */
	public boolean isType(EntityType<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	 /**
	 * default-Javadoc.
	 * @return String
	 */
	public String toStringID() {
		// TODO Auto-generated method stub
		return null;
	}

	
	 /**
	 * default-Javadoc.
	 * @param arg0 OWLNamedObjectVisitor
	 */
	public void accept(OWLNamedObjectVisitor arg0) {
		// TODO Auto-generated method stub
		
	}

	
	 /**
	 * default-Javadoc.
	 * @return IRI
	 */
	public IRI getIRI() {
		// TODO Auto-generated method stub
		IRI iri = IRI.create("http://www.w3.org/2002/07/owl#Thing");
		return iri;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology 
	 * @return Set<OWLClassExpression>
	 */
	public Set<OWLClassExpression> getDisjointClasses(OWLOntology arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 Set<OWLOntology>
	 * @return Set<OWLClassExpression>
	 */
	public Set<OWLClassExpression> getDisjointClasses(Set<OWLOntology> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology
	 * @return Set<OWLClassExpression>
	 */
	public Set<OWLClassExpression> getEquivalentClasses(OWLOntology arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 Set<OWLOntology>
	 * @return Set<OWLClassExpression>
	 */
	public Set<OWLClassExpression> getEquivalentClasses(Set<OWLOntology> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology
	 * @return  Set<OWLIndividual> 
	 */
	public Set<OWLIndividual> getIndividuals(OWLOntology arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 Set<OWLOntology>
	 * @return  Set<OWLIndividual> 
	 */
	public Set<OWLIndividual> getIndividuals(Set<OWLOntology> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology
	 * @return  Set<OWLClassExpression> 
	 */
	public Set<OWLClassExpression> getSubClasses(OWLOntology arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 Set<OWLOntology>
	 * @return  Set<OWLClassExpression> 
	 */
	public Set<OWLClassExpression> getSubClasses(Set<OWLOntology> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology
	 * @return  Set<OWLClassExpression> 
	 */
	public Set<OWLClassExpression> getSuperClasses(OWLOntology arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 Set<OWLOntology>
	 * @return  Set<OWLClassExpression> 
	 */
	public Set<OWLClassExpression> getSuperClasses(Set<OWLOntology> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 OWLOntology
	 * @return  boolean 
	 */
	public boolean isDefined(OWLOntology arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	/**
	 * default-Javadoc.
	 * @param arg0 Set<OWLOntology>
	 * @return  boolean 
	 */
	public boolean isDefined(Set<OWLOntology> arg0) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * default-Javadoc.
	 * @param owlEntity OWLEntity
	 * @return  boolean 
	 */
	public boolean containsEntityInSignature(OWLEntity owlEntity) {
		// TODO Auto-generated method stub
		return false;
	}

}
