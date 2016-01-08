package nl.ou.fresnelforms.fresneltowiki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.ou.fresnelforms.jena.JenaFresnelModel;
import nl.ou.fresnelforms.jena.JenaFresnelUtils;
import nl.ou.fresnelforms.jena.JenaUtils;
import nl.ou.fresnelforms.jena.PropertyMap;
import nl.ou.fresnelforms.utils.UriUtils;
import nl.ou.fresnelforms.vocabulary.FRESNEL;
import nl.ou.fresnelforms.vocabulary.OWF;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Method execute() converts a (formatted by the gui) ontology in Fresnel to an XML String for Semantic Forms wiki
 * import.
 * 
 * @author Joop
 */
public abstract class Fresnel2wiki {

	private static final String XML_HEADER = "<?xml version='1.0' encoding='UTF-8'?>\n"
			+ "<mediawiki xmlns='http://www.mediawiki.org/xml/export-0.5/'"
			+ " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
			+ " xsi:schemaLocation='http://www.mediawiki.org/xml/export-0.5/ http://www.mediawiki.org/xml/export-0.5.xsd'"
			+ " version='0.5' xml:lang='en'>\n";
	private static final String XML_FOOTER = "</mediawiki>";
	private static HashMap<Resource, SMWType> rangeMap = loadRangeMap();

	private static Logger log = LogManager.getLogger(Fresnel2wiki.class);

	/**
	 * Main function that executes the fresnelToWiki command.
	 * 
	 * @param jenaFresnelModel the jenaModel holding the ontology in Fresnel for makeLensList()
	 * @return a string with the wiki pages in XML
	 */
	public static String execute(JenaFresnelModel jenaFresnelModel) {
		Model jenaModel = jenaFresnelModel.getModel();
		String eol = "\n";
		// used for RDFExport
		Map<String, Map<Resource, SMWType>> smwImportMap = new HashMap<String, Map<Resource, SMWType>>();

		StringBuilder wikiXML = new StringBuilder(XML_HEADER);
		// For each lens ...
		int nroflenses = jenaModel.listStatements(null, RDF.type, FRESNEL.LENS).toList().size();
		int currentLens = 1;
		for (ResIterator lensIterator = jenaModel.listResourcesWithProperty(RDF.type, FRESNEL.LENS); lensIterator
				.hasNext();) {
			Resource lens = lensIterator.next();
			log.trace("determining Articles for lens " + currentLens + " of " + nroflenses);
			// Get the to be displayed properties associated with the lens
			List<Resource> properties = JenaFresnelUtils.getPropertiesToDisplay(lens);
			// Create the box wiki pages
			for (Article a : writeBox(properties, lens, smwImportMap)) {
				wikiXML.append(a.getXMLPage() + eol); // append each Article for this lens to the XML String
			}
			currentLens++;
		}
		for (Article a : makeSmwImportPages(smwImportMap, jenaModel)) {
			wikiXML.append(a.getXMLPage() + eol); // append each Article for the RDF Export to the XML String
		}
		wikiXML.append(XML_FOOTER);
		return wikiXML.toString();
	}

	/**
	 * Returns a mapping from XSD/RDFS/FRESNEL datatypes to Semantic Wiki datatypes.
	 * 
	 * @return the hashmap
	 */
	private static HashMap<Resource, SMWType> loadRangeMap() {
		HashMap<Resource, SMWType> hm = new HashMap<Resource, SMWType>();

		Resource[] dates = new Resource[] { XSD.date, XSD.dateTime, XSD.time };

		Resource[] numbers = new Resource[] { XSD.decimal, XSD.integer, XSD.nonPositiveInteger, XSD.negativeInteger,
				XSD.xlong, XSD.xint, XSD.xshort, XSD.nonNegativeInteger, XSD.unsignedLong, XSD.unsignedInt,
				XSD.unsignedShort, XSD.unsignedByte, XSD.positiveInteger, XSD.xfloat, XSD.xdouble, XSD.gYear };

		Resource[] strings = new Resource[] { RDFS.Literal, XSD.xstring, XSD.normalizedString, XSD.language, XSD.Name,
				XSD.token, XSD.NMTOKEN, XSD.NCName, XSD.base64Binary, XSD.xbyte, XSD.hexBinary, XSD.gYearMonth,
				XSD.gMonthDay, XSD.gDay, XSD.gMonth };

		Resource[] urls = new Resource[] { FRESNEL.IMAGE, XSD.anyURI };

		for (Resource resource : strings) {
			hm.put(resource, SMWType.STRING);
		}
		for (Resource resource : numbers) {
			hm.put(resource, SMWType.NUMBER);
		}
		for (Resource resource : dates) {
			hm.put(resource, SMWType.DATE);
		}
		for (Resource resource : urls) {
			hm.put(resource, SMWType.URL);
		}
		hm.put(XSD.xboolean, SMWType.BOOLEAN);
		return hm;
	}

	/**
	 * For each lens prepares the category, template, form and property pages.
	 * 
	 * @param properties
	 * @param lens
	 */
	private static List<Article> writeBox(List<Resource> properties, Resource lens,
			Map<String, Map<Resource, SMWType>> smwImportMap) {
		List<Article> articles = new ArrayList<Article>();
		// initialize strings that get built up per property
		StringBuilder frmRows = new StringBuilder();
		StringBuilder tplRows = new StringBuilder();
		String boxName = lens.getLocalName();
		Resource lensClassResource = lens.getPropertyResourceValue(FRESNEL.CLASSLENSDOMAIN);
		boxName = UriUtils.encodedURI(lensClassResource); // Use the classLensDomain as name for this box
		for (Resource prop : properties) {
			// first, prepare the map for associating the SMWType with the Property
			PropertyMap formattingProperties = JenaFresnelUtils.getFormattingProperties(prop, lens);
			SMWType smwType = getRange(formattingProperties); // get the unmodified range ("page" or a data type)
			String namespace = prop.getNameSpace();
			Map<Resource, SMWType> propertyTypes = smwImportMap.get(namespace);
			// We can also determine whether the main (not sub-) propertyPage was created before
			if (propertyTypes == null) {
				propertyTypes = new HashMap<Resource, SMWType>();
				smwImportMap.put(namespace, propertyTypes); // define the namespace mapping if needed
			}
			if (propertyTypes.get(prop) == null) {
				propertyTypes.put(prop, smwType);
				// add the main (not sub-) propertyPage once
				articles.add(makePropertyPage(prop, smwType, null, boxName, null, false));
			}
			// Now get the user modified (or not) range to adjust type
			String objectPropRngName = null; // stays null if prop is data type prop
			boolean isImage = FRESNEL.IMAGE.equals(formattingProperties.pick(FRESNEL.VALUE));
			boolean isExtURL = FRESNEL.EXTERNALLINK.equals(formattingProperties.pick(FRESNEL.VALUE));
			if (isImage || isExtURL) {
				smwType = SMWType.URL;
			} else {
				// A prop has (an autocomplete value or nothing (ObjectProp=type Page)) XOR (is a data type prop)
				Resource objectPropRange = (Resource) formattingProperties.pick(OWF.AUTOCOMPLETEFROMCLASS);
				if (objectPropRange != null) {
					objectPropRngName = UriUtils.encodedURI(objectPropRange); // For auto completion info. Remains null
																				// if none.
				}
			}
			// Build up the template and form rows in each cycle and make the property page.
			String propName = boxName + "-" + UriUtils.encodedURI(prop); // eg "Person-foaf-name"
			tplRows.append(tplRow(propName, formattingProperties, isImage));
			frmRows.append(frmRow(prop, objectPropRngName, smwType, formattingProperties, propName));
			articles.add(makePropertyPage(prop, smwType, objectPropRngName, boxName, propName, true));
		}
		articles.add(makeCategoryPage(boxName));
		articles.add(makeTemplatePage(boxName, tplRows.toString(),
				getCSS(JenaFresnelUtils.getFormattingProperties(lens), FRESNEL.RESOURCESTYLE)));
		articles.add(makeFormPage(boxName, frmRows.toString()));
		return articles;
	}

	/**
	 * Returns the data type of a data type property or type page if no data type found (object property).
	 * 
	 * @param formattingProperties of the property
	 * @return type of the property
	 */
	private static SMWType getRange(PropertyMap formattingProperties) {
		Resource range = null;
		SMWType smwType = SMWType.PAGE; // default type (object property)
		range = (Resource) formattingProperties.pick(OWF.DATATYPE);
		SMWType rangeType = rangeMap.get(range);
		if (rangeType != null) {
			smwType = rangeType; // get wiki data type, based on the range of the data type property
		}
		return smwType;
	}

	/**
	 * Gets the Fresnel styles in CSS.
	 * 
	 * @param formattingProperties
	 * @param styleType (e.g. FRESNEL.PROPERTYSTYLE)
	 * @return the style in string format, or empty string if none
	 */
	private static String getCSS(PropertyMap formattingProperties, Property styleType) {
		String style = "";
		RDFNode nodeStyle = formattingProperties.pick(styleType);
		if (nodeStyle != null) {
			style = nodeStyle.asLiteral().getString();
			if (!style.isEmpty()) {
				style = " style='" + style + "'";
			}
		}
		return style;
	}

	/**
	 * Returns the form parameters for the individual properties, if any.
	 * 
	 * @param smwType property's type
	 * @param formattingProperties
	 * @param formattingProperties
	 * @return the parameters, or empty string
	 */
	private static String frmsParams(SMWType smwType, PropertyMap formattingProperties) {
		String frmsParams = "";
		boolean mandatoryValue = false;
		boolean listValue = true;
		RDFNode mandatory = formattingProperties.pick(OWF.ISMANDATORY);
		RDFNode list = formattingProperties.pick(OWF.ISLIST);
		// handle hasType tag
		switch (smwType) {
		case BOOLEAN:
			frmsParams = "|input type=radiobutton|values=Yes,No";
			break;
		case DATE:
			frmsParams = "|input type=date";
			break;
		default:
			break;
		}
		// handle mandatory tag. the default is that a form field is optional
		// a mandatory field has the tag 'mandatory'
		if (mandatory != null) {
			// a property is defined, try to get the value of the literal.
			try {
				mandatoryValue = mandatory.asLiteral().getBoolean();
			} catch (Exception e) {
			}
		}
		if (mandatoryValue) {
			frmsParams += "|mandatory";
		}
		// handle isList tag. The default is that a form field is a list which is indicated by the tag 'list'
		if (list != null) {
			// a property is defined, try to get the value of the literal.
			try {
				listValue = list.asLiteral().getBoolean();
			} catch (Exception e) {
			}
		}
		if (listValue) {
			frmsParams += "|list";
		}
		return frmsParams;
	}

	/**
	 * Builds the row for one property in an inform box template, using Fresnel's format model (property, label and
	 * value).
	 * 
	 * @param prop prop the property to build the row for
	 * @param formattingProperties mapping containing the property's property, label an value styles.
	 * @param isImage whether or not the type of the property is an Image
	 * @return the row in div tags
	 */
	private static String tplRow(String name, PropertyMap formattingProperties, boolean isImage) {
		String labelDiv = null;
		String valueDiv = null;
		String propertyDiv = null;
		boolean listvalue = true;
		String rdfExportDiv = ""; // Default "" in case semantic prop link already available (always true except when
									// isImage). Otherwise concat a dummy div for special:ExportRDF
		// Fetch CSS styles
		String propertyStyle = getCSS(formattingProperties, FRESNEL.PROPERTYSTYLE);
		String labelStyle = getCSS(formattingProperties, FRESNEL.LABELSTYLE);
		String valueStyle = getCSS(formattingProperties, FRESNEL.VALUESTYLE);
		RDFNode label = formattingProperties.pick(FRESNEL.LABEL);
		RDFNode list = formattingProperties.pick(OWF.ISLIST);
		// get the value of the list tag. (true, false), the default is true
		try {
			listvalue = list.asLiteral().getBoolean();
		} catch (Exception e) {
		}
		valueDiv = "\t\t<div class='ib_value'" + valueStyle + ">";
		String delimiterString = getDelimiter(formattingProperties);
		if (isImage) { // then use the <img> tag
			if (listvalue) { // rdfExportDiv gets an array map to allow for multi values in the export
				valueDiv += "{{#arraymap:{{{" + name + "|}}}|,|xxx|<img src='xxx'/>" + delimiterString + "}}";
				rdfExportDiv = "\t\t<!-- Dummy div below for special:ExportRDF: -->\n\t\t<div style='display:none;'>"
						+ "{{#arraymap:{{{" + name + "|}}}|,|xxx|[[" + name + "::xxx]]}}</div>\n";
			} else { // rdfExportDiv does not get an array map to dis-allow for multi values in the export
				valueDiv += "<img src='{{{" + name + "|}}}'/>";
				rdfExportDiv = "\t\t<!-- Dummy div below for special:ExportRDF: -->\n\t\t<div style='display:none;'>"
						+ "[[" + name + "::" + "{{{" + name + "|}}}]]</div>\n";
			}
		} else { // array map function could take a delimiter value
			if (listvalue) { // handle the list
				valueDiv += "{{#arraymap:{{{" + name + "|}}}|,|xxx|[[" + name + "::xxx]]" + delimiterString + "}}";
			} else { // handle the single value
				valueDiv += "[[" + name + "::" + "{{{" + name + "|}}}]]";
			}
		}
		valueDiv += "</div>\n";
		labelDiv = "\t\t<div class='ib_label'" + labelStyle + ">";
		if (!FRESNEL.NONE.equals(label)) {
			labelDiv += StringEscapeUtils.escapeXml10(label.toString()); // escape user entries
		}
		labelDiv += "</div>\n";
		propertyDiv = "\t{{#if: {{{" + name + "|}}}|\n\t<div class='ib_property'" + propertyStyle + ">\n" + labelDiv
				+ valueDiv + rdfExportDiv + "\t</div>|}}\n\n";
		return propertyDiv;
	}

	private static String getDelimiter(PropertyMap formattingProperties) {
		String delimiterString = "";
		RDFNode delimiter = formattingProperties.pick(OWF.DELIMITER);
		if (delimiter != null) { // make sure \n gets through to wiki as breaktag
			delimiterString = delimiter.toString();
			String delTrim = delimiterString.trim();
			if ("\n".equals(delTrim) || "\\n".equals(delTrim)) {
				delimiterString = "<br/>";
			}
			delimiterString = "|" + delimiterString; // Prepend a found delimiter with a pipe for usage in an arraymap
		}
		return delimiterString;
	}

	/**
	 * Builds the row for one property in a Semantic Forms' form page.
	 * 
	 * @param prop the property to build the row for
	 * @param objectPropRngName range of an object property if (exists AND auto completion in gui enabled), null
	 *        otherwise
	 * @param frmsParams
	 * @return the row
	 */
	private static String frmRow(Resource prop, String objectPropRngName, SMWType smwType,
			PropertyMap formattingProperties, String name) {
		String autocomp = "";
		String frmsParams = frmsParams(smwType, formattingProperties);
		// rngName is always null for data type properties and for object props without autocompletion
		if (objectPropRngName != null) {
			autocomp = "|autocomplete on category=" + objectPropRngName;
		}
		return "|- \n" + "! " + prop.getLocalName() + ": \n" + "| {{{field|" + name + autocomp + frmsParams + " }}}\n";
	}

	/**
	 * Wraps up all the information for the property page and passes to makePage().
	 * 
	 * @param prop the property to make the page for
	 * @param smwType the type of the property
	 * @param objectPropRngName the name of the property's range
	 */
	private static Article makePropertyPage(Resource prop, SMWType smwType, String objectPropRngName, String boxName,
			String name, boolean subProp) {
		String pageContent = null;
		pageContent = "This property has type [[Has type::" + smwType.getAnnotation() + "]]. "
				+ "Equivalent URI is [[Equivalent URI::" + prop.getURI() + "]]. ";
		// rngName is null for data type properties or object props without autocompletion
		if (subProp && objectPropRngName != null) {
			pageContent += "This property uses the form [[Has default form::Form:" + boxName + "]]. ";
		}
		// see javaDoc makeSmwImportPages()
		String encodedURI = UriUtils.encodedURI(prop);
		String prefixedURI = JenaUtils.prefixedURI(prop);
		if (prefixedURI.equals(prop.getURI())) {
			prefixedURI = encodedURI; // if the URI could't be prefixed, return the encoded form
		}
		pageContent += "This relation is used to represent [[imported from::" + prefixedURI + "]]. ";
		if (subProp) {
			pageContent += "It is a Subproperty of [[Subproperty of::Property:" + encodedURI + "]].";
		}
		if (!subProp) {
			name = encodedURI;
		}
		return makePage("Property", name, pageContent);
	}

	/**
	 * Wraps up all the information for the category page and passes to makePage().
	 * 
	 * @param boxName
	 */
	private static Article makeCategoryPage(String boxName) {
		return makePage("Category", boxName, "");
	}

	/**
	 * Wraps up all the information for the template inform box page and passes to makePage().
	 * 
	 * @param boxName
	 * @param tplRows
	 * @param lensFormattingProperties
	 */
	private static Article makeTemplatePage(String boxName, String tplRows, String style) {
		// Start of Template page with default CSS
		String tplHead = "<includeonly>\n<div class='ib_resource'" + style + ">\n\n";
		String tplFoot = "</div>\n\n[[Category:" + boxName + "]]\n" + "</includeonly>";
		return makePage("Template", "Informbox " + boxName, tplHead + tplRows + tplFoot);
	}

	/**
	 * Wraps up all the information for the form page and passes to makePage().
	 * 
	 * @param boxName
	 * @param frmRows
	 */
	private static Article makeFormPage(String boxName, String frmRows) {

		String frmHead = "<noinclude>\n" + "This is the '" + boxName + "' form.\n"
				+ "To create a page with this form, enter the page name below;\n"
				+ "if a page with that name already exists, you will be sent to a form to edit that page.\n\n\n" +

				"{{#forminput:form=" + boxName + "}}\n\n" +

				"</noinclude><includeonly>\n"
				+ "<div id='wikiPreview' style='display: none; padding-bottom: 25px; margin-bottom: 25px; "
				+ "border-bottom: 1px solid #AAAAAA;'></div>\n" + "{{{for template|Informbox " + boxName + "|label="
				+ boxName + "}}}\n" + "{| class='formtable' \n";

		// End of Form page

		String frmFoot = "|} \n" + "{{{end template}}}\n\n" +

		"'''Free text:'''\n\n" +

		"{{{standard input|free text|rows=10}}}\n\n\n" +

		"{{{standard input|summary}}}\n\n" +

		"{{{standard input|minor edit}}} {{{standard input|watch}}}\n\n" +

		"{{{standard input|save}}} {{{standard input|preview}}} {{{standard input|changes}}} {{{standard input|cancel}}}\n"
				+ "</includeonly> \n";

		return makePage("Form", boxName, frmHead + frmRows + frmFoot);
	}

	/**
	 * Builds MediaWiki:Smw_import_ pages for reusing elements of external vocabularies directly within the wiki in
	 * complement with the [[importedFrom::_]] tag in makePropertyPage. See
	 * https://www.semantic-mediawiki.org/wiki/Help:Special_property_Imported_from
	 */
	private static List<Article> makeSmwImportPages(Map<String, Map<Resource, SMWType>> smwImportMap, Model jenaModel) {
		List<Article> articles = new ArrayList<Article>();
		final String eol = "\n";
		// make a MediaWiki:Smw_import_ page for every name space
		for (Entry<String, Map<Resource, SMWType>> page : smwImportMap.entrySet()) {
			String namespace = page.getKey();
			StringBuilder content = new StringBuilder(namespace + eol);

			for (Entry<Resource, SMWType> propertyType : page.getValue().entrySet()) {
				String propName = propertyType.getKey().getLocalName();
				String smwType = propertyType.getValue().getAnnotation();
				content.append(" " + propName + "|Type:" + smwType + eol);
			}
			String prefix = jenaModel.shortForm(namespace);
			if (!prefix.equals(namespace)) {
				prefix = prefix.substring(0, prefix.length() - 1);
			}
			String encodedNameSpace = UriUtils.encodedURI(prefix, prefix);
			articles.add(makePage("MediaWiki", "Smw_import_" + encodedNameSpace, content.toString()));
		}
		return articles;
	}

	/**
	 * Builds a page into a new article.
	 * 
	 * @param prefix
	 * @param name
	 * @param content
	 */
	private static Article makePage(String prefix, String name, String content) {
		if (content.isEmpty()) {
			content = "<span> </span>";
		}
		return new Article(prefix + ":" + name, content);
	}
}
