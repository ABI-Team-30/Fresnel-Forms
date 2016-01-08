/**
 * 
 */
package nl.ou.fresnelforms.jena;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import nl.ou.fresnelforms.fresnel.FresnelStyle;
import nl.ou.fresnelforms.fresnel.FresnelStyleClass;
import nl.ou.fresnelforms.fresnel.Lens;
import nl.ou.fresnelforms.fresnel.PropertyBinding;
import nl.ou.fresnelforms.vocabulary.FRESNEL;
import nl.ou.fresnelforms.vocabulary.OWF;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Adapter of the interface of a Lens to interaction with a Jena Model.
 *
 */
public class JenaLensAdapter {

	private Lens lens;

	/**
	 * Creates an adapter to adapt the interface of a Lens to interaction with a Jena Model.
	 * 
	 * @param lens the Lens to adapt
	 */

	public JenaLensAdapter(Lens lens) {
		this.lens = lens;
	}

	/**
	 * Fills a Lens from a Jena Resource.
	 * 
	 * @param resource the Jena Resource to use
	 */
	void fillFromResource(Resource resource) {
		List<Resource> showProperties = new ArrayList<Resource>();
		List<Resource> hideProperties = new ArrayList<Resource>();

		lens.setDisplayed(true);
		
		for (Statement lensstmt : resource.listProperties().toList()) {

			Property property = lensstmt.getPredicate();
			RDFNode object = lensstmt.getObject();

			if (FRESNEL.SHOWPROPERTIES.equals(property)) {
				showProperties = JenaUtils.getRDFResources(object);
			} else if (FRESNEL.HIDEPROPERTIES.equals(property)) {
				hideProperties = JenaUtils.getRDFResources(object);
			} else if (FRESNEL.GROUP.equals(property)) {
				fillFresnelStyleFromResource(lens.getFresnelStyle(), object.asResource());
			} else if (OWF.XPOS.equals(property)) {
				Point2D p = lens.getSavedPosition();
				lens.setSavedPosition(new Point2D.Double(object.asLiteral().getInt(), p.getY()));
			} else if (OWF.YPOS.equals(property)) {
				Point2D p = lens.getSavedPosition();
				lens.setSavedPosition(new Point2D.Double(p.getX(), object.asLiteral().getInt()));
			}
		}

		// Now that all properties are handled, create hideable lensproperties from them
		fillPropertyBindingsFromResources(showProperties, resource);
		hidePropertyBindingsFromResources(hideProperties);

	}

	/**
	 * Hides a FresnelForms Lens's properties according to RDF Resources.
	 * 
	 * @param resources the Resources to treat as hideProperties's objects.
	 * @param lens the Fresnel Lens to hide the properties for
	 */
	private void hidePropertyBindingsFromResources(List<Resource> resources) {
		for (Resource resource : resources) {
			PropertyBinding propertyBinding = lens.getPropertyBinding(resource.getURI());
			if (propertyBinding != null) {
				propertyBinding.setShown(false); // override the property's shown status
			}
		}
	}

	/**
	 * Fills the PropertyBindings for a Lens from given resources.
	 * 
	 * @param properties the Property Resources to use
	 * @param lensResource the Lens Resource to use
	 */
	private void fillPropertyBindingsFromResources(List<Resource> properties, Resource lensResource) {
		int index = 0;

		for (Resource resource : properties) {
			// For every property, retrieve the equivalent PropertyBinding from our Fresnel Model
			PropertyBinding propertyBinding = null;
			if (resource.isAnon()) {
				// An anonymous resource contains a property description
				Resource property = resource.getPropertyResourceValue(FRESNEL.PROPERTY);
				if (property != null) {
					propertyBinding = lens.getPropertyBinding(property.getURI());
				}
			} else {
				propertyBinding = lens.getPropertyBinding(resource.getURI());
			}

			// Create the PropertyBinding if it couldn't be found
			if (propertyBinding == null) {
				propertyBinding = new PropertyBinding(new nl.ou.fresnelforms.fresnel.Property(lens.getFresnel(),
						resource.getURI()), lens, index);
				lens.addPropertyBinding(propertyBinding);
			}

			// Now we have a PropertyBinding, we can format it, according to the RDF resources
			JenaPropertyBindingAdapter pbAdapter = new JenaPropertyBindingAdapter(propertyBinding,
					lensResource.getModel());
			pbAdapter.format(lensResource, resource);

			// By default, a property will be shown
			propertyBinding.setShown(true);
			propertyBinding.setIndex(index);
			index++;
		}

	}

	/**
	 * Fills a FresnelStyle from the properties concerning a resource.
	 * 
	 * @param fresnelStyle the FresnelStyle to fill
	 * @param resource the resource to search for styling information
	 */
	private void fillFresnelStyleFromResource(FresnelStyle fresnelStyle, Resource resource) {
		for (Statement statement : resource.listProperties().toList()) {
			Property property = statement.getPredicate();
			RDFNode object = statement.getObject();
			if (FRESNEL.RESOURCESTYLE.equals(property)) {
				fresnelStyle.setFresnelStyle(FresnelStyleClass.RESOURCE_STYLE, object.asLiteral().getString());
			} else if (FRESNEL.PROPERTYSTYLE.equals(property)) {
				fresnelStyle.setFresnelStyle(FresnelStyleClass.PROPERTY_STYLE, object.asLiteral().getString());
			} else if (FRESNEL.LABELSTYLE.equals(property)) {
				fresnelStyle.setFresnelStyle(FresnelStyleClass.LABEL_STYLE, object.asLiteral().getString());
			} else if (FRESNEL.VALUESTYLE.equals(property)) {
				fresnelStyle.setFresnelStyle(FresnelStyleClass.VALUE_STYLE, object.asLiteral().getString());
			}
		}
	}

}
