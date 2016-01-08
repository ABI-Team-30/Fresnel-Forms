package nl.ou.fresnelforms.jena;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.ou.fresnelforms.vocabulary.FRESNEL;
import nl.ou.fresnelforms.vocabulary.OWF;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Unittests for the static JenaFresnelUtils helper methods.
 */
public class JenaFresnelUtilsTest {
	private JenaFresnelModel jenaFresnelModel;
	private final String presOntologyName = "TheFirm.fresnel"; // name of the presentation ontology

	/**
	 * Sets up the presentation ontology for testing.
	 * 
	 * @throws Exception any exceptions that are generated.
	 */
	@Before
	public void setUp() throws Exception {
		jenaFresnelModel = new JenaFresnelModel();
		jenaFresnelModel.load(presOntologyName);
	}

	/**
	 * Tests whether described properties are correctly handled.
	 */
	@Test
	public void testDescribedProperty() {
		Model model = jenaFresnelModel.getModel();
		Resource testLens = model.createResource(jenaFresnelModel.getLocalNamespace() + "PersonLens");
		Resource testPropertyDescribed = model.createResource(model.getNsPrefixURI("TheFirm") + "contractsTo");
		Resource testPropertyClean = model.createResource(model.getNsPrefixURI("TheFirm") + "isEmployedBy");
		List<Resource> displayedProperties = JenaFresnelUtils.getPropertiesToDisplay(testLens);
		assertTrue(displayedProperties.contains(testPropertyClean)); // test for a clean (non-anuonymous) property
		assertTrue(displayedProperties.contains(testPropertyDescribed)); // test for a described (anonymous) property
	}

	/**
	 * Tests whether determination of formatting properties works correctly.
	 */
	@Test
	public void testPropertyFormattingProperties() {
		Model model = jenaFresnelModel.getModel();
		PropertyMap formattingProperties = null;
		Resource testLens = model.createResource(jenaFresnelModel.getLocalNamespace() + "PersonLens");
		Resource testPropertyDescribed = model.createResource(model.getNsPrefixURI("TheFirm") + "contractsTo");
		Resource testPropertyClean = model.createResource(model.getNsPrefixURI("TheFirm") + "isEmployedBy");
		Resource testPropertyLabelNone = model.createResource(model.getNsPrefixURI("TheFirm") + "freeLancesTo");
		Resource testPropertyLabelGroup = model.createResource(model.getNsPrefixURI("TheFirm")
				+ "indirectlyContractsTo");

		// test explicitly overridden label
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertyDescribed, testLens);
		assertEquals(model.createLiteral("contractedTo"), formattingProperties.pick(FRESNEL.LABEL));
		assertEquals(model.createTypedLiteral("color:red;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.LABELSTYLE));
		assertEquals(model.createTypedLiteral("color:white;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.VALUESTYLE));

		// test implicit label
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertyClean, testLens);
		assertEquals(model.createLiteral("isEmployedBy"), formattingProperties.pick(FRESNEL.LABEL));
		assertEquals(model.createTypedLiteral("color:red;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.LABELSTYLE));
		assertEquals(model.createTypedLiteral("color:white;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.VALUESTYLE));

		// test explicit fresnel:label fresnel:none
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertyLabelNone, testLens);
		assertEquals(FRESNEL.NONE, formattingProperties.pick(FRESNEL.LABEL));
		assertEquals(model.createTypedLiteral("color:red;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.LABELSTYLE));
		assertEquals(model.createTypedLiteral("color:white;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.VALUESTYLE));

		// test explicit fresnel:label fresnel:none
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertyLabelGroup, testLens);
		assertEquals(model.createTypedLiteral("color:red; color:blue;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.LABELSTYLE));
		assertEquals(model.createTypedLiteral("color:white;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.VALUESTYLE));
	}

	/**
	 * Tests whether determination of formatting properties works correctly.
	 */
	@Test
	public void testPropertyAutoCompletion() {
		Model model = jenaFresnelModel.getModel();
		PropertyMap formattingProperties = null;
		List<RDFNode> autoCompleteFromClass = null;
		Resource testLens = model.createResource(jenaFresnelModel.getLocalNamespace() + "PersonLens");
		Resource testPropertySingle = model.createResource(model.getNsPrefixURI("TheFirm") + "isEmployedBy");
		Resource testPropertyHappy = model.createResource(model.getNsPrefixURI("TheFirm") + "contractsTo");
		Resource testPropertyMulti = model.createResource(model.getNsPrefixURI("TheFirm") + "freeLancesTo");
		Resource testPropertyClean = model.createResource(model.getNsPrefixURI("TheFirm") + "worksFor");

		// test autocompletion with single range
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertySingle, testLens);
		autoCompleteFromClass = formattingProperties.get(OWF.AUTOCOMPLETEFROMCLASS);
		assertEquals(1, autoCompleteFromClass.size());
		assertEquals(model.createResource(model.getNsPrefixURI("TheFirm") + "Company"), autoCompleteFromClass.get(0));

		// test autocompletion with single range in a List
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertyHappy, testLens);
		autoCompleteFromClass = formattingProperties.get(OWF.AUTOCOMPLETEFROMCLASS);
		assertEquals(1, autoCompleteFromClass.size());
		assertEquals(model.createResource(model.getNsPrefixURI("TheFirm") + "Company"), autoCompleteFromClass.get(0));

		// test no autocompletion
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertyClean, testLens);
		autoCompleteFromClass = formattingProperties.get(OWF.AUTOCOMPLETEFROMCLASS);
		assertNull(autoCompleteFromClass);

		// test autocompletion with multiple ranges
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertyMulti, testLens);
		autoCompleteFromClass = formattingProperties.get(OWF.AUTOCOMPLETEFROMCLASS);
		assertEquals(2, autoCompleteFromClass.size());
		assertTrue(autoCompleteFromClass.contains(model.createResource(model.getNsPrefixURI("TheFirm") + "Company")));
		assertTrue(autoCompleteFromClass.contains(model.createResource(model.getNsPrefixURI("TheFirm") + "Person")));
	}

	/**
	 * Tests whether determination of datatypes works correctly.
	 */
	@Test
	public void testPropertyDataTypes() {
		Model model = jenaFresnelModel.getModel();
		PropertyMap formattingProperties = null;
		Resource testLens = model.createResource(jenaFresnelModel.getLocalNamespace() + "PersonLens");
		Resource testPropertyString = model.createResource(model.getNsPrefixURI("TheFirm") + "name");
		Resource testPropertyInt = model.createResource(model.getNsPrefixURI("TheFirm") + "age");
		Resource testObjectProperty = model.createResource(model.getNsPrefixURI("TheFirm") + "contractsTo");

		// test string datatype
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertyString, testLens);
		assertTrue(formattingProperties.containsKey(OWF.DATATYPE));
		assertTrue(formattingProperties.get(OWF.DATATYPE).contains(XSD.xstring));

		// test int datatype
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testPropertyInt, testLens);
		assertTrue(formattingProperties.containsKey(OWF.DATATYPE));
		assertTrue(formattingProperties.get(OWF.DATATYPE).contains(XSD.xint));

		// test object property (doesn't have a datatype)
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testObjectProperty, testLens);
		assertFalse(formattingProperties.containsKey(OWF.DATATYPE));
	}

	/**
	 * Tests whether determination of formatting properties works correctly.
	 */
	@Test
	public void testLensFormattingProperties() {
		Model model = jenaFresnelModel.getModel();
		PropertyMap formattingProperties = null;
		Resource testLens = model.createResource(jenaFresnelModel.getLocalNamespace() + "PersonLens");

		// For Lenses, the order for CSS concatenation is arbitrary.
		// (For Lenses, groups don't have priorities: each Lens's Group applies equally, so the choice is arbitrary)
		Literal pick1 = model.createTypedLiteral("color:purple; color:yellow;", FRESNEL.STYLINGINSTRUCTIONS.getURI());
		Literal pick2 = model.createTypedLiteral("color:yellow; color:purple;", FRESNEL.STYLINGINSTRUCTIONS.getURI());

		// test explicitly overridden label
		formattingProperties = JenaFresnelUtils.getFormattingProperties(testLens);

		RDFNode resourceStyle = formattingProperties.pick(FRESNEL.RESOURCESTYLE);
		// provided in :TheFirmGroup
		assertTrue(pick1.equals(resourceStyle) || pick2.equals(resourceStyle));

		// provided in :PersonLensGroup
		assertEquals(model.createTypedLiteral("color:red;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.LABELSTYLE));
		assertEquals(model.createTypedLiteral("color:white;", FRESNEL.STYLINGINSTRUCTIONS.getURI()),
				formattingProperties.pick(FRESNEL.VALUESTYLE));

		// labels aren't valid for Lenses, so shouldn't be provided
		assertNull(formattingProperties.get(FRESNEL.LABEL));

		// not provided at all
		assertNull(formattingProperties.get(FRESNEL.PROPERTYSTYLE));
	}

	/**
	 * Tests Retrieval of a property description.
	 */
	@Test
	public void testGetPropertyDescription() {
		Model model = jenaFresnelModel.getModel();
		Resource testLens = model.createResource(jenaFresnelModel.getLocalNamespace() + "PersonLens");
		Resource testLens2 = model.createResource(jenaFresnelModel.getLocalNamespace() + "CompanyLens");
		Resource testPropertyDescribed = model.createResource(model.getNsPrefixURI("TheFirm") + "contractsTo");
		Resource testPropertyClean = model.createResource(model.getNsPrefixURI("TheFirm") + "isEmployedBy");
		Resource pDescription = null;

		// test a description of a property for a lens
		pDescription = JenaFresnelUtils.getPropertyDescription(testPropertyDescribed, testLens);
		assertTrue(pDescription.hasProperty(RDF.type, FRESNEL.PROPERTYDESCRIPTION));
		assertTrue(pDescription.hasProperty(FRESNEL.PROPERTY, testPropertyDescribed));

		// test the description itself for the same lens
		Resource descriptionOfDescription = JenaFresnelUtils.getPropertyDescription(pDescription, testLens);
		assertEquals(pDescription, descriptionOfDescription);

		// test the description itself without lens
		descriptionOfDescription = JenaFresnelUtils.getPropertyDescription(pDescription, null);
		assertEquals(pDescription, descriptionOfDescription);

		// test the description itself for a lens it's not part of
		descriptionOfDescription = JenaFresnelUtils.getPropertyDescription(pDescription, testLens2);
		assertNull(descriptionOfDescription);

		// test a property for a lens without a description
		pDescription = JenaFresnelUtils.getPropertyDescription(testPropertyClean, testLens);
		assertFalse(pDescription.hasProperty(RDF.type, FRESNEL.PROPERTYDESCRIPTION));
		assertFalse(pDescription.hasProperty(FRESNEL.PROPERTY, testPropertyClean));
		assertEquals(testPropertyClean, pDescription);

		// test a property for no lens
		pDescription = JenaFresnelUtils.getPropertyDescription(testPropertyClean, null);
		assertEquals(testPropertyClean, pDescription);

		// test a property for a lens it's not part of
		pDescription = JenaFresnelUtils.getPropertyDescription(testPropertyClean, testLens2);
		assertNull(pDescription);
	}

	/**
	 * Tests determination of whether a resource is a description.
	 */
	@Test
	public void testIsPropertyDescription() {
		Model model = jenaFresnelModel.getModel();
		Resource testLens = model.createResource(jenaFresnelModel.getLocalNamespace() + "PersonLens");
		Resource testPropertyDescribed = model.createResource(model.getNsPrefixURI("TheFirm") + "contractsTo");
		Resource testPropertyClean = model.createResource(model.getNsPrefixURI("TheFirm") + "isEmployedBy");
		List<Resource> displayedProperties = JenaFresnelUtils.getPropertiesToDisplay(testLens);
		assertTrue(displayedProperties.contains(testPropertyClean)); // test for a clean (non-anuonymous) property
		assertTrue(displayedProperties.contains(testPropertyDescribed)); // test for a described (anonymous) property
	}

	/**
	 * Tests whether determination of properties to display works correctly.
	 */
	@Test
	public void testPropertiesToDisplay() {
		Model model = jenaFresnelModel.getModel();
		Resource testLens = model.createResource(jenaFresnelModel.getLocalNamespace() + "PersonLens");
		Resource testPropertyDescribed = model.createResource(model.getNsPrefixURI("TheFirm") + "contractsTo");
		Resource testPropertyClean = model.createResource(model.getNsPrefixURI("TheFirm") + "isEmployedBy");
		Resource testPropertyHidden = model.createResource(model.getNsPrefixURI("TheFirm") + "worksFor");
		List<Resource> displayedProperties = JenaFresnelUtils.getPropertiesToDisplay(testLens);
		assertTrue(displayedProperties.contains(testPropertyClean));
		assertTrue(displayedProperties.contains(testPropertyDescribed));
		assertFalse(displayedProperties.contains(testPropertyHidden));
	}

}
