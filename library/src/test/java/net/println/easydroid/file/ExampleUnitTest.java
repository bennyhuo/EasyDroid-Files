package net.println.easydroid.file;

import org.junit.Test;

import java.io.IOException;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void testCopy() throws IOException {
        EFile eFile = new EFile("src");
        eFile.copyInto("../temp", true);
        eFile = new EFile("../temp/src");
        eFile.moveTo("../temp/newsrc");
    }
}