package nl.ou.fresnelforms.fresneltowikitest;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import nl.ou.fresnelforms.fresneltowiki.Article;
import nl.ou.fresnelforms.fresneltowiki.Fresnel2wiki;
import nl.ou.fresnelforms.jena.JenaFresnelModel;

/**
 * SuperClass for FresnelToWikiTest and FresnelToWikiTBLTest.
 * 
 * @author Joop
 *
 */
public class FresnelToWikiTestSuper {
	/**
	 * To get article's title.
	 */
	protected static final int ADJUST_1 = 7;
	/**
	 * To get article's content.
	 */
	protected static final int ADJUST_2 = 27;
	/**
	 * XML first line.
	 */
	protected static final String FIRST_LINE = "<?xml version='1.0' encoding='UTF-8'?>";
	/**
	 * Used for debugging.
	 */
	private static Logger log = LogManager.getLogger(FresnelToWikiTest.class);
	/**
	 * List holding the article's.
	 */
	private ArrayList<Article> articles = new ArrayList<Article>();
	/**
	 * String holding the articles in XML format.
	 */
	private String xmlPages = null;

	/**
	 * Constructor.
	 */
	public FresnelToWikiTestSuper() {
		super();
	}

	/**
	 * Tests whether the selected page contains the given string.
	 * 
	 * @param pageTitle title
	 * @param string string to contain
	 * @return true if containing, false otherwise
	 */
	protected boolean containsString(String pageTitle, String string) {
		Article page = null;
		boolean b = false;
		for (Article a : getArticles()) {
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

	/**
	 * Tests whether articles contains a page with the given title.
	 * 
	 * @param pageTitle title
	 * @return true if containing
	 */
	protected boolean containsPage(String pageTitle) {
		boolean b = false;
		for (Article a : getArticles()) {
			if (a.getTitle().equals(pageTitle)) {
				b = true;
				break;
			}
		}
		return b;
	}

	/**
	 * Revert fresnel2wiki XML output to articles.
	 * 
	 * @param jenaModel the model
	 */
	protected void buildPages(JenaFresnelModel jenaModel) {
		// Get the XML pages in String
		setXmlPages(Fresnel2wiki.execute(jenaModel));
		// Convert the String back to articles in a list
		String buildPages = getXmlPages();
		int index = buildPages.indexOf("</text>");
		while (index > 0) {
			String sub = buildPages.substring(0, index);
			Article article = new Article(sub.substring(sub.indexOf("<title>") + ADJUST_1, sub.indexOf("</title>")),
					sub.substring(sub.indexOf("<text") + ADJUST_2));
			getArticles().add(article);
			log.debug(article.getTitle() + "--" + article.getContent());
			buildPages = buildPages.substring(index + "</text>".length());
			index = buildPages.indexOf("</text>");
		}
	}

	/**
	 * Getter.
	 * 
	 * @return XmlPages
	 */
	public String getXmlPages() {
		return xmlPages;
	}

	/**
	 * Setter.
	 * 
	 * @param xmlPages the pages
	 */
	public void setXmlPages(String xmlPages) {
		this.xmlPages = xmlPages;
	}

	/**
	 * Getter.
	 * 
	 * @return articles
	 */
	public ArrayList<Article> getArticles() {
		return articles;
	}

}