package nl.ou.fresnelforms.fresnel;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import nl.ou.fresnelforms.ontology.Class;
import nl.ou.fresnelforms.ontology.DatatypeProperty;
import nl.ou.fresnelforms.ontology.ObjectProperty;
import nl.ou.fresnelforms.view.LensBox;

/**
 * The lens class.
 *
 */
public class Lens extends NamedObject {

	private Class classLensDomain;
	private ArrayList<Property> properties;
	private ArrayList<PropertyBinding> propertyBindings;
	private ArrayList<Lens> parents;
	private LensBox lensBox;
	private boolean displayed = true;
	private Point2D savedPosition = new Point2D.Double(0, 0);
	private FresnelStyle fresnelStyle;

	/**
	 * The lens class constructor.
	 * 
	 * @param fresnel the Fresnel model to which this lens belongs
	 * @param classLensDomain the domain class for this lens
	 */
	public Lens(Fresnel fresnel, Class classLensDomain) {
		super(fresnel, classLensDomain.getURI());
		this.classLensDomain = classLensDomain;
		this.properties = new ArrayList<Property>();
		this.propertyBindings = new ArrayList<PropertyBinding>();
		this.parents = new ArrayList<Lens>();
		int index = 0;
		for (DatatypeProperty d : classLensDomain.getDataProperties()) {
			Property property = new Property(fresnel, d);
			properties.add(property);
			propertyBindings.add(new PropertyBinding(property, this, index));
			index++;
		}
		for (ObjectProperty o : classLensDomain.getObjectProperties()) {
			Property property = new Property(fresnel, o);
			properties.add(property);
			propertyBindings.add(new PropertyBinding(property, this, index));
			index++;
		}
		this.fresnelStyle = new FresnelStyle();
	}

	/**
	 * The lens constructor.
	 * 
	 * @param fresnel the Fresnel model to which this lens belongs
	 * @param uri the name of the lens.
	 */
	public Lens(Fresnel fresnel, String uri) {
		super(fresnel, uri);
		this.classLensDomain = null;
		this.properties = new ArrayList<Property>();
		this.propertyBindings = new ArrayList<PropertyBinding>();
		this.parents = new ArrayList<Lens>();
	}

	/**
	 * @return the lens domain class
	 */
	public Class getClassLensDomain() {
		return classLensDomain;
	}

	/**
	 * @return the lens's name from a fresnel perspective
	 */
	public String getFresnelName() {
		return getEncodedName() + "Lens"; // return a unique Lensname (prefixed & uri-encoded)
	}

	/**
	 * @param property the dataobject property for the lens
	 */
	public void addProperty(Property property) {
		properties.add(property);
	}

	/**
	 * @return the data/object properties of this lens
	 */
	public ArrayList<Property> getProperties() {
		return properties;
	}

	/**
	 * @return the object properties bindings of this lens
	 */
	public ArrayList<PropertyBinding> getPropertyBindings() {
		Collections.sort(propertyBindings, new PropertyBindingIndexComparator());
		return propertyBindings;
	}

	/**
	 * @param parent the parent of this lens
	 */
	public void addParent(Lens parent) {
		parents.add(parent);
		@SuppressWarnings("unchecked")
		ArrayList<Lens> localParents = (ArrayList<Lens>) parents.clone();
		ArrayList<Lens> coveredParents = new ArrayList<Lens>();
		int index = this.propertyBindings.size();
		int size = localParents.size();

		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Lens localParent = localParents.get(i);
				if (!coveredParents.contains(localParent)) {
					for (Property parentProperty : localParent.getProperties()) {
						PropertyBinding propertyBinding = new PropertyBinding(parentProperty, this, index);
						if (!propertyBindings.contains(propertyBinding)) {
							propertyBindings.add(propertyBinding);
							index++;
						}
					}
					localParents.addAll(localParent.getParents());
					coveredParents.add(localParent);
					size = localParents.size();
				}
			}
		}
	}

	/**
	 * @param uri the uri of the property
	 * @return the object property binding for the property with the given name
	 */
	public PropertyBinding getPropertyBinding(String uri) {
		for (PropertyBinding pb : propertyBindings) {
			if (pb.getProperty().getURI().equals(uri)) {
				return pb;
			}
		}
		return null;
	}

	/**
	 * @return the parents of this lens
	 */
	public ArrayList<Lens> getParents() {
		return parents;
	}

	/**
	 * @param lensBox the lensbox for this lens
	 */
	public void setLensBox(LensBox lensBox) {
		this.lensBox = lensBox;
	}

	/**
	 * @return the lensbox for this lens
	 */
	public LensBox getLensBox() {
		return lensBox;
	}

	/**
	 * @return true if lens is visible
	 */
	public boolean isDisplayed() {
		return displayed;
	}

	/**
	 * @param displayed true if the lens should be visible
	 */
	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	/**
	 * @return the fresnel style def. for this lens
	 */
	public FresnelStyle getFresnelStyle() {
		return this.fresnelStyle;
	}

	/**
	 * @return the objectproperties bindings that are visible
	 */
	public ArrayList<PropertyBinding> getShowPropertyBindings() {
		ArrayList<PropertyBinding> showProperties = new ArrayList<PropertyBinding>();
		for (PropertyBinding p : propertyBindings) {
			if (p.isShown()) {
				showProperties.add(p);
			}
		}
		Collections.sort(showProperties, new PropertyBindingIndexComparator());
		return showProperties;
	}

	/**
	 * @return the objectproperties bindings that are not visible
	 */
	public ArrayList<PropertyBinding> getHidePropertyBindings() {
		ArrayList<PropertyBinding> hideProperties = new ArrayList<PropertyBinding>();
		for (PropertyBinding p : propertyBindings) {
			if (!p.isShown()) {
				hideProperties.add(p);
			}
		}
		Collections.sort(hideProperties, new PropertyBindingIndexComparator());
		return hideProperties;
	}

	/**
	 * @return the data properties of this lens
	 */
	public ArrayList<Property> getDataProperties() {
		ArrayList<Property> dataProperties = new ArrayList<Property>();
		for (Property p : properties) {
			if (p.getProperty() instanceof DatatypeProperty) {
				dataProperties.add(p);
			}
		}
		return dataProperties;
	}

	/**
	 * @return the object properties of this lens
	 */
	public ArrayList<Property> getObjectProperties() {
		ArrayList<Property> objectProperties = new ArrayList<Property>();
		for (Property p : properties) {
			if (p.getProperty() instanceof ObjectProperty) {
				objectProperties.add(p);
			}
		}
		return objectProperties;
	}

	/**
	 * @return the property bindings with style formatting
	 */
	public ArrayList<PropertyBinding> getFormattedPropertyBindings() {
		ArrayList<PropertyBinding> formattedPropertyBindings = new ArrayList<PropertyBinding>();
		for (PropertyBinding p : propertyBindings) {
			if (p.isFormatted()) {
				formattedPropertyBindings.add(p);
			}
		}
		return formattedPropertyBindings;
	}

	/**
	 * @return the number of anchestors
	 */
	public int getNrOfAncestors() {
		if (parents.size() > 0) {
			int max = 0;
			for (Lens parent : parents) {
				if (parent.getNrOfAncestors() > max) {
					max = parent.getNrOfAncestors();
				}
			}
			return 1 + max;
		} else {
			return 0;
		}
	}

	/**
	 * @param position x,y position of the lens
	 */
	public void setSavedPosition(Point2D position) {
		this.savedPosition = position;
	}

	/**
	 * @return point of the x,y postion of the lens
	 */
	public Point2D getSavedPosition() {
		return savedPosition;
	}

	/**
	 * @param pBinding the binding to be added.
	 */
	public void addPropertyBinding(PropertyBinding pBinding) {
		properties.add(pBinding.getProperty());
		for (PropertyBinding pb : propertyBindings) {
			if (pb.getIndex() >= pBinding.getIndex()) {
				int old = pb.getIndex();
				pb.setIndex(old + 1);
			}
		}
		propertyBindings.add(pBinding);
	}

	/*
	 * Added 02122014 TT (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result;
		if (getURI() != null) {
			result += getURI().hashCode();
		}
		if (classLensDomain != null) {
			result += classLensDomain.hashCode();
		}
		if (lensBox != null) {
			result += lensBox.hashCode();
		}
		return result;
	}

	/*
	 * Added 02122014 TT (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Lens other = (Lens) obj;
		if (getURI() == null) {
			if (other.getURI() != null) {
				return false;
			}
		} else if (!getURI().equals(other.getURI())) {
			return false;
		}
		return true;
	}

}
