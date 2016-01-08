package nl.ou.fresnelforms.fresnel;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.vocabulary.XSD;
import nl.ou.fresnelforms.ontology.PropertyRestriction;

/**
 * The property binding class.
 *
 */
public class PropertyBinding {
	/**
	 * The default delimiter for a collection of values.
	 */
	public static final String DEFAULT_DELIMITER = ", ";

	private Property property;
	private Lens lens;
	private String label;
	private String delimiter;
	private String defaultLabel;
	private PropertyType propertyType;
	private FresnelStyle fresnelStyle;
	private boolean show;
	private boolean labelEnabled;
	private boolean autoCompletionEnabled;
	private int index;
	private boolean isList;
	private boolean mandatory;

	/**
	 * Constructs a PropertyBinding.
	 * 
	 * @param property the property
	 * @param lens the lens the property is bound to
	 * @param index the index
	 */
	public PropertyBinding(Property property, Lens lens, int index) {
		this.property = property;
		this.lens = lens;
		this.show = true;
		this.labelEnabled = true;
		this.autoCompletionEnabled = false;
		this.index = index;
		this.defaultLabel = property.getLocalName();
		this.label = this.defaultLabel;
		//check for label, if present overwrite the default.
		if (property.getProperty()!=null){
			if (!property.getProperty().getLabel().isEmpty()){
				this.label = property.getProperty().getLabel();
			}
		} 
		this.delimiter = DEFAULT_DELIMITER;
		this.propertyType = PropertyType.URI;
		this.fresnelStyle = new FresnelStyle();
		this.isList = true;
		this.mandatory =  false;
		//init restrictions if there are any
		nl.ou.fresnelforms.ontology.Property dtp = property.getProperty();
		if (lens.getClassLensDomain()!=null){
			nl.ou.fresnelforms.ontology.Class cls = lens.getClassLensDomain();
			if (cls.hasRestrictions()){
				PropertyRestriction propres = cls.getPropertyrestrictions().get(dtp);
				if (propres != null){
					this.isList = !( propres.getMaxCardinality() == 1);
					this.mandatory = (propres.getMinCardinality() == 1);
				}
			}
		}
	}

	/**
	 * @return true if the property is multivalue otherwise false
	 */
	public boolean isList() {
		return isList;
	}

	/**
	 * Sets the multivalue for this property.
	 * 
	 * @param multivalue the multivalue to set
	 */
	public void setIsList(boolean multivalue) {
		this.isList = multivalue;
	}

	/**
	 * @return true if the property is mandatory otherwise false
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * Sets the mandatory value for this property.
	 * 
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	/**
	 * @return the delimiter of this binding.
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * Sets the delimiter of this binding.
	 * 
	 * @param delimiter the delimiter to set
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the property of this binding
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * @return the lens of this binding
	 */
	public Lens getLens() {
		return lens;
	}

	/**
	 * @return the label of this binding
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the label of this binding
	 */
	public String getDefaultLabel() {
		return defaultLabel;
	}

	/**
	 * @param label the label of this binding
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Enables or Disables the label visibility.
	 * 
	 * @param enabled true when the label must be shown, false otherwise
	 */
	public void setLabelEnabled(boolean enabled) {
		this.labelEnabled = enabled;
	}

	/**
	 * Enables or Disables autocompletion.
	 * 
	 * @param enabled true when the label must be shown, false otherwise
	 */
	public void setAutoCompletionEnabled(boolean enabled) {
		this.autoCompletionEnabled = enabled;
	}

	/**
	 * @return true when the label will be shown, false otherwise
	 */
	public boolean isLabelEnabled() {
		return this.labelEnabled;
	}

	/**
	 * @return true when autocompletion is enabled, false otherwise
	 */
	public boolean isAutoCompletionEnabled() {
		return isAutoCompletionAvailable() && this.autoCompletionEnabled;
	}

	/**
	 * @return true when autocompletion is available for the property, false otherwise
	 */
	public boolean isAutoCompletionAvailable() {
		return (property != null) && (property.getProperty() != null) && property.getProperty().isObjectProperty();
	}

	/**
	 * @return the property type of this binding
	 */
	public PropertyType getPropertyType() {
		return this.propertyType;
	}

	/**
	 * @param propertyType the property type of this binding
	 */
	public void setPropertyType(PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	/**
	 * @return true if this binding is visible
	 */
	public boolean isShown() {
		return this.show;
	}

	/**
	 * @param show true if this binding is visible
	 */
	public void setShown(boolean show) {
		this.show = show;
	}

	/**
	 * @return the index of this binding
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index of this binding
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the fresnel style of this binding
	 */
	public FresnelStyle getFresnelStyle() {
		return this.fresnelStyle;
	}

	/**
	 * @return the datatypeURIs of this binding, if applicable
	 */
	public List<String> getDataTypeURIs() {
		List<String> result = new ArrayList<String>();
		nl.ou.fresnelforms.ontology.Property baseProperty = property.getProperty();
		if (baseProperty != null && baseProperty.isDatatypeProperty()) {
			nl.ou.fresnelforms.ontology.DatatypeProperty dataTypeProperty = baseProperty.asDatatypeProperty();
			for (nl.ou.fresnelforms.ontology.DataType dataType : dataTypeProperty.getRange()) {
				result.add(dataType.getURI());
			}
			// if no dataType was defined for this DataTypeProperty, make it an xsd:string by default
			if (result.isEmpty()) {
				result.add(XSD.xstring.getURI());
			}
		}
		return result;
	}

	/**
	 * @return true if this binding is formatted.
	 */
	public boolean isFormatted() {
		boolean hasCustomLabel = !(isLabelEnabled() && getLabel().equals(getDefaultLabel()))
				&& getPropertyType().equals(PropertyType.URI);
		boolean isImageOrLink = PropertyType.IMAGE.equals(getPropertyType())
				|| PropertyType.EXTERNAL_LINK.equals(getPropertyType());
		boolean styled = getFresnelStyle().isFormatted() || hasCustomLabel || isImageOrLink
				|| !DEFAULT_DELIMITER.equals(getDelimiter());
		return styled || isAutoCompletionEnabled() || !isList() || isMandatory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if (lens != null) {
			result += lens.hashCode();
		}
		result = prime * result;
		if (property != null) {
			result += property.hashCode();
		}
		return result;
	}

	/**
	 * @return true if this binding has the same property and lens.
	 * @param obj the object to compare
	 * @throws NullPointerException if parameter obj has the value null.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof PropertyBinding) {
			PropertyBinding propertyBinding = (PropertyBinding) obj;
			if (propertyBinding.getProperty().equals(getProperty()) && propertyBinding.getLens().equals(getLens())) {
				return true;
			}
		}
		return false;
	}

}
