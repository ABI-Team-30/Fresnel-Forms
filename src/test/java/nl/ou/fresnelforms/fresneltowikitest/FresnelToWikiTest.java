package nl.ou.fresnelforms.fresneltowikitest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import nl.ou.fresnelforms.jena.JenaFresnelModel;

import org.junit.Before;
import org.junit.Test;

/**
 * Unittests for the abstract FresnelToWiki class.
 * 
 * @author Joop
 *
 */
public class FresnelToWikiTest extends FresnelToWikiTestSuper {
	private static final String PERSON_FORM_PAGE = "Form:TheFirm-Person";
	private static final String COMPANY_FORM_STRING = "This is the &apos;TheFirm-Company&apos; form.";
	private static final String PERSON_BOX_PAGE = "Template:Informbox TheFirm-Person";
	private static final String COMPANY_BOX_PAGE = "Template:Informbox TheFirm-Company";
	private static final String PAGE_TITLE_1 = "Category:TheFirm-Person";
	private static final String PAGE_TITLE_2 = "Property:TheFirm-Person-TheFirm-isEmployedBy";
	private static final String PAGE_TITLE_3 = "Form:TheFirm-Company";
	private static final int EXPECTED_SIZE = 28;
	private static final String FRESNEL = "TheFirm.fresnel";
	/**
	 * Sets up xmlPages and articles.
	 * 
	 * @throws IOException an IO exception
	 */
	@Before
	public void setUp() throws IOException {
		JenaFresnelModel jenaModel = new JenaFresnelModel();
		jenaModel.load(FRESNEL);
		buildPages(jenaModel);
	}

	/**
	 * Tests certain aspects of the output according to expectations.
	 */
	@Test
	public void testFresnelToWiki() {
		assertTrue(getXmlPages() != null);
		assertTrue(getXmlPages().substring(0, getXmlPages().indexOf("\n")).equals(FIRST_LINE));
		assertTrue(getArticles().size() == EXPECTED_SIZE); // Tests correct amount of pages.
		assertTrue(containsPage(PAGE_TITLE_1)); // Tests whether articles contains an article with the given title.
		assertTrue(containsPage(PAGE_TITLE_2));
		// Tests whether the selected page contains the given string.
		assertTrue(containsString(PAGE_TITLE_3, COMPANY_FORM_STRING));
		// Property worksFor is a hidden property
		assertFalse(containsPage("Property:TheFirm-worksFor"));
		// Tests for relabeled properties
		assertTrue(containsString(PERSON_BOX_PAGE, "contractedTo"));
		// Tests for auto completion
		assertTrue(containsString(PERSON_FORM_PAGE,
				"{{{field|TheFirm-Person-TheFirm-isEmployedBy|autocomplete on category=TheFirm-Company")
				&& (containsString(PERSON_FORM_PAGE,
						"{{{field|TheFirm-Person-TheFirm-freeLancesTo|autocomplete on category=TheFirm-Company") || containsString(
						PERSON_FORM_PAGE,
						"{{{field|TheFirm-Person-TheFirm-freeLancesTo|autocomplete on category=TheFirm-Person"))
				&& !containsString(PERSON_FORM_PAGE, "{{{autocomplete on category=null"));
		// Tests for resourceStyle Person Box (a more specific style has been added in the test fresnel file)
		assertTrue(containsString(PERSON_BOX_PAGE, "color:yellow; color:purple;")
				|| containsString(PERSON_BOX_PAGE, "color:purple; color:yellow;"));
		// Tests for resourceStyle Company Box (only the general style has been added to default styling)
		assertTrue(containsString(COMPANY_BOX_PAGE, "color:yellow;"));
		// Tests for cascading labelStyle indirectlyContractsTo property
		assertTrue(containsString(PERSON_BOX_PAGE, "color:red; color:blue;&apos;&gt;indirectlyContractsTo"));
		// Tests for image in template row
		assertTrue(containsString(PERSON_BOX_PAGE,
				"{{#arraymap:{{{TheFirm-Person-TheFirm-contractsTo|}}}|,|xxx|&lt;img src=&apos;xxx&apos;/&gt;|&amp;}}"));
		// Tests for delimiter in template row
		assertTrue(containsString(PERSON_BOX_PAGE, "TheFirm-indirectlyContractsTo::xxx]]|&lt;br/&gt;}}"));
		// Tests escaping of labels
		assertTrue(containsString(PERSON_BOX_PAGE,
				"&lt;div class=&apos;ib_label&apos; style=&apos;color:red;&apos;&gt;&amp;lt;name&amp;gt;&lt;/div&gt;"));
		// Tests for datatypes
		assertTrue(containsString("Property:TheFirm-Person-TheFirm-name", "Has type::String")
				&& containsString("Property:TheFirm-Person-TheFirm-indirectlyContractsTo", "Has type::URL")
				&& containsString("Property:TheFirm-Person-TheFirm-contractsTo", "Has type::URL")
				&& containsString("Property:TheFirm-Person-TheFirm-freeLancesTo", "Has type::Page"));
		assertTrue(containsString("Property:TheFirm-Person-TheFirm-birthYear", "Has type::Number"));
		assertTrue(containsString("Property:TheFirm-Person-TheFirm-timeOfBirth", "Has type::Date"));
		// Tests frmsParams(), frmRow() and Boolean datatype
		assertTrue(containsString(PERSON_FORM_PAGE, "TheFirm-isAvailable|input type=radiobutton"));
		// Tests Semantic MediaWiki's Special:ExportRDF preparations: smw_import_ and importedFrom::
		assertTrue(containsString("MediaWiki:Smw_import_TheFirm",
				"http://www.workingontologist.org/Examples/Chapter6/TheFirm.owl#"));
		assertTrue(containsString("MediaWiki:Smw_import_TheFirm", "isEmployedBy|Type:Page"));
		assertTrue(containsString("Property:TheFirm-Person-TheFirm-isEmployedBy", "[[imported from::TheFirm:isEmployedBy]]"));
	}
}
