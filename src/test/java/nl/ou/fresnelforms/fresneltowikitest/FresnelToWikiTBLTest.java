package nl.ou.fresnelforms.fresneltowikitest;

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
public class FresnelToWikiTBLTest extends FresnelToWikiTestSuper {
	private static final int EXPECTED_SIZE = 48;
	private static final String FRESNEL_URL = "TBLMarch25.n3";

	/**
	 * Sets up xmlPages and articles.
	 * 
	 * @throws IOException an IO exception
	 */
	@Before
	public void setUp() throws IOException {
		JenaFresnelModel jenaModel = new JenaFresnelModel();
		jenaModel.load(FRESNEL_URL);
		buildPages(jenaModel);
	}

	/**
	 * Tests certain aspects of the output according to expectations.
	 */
	@Test
	public void testFresnelToWiki() {
		assertTrue(getXmlPages() != null);
		assertTrue(getXmlPages().substring(0, getXmlPages().indexOf("\n")).equals(FIRST_LINE));

		// Tests for correct amount of pages.
		assertTrue(getArticles().size() == EXPECTED_SIZE);

		// Tests whether articles contains an article with the given title.
		assertTrue(containsPage("Property:foaf-name"));
		assertTrue(containsPage("Property:owl-Thing-foaf-name"));

		// Tests whether the selected page contains the given string.
		assertTrue(containsString("Property:owl-Thing-owf_style_tim_berners_lee_extract-caption",
				"It is a Subproperty of [[Subproperty of::Property:owf_style_tim_berners_lee_extract-caption]]."));
		// Checks for dummy div for rdf export purposes
		assertTrue(containsString(
				"Template:Informbox owf_style_tim_berners_lee_extract-Person",
				"&lt;div style=&apos;display:none;&apos;&gt;{{#arraymap:{{{owf_style_tim_berners_lee_extract-Person-foaf-depiction|}}}|,|xxx|[[owf_style_tim_berners_lee_extract-Person-foaf-depiction::xxx]]}}&lt;/div&gt;"));
	}
}
