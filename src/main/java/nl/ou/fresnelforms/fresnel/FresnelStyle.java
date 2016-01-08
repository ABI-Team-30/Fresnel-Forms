package nl.ou.fresnelforms.fresnel;

/**
 * class representing a fresnel style.
 *
 */
public class FresnelStyle {

	private static final String STYLE_CLASS = "fresnel:styleClass";
	private static final String STYLING_INSTRUCTIONS = "fresnel:stylingInstructions";

	private String resourceStyle = "";
	private String propertyStyle = "";
	private String labelStyle = "";
	private String valueStyle = "";

	/**
	 * @param fresnelStyleClass the fresnel style enumeration class
	 * @return the style definition
	 */
	public String getFresnelStyle(FresnelStyleClass fresnelStyleClass) {
		if (fresnelStyleClass.equals(FresnelStyleClass.RESOURCE_STYLE)) {
			return this.resourceStyle;
		}
		if (fresnelStyleClass.equals(FresnelStyleClass.PROPERTY_STYLE)) {
			return this.propertyStyle;
		}
		if (fresnelStyleClass.equals(FresnelStyleClass.LABEL_STYLE)) {
			return this.labelStyle;
		}
		if (fresnelStyleClass.equals(FresnelStyleClass.VALUE_STYLE)) {
			return this.valueStyle;
		}
		return "";
	}

	/**
	 * @param fresnelStyleClass the fresnel style enumeration class
	 * @param value the style definition
	 */
	public void setFresnelStyle(FresnelStyleClass fresnelStyleClass, String value) {
		if (fresnelStyleClass.equals(FresnelStyleClass.RESOURCE_STYLE)) {
			this.resourceStyle = value;
		}
		if (fresnelStyleClass.equals(FresnelStyleClass.PROPERTY_STYLE)) {
			this.propertyStyle = value;
		}
		if (fresnelStyleClass.equals(FresnelStyleClass.LABEL_STYLE)) {
			this.labelStyle = value;
		}
		if (fresnelStyleClass.equals(FresnelStyleClass.VALUE_STYLE)) {
			this.valueStyle = value;
		}
	}

	/**
	 * @param fresnelStyleClass the fresnel style enumeration class
	 * @return the styltype as string
	 */
	public String getFresnelStyleType(FresnelStyleClass fresnelStyleClass) {
		String value = getFresnelStyle(fresnelStyleClass);
		if (value.contains(":")) {
			return STYLING_INSTRUCTIONS;
		}
		return STYLE_CLASS;
	}

	/**
	 * @return true if one of the style definitions is set.
	 */
	public boolean isFormatted() {
		boolean allEmpty = resourceStyle.isEmpty() && propertyStyle.isEmpty() && labelStyle.isEmpty();
		allEmpty = allEmpty && valueStyle.isEmpty();
		return !allEmpty;
	}

}
