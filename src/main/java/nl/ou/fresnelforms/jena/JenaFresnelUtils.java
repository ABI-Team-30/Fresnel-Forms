package nl.ou.fresnelforms.jena;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.ou.fresnelforms.vocabulary.FRESNEL;
import nl.ou.fresnelforms.vocabulary.OWF;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Abstract Class with some static helper methods which implement Fresnel semantics on a Apache Jena model.
 */
public abstract class JenaFresnelUtils {

	/**
	 * The priority level of a Lens's Group for formatting properties.
	 */
	private static final int GROUP_PRIORITY = 1;

	/**
	 * These are the Lens-applicable properties which are valid for a Group.
	 */
	public static final Resource[] STYLE_PROPERTIES = { FRESNEL.RESOURCESTYLE, FRESNEL.PROPERTYSTYLE,
			FRESNEL.LABELSTYLE, FRESNEL.VALUESTYLE };

	/**
	 * These are the Property-applicable properties which are valid for a Group.
	 */
	public static final Resource[] PROPERTY_STYLE_PROPERTIES = { FRESNEL.PROPERTYSTYLE, FRESNEL.LABELSTYLE,
			FRESNEL.VALUESTYLE };

	/**
	 * These are the Property-applicable properties which are valid for a Format.
	 */
	public static final Resource[] FORMAT_PROPERTIES = { FRESNEL.PROPERTYSTYLE, FRESNEL.LABELSTYLE, FRESNEL.VALUESTYLE,
			FRESNEL.LABEL, FRESNEL.VALUE, OWF.AUTOCOMPLETEFROMCLASS, OWF.DELIMITER, OWF.DATATYPE, OWF.ISMANDATORY, OWF.ISLIST };

	/**
	 * Returns the property which is described by the given property. When the given property is anonymous and is a
	 * fresnel:propertyDescription, it's fresnel:Property is returned, else the given property itself is returned.
	 * 
	 * @param property the given property
	 * @return the property which is described by the given property
	 */
	public static Resource getDescribedProperty(Resource property) {
		Resource realProperty = property;
		if (property.isAnon() && property.hasProperty(RDF.type, FRESNEL.PROPERTYDESCRIPTION)) {
			realProperty = property.getPropertyResourceValue(FRESNEL.PROPERTY);
		}
		return realProperty;
	}

	/**
	 * Determines the priority of a format for a property, according to a number of lensGroups and an explicitly defined
	 * Format. The number of priorities is: 1 for the explicit format + (the number of groups of which the lens is part
	 * ) + 1 for the Lens's Groups themselves + 1 for just a matching property format (without matching group)
	 * 
	 * @param format the Format to determine the priority of
	 * @param property the property to which the format must apply
	 * @param lensGroups the lensGroups to take into account (can be an empty set if there are none defined)
	 * @param explicitFormat the explicitly defined Format for the property (can be null if not defined)
	 * @return the priority of the format for the property, taking the lensGroups and explicitly defined format into
	 *         account
	 */
	private static int getFormatPriority(Resource format, Resource property, Set<Resource> lensGroups,
			Resource explicitFormat) {
		boolean hasPropertyAsDomain = format.hasProperty(FRESNEL.PROPERTYFORMATDOMAIN, property);
		int nrOfMatchingGroups = getNrOfMatchingGroups(lensGroups, format);
		int priority = -1; // By default, use -1 for no match at all until proven otherwise

		if (format.equals(explicitFormat)) {
			priority = getNrOfFormatPriorities(lensGroups.size()) - 1; // Highest priority for explicitly referenced
																		// formats
		} else if (hasPropertyAsDomain) {
			if (nrOfMatchingGroups > 0) {
				// a higher priority than that of just matching groups
				priority = GROUP_PRIORITY + nrOfMatchingGroups;
			} else {
				if (format.getPropertyResourceValue(FRESNEL.GROUP) == null) {
					priority = 0; // lowest priority, only when there are no fresnel:Groups defined
				}
			}
		}

		return priority;
	}

	/**
	 * Determines the Fresnel groups a resource is part of.
	 * 
	 * @param resource the resource for which to determine the groups it is part of
	 * @return the distinct Fresnel groups the resource is part of
	 */
	public static Set<Resource> getGroups(Resource resource) {
		Set<Resource> groups = new HashSet<Resource>();
		if (resource != null) {
			for (StmtIterator stmtIterator= resource.listProperties(FRESNEL.GROUP); stmtIterator.hasNext();) {
				Statement statement = stmtIterator.next();
				if (statement.getObject().isResource()) {
					groups.add(statement.getResource());
				}
			}
		}
		return groups;
	}

	/**
	 * Normalizes a CSS String by trimming it from heading and trailing spaces and delimiting it by a semicolon.
	 * 
	 * @param css the CSS String to normalize
	 * @return the normalized CSS String
	 */
	private static String normalizeCSS(String css) {
		final String delimiter = ";";
		String result = css.trim();
		if (!result.endsWith(delimiter)) {
			result = result + delimiter;
		}
		return result;
	}

	/**
	 * Determines the number of format priorities for determining formatting Properties on a Lens. This number is 1 for
	 * the explicit format + (the number of groups of which the lens is part ) + 1 for the Lens's Groups themselves + 1
	 * for just a matching property format (without matching group)
	 * 
	 * @param nrOfLensGroups the number of Groups of which the lens is part
	 * @return the number of Format priorities
	 */
	private static int getNrOfFormatPriorities(int nrOfLensGroups) {
		return 1 + 1 + nrOfLensGroups + 1;
	}

	/**
	 * Returns the number of matching groups of which a resource is a member.
	 * 
	 * @param groups the groups which are checked for membership
	 * @param resource the resource to check
	 * @return the number of matching groups of which the resource is a member
	 */
	private static int getNrOfMatchingGroups(Set<Resource> groups, Resource resource) {
		Set<Resource> ownGroups = getGroups(resource);
		ownGroups.retainAll(groups); // redefine ownGroups to be the intersection with groups
		return ownGroups.size();
	}

	/**
	 * Returns properties to display for a lens, according to Fresnel rules. The fresnel rules state that every property
	 * which is in fresnel:showProperties, but not in fresnel:hideProperties, must be displayed.
	 * 
	 * @param lens the lens to return the displayed properties for
	 * @return the properties to display
	 */
	public static List<Resource> getPropertiesToDisplay(Resource lens) {
		List<Resource> result = new ArrayList<Resource>();
		List<Resource> showProperties = JenaUtils
				.getRDFResources(lens.getPropertyResourceValue(FRESNEL.SHOWPROPERTIES));
		List<Resource> hideProperties = JenaUtils
				.getRDFResources(lens.getPropertyResourceValue(FRESNEL.HIDEPROPERTIES));

		for (Resource property : showProperties) {
			Resource realProperty = getDescribedProperty(property); // take the real property
			if (!hideProperties.contains(realProperty)) {
				result.add(realProperty);
			}
		}
		return result;
	}

	/**
	 * Returns the property description of a property for a lens. If no lens is provided the property itself (possibly a
	 * description) will be returned. If the property is valid for the lens, but there is no property description, the
	 * property itself will be returned. If the property isn't valid for the lens, null will be returned.
	 * 
	 * @param property the property to return the description of
	 * @param lens the lens which describes the property
	 * @return the property which is described by the given lens
	 */
	public static Resource getPropertyDescription(Resource property, Resource lens) {
		if (lens != null) {
			for (Resource pDescription : JenaUtils.getRDFResources(lens
					.getPropertyResourceValue(FRESNEL.SHOWPROPERTIES))) {
				if (property.equals(pDescription)
						|| (isPropertyDescription(pDescription) && pDescription.hasProperty(FRESNEL.PROPERTY, property))) {
					return pDescription;
				}
			}
		} else {
			return property;
		}
		return null;
	}

	/**
	 * Returns whether a resource is a property description.
	 * 
	 * @param resource the resource to check
	 * @return true if the resource is a property description, false otherwise
	 */
	public static boolean isPropertyDescription(Resource resource) {
		return resource.hasProperty(RDF.type, FRESNEL.PROPERTYDESCRIPTION);
	}

	/**
	 * Determines the formatting properties of a property for a lens by prioritizing all applicable Formats.
	 * 
	 * @param property the property to determine the formatting properties for
	 * @param lens the lens to take into account (can be null)
	 * @return the prioritized formatting properties of a property
	 */
	public static PropertyMap getFormattingProperties(Resource property, Resource lens) {
		return getFormattingProperties(property, lens, true);
	}

	/**
	 * Determines the formatting properties of a property for a lens by prioritizing all applicable Formats.
	 * 
	 * @param property the property to determine the formatting properties for
	 * @param lens the lens to take into account (can be null)
	 * @param handleGroups whether or not to take Group properties into account
	 * @return the prioritized formatting properties of a property
	 */
	public static PropertyMap getFormattingProperties(Resource property, Resource lens, boolean handleGroups) {
		PropertyMap result = new PropertyMap();

		List<Set<Resource>> prioritizedFormats = getPrioritizedFormatsAndGroups(property, lens);

		// register every property of every format in a prioritized way
		// (properties of formats with higher priority override those of lower priority)
		Set<Resource> applicableProperties = null;
		for (int i = 0; i < prioritizedFormats.size(); i++) {
			Set<Resource> formatsAndGoups = prioritizedFormats.get(i);
			if (handleGroups && i == GROUP_PRIORITY) {
				// take only properties of a Group, which are applicable to a Property
				applicableProperties = new HashSet<Resource>(Arrays.asList(PROPERTY_STYLE_PROPERTIES));
			} else if (i != GROUP_PRIORITY) {
				// take only properties of a Format, which are applicable to a Property
				applicableProperties = new HashSet<Resource>(Arrays.asList(FORMAT_PROPERTIES));
			} else {
				// This is a group while handlGroups is false, so forget it
				continue;
			}
			// now, take every applicable property and define it in the Map (possibly overriding a previous one)
			assembleProperties(result, formatsAndGoups, applicableProperties);
		}
		return result;
	}

	/**
	 * Determines the formatting properties of a Lens.
	 * 
	 * @param lens the Lens for which to determine the formattingProperties
	 * @return the formatting properties of a Lens
	 */
	public static PropertyMap getFormattingProperties(Resource lens) {
		PropertyMap result = new PropertyMap();
		assembleProperties(result, getGroups(lens), new HashSet<Resource>(Arrays.asList(STYLE_PROPERTIES)));
		return result;
	}

	/**
	 * Assembles a map of applicable Properties from a set of Resources. For Styling properties, this will concatenate
	 * the normalized CSS Strings. For other properties, this will only keep the last (most specific) one.
	 * 
	 * @param assembly the assembly to fill
	 * @param resources the resources from which to assemble the properties
	 * @param applicableProperties the Set of applicable properties to assemble
	 */
	private static void assembleProperties(Map<Resource, List<RDFNode>> assembly, Set<Resource> resources,
			Set<Resource> applicableProperties) {
		List<Resource> styleProperties = new ArrayList<Resource>(Arrays.asList(STYLE_PROPERTIES));
		Set<Resource> preAssembledProperties = new HashSet<Resource>(assembly.keySet());
		Set<Resource> seenThisIteration = new HashSet<Resource>();
		for (Resource resource : resources) {
			for (Statement statement : resource.listProperties().toList()) {
				Resource formatProperty = statement.getPredicate();
				RDFNode object = statement.getObject();
				if (applicableProperties.contains(formatProperty)) {
					// first, check if there's already a list of nodes present for this property
					List<RDFNode> values = null;
					if (assembly.containsKey(formatProperty)) {
						values = assembly.get(formatProperty);
					} else {
						// create a new list to fill for this property
						values = new ArrayList<RDFNode>();
						assembly.put(formatProperty, values);
					}
					if (styleProperties.contains(formatProperty)) {
						// Styling properties will be concatenated, so at any time, at most one RDFNode will be defined
						Literal literal = object.asLiteral(); // Every styling object is a literal
						String typeURI = literal.getDatatypeURI();
						String sourceCSS = literal.getString(); // The literal contains CSS (or should)
						String normalizedCSS = normalizeCSS(sourceCSS); // Normalize the CSS
						if (values.size() > 0) {
							String oldStyle = values.get(0).asLiteral().getString();
							values.remove(0); // remove the old style, to replace it with the concatenation
							object = object.getModel().createTypedLiteral(oldStyle + " " + normalizedCSS, typeURI);
						} else if (!sourceCSS.equals(normalizedCSS)) { // Is it necessary to create a new object?
							object = object.getModel().createTypedLiteral(normalizedCSS, typeURI);
						}
					} else {
						if (!seenThisIteration.contains(formatProperty)) {
							// if the property wasn't handled yet by this iteration of assembleProperties
							if (preAssembledProperties.contains(formatProperty)) {
								// clear property lists from lower priorities (so only the first time)
								assembly.get(formatProperty).clear();
							}
							seenThisIteration.add(formatProperty); // register our property
						}
					}
					values.add(object); // Add the object to the list of RDFNodes
				}
			}
		}
	}

	/**
	 * Returns all Groups and Formats which are applicable for a Property on a Lens, in a prioritized way.
	 * 
	 * @see JenaFresnelUtils#getNrOfFormatPriorities(int)
	 * @param property the property to determine the formatting properties for
	 * @param lens the lens to take into account (can be null)
	 * @return the prioritized formatting properties of a property
	 */
	private static List<Set<Resource>> getPrioritizedFormatsAndGroups(Resource property, Resource lens) {
		Resource explicitFormat = null;
		// get the property description, if applicable, for it is needed for determining explicity
		Resource pDescription = getPropertyDescription(property, lens);
		Resource realProperty = property;

		// When the property is a fresnel:propertyDescription, treat it as such
		if (isPropertyDescription(pDescription)) {
			realProperty = pDescription.getPropertyResourceValue(FRESNEL.PROPERTY);
			explicitFormat = pDescription.getPropertyResourceValue(FRESNEL.USE);
		}

		// get the groups of which the lens is part of
		Set<Resource> lensGroups = getGroups(lens);

		// Calculate the number of priorities.
		int nrOfPriorities = getNrOfFormatPriorities(lensGroups.size());

		// create the prioritized format lists
		List<Set<Resource>> prioritizedFormats = new ArrayList<Set<Resource>>();
		for (int i = 0; i < nrOfPriorities; i++) {
			prioritizedFormats.add(new HashSet<Resource>());
		}

		// first, add all the Lens's groups to the list for single group matches (with no matching propertyFormatDomain)
		if (lensGroups.size() > 0) {
			prioritizedFormats.get(GROUP_PRIORITY).addAll(lensGroups);
		}

		// check every format and prioritize it
		for (ResIterator rIterator = lens.getModel().listResourcesWithProperty(RDF.type, FRESNEL.FORMAT); rIterator
				.hasNext();) {
			Resource format = rIterator.next();
			int priority = getFormatPriority(format, realProperty, lensGroups, explicitFormat);
			if (priority >= 0) { // only when the format matches...
				prioritizedFormats.get(priority).add(format); // ...add it to the set of prioritized Formats
			}
		}

		return prioritizedFormats;
	}

}
