/**
 * 
 */
package nl.ou.fresnelforms.jena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import nl.ou.fresnelforms.fresnel.FresnelStyleClass;
import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.fresnel.PropertyType;
import nl.ou.fresnelforms.vocabulary.FRESNEL;
import nl.ou.fresnelforms.vocabulary.OWF;

/**
 * Adapter of the interface of a PropertyBinding to interaction with a Jena Model.
 *
 */
public class JenaPropertyBindingAdapter {
	private PropertyBinding propertyBinding;
	private Model model;

	/**
	 * Map of Fresnel vocabulary Properties to FresnelStyleClass items.
	 */
	private Map<Property, FresnelStyleClass> jenaFresnelStyleMap = null;

	/**
	 * Creates an adapter to adapt the interface of a PropertyBinding to interaction with a Jena Model.
	 * 
	 * @param propertyBinding the property Binding.
	 * @param model the Jena Model.
	 */
	public JenaPropertyBindingAdapter(PropertyBinding propertyBinding, Model model) {
		this.propertyBinding = propertyBinding;
		this.model = model;

		jenaFresnelStyleMap = new HashMap<Property, FresnelStyleClass>();
		jenaFresnelStyleMap.put(FRESNEL.RESOURCESTYLE, FresnelStyleClass.RESOURCE_STYLE);
		jenaFresnelStyleMap.put(FRESNEL.PROPERTYSTYLE, FresnelStyleClass.PROPERTY_STYLE);
		jenaFresnelStyleMap.put(FRESNEL.LABELSTYLE, FresnelStyleClass.LABEL_STYLE);
		jenaFresnelStyleMap.put(FRESNEL.VALUESTYLE, FresnelStyleClass.VALUE_STYLE);

	}

	/**
	 * Creates label items for a Format from a PropertyBinding.
	 * 
	 * @param format the format to create the label items for
	 */
	void createLabel(Resource format) {
		// Set the label of the formatted property
		RDFNode label = null;
		if (propertyBinding.isLabelEnabled()) {
			label = model.createLiteral(propertyBinding.getLabel());
		} else {
			label = FRESNEL.NONE; // disable it by using fresnel:none
		}
		JenaUtils.replaceProperty(format, FRESNEL.LABEL, label);
	}

	/**
	 * Creates delimiter resources for a Format from a PropertyBinding.
	 * 
	 * @param format the format to create the resources for
	 */
	void createDelimiterFrom(Resource format) {
		String delimiter = propertyBinding.getDelimiter();
		Literal delimiterLiteral = null;
		if (!PropertyBinding.DEFAULT_DELIMITER.equals(delimiter)) {
			delimiterLiteral = model.createLiteral(delimiter); // only define the Literal when its not the default
		}
		JenaUtils.replaceProperty(format, OWF.DELIMITER, delimiterLiteral);
	}

	/**
	 * Creates autocompletion resources for a Format from a PropertyBinding.
	 * 
	 * @param format the format to create the resources for
	 */
	void createAutoCompletion(Resource format) {
		JenaUtils.replaceProperty(format, OWF.AUTOCOMPLETEFROMCLASS, getRanges());
	}

	/**
	 * Creates datatype resources for a Format from a PropertyBinding.
	 * 
	 * @param format the format to create the resources for
	 */
	void createDataType(Resource format) {
		List<Resource> dataTypes = new ArrayList<Resource>();
		for (String uri : propertyBinding.getDataTypeURIs()) {
			dataTypes.add(model.createResource(uri));
		}
		JenaUtils.replaceProperty(format, OWF.DATATYPE, dataTypes);
	}

	/**
	 * Creates isList resources for a Format from a PropertyBinding.
	 * 
	 * @param format the format to create the resources for
	 */
	void createIsList(Resource format) {
		Literal isList = null;
		if (!propertyBinding.isList()) { // only define the Literal when its not the default (true)
			isList = model.createTypedLiteral(false, XSDDatatype.XSDboolean);
		}
		JenaUtils.replaceProperty(format, OWF.ISLIST, isList);
	}

	/**
	 * Creates isMandatory resources for a Format from a PropertyBinding.
	 * 
	 * @param format the format to create the resources for
	 */
	void createIsMandatory(Resource format) {
		Literal isMandatory = null;
		if (propertyBinding.isMandatory()) { // only define the Literal when its not the default (false)
			isMandatory = model.createTypedLiteral(true, XSDDatatype.XSDboolean);
		}
		JenaUtils.replaceProperty(format, OWF.ISMANDATORY, isMandatory);
	}

	/**
	 * Creates property type resources for a Format from a PropertyBinding.
	 * 
	 * @param format the format to create the resources for
	 */
	void createPropertyType(Resource format) {
		PropertyType type = propertyBinding.getPropertyType();
		Resource fresnelType = null;
		switch (type) {
		case IMAGE:
			// define the Property's value as a Fresnel image
			fresnelType = FRESNEL.IMAGE;
			break;
		case EXTERNAL_LINK:
			// define the Property's value as a Fresnel External link
			fresnelType = FRESNEL.EXTERNALLINK;
			break;
		default:
		}
		JenaUtils.replaceProperty(format, FRESNEL.VALUE, fresnelType);
	}

	/**
	 * Formats a PropertyBinding according to the information in lens and property Resources.
	 * 
	 * @param lens the lens's Resource to use for formatting
	 * @param property the property's Resource to use for Formatting
	 */
	void format(Resource lens, Resource property) {
		PropertyMap formattingProperties = JenaFresnelUtils.getFormattingProperties(property, lens, false);

		setLabel(formattingProperties);
		setStyling(formattingProperties);
		setDelimiter(formattingProperties);
		setAutocompletion(formattingProperties);
		setPropertyType(formattingProperties);
		setIsList(formattingProperties);
		setMandatory(formattingProperties);
	}

	/**
	 * Sets the mandatory property of a PropertyBinding according to specific formatting properties.
	 * 
	 * @param formattingProperties the formatting properties to use
	 */
	private void setMandatory(PropertyMap formattingProperties) {
		RDFNode object;
		// get the type of the property
		object = formattingProperties.pick(OWF.ISMANDATORY);
		if (object != null && object.isLiteral()) {
			propertyBinding.setMandatory(object.asLiteral().getBoolean());
		}
	}

	/**
	 * Sets the isList property of a PropertyBinding according to specific formatting properties.
	 * 
	 * @param formattingProperties the formatting properties to use
	 */
	private void setIsList(PropertyMap formattingProperties) {
		RDFNode object;
		// get the type of the property
		object = formattingProperties.pick(OWF.ISLIST);
		if (object != null && object.isLiteral()) {
			propertyBinding.setIsList(object.asLiteral().getBoolean());
		}
	}

	/**
	 * Sets the property type of a PropertyBinding according to specific formatting properties.
	 * 
	 * @param formattingProperties the formatting properties to use
	 */
	private void setPropertyType(PropertyMap formattingProperties) {
		RDFNode object;
		// get the type of the property
		object = formattingProperties.pick(FRESNEL.VALUE);
		if (object != null) {
			if (FRESNEL.IMAGE.equals(object.asResource())) {
				propertyBinding.setPropertyType(PropertyType.IMAGE);
			}
			if (FRESNEL.EXTERNALLINK.equals(object.asResource())) {
				propertyBinding.setPropertyType(PropertyType.EXTERNAL_LINK);
			}
		}
	}

	/**
	 * Sets the autocompletion of a PropertyBinding according to specific formatting properties.
	 * 
	 * @param formattingProperties the formatting properties to use
	 */
	private void setAutocompletion(PropertyMap formattingProperties) {
		List<RDFNode> objects;
		// get autocompletion
		objects = formattingProperties.get(OWF.AUTOCOMPLETEFROMCLASS);
		if (objects != null) {
			propertyBinding.setAutoCompletionEnabled(true);
		}
	}

	/**
	 * Sets the delimiter of a PropertyBinding according to specific formatting properties.
	 * 
	 * @param formattingProperties the formatting properties to use
	 */
	private void setDelimiter(PropertyMap formattingProperties) {
		RDFNode object;
		// just pick one of the delimiters
		object = formattingProperties.pick(OWF.DELIMITER);
		if (object != null && object.isLiteral()) {
			propertyBinding.setDelimiter(object.asLiteral().getString());
		}
	}

	/**
	 * Sets the styling of a PropertyBinding according to specific formatting properties.
	 * 
	 * @param formattingProperties the formatting properties to use
	 */
	private void setStyling(PropertyMap formattingProperties) {
		RDFNode object;
		// Get the Styling
		for (Resource styleProperty : JenaFresnelUtils.PROPERTY_STYLE_PROPERTIES) {
			// we can just pick the style from the properties, for they are concatenated
			object = formattingProperties.pick(styleProperty);
			if (object != null && object.isLiteral()) {
				propertyBinding.getFresnelStyle().setFresnelStyle(jenaFresnelStyleMap.get(styleProperty),
						object.asLiteral().getString());
			}
		}
	}

	/**
	 * Sets the label of a PropertyBinding according to specific formatting properties.
	 * 
	 * @param formattingProperties the formatting properties to use
	 */
	private void setLabel(PropertyMap formattingProperties) {
		RDFNode object;
		// Get the label of the property. For the time being, just pick the first one
		object = formattingProperties.pick(FRESNEL.LABEL);
		if (object != null) {
			if (object.isResource()) {
				propertyBinding.setLabelEnabled(!FRESNEL.NONE.equals(object.asResource()));
			} else {
				propertyBinding.setLabelEnabled(true);
				propertyBinding.setLabel(object.asLiteral().getString());
			}
		}
	}

	/**
	 * Determines the Range resources to use for autocompletion.
	 * 
	 * @return the List of range Resources for the PropertyBinding (may be empty)
	 */
	private List<Resource> getRanges() {
		List<Resource> result = new ArrayList<Resource>();
		if (propertyBinding.isAutoCompletionEnabled()) {
			nl.ou.fresnelforms.fresnel.Property fresnelProperty = propertyBinding.getProperty();
			if (fresnelProperty != null) {
				nl.ou.fresnelforms.ontology.Property property = fresnelProperty.getProperty();
				if (property != null && property.isObjectProperty()) {
					List<nl.ou.fresnelforms.ontology.Class> ranges = property.asObjectProperty().getRange();
					for (nl.ou.fresnelforms.ontology.Class range : ranges) {
						Resource thisRange = model.createResource(range.getURI());
						if (!result.contains(thisRange)) {
							result.add(thisRange);
						}
					}
				}
			}
		}
		return result;
	}

}
