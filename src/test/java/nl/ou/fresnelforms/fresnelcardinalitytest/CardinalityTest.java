package nl.ou.fresnelforms.fresnelcardinalitytest;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresnel.Lens;
import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.main.OWLImport;
import nl.ou.fresnelforms.ontology.Class;
import nl.ou.fresnelforms.ontology.Ontology;
import nl.ou.fresnelforms.ontology.Property;
import nl.ou.fresnelforms.ontology.PropertyRestriction;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Test reading of the cardinality properties and setting in the fresnel mode.
 * @author jn.theunissen@studie.ou.nl
 *
 */
public class CardinalityTest {
	private static final String ONTOLOGY_URL = "JeamesDeanMovies.owl";
	private static final String CLASSONEMOVIEOWNER_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#OneMovieOwner";
	private static final String CLASSMOVIEOWNER_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#MovieOwner";
	private static final String CLASSCANBEONEMOVIEOWNER_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#CanBeOneMovieOwner";
	private static final String CLASSMAYBEMOVIEOWNER_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#MaybeMovieOwner";
	private static final String OWNSMOVIEPROPERTY_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#ownsMovie";
	private PropertyBinding pbonemovieowner,pbmovieowner,pbcanbeonemovieowner, pbmaybemovieowner;
	private PropertyRestriction onemovieownerpropertyrestriction, movieownerpropertyrestriction,canbeonemovieownerpropertyrestriction,maybemovieownerpropertyrestriction;

	/**
	 * Setup the test.
	 * @throws Exception throw any exception
	 */
	@Before
	public void setUp() throws Exception {
		OWLOntologyManager manager;
		Ontology activeOntologyModel;
		Fresnel fresnelmodel;
		Lens tmpLens;
		Class tmpclass;
		Property tmpproperty;

		manager = OWLManager.createOWLOntologyManager(); // use owlapi's OWLManager
		//load the active ontology
		URL url = getClass().getClassLoader().getResource(ONTOLOGY_URL);
		File file = new File(url.toURI());
		OWLOntology srcontology = manager.loadOntologyFromOntologyDocument(file); // load the source ontology
		//Init the fresnelforms ontology
		activeOntologyModel = OWLImport.initializeOntology(srcontology); // and import it into our own Ontology object
		//Init the Fresnel ontology
		fresnelmodel = new Fresnel(activeOntologyModel);
		//init onemovieowner 
		tmpLens = fresnelmodel.getLens(CLASSONEMOVIEOWNER_URI);
		pbonemovieowner = tmpLens.getPropertyBinding(OWNSMOVIEPROPERTY_URI); 
		tmpclass = tmpLens.getClassLensDomain();
		tmpproperty = pbonemovieowner.getProperty().getProperty();
		onemovieownerpropertyrestriction=  tmpclass.getPropertyrestrictions().get(tmpproperty);

		//init movieowner
		tmpLens = fresnelmodel.getLens(CLASSMOVIEOWNER_URI);
		pbmovieowner = tmpLens.getPropertyBinding(OWNSMOVIEPROPERTY_URI); 
		tmpclass = tmpLens.getClassLensDomain();
		tmpproperty = pbmovieowner.getProperty().getProperty();
		movieownerpropertyrestriction=  tmpclass.getPropertyrestrictions().get(tmpproperty);

		//init canbemovieowner
		tmpLens = fresnelmodel.getLens(CLASSCANBEONEMOVIEOWNER_URI);
		pbcanbeonemovieowner = tmpLens.getPropertyBinding(OWNSMOVIEPROPERTY_URI); 
		tmpclass = tmpLens.getClassLensDomain();
		tmpproperty = pbcanbeonemovieowner.getProperty().getProperty();
		canbeonemovieownerpropertyrestriction=  tmpclass.getPropertyrestrictions().get(tmpproperty);

		//init maybemovieowner
		tmpLens = fresnelmodel.getLens(CLASSMAYBEMOVIEOWNER_URI);
		pbmaybemovieowner = tmpLens.getPropertyBinding(OWNSMOVIEPROPERTY_URI); 
		tmpclass = tmpLens.getClassLensDomain();
		tmpproperty = pbmaybemovieowner.getProperty().getProperty();
		maybemovieownerpropertyrestriction =  tmpclass.getPropertyrestrictions().get(tmpproperty);

	}

	/**
	 * 
	 */
	@Test
	public void test() {
		//onemovie owner
		assertFalse(pbonemovieowner.isList());
		assertTrue( pbonemovieowner.isMandatory());
		assertEquals(1,onemovieownerpropertyrestriction.getMinCardinality());
		assertEquals(1,onemovieownerpropertyrestriction.getMaxCardinality());
		//movieowner
		assertTrue(pbmovieowner.isList());
		assertTrue( pbmovieowner.isMandatory());
		assertEquals(1,movieownerpropertyrestriction.getMinCardinality());
		assertEquals(-1,movieownerpropertyrestriction.getMaxCardinality());
		//canbeonemovieowner
		assertFalse(pbcanbeonemovieowner.isList());
		assertFalse( pbcanbeonemovieowner.isMandatory());
		assertEquals(0,canbeonemovieownerpropertyrestriction.getMinCardinality());
		assertEquals(1,canbeonemovieownerpropertyrestriction.getMaxCardinality());
		//maybemovieowner
		assertTrue(pbmaybemovieowner.isList());
		assertFalse( pbmaybemovieowner.isMandatory());
		assertNull(maybemovieownerpropertyrestriction);
		
	}

}
