/**
 * 
 */
package nl.ou.fresnelforms.fresnelequalitytest;

import static org.junit.Assert.*;
import nl.ou.fresnelforms.fresnel.Fresnel;
import nl.ou.fresnelforms.fresnel.Lens;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unittest for the Lens object.
 * 
 * @author jn.theunissen@studie.ou.nl
 *
 */
public class LensEqualityTest {
	private static Lens l1 = null;
	private static Lens l2 = null;
	private static Lens l3 = null;
	private static Lens l4 = null;
	private static Lens l5 = null;

	/**
	 * Sets up the Lens's to test.
	 * 
	 * @throws java.lang.Exception any exceptions that are generated.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// setup 2 different Lens's (l1,l2)) and 2 the same Lens's (l1,l3) and 2 null properties. (p4,p5)
		Fresnel fresnel = new Fresnel();
		l1 = new Lens(fresnel, "Lens 1");
		l2 = new Lens(fresnel, "Lens 2");
		l3 = new Lens(fresnel, "Lens 1");
	}

	/**
	 * setup method called for each test.
	 * 
	 * @throws java.lang.Exception any exceptions that are generated.
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test the non-null Lens's Test method for {@link nl.ou.fresnelforms.fresnel.Lens#hashCode()}.
	 */
	public void testHashCode() {
		// equal properties have equal hascodes.
		assertTrue("Nonequal Hashcode of Lens detected.", l1.hashCode() == l1.hashCode());
		assertTrue("NonEqual Hashcode of equal lenses detected.", l1.hashCode() == l3.hashCode());
		// non equal properties have non equal hascodes.
		assertFalse("Equal Hashcode of non equal lenses detected.", l1.hashCode() == l2.hashCode());
	}

	/**
	 * Test the null Lens's Test method for {@link nl.ou.fresnelforms.fresnel.Lens#hashCode()}.
	 */
	@Test(expected = NullPointerException.class)
	public void testNullHashCode() {
		// equal properties have equal hascodes.
		assertTrue("Nonequal Hashcode of null lenses detected.", l4.hashCode() == l5.hashCode());
		// non equal properties have non equal hascodes.
		assertFalse("Equal Hashcode of null and non-null lenses detected.", l1.hashCode() == l4.hashCode());
	}

	/**
	 * Test the non-null Lens's Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		String test = "news:comp.lang.java";
		// equal properties.
		assertTrue("Nonequality of Lens detected.", l1.equals(l1));
		assertTrue("Nonequality of equal lenses detected.", l1.equals(l3));
		// non equal properties.
		assertFalse("Equality of nonequal lenses detected.", l1.equals(l2));
		assertFalse("Equality of different class and a Lens detected.", l1.equals(test));
	}

	/**
	 * Test the null Lens's Test method for {@link java.lang.Object#equals(java.lang.Object)}.
	 */
	@Test(expected = NullPointerException.class)
	public void testEqualsNullObject() {
		// equal properties.
		assertTrue("Nonequality of null lenses detected.", l4.equals(l5));
		// non equal properties.
		assertFalse("Equality of null and non null lenses detected.", l1.equals(l4));
	}

}
