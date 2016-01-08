package nl.ou.fresnelforms.fresnelequalitytest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This testsuite runs all the equality tests.
 * @author jn.theunissen@studie.ou.nl
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ LensEqualityTest.class, PropertyBindingEqualityTest.class,
		PropertyEqualityTest.class })
public class AllEquilityTestSuite {

}
