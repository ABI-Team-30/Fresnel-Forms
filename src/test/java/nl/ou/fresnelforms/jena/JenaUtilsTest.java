package nl.ou.fresnelforms.jena;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Unittests for the Jena Utilities.
 * @author a.mekkering@studie.ou.nl
 *
 */
public class JenaUtilsTest extends JenaUtils {
	private Resource res1 = null;
	private Resource res2 = null;
	private Property prop1 = null;
	private Property prop2 = null;
	private Literal answertotheultimatequestion = null;
	private RDFList list = null;

	/**
	 * Sets up the Apache Jena model and resources for testing.
	 * @throws Exception any exceptions that are generated.
	 */
	@Before
	public void setUpBefore() throws Exception {
		final int answer = 42;
		Model model = ModelFactory.createDefaultModel();
		res1 = model.createResource(":res1");
		res2 = model.createResource(":res2");
		prop1 = model.createProperty(":prop1");
		prop2 = model.createProperty(":prop2");
		answertotheultimatequestion = model.createTypedLiteral(answer);
		RDFNode[] members = new RDFNode[] {res1, res2};
		list = model.createList(members);

		model.add(res1, prop1, res2);
		model.add(res1, prop2, res1);
		model.add(res1, prop2, answertotheultimatequestion);
		model.add(res1, prop2, res2);
		model.add(res1, prop2, list);
	}

	/**
	 * Tests whether RDF Properties are correctly replaced. 
	 */
	@Test
	public void testReplaceProperty() {
		Model model = res1.getModel();
		final int nrOfProperties = 4; 

		// Test (normal) property replacement
		assertEquals(res1.getProperty(prop1).getObject(), res2);
		JenaUtilsTest.replaceProperty(res1, prop1, answertotheultimatequestion);
		assertEquals(res1.getProperty(prop1).getObject(), answertotheultimatequestion);
		
		// Test replacement of multiple property values, including a list
		assertEquals(nrOfProperties, res1.listProperties(prop2).toList().size());
		assertTrue(model.containsResource(list));
		JenaUtilsTest.replaceProperty(res1, prop2, answertotheultimatequestion);
		// Test wether all old properties have been removed and replaced by the new one
		assertEquals(1, res1.listProperties(prop2).toList().size());
		assertEquals(res1.getProperty(prop2).getObject(), answertotheultimatequestion);
		// Test wether the list is removed from the model
		assertFalse(model.containsResource(list));
		
		// Test replacement of a non-existing property
		assertFalse(res2.listProperties().hasNext());
		JenaUtils.replaceProperty(res2, prop1, answertotheultimatequestion);
	}

	/**
	 * Tests whether RDF Resources are correctly extracted from an RDFNode or an RDFList. 
	 */
	@Test
	public void testGetRDFResources() {
		// Test with an RDFList as node
		List<Resource> resources1 = JenaUtils.getRDFResources(list);
		assertEquals(2, resources1.size());
		assertTrue(resources1.contains(res1));
		assertTrue(resources1.contains(res2));
		// Test with a single Resource as node
		List<Resource> resources2 = JenaUtils.getRDFResources(res2);
		assertEquals(1, resources2.size());
		assertTrue(resources2.contains(res2));
		// Test with a Literal as node (a Literal is not a Resource)
		List<Resource> resources3 = JenaUtils.getRDFResources(answertotheultimatequestion);
		assertEquals(0, resources3.size());
	}

}
