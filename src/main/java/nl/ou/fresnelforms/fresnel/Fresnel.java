package nl.ou.fresnelforms.fresnel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.ou.fresnelforms.ontology.Class;
import nl.ou.fresnelforms.ontology.Ontology;

/**
 * The Fresnel ontology model class.
 *
 */
public class Fresnel {

	private Ontology ontology;
	private Map<String, Lens> lensesByUri;
	private Map<Class, Lens> lensesByClass;

	/**
	 * Fresnel constructor.
	 */
	public Fresnel() {
		this.lensesByUri = new HashMap<String, Lens>();
		this.lensesByClass = new HashMap<Class, Lens>();
		this.ontology = null;
	}

	/**
	 * Fresnel constructor.
	 * 
	 * @param ontology domain ontology
	 */
	public Fresnel(Ontology ontology) {
		this.ontology = ontology;
		this.lensesByUri = new HashMap<String, Lens>();
		this.lensesByClass = new HashMap<Class, Lens>();
		List<nl.ou.fresnelforms.ontology.Class> classes = ontology.getClasses();
		for (nl.ou.fresnelforms.ontology.Class c : classes) {
			Lens lens = new Lens(this, c);
			addLens(lens);
		}
		/* Init generalizations */
		for (Lens lens : lensesByUri.values()) {
			List<Class> parentClasses = lens.getClassLensDomain().getParents();
			for (Class parentClass : parentClasses) {
				lens.addParent(getLensForClassLensDomain(parentClass));
			}
		}
	}

	/**
	 * @return domain ontology
	 */
	public Ontology getOntology() {
		return this.ontology;
	}

	/**
	 * @return the fresnel lenses.
	 */
	public Collection<Lens> getLenses() {
		return lensesByUri.values();
	}

	/**
	 * @return the visible lenses
	 */
	public ArrayList<Lens> getDisplayedLenses() {
		ArrayList<Lens> displayedLenses = new ArrayList<Lens>();
		for (Lens lens : getLenses()) {
			if (lens.isDisplayed()) {
				displayedLenses.add(lens);
			}
		}
		return displayedLenses;
	}

	/**
	 * @return the hidden lenses
	 */
	public ArrayList<Lens> getHiddenLenses() {
		ArrayList<Lens> hiddenLenses = new ArrayList<Lens>();
		for (Lens lens : getLenses()) {
			if (!lens.isDisplayed()) {
				hiddenLenses.add(lens);
			}
		}
		return hiddenLenses;
	}

	/**
	 * @param lens the lens to be added list
	 */
	public void addLens(Lens lens) {
		lensesByUri.put(lens.getURI(), lens);
		Class classLensDomain = lens.getClassLensDomain();
		if (classLensDomain != null) {
			lensesByClass.put(lens.getClassLensDomain(), lens);
		}
	}

	/**
	 * @param lens the lens to be removed
	 */
	public void removeLens(Lens lens) {
		lensesByUri.remove(lens.getURI());
	}

	/**
	 * @param classLensDomain the class from the domain
	 * @return the lens for this domain class.
	 */
	public Lens getLensForClassLensDomain(Class classLensDomain) {
		return lensesByClass.get(classLensDomain);
	}

	/**
	 * @param uri the uri of the lens
	 * @return the lens with the given name
	 */
	public Lens getLens(String uri) {
		return lensesByUri.get(uri);
	}
}
