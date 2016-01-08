package nl.ou.fresnelforms.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresneltowiki.Fresnel2wiki;
import nl.ou.fresnelforms.ontology.Ontology;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;

import com.hp.hpl.jena.util.FileUtils;

/**
 * Main class of the plugin.
 * 
 */
public class Controller {
	private Ontology ontology;
	private Fresnel fresnel;
	private FresnelManager fresnelManager;

	private static Logger log = LogManager.getLogger(Controller.class);

	/**
	 * The Controller constructor.
	 * 
	 * @param manager the manager
	 */
	public Controller(OWLModelManager manager) {
		ontology = OWLImport.getActiveOntology(manager);
		fresnelManager = new FresnelManager();
		if (ontology == null) {
			log.warn("ontology is null");
		}
	}

	/**
	 * Checks if the fresnel model is empty.
	 * 
	 * @return true if the fresnelmode is empty
	 */
	public boolean isFresnelEmpty() {
		return (fresnel == null);
	}

	/**
	 * @return the ontology
	 */
	public Ontology getOntology() {
		return ontology;
	}

	/**
	 * @return The Fresnel ontology
	 */
	public Fresnel getFresnel() {
		return fresnel;
	}

	/**
	 * Handle button click save fresnel.
	 */
	public void saveFresnel() {
		JFileChooser fc = new JFileChooser();
		//show xml files
		fc.setFileFilter(new FileNameExtensionFilter(".n3, .fresnel", "n3", "fresnel"));
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// check for file extension
			if (!"n3".equalsIgnoreCase(FileUtils.getFilenameExt(file.getName())) &&
					!"fresnel".equalsIgnoreCase(FileUtils.getFilenameExt(file.getName()))) {
			    file = new File(file.toString() + ".n3");  // append .n3
			}
			String url = file.getAbsolutePath();
			try {
				log.debug("saving " + url + "...");
				fresnelManager.save(fresnel, url);
				log.debug(url + " saved");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handle button click save wiki.
	 */
	public void saveWiki() {
		JFileChooser fc = new JFileChooser();
		//show xml files
		fc.setFileFilter(new FileNameExtensionFilter(".xml", "xml"));		
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// check for file extension
			if (!"xml".equalsIgnoreCase(FileUtils.getFilenameExt(file.getName()))) {
			    file = new File(file.toString() + ".xml");  // append .xml
			}
			String url = file.getAbsolutePath();
			try {
				Writer writer = new BufferedWriter(new FileWriter(url));
				String wikiXML = Fresnel2wiki.execute(fresnelManager.getJenaFresnelModel(fresnel));
				log.debug("saving " + url + "...");
				writer.write(wikiXML);
				writer.close();
				log.debug(url + " saved");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handle button click load fresnel.
	 */
	public void loadFresnel() {
		JFileChooser fc = getFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String url = file.getAbsolutePath();
			try {
				log.debug("loading " + url + "...");
				fresnel = fresnelManager.load(url, ontology);
				log.debug(url + " loaded");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handle button click generate default fresnel.
	 */
	public void generateDefaultFresnel() {
		this.fresnel = new Fresnel(ontology);
	}

	private JFileChooser getFileChooser() {
		JFileChooser fc = new JFileChooser();
		FileFilter allFilter = fc.getAcceptAllFileFilter();
		fc.removeChoosableFileFilter(allFilter);
		fc.addChoosableFileFilter(new FresnelFileFilter());
		fc.addChoosableFileFilter(allFilter);
		return fc;
	}

}
