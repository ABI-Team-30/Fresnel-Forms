package nl.ou.fresnelforms.jena;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresnel.FresnelStyle;
import nl.ou.fresnelforms.fresnel.FresnelStyleClass;
import nl.ou.fresnelforms.fresnel.Lens;
import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.ontology.Ontology;
import nl.ou.fresnelforms.vocabulary.FRESNEL;
import nl.ou.fresnelforms.vocabulary.OWF;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;

/**
 * Class for interfacing the Fresnel model with the Apache Jena RDF Model.
 */
public class JenaFresnelModel {

	/**
	 * The filetype to use for loading from and saving to files.
	 */
	private static final String FILETYPE = "TURTLE";

	/**
	 * The default local namespace to use.
	 */
	private static final String DEFAULTNS = "#";

	/**
	 * The Apache Jena Model, either built from a Fresnel class or loaded from file.
	 */
	private Model model = null;

	/**
	 * Constructor filling some initial requirements.
	 */
	public JenaFresnelModel() {
		initialize();
	}

	private static Logger log = LogManager.getLogger(JenaFresnelModel.class);

	/**
	 * Initializes a new JenaFresnelModel.
	 */
	private void initialize() {
		model = ModelFactory.createDefaultModel();
	}

	/**
	 * Returns the Jena model.
	 * 
	 * @return the Jena Model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param url the url of the file.
	 * @throws IOException if an IO error occurs
	 */
	public void load(String url) throws IOException {
		// use the FileManager to find the input file
		InputStream in = FileManager.get().open(url);
		if (in == null) {
			throw new IllegalArgumentException("File: " + url + " not found");
		}

		model.read(in, null, FILETYPE); // parse the file as a RDF Model
		in.close();
	}

	/**
	 * Saves the model.
	 * 
	 * @param url the url of the file
	 * 
	 * @throws IOException when the file could not be created correctly
	 */
	public void save(String url) throws IOException {
		OutputStream output;
		output = new FileOutputStream(url);
		model.write(output, FILETYPE);
		output.close();
	}

	/**
	 * Returns the local namespace for the Lens and Format instances.
	 * 
	 * @return the local namespace
	 */
	public String getLocalNamespace() {
		return model.getNsPrefixURI("");
	}

	/**
	 * Determines essential prefixes from the given fresnel model.
	 * 
	 * @param fresnel the fresnel instance from which to determine essential prefixes.
	 */
	private void preparePrefixes(Fresnel fresnel) {
		// set the default namespace if absent
		if (model.getNsPrefixURI("") == null) {
			model.setNsPrefix("", DEFAULTNS); // set (or override) the default namespace to a local one
		}

		// Extract some needed source ontology information from owlapi
		OWLOntology owlOnt = fresnel.getOntology().getOWLOntology();
		PrefixManager pManager = owlOnt.getOWLOntologyManager().getOntologyFormat(owlOnt).asPrefixOWLOntologyFormat();

		String ontologyName = fresnel.getOntology().getName();
		// take all prefixes from the source owlapi ontology in our presentation ontology, for they may be referred to
		for (Entry<String, String> entry : pManager.getPrefixName2PrefixMap().entrySet()) {
			String key = entry.getKey();
			// remove the last character (:), for in Jena it isn't part of the prefix itself
			String prefix = key.substring(0, key.length() - 1);
			if ("".equals(prefix)) {
				// Use the ontologyname as replacement for the source ontology's default prefix
				prefix = ontologyName;
			}
			model.setNsPrefix(prefix, entry.getValue());
		}

		// add (or override) prefixes which are needed indefinitely
		model.setNsPrefix("rdf", RDF.getURI());
		model.setNsPrefix("xsd", XSD.getURI());
		model.setNsPrefix("owf", OWF.getURI());
		model.setNsPrefix("fresnel", FRESNEL.getURI());
	}

	/**
	 * Creates a JenaFresnelModel from a Fresnel model.
	 * 
	 * @param fresnel the fresnel model
	 * @return The JenaFresnelModel itself, to permit cascading
	 */
	public JenaFresnelModel mergeFresnel(Fresnel fresnel) {
		log.debug("merging Fresnel ontology with Jena Model");
		// prepare the essential prefixes according to the given fresnel model
		preparePrefixes(fresnel);

		// Create one Group to which all Lenses and Formats belong
		Resource ontologyGroup = model.createResource(getLocalNamespace() + fresnel.getOntology().getName() + "Group");
		List<Lens> lenses = fresnel.getDisplayedLenses();
		int currentLens = 1;
		for (Lens displayedLens : lenses) {
			log.trace("creating RDF triples for lens " + currentLens + " of " + lenses.size() + "...");
			// convert every internal Lens to a fresnel Lens
			Resource lensClass = model.createResource(displayedLens.getURI()); // the uri of the lens is that of the
																				// class it describes
			Resource lens = model.createResource(getLocalNamespace() + displayedLens.getFresnelName());
			Resource lensGroup = model.createResource(getLocalNamespace() + displayedLens.getFresnelName() + "Group");
			JenaUtils.replaceProperty(lens, RDF.type, FRESNEL.LENS);
			JenaUtils.replaceProperty(lens, FRESNEL.PURPOSE, FRESNEL.DEFAULTLENS);
			JenaUtils.replaceProperty(lens, FRESNEL.CLASSLENSDOMAIN, lensClass);
			JenaUtils.replaceProperty(lens, FRESNEL.GROUP, ontologyGroup);
			lens.addProperty(FRESNEL.GROUP, lensGroup); // make the lens part of a second lens-specific Group
			// All properties are saved in the showProperties for sorting purposes
			JenaUtils.replaceProperty(lens, FRESNEL.SHOWPROPERTIES,
					createPropertiesList(displayedLens.getPropertyBindings()));
			// Only hidden properties are added to the hideProperties list
			JenaUtils.replaceProperty(lens, FRESNEL.HIDEPROPERTIES,
					createPropertiesList(displayedLens.getHidePropertyBindings()));
			Point2D pos = displayedLens.getSavedPosition();
			JenaUtils.replaceProperty(lens, OWF.XPOS, model.createTypedLiteral((int) pos.getX()));
			JenaUtils.replaceProperty(lens, OWF.YPOS, model.createTypedLiteral((int) pos.getY()));

			JenaUtils.replaceProperty(lensGroup, RDF.type, FRESNEL.GROUP_TYPE); // define the lens's group
			createStylingProperties(lensGroup, displayedLens.getFresnelStyle()); // Create styling in the lens's Group
			createFormatsFromFormattedPropertyBindings(displayedLens);
			createFormatsFromLensProperties(displayedLens, ontologyGroup);
			currentLens++;
		}
		// Remove all hidden lenses from the model
		for (Lens hiddenLens : fresnel.getHiddenLenses()) {
			Resource lens = model.createResource(getLocalNamespace() + hiddenLens.getFresnelName());
			model.removeAll(lens, null, null);
		}
		log.debug("Fresnel ontology merged with Jena Model");
		// return this model to permit cascading
		return this;
	}

	/**
	 * Creates a PropertyList for a model, according to provided PropertyBindings.
	 * 
	 * @param propertyBindings the propertyBindings to use for the list
	 * @return The RDFList created from the propertyBindings
	 */
	private RDFList createPropertiesList(List<PropertyBinding> propertyBindings) {
		List<RDFNode> properties = new ArrayList<RDFNode>();
		for (PropertyBinding propertyBinding : propertyBindings) {
			nl.ou.fresnelforms.fresnel.Property thisProperty = propertyBinding.getProperty();
			String lensName = propertyBinding.getLens().getFresnelName();
			Resource property = model.createResource(thisProperty.getURI());
			Resource resource = null;
			if (propertyBinding.isFormatted() && propertyBinding.isShown()) {
				resource = model.createResource(); // create an anonymous resource
				resource.addProperty(RDF.type, FRESNEL.PROPERTYDESCRIPTION);
				resource.addProperty(FRESNEL.PROPERTY, property);
				resource.addProperty(FRESNEL.USE,
						model.createResource(getLocalNamespace() + thisProperty.getEncodedName() + lensName + "Format"));
			} else {
				// non-formatted or hidden properties are registered as-is
				resource = property;
			}
			properties.add(resource);
		}
		RDFList result = model.createList(properties.iterator()); // create an RDF list from the Java List

		return result;
	}

	/**
	 * Converts internal styling items (css) to Fresnel styling properties.
	 * 
	 * @param resource The resource to define the styling for
	 * @param lensStyle The styling items to use
	 * @return The resource, to permit cascading
	 */
	private Resource createStylingProperties(Resource resource, FresnelStyle lensStyle) {
		// Fill the style mappings
		HashMap<FresnelStyleClass, Property> fresnelJenaStyleMap = new HashMap<FresnelStyleClass, Property>();
		fresnelJenaStyleMap.put(FresnelStyleClass.RESOURCE_STYLE, FRESNEL.RESOURCESTYLE);
		fresnelJenaStyleMap.put(FresnelStyleClass.PROPERTY_STYLE, FRESNEL.PROPERTYSTYLE);
		fresnelJenaStyleMap.put(FresnelStyleClass.LABEL_STYLE, FRESNEL.LABELSTYLE);
		fresnelJenaStyleMap.put(FresnelStyleClass.VALUE_STYLE, FRESNEL.VALUESTYLE);

		for (FresnelStyleClass styleClass : FresnelStyleClass.values()) {
			String styleString = lensStyle.getFresnelStyle(styleClass);
			Literal style = null;
			if (!styleString.isEmpty()) {
				String styleUri = null;
				if (styleString.contains(":")) {
					styleUri = FRESNEL.STYLINGINSTRUCTIONS.getURI();
				} else {
					styleUri = FRESNEL.STYLECLASS.getURI();
				}
				style = model.createTypedLiteral(styleString, styleUri);
			}
			JenaUtils.replaceProperty(resource, fresnelJenaStyleMap.get(styleClass), style);
		}
		return resource;
	}

	/**
	 * Converts internal properties of a lens to Fresnel Formats.
	 * 
	 * @param displayedLens the Lens of which to convert the properties
	 * @param group the Fresnel Group to to which the Formats belong
	 * @return The resource, to permit cascading
	 */
	private void createFormatsFromLensProperties(Lens displayedLens, Resource group) {
		for (nl.ou.fresnelforms.fresnel.Property property : displayedLens.getProperties()) {
			// For every property of this lens, create a Format to describe it.
			Resource format = model.getResource(getLocalNamespace() + property.getEncodedName() + "Format");
			String uri = property.getURI();
			JenaUtils.replaceProperty(format, RDF.type, FRESNEL.FORMAT);
			JenaUtils.replaceProperty(format, FRESNEL.PROPERTYFORMATDOMAIN, model.createResource(property.getURI()));
			JenaUtils.replaceProperty(format, FRESNEL.GROUP, group);
			JenaUtils.replaceProperty(format, FRESNEL.LABEL, model.createLiteral(property.getLocalName()));
			JenaPropertyBindingAdapter pbAdapter = new JenaPropertyBindingAdapter(
					displayedLens.getPropertyBinding(uri), model);
			pbAdapter.createDataType(format);
		}
	}

	/**
	 * Converts internal formatted property bindings of a lens to Fresnel Formats.
	 * 
	 * @param displayedLens the Lens of which to convert the formatted property bindings
	 * @return The resource, to permit cascading
	 */
	private void createFormatsFromFormattedPropertyBindings(Lens displayedLens) {
		for (PropertyBinding propertyBinding : displayedLens.getFormattedPropertyBindings()) {
			String lensName = displayedLens.getFresnelName();
			nl.ou.fresnelforms.fresnel.Property thisproperty = propertyBinding.getProperty();
			// For every formatted property binding of a lens, create a specific Format
			Resource format = model.createResource(getLocalNamespace() + thisproperty.getEncodedName() + lensName
					+ "Format");
			JenaUtils.replaceProperty(format, RDF.type, FRESNEL.FORMAT);
			// handle the css styles
			createStylingProperties(format, propertyBinding.getFresnelStyle());

			// Create an adapter to use the PropertyBinding for the Jena Model.
			JenaPropertyBindingAdapter pbAdapter = new JenaPropertyBindingAdapter(propertyBinding, model);
			pbAdapter.createPropertyType(format);
			pbAdapter.createLabel(format);
			pbAdapter.createDelimiterFrom(format);
			pbAdapter.createIsList(format);
			pbAdapter.createIsMandatory(format);
			pbAdapter.createAutoCompletion(format);
		}
	}

	/**
	 * Converts the RDF model to a FresnelForms model.
	 * 
	 * @param ontology the domain ontology
	 * @return the fresnel model
	 */
	public Fresnel asFresnel(Ontology ontology) {
		Fresnel fresnel = new Fresnel(ontology);

		for (Lens lens : fresnel.getLenses()) {
			lens.setDisplayed(false);
			for (PropertyBinding pb : lens.getPropertyBindings()) {
				pb.setShown(false);
			}
		}

		// Handle every Fresnel Lens
		for (Resource lensResource : model.listResourcesWithProperty(RDF.type, FRESNEL.LENS).toList()) {
			fillLensFromResource(lensResource, fresnel);
		}

		return fresnel;
	}

	/**
	 * Construct a lens from a RDF Resource.
	 * 
	 * @param resource the Resource to treat as a Lens.
	 * @param fresnel the Fresnel Ontology
	 * @return the fresnel Lens
	 */
	private Lens fillLensFromResource(Resource resource, Fresnel fresnel) {
		// Currently, a lens must have a classLensDomain, take its URI
		Resource lensClassResource = resource.getPropertyResourceValue(FRESNEL.CLASSLENSDOMAIN);
		Lens lens = fresnel.getLens(lensClassResource.getURI());
		if (lens == null) {
			lens = new Lens(fresnel, lensClassResource.getURI());
		}

		JenaLensAdapter lensAdapter = new JenaLensAdapter(lens);
		lensAdapter.fillFromResource(resource);

		return lens;
	}

}