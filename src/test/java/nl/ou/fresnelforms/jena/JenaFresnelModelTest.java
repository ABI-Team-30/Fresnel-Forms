/**
 * Testing package for the FresnelManager
 */
package nl.ou.fresnelforms.jena;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URL;

import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresnel.Lens;
import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.main.OWLImport;
import nl.ou.fresnelforms.ontology.Ontology;
import nl.ou.fresnelforms.vocabulary.FRESNEL;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Unittests for the JenaFresnelModel.
 */
public class JenaFresnelModelTest {
	private Ontology ontology1;
	private JenaFresnelModel jenaFresnelModel;
	private Fresnel fresnel;
	private Lens thingLens;
	private Lens personLens;
	private Lens companyLens;
	private final String srcOntologyName = "TheFirm.n3"; // name of the source ontology
	private final String presOntologyName = "TheFirm.fresnel"; // name of the presentation ontology
	private final String theFirmUri = "http://www.workingontologist.org/Examples/Chapter6/TheFirm.owl#";

	/**
	 * Sets up the source and presentation ontologies for testing.
	 * 
	 * @throws Exception any exceptions that are generated.
	 */
	@Before
	public void setUp() throws Exception {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager(); // use owlapi's OWLManager

		// use the java classloader for getting a resource from the classpath (scr/test/resources)
		URL url = getClass().getClassLoader().getResource(srcOntologyName);
		File file = new File(url.toURI());
		OWLOntology srcOntology = manager.loadOntologyFromOntologyDocument(file); // load the source ontology
		ontology1 = OWLImport.initializeOntology(srcOntology); // and import it into our own Ontology object

		jenaFresnelModel = new JenaFresnelModel();
		jenaFresnelModel.load(presOntologyName);
		fresnel = jenaFresnelModel.asFresnel(ontology1); // load the presentation ontology
		thingLens = fresnel.getLens(OWL.Thing.getURI());
		personLens = fresnel.getLens(theFirmUri + "Person");
		companyLens = fresnel.getLens(theFirmUri + "Company");
	}

	/**
	 * Tests whether the RDF model is correctly converted to the Fresnel model.
	 */
	@Test
	public void testToFresnel() {
		assertNotNull(thingLens);
		assertNotNull(personLens);
		assertNotNull(companyLens);
		nl.ou.fresnelforms.ontology.Class personClass = personLens.getClassLensDomain();
		PropertyBinding pb1 = personLens.getPropertyBinding(theFirmUri + "indirectlyContractsTo");
		assertNotNull(pb1);
		assertTrue(pb1.isShown());
		PropertyBinding pb2 = personLens.getPropertyBinding(theFirmUri + "worksFor");
		assertNotNull(pb2);
		assertFalse(pb2.isShown());
		assertTrue(pb2.isLabelEnabled());
		assertEquals(pb2.getLabel(), pb2.getDefaultLabel());
		assertTrue("Person".equals(URI.create(personClass.getURI()).getFragment()));
		assertTrue("Person".equals(personLens.getName()));
		// Test handling of the label of formatted properties
		PropertyBinding pb3 = personLens.getPropertyBinding(theFirmUri + "contractsTo");
		assertNotNull(pb3);
		assertTrue(pb3.isLabelEnabled());
		assertEquals("contractedTo", pb3.getLabel());
		// Test handling of disabled labels
		PropertyBinding pb4 = personLens.getPropertyBinding(theFirmUri + "freeLancesTo");
		assertNotNull(pb4);
		assertFalse(pb4.isLabelEnabled());
	}

	/**
	 * Tests whether all shown lenses are present in the RDF model.
	 */
	@Test
	public void testLensesPresentFromFresnel() {
		Model model = jenaFresnelModel.mergeFresnel(fresnel).getModel();
		String localNamespace = model.getNsPrefixURI("");
		assertTrue(model.contains(model.createResource(localNamespace + "owl-ThingLens"), RDF.type, FRESNEL.LENS));
		assertTrue(model.contains(model.createResource(localNamespace + "PersonLens"), RDF.type, FRESNEL.LENS));
		assertTrue(model.contains(model.createResource(localNamespace + "CompanyLens"), RDF.type, FRESNEL.LENS));
	}

	/**
	 * Tests whether a Lens is removed from the RDF model when it is hidden by the user.
	 */
	@Test
	public void testRemovingLensFromFresnel() {
		companyLens.setDisplayed(false);
		Model model = jenaFresnelModel.mergeFresnel(fresnel).getModel();
		String localNamespace = model.getNsPrefixURI("");
		assertTrue(model.contains(model.createResource(localNamespace + "owl-ThingLens"), RDF.type, FRESNEL.LENS));
		assertTrue(model.contains(model.createResource(localNamespace + "PersonLens"), RDF.type, FRESNEL.LENS));
		assertFalse(model.contains(model.createResource(localNamespace + "CompanyLens"), null, (RDFNode) null));
	}

	/**
	 * Tests whether newly defined properties are correctly converted to the Lens's right PropertiesList.
	 */
	@Test
	public void testNewPropertyFromFresnel() {
		final String showName = "ShowName";
		final String hideName = "HideName";
		Model model = jenaFresnelModel.mergeFresnel(fresnel).getModel();

		String prefixUri = model.getNsPrefixURI("TheFirm");
		nl.ou.fresnelforms.fresnel.Property showProperty = new nl.ou.fresnelforms.fresnel.Property(fresnel, prefixUri + showName);
		nl.ou.fresnelforms.fresnel.Property hideProperty = new nl.ou.fresnelforms.fresnel.Property(fresnel, prefixUri + hideName);
		PropertyBinding showPropertyBinding = new PropertyBinding(showProperty, thingLens, 0);
		PropertyBinding hidePropertyBinding = new PropertyBinding(hideProperty, thingLens, 0);
		hidePropertyBinding.setShown(false);
		thingLens.addPropertyBinding(showPropertyBinding);
		thingLens.addPropertyBinding(hidePropertyBinding);
		String localNamespace = model.getNsPrefixURI("");
		model = jenaFresnelModel.mergeFresnel(fresnel).getModel(); // re-determine the model from fresnel, including new
																	// items
		Resource sproperty = model.createResource(model.getNsPrefixURI("TheFirm") + showName);
		Resource hproperty = model.createResource(model.getNsPrefixURI("TheFirm") + hideName);
		Resource testLens = model.createResource(localNamespace + thingLens.getFresnelName());
		Statement showPropertiesStatement = model.getProperty(testLens, FRESNEL.SHOWPROPERTIES);
		RDFNode showPropertiesNode = showPropertiesStatement.getObject();
		assertTrue(showPropertiesNode.canAs(RDFList.class));
		RDFList showPropertiesList = showPropertiesNode.as(RDFList.class);
		assertTrue(showPropertiesList.contains(sproperty));
		Statement hidePropertiesStatement = model.getProperty(testLens, FRESNEL.HIDEPROPERTIES);
		RDFNode hidePropertiesNode = hidePropertiesStatement.getObject();
		assertTrue(hidePropertiesNode.canAs(RDFList.class));
		RDFList hidePropertiesList = hidePropertiesNode.as(RDFList.class);
		assertTrue(hidePropertiesList.contains(hproperty));
	}

	/**
	 * Tests whether unsupported triples from the source file are still present in the converted model.
	 */
	@Test
	public void testUnsupportedPropertyFromFresnel() {
		Model model = jenaFresnelModel.mergeFresnel(fresnel).getModel();
		String localNamespace = model.getNsPrefixURI("");
		Resource testLens = model.createResource(localNamespace + "PersonLens");
		Property property = model.createProperty(model.getNsPrefixURI("unsupp") + "justAProperty");
		Statement foundStatement = model.getProperty(testLens, property);
		assertTrue("JustForTesting".equals(foundStatement.getLiteral().getString()));
	}

	/**
	 * Tests whether formatted properties are correctly converted to RDF triples.
	 */
	@Test
	public void testFormattedPropertyFromFresnel() {
		Model model = jenaFresnelModel.mergeFresnel(fresnel).getModel();
		String localNamespace = model.getNsPrefixURI("");
		Resource testLens = model.createResource(localNamespace + "PersonLens");
		Resource testProperty = model.createResource(model.getNsPrefixURI("TheFirm") + "contractsTo");
		Resource testProperty2 = model.createResource(model.getNsPrefixURI("TheFirm") + "freeLancesTo");
		Resource testFormat = model.createResource(localNamespace + "contractsToPersonLensFormat");
		Resource testFormat2 = model.createResource(localNamespace + "freeLancesToPersonLensFormat");
		// Test for a formatted property using SPARQL
		// First, define some resources to use as SPARQL variable bindings
		QuerySolutionMap initialBindings = new QuerySolutionMap();
		initialBindings.add("lens", testLens);
		initialBindings.add("_showprops", FRESNEL.SHOWPROPERTIES);
		initialBindings.add("_rdftype", RDF.type);
		initialBindings.add("_propdescr", FRESNEL.PROPERTYDESCRIPTION);
		initialBindings.add("_prop", FRESNEL.PROPERTY);
		initialBindings.add("_use", FRESNEL.USE);
		// Create an empty query, using a list: prefix (for list:member)
		Query query = new Query();
		query.setPrefix("list", "http://jena.hpl.hp.com/ARQ/list#");
		// Now, the query string itself can be defined quite easily...
		String queryString = "SELECT ?prop ?format WHERE { " + "?lens ?_showprops ?list . "
				+ "?list list:member ?member . " + "?member ?_rdftype ?_propdescr ; " + "?_prop ?prop ; "
				+ "?_use ?format . " + "}";
		// ... and just parse it
		query = QueryFactory.parse(query, queryString, "", Syntax.defaultQuerySyntax);
		// So, what are the results?
		QueryExecution qe = QueryExecutionFactory.create(query, model, initialBindings);
		ResultSet results = qe.execSelect();
		// Now test the results
		int nrOfAssertedMatches = 0;
		for (QuerySolution solution : ResultSetFormatter.toList(results)) {
			if (testProperty.equals(solution.get("prop"))) {
				assertEquals(testFormat, solution.get("format"));
				nrOfAssertedMatches++;
			} else if (testProperty2.equals(solution.get("prop"))) {
				assertEquals(testFormat2, solution.get("format"));
				nrOfAssertedMatches++;
			}
		}
		// And assert that we've seen our expected results
		assertEquals(2, nrOfAssertedMatches);
		// And check the label
		Statement foundStatement = model.getProperty(testFormat, FRESNEL.LABEL);
		assertTrue("contractedTo".equals(foundStatement.getLiteral().getString()));
		// Check a disabled label
		Statement foundStatement2 = model.getProperty(testFormat2, FRESNEL.LABEL);
		assertEquals(FRESNEL.NONE, foundStatement2.getObject().asResource());
	}
}