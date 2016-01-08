package nl.ou.fresnelforms.main;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Fresnel file filter class.
 */
public class FresnelFileFilter extends FileFilter {

	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		String extension = null;
		String s = file.getName();
		int i = s.lastIndexOf('.');
		if (i > 0 &&  i < s.length() - 1) {
			extension = s.substring(i+1).toLowerCase();
		}
		if (extension != null) {
	        if ( "n3".equals(extension) ||
	        	 "fresnel".equals(extension)) {
	              return true;
	        }
	    }
		return false;
	}

	@Override
	public String getDescription() {
		return "n3, fresnel";
	}

}
