package nl.ou.fresnelforms.fresneltowikitest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresnel.Lens;
import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.fresneltowiki.Article;
import nl.ou.fresnelforms.fresneltowiki.Fresnel2wiki;
import nl.ou.fresnelforms.jena.JenaFresnelModel;
import nl.ou.fresnelforms.main.OWLImport;
import nl.ou.fresnelforms.ontology.Ontology;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Unittests for the abstract FresnelToWikiProperty class.
 * Properties are set in the fresnel model
 * Test has to show that these are exported to the wikiformat.
 * 
 * @author Joop
 *
 */
public class FresnelToWikiPropertyTest {
	private static final String ONTOLOGY_URL = "TheFirm.n3";
	private static final String FRESNEL_URL = "TheFirm.fresnel";
	private String xmlPages = null;
	private Fresnel fresnelmodel;
	private Ontology activeOntologyModel;
	private JenaFresnelModel jenaModel;
	private OWLOntologyManager manager;
	private static final String PERSONLENS_URI = "http://www.workingontologist.org/Examples/Chapter6/TheFirm.owl#Person";
	private static final String ISAVAILABLE_URI = "http://www.workingontologist.org/Examples/Chapter6/TheFirm.owl#isAvailable";
	private static final int ADJUST_1 = 7;
	private static final int ADJUST_2 = 27;
	
	
	/**
	 * Sets up fresnelmode with the properties to set 
	 * and xmlPages as the wikiexport.
	 * 
	 * @throws Exception 
	 */
	@Before
	public void setUp() throws Exception {
		manager = OWLManager.createOWLOntologyManager(); // use owlapi's OWLManager

		//load the active ontology
		URL url = getClass().getClassLoader().getResource(ONTOLOGY_URL);
		File file = new File(url.toURI());
		OWLOntology srcontology = manager.loadOntologyFromOntologyDocument(file); // load the source ontology
		activeOntologyModel = OWLImport.initializeOntology(srcontology); // and import it into our own Ontology object
		
		//init the fresnel model
		jenaModel = new JenaFresnelModel();
		jenaModel.load(FRESNEL_URL);
		fresnelmodel = jenaModel.asFresnel(activeOntologyModel);

		// Get the XML pages in String
		xmlPages = Fresnel2wiki.execute(jenaModel);
	}

	/**
	 * Tests the setup for this test.
	 */
	@Test
	public void testFresnelToWikiPropertyTest() {
		assertTrue(activeOntologyModel != null);
		assertTrue(jenaModel != null);
		assertTrue(fresnelmodel != null);
		assertTrue(xmlPages != null);
	}
		
	/**
	 * Tests the isList property.
	 */
	@Test
	public void testIsListProperty() {
		ArrayList<Article> articles = new ArrayList<Article>();

		// test is for the person lens property is worksFor
		//test the default setting of the isList property (true)
		jenaModel = jenaModel.mergeFresnel(fresnelmodel);
		//export fresnel to wikiformat
		xmlPages = Fresnel2wiki.execute(jenaModel);
		articles = articlesFromXML(xmlPages);
		assertTrue(containsString("Form:TheFirm-Person", "{{{field|TheFirm-Person-TheFirm-isAvailable|list }}}", articles ));

		//test for the isList property = false
		Lens personLens = fresnelmodel.getLens(PERSONLENS_URI);
		PropertyBinding personPropBinding = personLens.getPropertyBinding(ISAVAILABLE_URI);
		personPropBinding.setIsList(false);
		jenaModel = jenaModel.mergeFresnel(fresnelmodel);
		//export fresnel to wikiformat
		xmlPages = Fresnel2wiki.execute(jenaModel);
		articles = articlesFromXML(xmlPages);
		assertTrue(containsString("Form:TheFirm-Person", "{{{field|TheFirm-Person-TheFirm-isAvailable }}}", articles ));
		
		//test for the isList property = true
		personPropBinding.setIsList(true);
		jenaModel = jenaModel.mergeFresnel(fresnelmodel);
		//export fresnel to wikiformat
		xmlPages = Fresnel2wiki.execute(jenaModel);
		articles = articlesFromXML(xmlPages);
		assertTrue(containsString("Form:TheFirm-Person", "{{{field|TheFirm-Person-TheFirm-isAvailable|list }}}", articles ));
	}
	
	/**
	 * Tests the isMandatory property.
	 */
	@Test
	public void testIsMandatoryProperty() {
		ArrayList<Article> articles = new ArrayList<Article>();
		// test is for the person lens property is worksFor
		//test the default setting of the isMandatory property (false)

		//test for the isMandatory  property = true
		//and list=true the default
		Lens personLens = fresnelmodel.getLens(PERSONLENS_URI);
		PropertyBinding personPropBinding = personLens.getPropertyBinding(ISAVAILABLE_URI);
		personPropBinding.setMandatory(true);
		jenaModel = jenaModel.mergeFresnel(fresnelmodel);
		//export fresnel to wikiformat
		xmlPages = Fresnel2wiki.execute(jenaModel);
		articles = articlesFromXML(xmlPages);
		assertTrue(containsString("Form:TheFirm-Person", "{{{field|TheFirm-Person-TheFirm-isAvailable|mandatory|list }}}", articles ));

		//test for the isMandatory property = false
		//and list=true the default
		personPropBinding.setMandatory(false);
		jenaModel = jenaModel.mergeFresnel(fresnelmodel);
		//export fresnel to wikiformat
		xmlPages = Fresnel2wiki.execute(jenaModel);
		articles = articlesFromXML(xmlPages);
		assertTrue(containsString("Form:TheFirm-Person", "{{{field|TheFirm-Person-TheFirm-isAvailable|list }}}", articles ));

		//test for the isMandatory  property = true
		//and list=false 
		personPropBinding.setMandatory(true);
		personPropBinding.setIsList(false);
		jenaModel = jenaModel.mergeFresnel(fresnelmodel);
		//export fresnel to wikiformat
		xmlPages = Fresnel2wiki.execute(jenaModel);
		articles = articlesFromXML(xmlPages);
		assertTrue(containsString("Form:TheFirm-Person", "{{{field|TheFirm-Person-TheFirm-isAvailable|mandatory }}}", articles ));

		//test for the isMandatory property = false
		//and list=false
		personPropBinding.setMandatory(false);
		personPropBinding.setIsList(false);
		jenaModel = jenaModel.mergeFresnel(fresnelmodel);
		//export fresnel to wikiformat
		xmlPages = Fresnel2wiki.execute(jenaModel);
		articles = articlesFromXML(xmlPages);
		assertTrue(containsString("Form:TheFirm-Person", "{{{field|TheFirm-Person-TheFirm-isAvailable }}}", articles ));
		
	}

    private ArrayList<Article> articlesFromXML(String smwxml){	
    	// Convert the String back to articles in a list
    	ArrayList<Article> articles = new ArrayList<Article>();
		int index = smwxml.indexOf("</text>");
		while (index > 0) {
			String sub = smwxml.substring(0, index);
			Article article = new Article(sub.substring(sub.indexOf("<title>") + ADJUST_1, sub.indexOf("</title>")),
					sub.substring(sub.indexOf("<text") + ADJUST_2));
			articles.add(article);
			smwxml = smwxml.substring(index + "</text>".length());
			index = smwxml.indexOf("</text>");
		}
		return articles;
    }
    
	
	
	/**
	 * Tests if the selected page contains the given string.
	 * 
	 * @param pageTitle
	 * @param string
	 * @return true if containing, false otherwise
	 */
	private boolean containsString(String pageTitle, String string, ArrayList<Article> articles) {
		Article page = null;
		boolean b = false;
		
		for (Article a : articles) {
			if (a.getTitle().equals(pageTitle)) {
				page = a;
				break;
			}
		}
		if (page != null && page.getContent().contains(string)) {
			b = true;
		}
		return b;
	}



}
