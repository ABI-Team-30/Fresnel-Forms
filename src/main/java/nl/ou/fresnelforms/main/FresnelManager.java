package nl.ou.fresnelforms.main;

import java.io.IOException;

import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.jena.JenaFresnelModel;
import nl.ou.fresnelforms.ontology.Ontology;

/**
 * Manager Class for managing conversion from/to Apache Jena RDF Model and loading/saving them.
 */
public class FresnelManager {
	/**
	 * The last known Apache Jena Model, either built from a Fresnel class or loaded from file.
	 */
	private JenaFresnelModel jenaFresnelModel = null;

	/**
	 * Constructs a new fresnel manager with empty jenaFresnelModel.
	 */
	public FresnelManager() {
		jenaFresnelModel = new JenaFresnelModel();
	}
	
	/**
	 * @param url the url of the file.
	 * @param ontology the domain ontology
	 * @return the fresnel model
	 * @throws IOException if an IO error occurs
	 */
	public Fresnel load(String url, Ontology ontology) throws IOException {
		// create an empty Jena model
		jenaFresnelModel = new JenaFresnelModel();
		// load it from the given url
		jenaFresnelModel.load(url);
		// Convert it to our FresnelForms model
		return jenaFresnelModel.asFresnel(ontology);
	}

	/**
	 * Saves the fresnel model using Apache Jena.
	 * 
	 * @param fresnel the fresnel model
	 * @param url the url of the file
	 * 
	 * @throws IOException when the file could not be created correctly
	 */
	public void save(Fresnel fresnel, String url) throws IOException {
		jenaFresnelModel = getJenaFresnelModel(fresnel);
		// save the (merged) Jena model
		jenaFresnelModel.save(url);
	}

	/**
	 * Returns the Jena Fresnel model which represents the internal Fresnel Forms model.
	 * 
	 * @param fresnel the fresnel model
	 * @return The Jena Fresnel model which represents the internal Fresnel Forms model
	 * 
	 */
	public JenaFresnelModel getJenaFresnelModel(Fresnel fresnel) {
		// merge the info from FresnelForms with the Jena model
		return jenaFresnelModel.mergeFresnel(fresnel);
	}

}