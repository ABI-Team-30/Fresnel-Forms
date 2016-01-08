package nl.ou.fresnelforms.fresnelowlimporttest;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import nl.ou.fresnelforms.main.OWLImport;
import nl.ou.fresnelforms.ontology.Ontology;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * This Unit test is for testing the import of an ontology.
 * 
 * @author jn.theunissen@studie.ou.nl
 *
 */
public class OWLImportTest {
	private Ontology ontology1;
	private final String srcontologyname1 = "TheFirm.n3"; // name of the source ontology
	private OWLOntologyManager manager;

	private static Logger log = LogManager.getLogger(OWLImportTest.class);

	/**
	 * Create the OWL manager.
	 * 
	 * @throws Exception if the owlmanager can not be created.
	 */
	@Before
	public void setUp() throws Exception {
		manager = OWLManager.createOWLOntologyManager(); // use owlapi's OWLManager
	}

	/**
	 * controle test with correct ontology.
	 */
	@Test
	public void testInitializeOntology1() {
		try {
			URL url = getClass().getClassLoader().getResource(srcontologyname1);
			File file = new File(url.toURI());
			OWLOntology srcontology = manager.loadOntologyFromOntologyDocument(file); // load the source ontology
			ontology1 = OWLImport.initializeOntology(srcontology); // and import it into our own Ontology object
			assertNotNull("Ontology not null test failed.", ontology1);
		} catch (Exception e) {
			log.error(e.toString());
		}
	}

}
