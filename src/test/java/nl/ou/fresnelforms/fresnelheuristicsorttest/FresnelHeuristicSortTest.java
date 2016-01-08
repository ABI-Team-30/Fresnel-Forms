package nl.ou.fresnelforms.fresnelheuristicsorttest;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URL;

import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresnel.Lens;
import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.main.OWLImport;
import nl.ou.fresnelforms.ontology.Ontology;
import nl.ou.fresnelforms.ontology.Property;
import nl.ou.fresnelforms.view.LensBox;
import nl.ou.fresnelforms.view.PropertyLabel;
import nl.ou.fresnelforms.view.PropertyLabelAlphabetComparator;
import nl.ou.fresnelforms.view.PropertyLabelHeuristicComparator;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * @author jn.theunissen@studie.ou.nl
 * 
 */
public class FresnelHeuristicSortTest {
	private static final String ONTOLOGY_URL = "JeamesDeanMovies_heuristicsorttest.owl";
	private static final String CLASSACTOR_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#Actor";
	private static final String BIRTHDAYPROPERTY_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#birthDate";
	private static final String PLAYSINPROPERTY_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#playsin";
	private static final String OWNSMOVIEPROPERTY_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#ownsMovie";
	private static final String IDPROPERTY_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#id";
	private static final String BSNPROPERTY_URI = "http://www.semanticweb.org/teunt/ontologies/2015/2/untitled-ontology-432#bsn";
	private static final String FOAFNAMEPROPERTY_URI="http://xmlns.com/foaf/0.1/name";
	private static final String FOAFFIRSTNAMEPROPERTY_URI="http://xmlns.com/foaf/0.1/firstname";
	private static final String FOAFLASTNAMEPROPERTY_URI="http://xmlns.com/foaf/0.1/lastname";
	private static final String FOAFHOMEPAGEPROPERTY_URI="http://xmlns.com/foaf/0.1/homepage";

	private URI uri1,uri2;
	private Property prop1,prop2;
	private PropertyLabel proplabel1,proplabel2;
	private Ontology activeOntologyModel;
	private Fresnel fresnelmodel;
	private LensBox lbactor;
	private PropertyBinding propbinding1,propbinding2;
	private PropertyLabelAlphabetComparator proplblalphacomp;
	private PropertyLabelHeuristicComparator proplblheuristiccomp;
	private Lens actorLens;


	/**
	 * @throws Exception The exception that can be thrown
	 */
	@Before
	public void setUp() throws Exception {
		OWLOntologyManager manager;
		
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
		actorLens = fresnelmodel.getLens(CLASSACTOR_URI);
		
		propbinding1 = actorLens.getPropertyBinding(BIRTHDAYPROPERTY_URI);
		propbinding2 = actorLens.getPropertyBinding(PLAYSINPROPERTY_URI);
		
		lbactor = new LensBox(actorLens);
		proplabel1 = new PropertyLabel(propbinding1, lbactor);
		proplabel2 = new PropertyLabel(propbinding2, lbactor);
		
		proplblalphacomp = new PropertyLabelAlphabetComparator();
		proplblheuristiccomp = new PropertyLabelHeuristicComparator(activeOntologyModel.getRangeClassFrequenties(),activeOntologyModel.getPropertyFrequenties());
		
		//setup the uri and properties
		uri1 = new URI("http://xmlns.com/foaf/0.1/account");
		uri2 = new URI("foaf:account");
		prop1 = new Property("http://xmlns.com/foaf/0.1/name");
		prop2 = new Property("http://www.w3.org/2001/XMLSchema#date");
	}

	/**
	 * The tests.
	 */
	@Test
	public void test() {
		assertEquals("http://xmlns.com/foaf/0.1/account",uri1.toString());
		assertEquals("foaf:account",uri2.toString());
		assertEquals("http",uri1.getScheme());
		assertEquals("foaf",uri2.getScheme());
		assertEquals("//xmlns.com/foaf/0.1/account",uri1.getSchemeSpecificPart());
		assertEquals("account",uri2.getSchemeSpecificPart());
		assertFalse(prop1.isFunctional());
		assertFalse(prop1.isInversefunctional());
		assertEquals("http",prop1.getPrefix());
		assertEquals("name",prop1.getResourceName());
		assertEquals("http",prop2.getPrefix());
		assertEquals("date",prop2.getResourceName());
	}
	
	
	/**
	 * The alphabetic comparator test.
	 */
	@Test
	public void alphabeticalSortTest(){
		proplabel1.getPropertyBinding().setLabel("A");
		proplabel2.getPropertyBinding().setLabel("A");
		assertEquals(0, proplblalphacomp.compare(proplabel1, proplabel2) );
		proplabel1.getPropertyBinding().setLabel("A");
		proplabel2.getPropertyBinding().setLabel("Z");
		assertEquals(-1, proplblalphacomp.compare(proplabel1, proplabel2));
		proplabel1.getPropertyBinding().setLabel("Z");
		proplabel2.getPropertyBinding().setLabel("A");
		assertEquals(1,proplblalphacomp.compare(proplabel1, proplabel2));
		proplabel1.getPropertyBinding().setLabel("a");
		proplabel2.getPropertyBinding().setLabel("A");
		assertEquals(0, proplblalphacomp.compare(proplabel1, proplabel2) );
	}
	
	/**
	 * The heuristic comparator dataproperties test.
	 */
	@Test
	public void heuristicSortDataTest(){
		//test the buckets with datatype properties
		//bucket A
		propbinding1 = actorLens.getPropertyBinding(FOAFNAMEPROPERTY_URI); //bucket A
		propbinding2 = actorLens.getPropertyBinding(FOAFNAMEPROPERTY_URI); //bucket A
		proplabel1 = new PropertyLabel(propbinding1, lbactor);
		proplabel2 = new PropertyLabel(propbinding2, lbactor);
		//equal propertylabels are in the same bucket.
		assertEquals(PropertyLabelHeuristicComparator.BUCKETA,proplblheuristiccomp.getbucket(proplabel1));
		assertEquals(0,proplblheuristiccomp.compare(proplabel1, proplabel2)); 
		assertEquals(0,proplblheuristiccomp.compare(proplabel2, proplabel1)); 
		//Bucket A en E
		propbinding2 = actorLens.getPropertyBinding(FOAFLASTNAMEPROPERTY_URI); //bucket E
		proplabel2 = new PropertyLabel(propbinding2, lbactor);
		assertEquals(PropertyLabelHeuristicComparator.BUCKETE,proplblheuristiccomp.getbucket(proplabel2));
		assertEquals(-1,proplblheuristiccomp.compare(proplabel1, proplabel2)); // A precedes E 
		assertEquals(1,proplblheuristiccomp.compare(proplabel2, proplabel1)); // E follows A 
		//Bucket E en F
		propbinding1 = actorLens.getPropertyBinding(FOAFHOMEPAGEPROPERTY_URI); //bucket F
		proplabel1 = new PropertyLabel(propbinding1, lbactor);
		assertEquals(PropertyLabelHeuristicComparator.BUCKETF,proplblheuristiccomp.getbucket(proplabel1));
		assertEquals(-1,proplblheuristiccomp.compare(proplabel2, proplabel1)); //E precedes F
		assertEquals(1,proplblheuristiccomp.compare(proplabel1, proplabel2)); //F follows E
		//Bucket B en F
		propbinding2 = actorLens.getPropertyBinding(BIRTHDAYPROPERTY_URI); //bucket B
		proplabel2 = new PropertyLabel(propbinding2, lbactor);
		assertEquals(PropertyLabelHeuristicComparator.BUCKETB,proplblheuristiccomp.getbucket(proplabel2));
		assertEquals(-1,proplblheuristiccomp.compare(proplabel2, proplabel1)); //B precedes F
		assertEquals(1,proplblheuristiccomp.compare(proplabel1, proplabel2)); //F follows B
		//Bucket B en D
		propbinding1 = actorLens.getPropertyBinding(IDPROPERTY_URI); //bucket D
		proplabel1 = new PropertyLabel(propbinding1, lbactor);
		assertEquals(PropertyLabelHeuristicComparator.BUCKETD,proplblheuristiccomp.getbucket(proplabel1));
		assertEquals(1,proplblheuristiccomp.compare(proplabel1, proplabel2));  //D follows B
		assertEquals(-1,proplblheuristiccomp.compare(proplabel2, proplabel1));// B precedes D
		//Bucket D en Z
		propbinding2 = actorLens.getPropertyBinding(BSNPROPERTY_URI); //bucket Z
		proplabel2 = new PropertyLabel(propbinding2, lbactor);
		assertEquals(PropertyLabelHeuristicComparator.BUCKETZ,proplblheuristiccomp.getbucket(proplabel2));
		assertEquals(1,proplblheuristiccomp.compare(proplabel2, proplabel1)); //D precedes Z
		assertEquals(-1,proplblheuristiccomp.compare(proplabel1, proplabel2)); //Z follow D
		
		//Bucket D en C
		propbinding2 = actorLens.getPropertyBinding(PLAYSINPROPERTY_URI); //bucket C
		proplabel2 = new PropertyLabel(propbinding2, lbactor);
		assertEquals(PropertyLabelHeuristicComparator.BUCKETC,proplblheuristiccomp.getbucket(proplabel2));
		assertEquals(-1,proplblheuristiccomp.compare(proplabel2, proplabel1)); //C precedes D
		assertEquals(1,proplblheuristiccomp.compare(proplabel1, proplabel2)); //D follow C
	}

	/**
	 * The heuristic comparator object properties test.
	 */
	@Test
	public void heuristicSortObjectTest(){
		
		//test the buckets with objectproperties.
		//test the inbucket sort
		propbinding1 = actorLens.getPropertyBinding(OWNSMOVIEPROPERTY_URI); //bucket C
		proplabel1 = new PropertyLabel(propbinding1, lbactor);
		//equal propertylabels are in the same bucket.
		assertEquals(PropertyLabelHeuristicComparator.BUCKETC,proplblheuristiccomp.getbucket(proplabel1));
		assertEquals(PropertyLabelHeuristicComparator.BUCKETC,proplblheuristiccomp.getbucket(proplabel2));
		//equal ranks so frequentie orderning
		assertEquals(-1,proplblheuristiccomp.compare(proplabel1, proplabel2)); 
		assertEquals(1,proplblheuristiccomp.compare(proplabel2, proplabel1)); 

		//Bucket E
		propbinding1 = actorLens.getPropertyBinding(FOAFFIRSTNAMEPROPERTY_URI);
		propbinding2 = actorLens.getPropertyBinding(FOAFLASTNAMEPROPERTY_URI);
		proplabel1 = new PropertyLabel(propbinding1, lbactor);
		proplabel2 = new PropertyLabel(propbinding2, lbactor);
		//equal frequentie so alphabeticl ordening
		assertEquals(0,proplblheuristiccomp.compare(proplabel1, proplabel1));
		assertEquals(-1,proplblheuristiccomp.compare(proplabel1, proplabel2));
		
	}


}
