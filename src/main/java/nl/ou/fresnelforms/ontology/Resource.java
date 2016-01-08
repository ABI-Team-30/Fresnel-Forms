package nl.ou.fresnelforms.ontology;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * The basic class for all OWL elements (Classes & Properties).
 *
 */
public class Resource {

	/**
	 * The URI of the element.
	 */
	private URI uri;

	/**
	 * Creates a basic Resource with a URI.
	 * 
	 * @param uri the URI to use
	 */
	protected Resource(String uri){
		try{
			this.uri  = new URI(uri);
		} catch (URISyntaxException e) {
			throw new ResourceURIRequiredException("URI " + getURI() 
					+ " isn't a correct URI for a resource.");
		}
	}

	/**
	 * @return the uri of the Resource as a string
	 */
	public String getURI() {
		return uri.toString();  //      uri;
	};
	
	/**
	 * @return the prefix of this uri.
	 */
	public String getPrefix(){
		return uri.getScheme();
	}
	
	/**
	 * @return the last part of the schemespecificpart of the uri 
	 */
	public String getResourceName(){
		
		String rname = uri.getFragment();
		if (rname==null || rname.length()==0){
			rname =uri.getPath();
			if (rname.contains("/")){
				rname = rname.substring(rname.lastIndexOf("/"));
				//strip off the last backslash
				rname = rname.replace("/", "");
			} 
		}
		return rname;
	}
	
}
