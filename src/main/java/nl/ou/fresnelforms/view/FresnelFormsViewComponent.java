package nl.ou.fresnelforms.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.main.Controller;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import javax.swing.JOptionPane;

/**
 * Main view component of the MDD Fresnel plugin.
 */
public class FresnelFormsViewComponent extends AbstractOWLViewComponent implements OWLModelManagerListener {
	private static final int BORDER_WIDTH = 600;
	private static final long serialVersionUID = -4515710047558710080L;

	/** default diagram width. */
	public static final int DIAGRAM_WIDTH = 10000;
	/** default diagram height. */
	public static final int DIAGRAM_HEIGHT = 10000;

	private static final Logger LOG = Logger.getLogger(FresnelFormsViewComponent.class);
	private OWLModelManager manager;
	private Controller controller;
	private LensDiagram lensDiagram;
	private JScrollPane scrollPane;

	@Override
	protected void initialiseOWLView() {
		manager = getOWLModelManager();
		manager.addListener(this); // Add this view as listener for Protégé's events

		setLayout(new BorderLayout());
		LOG.info("Fresnel Forms View Component initialized");

		controller = new Controller(manager);

		JPanel menu = new JPanel();
		menu.setLayout(new FlowLayout());
		this.add(menu, BorderLayout.NORTH);

		JButton defaultBtn = new JButton("Default Fresnel");
		defaultBtn.addActionListener(new DefaultFresnelListener());
		menu.add(defaultBtn);

		JButton saveBtn = new JButton("Save Fresnel");
		saveBtn.addActionListener(new SaveFresnelListener());
		menu.add(saveBtn);

		JButton loadBtn = new JButton("Load Fresnel");
		loadBtn.addActionListener(new LoadFresnelListener());
		menu.add(loadBtn);

		JButton wikiBtn = new JButton("Save Wiki");
		wikiBtn.addActionListener(new SaveWikiListener());
		menu.add(wikiBtn);
		
	}

	/**
	 * Listener for Default Fresnel action.
	 */
	private class DefaultFresnelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// check if there is an active model, if so ask user
			// if it is ok to overwrite.
			if (confirmReplaceActiveModel()) {
				try {
					setBusyCursor();
					// create a new controller for the ontology may have changed
					controller = new Controller(manager);
					// generate a default fresnel
					controller.generateDefaultFresnel();
					initDiagram();
					lensDiagram.resetLensPositions(); 
				} finally {
					setDefaultCursor(); 
				}
			}
		}
	}

	/**
	 * Listener for Load Fresnel action.
	 */
	private class LoadFresnelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// check if there is an active model, if so ask user
			// if it is ok to overwrite.
			if (confirmReplaceActiveModel()) {
				try {
					setBusyCursor();
					// load a fresnel file.
					controller.loadFresnel();
					initDiagram();
				} finally {
					setDefaultCursor(); 
				}
			}
		}
	}

	/**
	 * Listener for Save Fresnel action.
	 */
	private class SaveFresnelListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			controller.saveFresnel();
		}
	}

	/**
	 * Listener for Save Wiki action.
	 */
	private class SaveWikiListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			controller.saveWiki();
		}
	}

	/**
	 * Check if there is a current fresnel model. is so ask the user if it is ok to be overwritten.
	 * 
	 * @return true if the model may be overwritten otherwise false
	 */
	private boolean confirmReplaceActiveModel() {
		boolean resultflg = true;
		// check if there is an active model, if so ask user for confirmation.
		if (!controller.isFresnelEmpty()) {
			if (JOptionPane.showConfirmDialog(null, "This will overwrite the current fresnel, are you sure?",
					"Question", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
				// break out of this method
				resultflg = false;
			}
		}
		return resultflg;
	}

	/**
	 * init the diagram.
	 */
	public void initDiagram() {
		Fresnel fresnel = controller.getFresnel();
		Dimension dim = new Dimension();
		
		dim.width = this.getWidth(); //DIAGRAM_WIDTH;
		dim.height = this.getHeight(); //DIAGRAM_HEIGHT;
		if (scrollPane != null) {
			this.remove(scrollPane);
		}
		lensDiagram = new LensDiagram(fresnel, dim);
		lensDiagram.setBackground(Color.WHITE);
		lensDiagram.setPreferredSize(dim);
		scrollPane = new JScrollPane(lensDiagram);
		this.add(scrollPane);
		Dimension size = scrollPane.getViewport().getViewSize();
		int x = (size.width) / 2 - BORDER_WIDTH;
		scrollPane.getViewport().setViewPosition(new Point(x, 0));
		this.paintAll(getGraphics());
	}

	@Override
	protected void disposeOWLView() {
		manager.removeListener(this);
	}

	/**
	 * this method sets a busy cursor
	 */
	private void setBusyCursor(){
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	/**
	 * this method sets the default cursor
	 */
	private void setDefaultCursor(){
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * Listener method for listening to Protégé events.
	 * 
	 * @see org.protege.editor.owl.model.event.OWLModelManagerListener#handleChange(org.protege.editor.owl.model.event.OWLModelManagerChangeEvent)
	 * @param event The event
	 **/
	public void handleChange(OWLModelManagerChangeEvent event) {
		switch (event.getType()) {
		case ACTIVE_ONTOLOGY_CHANGED: // The active ontology has changed
			controller = new Controller(manager);
			initDiagram();
			break;
		default:
			break;
		}
	}
}
