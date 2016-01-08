/**
 * 
 */
package nl.ou.fresnelforms.fresnelequalitytest;

import static org.junit.Assert.*;
import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresnel.Property;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unittest for the property object.
 * 
 * @author jn.theunissen@studie.ou.nl
 *
 */
public class PropertyEqualityTest {
	private static Property p1 = null;
	private static Property p2 = null;
	private static Property p3 = null;
	private static Property p4 = null;
	private static Property p5 = null;

	/**
	 * Sets up the property's to test.
	 * 
	 * @throws java.lang.Exception any exceptions that are generated.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Fresnel fresnel = new Fresnel();
		// setup 2 different property's (p1,p2)) and 2 the same property's (p1,p3) and 2 null properties. (p4,p5)
		p1 = new Property(fresnel, "news:comp.lang.java#Property1");
		p2 = new Property(fresnel, "http://java.sun.com/j2se/1.3/#Property2");
		p3 = new Property(fresnel, "news:comp.lang.java#Property1");
	}

	/**
	 * Test the non-null property's Test method for {@link nl.ou.fresnelforms.fresnel.Property#hashCode()}.
	 */
	public void testHashCode() {
		// equal properties have equal hascodes.
		assertTrue("Nonequal Hashcode of property detected.", p1.hashCode() == p1.hashCode());
		assertTrue("Nonequal Hashcode of equal properties detected.", p1.hashCode() == p3.hashCode());
		// non equal properties have non equal hascodes.
		assertFalse("Equal Hashcode of non equal properties detected.", p1.hashCode() == p2.hashCode());
	}

	/**
	 * Test the null property's Test method for {@link nl.ou.fresnelforms.fresnel.Property#hashCode()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testNullHashCode() {
		// equal properties have equal hascodes.
		assertTrue("Nonequal Hashcode of null properties detected.", p4.hashCode() == p5.hashCode());
		// non equal properties have non equal hascodes.
		assertFalse("Equal Hashcode of null and non-null properties detected.", p1.hashCode() == p4.hashCode());
	}

	/**
	 * Test the non-null property's Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		String test = "news:comp.lang.java";
		// equal properties.
		assertTrue("Nonequality of property detected.", p1.equals(p1));
		assertTrue("Nonequality of equal properties detected.", p1.equals(p3));
		// non equal properties.
		assertFalse("Equality of nonequal properties detected.", p1.equals(p2));
		assertFalse("Equality of different class and a property detected.", p1.equals(test));
	}

	/**
	 * Test the null property's Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */
	@Test(expected = NullPointerException.class)
	public void testEqualsNullObject() {
		// equal properties.
		assertTrue("Nonequality of null properties detected.", p4.equals(p5));
		// non equal properties.
		assertFalse("Equality of null and non null properties detected.", p1.equals(p4));
	}

}
