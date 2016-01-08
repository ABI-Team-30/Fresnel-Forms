package nl.ou.fresnelforms.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import nl.ou.fresnelforms.ontology.Class;
import nl.ou.fresnelforms.ontology.Property;

/**
 * Comparator class for property labels that compares their labels heuristically. Note: this comparator imposes
 * orderings that are inconsistent with equals
 */
public class PropertyLabelHeuristicComparator implements Comparator<PropertyLabel> {

	/**
	 * bucket constants.
	 */
	public static final int BUCKETA = 1, BUCKETB = 2, BUCKETC = 3, BUCKETD = 4, BUCKETE = 5, BUCKETF = 6, BUCKETZ = 7;

	private static final int HIGHPRIO = 1, SECPRIO = 2, LOWPRIO = 3;
	private static final String DELIMITER = " ";
	private static final String BUCKETALABELS = "name code identifying identifier label";
	private static final String BUCKETELABELS = "foaf:name foaf:nick foaf:givenname foaf:familyname foaf:family_name foaf:firstname foaf:lastname foaf:surname foaf:topic http://schema.org/alternatename http://schema.org/name dc:title dc:subject dc:identifier";
	private static final String BUCKETFLABELS = "foaf:homepage foaf:isprimarytopicof foaf:primarytopic foaf:knows foaf:made foaf:maker foaf:page foaf:gender http://schema.org/description http://schema.org/sameas http://schema.org/url http://schema.org/about http://schema.org/category http://schema.org/object dc:creator dc:description dc:type dct:alternative";
	private ArrayList<IRI> bucketBtypes = initBucketB();
	private ArrayList<IRI> rangerankdatatypes = initRangeRankDataTypes();

	private String[] inbuckethp = { "start", "begin", "birth", "former", "previous", "opening", "predecessor" };
	private String[] inbucketsp = { "stop", "end", "death", "future", "subsequent", "closing", "successor" };

	private List<Map.Entry<nl.ou.fresnelforms.ontology.Class, Integer>> rangeranklist = null;
	private HashMap<Property, Integer> propertyfrequentymap = null;

	/**
	 * Constructor with parameters.
	 * 
	 * @param rangerank the range rank list of an ontology.
	 * @param propertyfrequenties the property frequentylist of an ontology.
	 */
	public PropertyLabelHeuristicComparator(HashMap<nl.ou.fresnelforms.ontology.Class, Integer> rangerank,
			HashMap<Property, Integer> propertyfrequenties) {
		super();
		if (rangerank != null) {
			// convert the hashmap to a linkedlist
			rangeranklist = new LinkedList<Map.Entry<nl.ou.fresnelforms.ontology.Class, Integer>>(rangerank.entrySet());
			// sort the linkedlist on the values.
			ClassValueComparator comp = new ClassValueComparator();
			Collections.sort(rangeranklist, Collections.reverseOrder(comp));
		}
		if (propertyfrequenties != null) {
			propertyfrequentymap = propertyfrequenties;
		}
	}

	/**
	 * comparator of two propertylabels by their labels.
	 * 
	 * @param p1 propertylabel 1
	 * @param p2 propertylabel 2
	 * @return -1,0,1
	 */
	public int compare(PropertyLabel p1, PropertyLabel p2) {
		int p1bucket, p2bucket, inbucketprio1, inbucketprio2;
		int pfreq1, pfreq2, rangerank1, rangerank2;
		String lbl1, lbl2;
		int cmpresult = 0;
		boolean sameflg = false;

		p1bucket = getbucket(p1);
		p2bucket = getbucket(p2);
		sameflg = (p1bucket == p2bucket);
		if (sameflg) { // both label share in bucketC compare the rangerank
			if (p1bucket == BUCKETC) {
				rangerank2 = getRangeRank(p2);
				rangerank1 = getRangeRank(p1);
				sameflg = (rangerank1 == rangerank2);
				if (!sameflg) {
					if (rangerank1 > 0 && rangerank2 > 0) {
						cmpresult = (int) Math.signum(rangerank1 - rangerank2);
					} else { // one or both rangeranks are non existent (-1)
						cmpresult = (int) Math.signum(rangerank2 - rangerank1);
					}
					sameflg = false;
				}
			} else {
				// both labels are in the same bucket but not bucket C, so do some inbucket orderning.
				inbucketprio1 = getinbucketprio(p1);
				inbucketprio2 = getinbucketprio(p2);
				sameflg = (inbucketprio1 == inbucketprio2);
				if (!sameflg) { // compare the two inbucket ordering
					cmpresult = (int) Math.signum(inbucketprio1 - inbucketprio2);
				}
			}
			if (sameflg) { // more ordering is neccesary, based on property frequency
				pfreq1 = getPropertyFrequenty(p1);
				pfreq2 = getPropertyFrequenty(p2);
				if (pfreq1 == pfreq2) {
					// last ordering is on the alphabet.
					lbl1 = p1.getPropertyBinding().getLabel();
					lbl2 = p2.getPropertyBinding().getLabel();
					cmpresult = (int) Math.signum(lbl1.compareToIgnoreCase(lbl2));
				} else { // compare the frequentie ordering higer is first in order
					cmpresult = (int) Math.signum(pfreq2 - pfreq1);
				}
			}
		} else { // compare the two buckets
			cmpresult = (int) Math.signum(p1bucket - p2bucket);
		}
		return cmpresult;
	}

	/**
	 * Determine the bucket for this propertylabel.
	 * 
	 * @param pl the property label
	 * @return the bucket constant
	 */
	public int getbucket(PropertyLabel pl) {

		String label = pl.getPropertyBinding().getLabel().toLowerCase() + DELIMITER;
		String propname = pl.getPropertyBinding().getProperty().getPrefixedURI().toLowerCase() + DELIMITER;
		int bucketresult = BUCKETZ;

		// rule 1
		if (BUCKETALABELS.contains(label)) {
			bucketresult = BUCKETA;
		} else if (BUCKETELABELS.contains(propname)) {
			// rule 2
			bucketresult = BUCKETE;
		} else if (BUCKETFLABELS.contains(label)) {
			// rule 3
			bucketresult = BUCKETF;
		} else if (bucketBtypes.contains(getpropdatatypeIRI(pl))) {
			// rule 4
			bucketresult = BUCKETB;
		} else if (getRangeRank(pl) >= 0) {
			// rule 5
			bucketresult = BUCKETC;
		} else if (isFunctional(pl)) {
			// rule 6
			bucketresult = BUCKETD;
		} else {
			// rule 7
			bucketresult = BUCKETZ;
		}
		return bucketresult;
	}

	/**
	 * test if label is highpriority, second priority or no priority.
	 * 
	 * @param pl the property label
	 * @return highpriotiy, secondpriority or -1 for no priority
	 */
	private int getinbucketprio(PropertyLabel pl) {
		int prioresult = LOWPRIO;
		String label = pl.getPropertyBinding().getLabel();
		// test for high priority
		for (String term : inbuckethp) {
			if (label.contains(term)) {
				prioresult = HIGHPRIO;
				break;
			}
		}
		if (prioresult < 0) {
			// test for second priority
			for (String term : inbucketsp) {
				if (label.contains(term)) {
					prioresult = SECPRIO;
					break;
				}
			}
		}
		return prioresult;
	}

	/**
	 * Gets the property frequentie of the propertylabel.
	 * 
	 * @param pl the propertylabel
	 * @return the frequentie count for this property
	 */
	private int getPropertyFrequenty(PropertyLabel pl) {
		return propertyfrequentymap.get(pl.getProperty());
	}

	/**
	 * Gets the IRI of the xsdtype of this property
	 * 
	 * @param pl the propertylabel
	 * @return the IRI or null
	 */
	private IRI getpropdatatypeIRI(PropertyLabel pl) {
		IRI xsdIRI = null;
		Property ontprop = pl.getPropertyBinding().getProperty().getProperty();
		if (ontprop.isDatatypeProperty()) {
			xsdIRI = ontprop.asDatatypeProperty().getDataTypeIRI();
		}
		return xsdIRI;
	}

	/**
	 * Gets the range rank of the range of the property
	 * 
	 * @param pl the propertylabel
	 * @return the rangerank or -1 if the property has no range.
	 */
	private int getRangeRank(PropertyLabel pl) {
		int rangerank = -1;
		// ArrayList<DataType> dtlist;
		List<Class> rangelist, parentlist;
		Class range;
		Class keyclass;
		// handle data and object properties differently
		Property prop = pl.getProperty();
		if (prop.isDatatypeProperty()) {
			// handle datatype property
			IRI xsdIRI = getpropdatatypeIRI(pl);
			if (rangerankdatatypes.contains(xsdIRI)) {
				rangerank = 0;
			}
		} else {
			// handle objectproperty
			rangelist = prop.asObjectProperty().getRange();
			if (rangelist != null) {
				range = rangelist.get(0);
				parentlist = range.getParents();
				// search the rank
				int i = 0;
				while (i < rangeranklist.size() && rangerank < 0) {
					keyclass = rangeranklist.get(i).getKey();
					for (Class parent : parentlist) {
						if (keyclass.getURI().toString().equalsIgnoreCase(parent.getURI().toString())) {
							rangerank = i;
						}
					}
					i++;
				}
			}
		}

		return rangerank;
	}

	/**
	 * @param pl the propertylabel
	 * @return true if the property is functional or inverse functional
	 */
	private boolean isFunctional(PropertyLabel pl) {
		Property ontprop = pl.getPropertyBinding().getProperty().getProperty();
		return (ontprop.isFunctional() || ontprop.isInversefunctional());
	}

	/**
	 * @return the arraylist with the iri's of the xsd types for bucketB
	 */
	private ArrayList<IRI> initBucketB() {
		ArrayList<IRI> xsditems = new ArrayList<IRI>();
		xsditems.add(XSDVocabulary.DATE.getIRI());
		xsditems.add(XSDVocabulary.DATE_TIME.getIRI());
		xsditems.add(XSDVocabulary.TIME.getIRI());
		xsditems.add(XSDVocabulary.G_YEAR_MONTH.getIRI());
		xsditems.add(XSDVocabulary.G_YEAR.getIRI());
		xsditems.add(XSDVocabulary.G_MONTH_DAY.getIRI());
		xsditems.add(XSDVocabulary.G_MONTH.getIRI());
		xsditems.add(XSDVocabulary.G_DAY.getIRI());
		return xsditems;
	}

	/**
	 * @return The arraylist with the xsd dattypes with rangerank 0.
	 */
	private ArrayList<IRI> initRangeRankDataTypes() {
		ArrayList<IRI> xsditems = new ArrayList<IRI>();
		xsditems.add(XSDVocabulary.BYTE.getIRI());
		xsditems.add(XSDVocabulary.DOUBLE.getIRI());
		xsditems.add(XSDVocabulary.FLOAT.getIRI());
		xsditems.add(XSDVocabulary.DECIMAL.getIRI());
		xsditems.add(XSDVocabulary.BOOLEAN.getIRI());
		xsditems.add(XSDVocabulary.INT.getIRI());
		xsditems.add(XSDVocabulary.SHORT.getIRI());
		xsditems.add(XSDVocabulary.STRING.getIRI());
		xsditems.add(XSDVocabulary.UNSIGNED_BYTE.getIRI());
		xsditems.add(XSDVocabulary.UNSIGNED_INT.getIRI());
		xsditems.add(XSDVocabulary.UNSIGNED_LONG.getIRI());
		xsditems.add(XSDVocabulary.UNSIGNED_SHORT.getIRI());
		xsditems.add(XSDVocabulary.NON_NEGATIVE_INTEGER.getIRI());
		xsditems.add(XSDVocabulary.NEGATIVE_INTEGER.getIRI());
		xsditems.add(XSDVocabulary.NON_POSITIVE_INTEGER.getIRI());
		xsditems.add(XSDVocabulary.POSITIVE_INTEGER.getIRI());
		xsditems.add(XSDVocabulary.DURATION.getIRI());
		return xsditems;
	}

	/**
	 * Inner comparator class for the map.entr array sorting.
	 */
	class ClassValueComparator implements Comparator<Map.Entry<nl.ou.fresnelforms.ontology.Class, Integer>> {

		/**
		 * Compare two map entries.
		 * 
		 * @param o1 object 1 Map.Entry<Class, Integer>
		 * @param o2 object 2 Map.Entry<Class, Integer>
		 * @return -1 of o1 <o2, 1 if o1>o2 otherwise 0
		 */
		public int compare(Map.Entry<nl.ou.fresnelforms.ontology.Class, Integer> o1,
				Map.Entry<nl.ou.fresnelforms.ontology.Class, Integer> o2) {
			return (o1.getValue()).compareTo(o2.getValue());
		}
	}

	/**
	 * Inner comparator class for the map.entr array sorting.
	 */
	class PropertyValueComparator implements Comparator<Map.Entry<Property, Integer>> {

		/**
		 * Compare two map entries.
		 * 
		 * @param o1 object 1 Map.Entry<Property, Integer>
		 * @param o2 object 2 Map.Entry<Property, Integer>
		 * @return -1 of o1 <o2, 1 if o1>o2 otherwise 0
		 */
		public int compare(Map.Entry<Property, Integer> o1, Map.Entry<Property, Integer> o2) {
			return (o1.getValue()).compareTo(o2.getValue());
		}
	}

}
