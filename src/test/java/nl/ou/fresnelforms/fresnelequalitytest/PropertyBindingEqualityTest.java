/**
 * 
 */
package nl.ou.fresnelforms.fresnelequalitytest;

import static org.junit.Assert.*;
import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresnel.Lens;
import nl.ou.fresnelforms.fresnel.Property;
import nl.ou.fresnelforms.fresnel.PropertyBinding;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unittest for the property object.
 * 
 * @author jn.theunissen@studie.ou.nl
 *
 */
public class PropertyBindingEqualityTest {
	private static PropertyBinding pb1 = null;
	private static PropertyBinding pb2 = null;
	private static PropertyBinding pb3 = null;
	private static PropertyBinding pb4 = null;
	private static PropertyBinding pb5 = null;

	/**
	 * Sets up the property's to test.
	 * 
	 * @throws java.lang.Exception any exceptions that are generated.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		int pindex = 1;
		Fresnel fresnel = new Fresnel();

		Property p1 = new Property(fresnel, "news:comp.lang.java#Property1");
		Property p2 = new Property(fresnel, "http://java.sun.com/j2se/1.3/#Property2");
		Lens l1 = new Lens(fresnel, "Lens 1");
		Lens l2 = new Lens(fresnel, "Lens 2");

		// setup 2 different property's (p1,p2)) and 2 the same property's (p1,p3) and 2 null properties. (p4,p5)
		pb1 = new PropertyBinding(p1, l1, pindex);
		pb2 = new PropertyBinding(p2, l2, ++pindex);
		pb3 = new PropertyBinding(p1, l1, ++pindex);
	}

	/**
	 * Test the non-null property's Test method for {@link nl.ou.fresnelforms.fresnel.Property#hashCode()}.
	 */
	public void testHashCode() {
		// equal properties have equal hascodes.
		assertTrue("Nonequal Hashcode of propertybinding detected.", pb1.hashCode() == pb1.hashCode());
		assertTrue("Nonequal Hashcode of equal propertybindings detected.", pb1.hashCode() == pb3.hashCode());
		// non equal properties have non equal hascodes.
		assertFalse("Equal Hashcode of non equal propertybindings detected.", pb1.hashCode() == pb2.hashCode());
	}

	/**
	 * Test the null propertybinding's Test method for {@link fresnel.propertybinding#hashCode()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testNullHashCode() {
		// equal properties have equal hascodes.
		assertTrue("Nonequal Hashcode of null propertybindings detected.", pb4.hashCode() == pb5.hashCode());
		// non equal properties have non equal hascodes.
		assertFalse("Equal Hashcode of null and non-null propertybindings detected.", pb1.hashCode() == pb4.hashCode());
	}

	/**
	 * Test the non-null propertybinding's Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		String test = "news:comp.lang.java";
		// equal properties.
		assertTrue("Nonequality of propertybinding detected.", pb1.equals(pb1));
		assertTrue("Nonequality of equal propertybindings detected.", pb1.equals(pb3));
		// non equal properties.
		assertFalse("Equality of nonequal propertybindings detected.", pb1.equals(pb2));
		assertFalse("Equality of different class and a propertybinding detected.", pb1.equals(test));
	}

	/**
	 * Test the null propertybinding's Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */
	@Test(expected = NullPointerException.class)
	public void testEqualsNullObject() {
		// equal properties.
		assertTrue("Nonequality of null propertybindings detected.", pb4.equals(pb5));
		// non equal properties.
		assertFalse("Equality of null and non null propertybindings detected.", pb1.equals(pb4));
	}

}
