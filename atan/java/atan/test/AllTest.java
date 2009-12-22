package atan.test;

//~--- non-JDK imports --------------------------------------------------------

import junit.framework.TestCase;
import junit.framework.TestSuite;

import junit.textui.TestRunner;

/**
 * Class description
 * @author Atan
 */
public class AllTest extends TestCase {

    /**
     * Constructs ...
     * @param name
     */
    public AllTest(String name) {
        super(name);
    }

    /**
     * Method description
     * @return
     */
    static TestSuite suite() {
        TestSuite s = new TestSuite();
        s.addTest(CommandFactoryTest.suite());
        s.addTest(ParserTest.suite());
        s.addTest(GeneratedTests.suite());
        return s;
    }

    /**
     * Method description
     * @param arg
     */
    public static void main(String[] arg) {
        TestRunner.run(suite());
    }

    /**
     * Method description
     */
    public void test() {

        // to be
    }
}