package org.python.util.install;

import junit.framework.TestCase;

public class UnicodeSequencesTest extends TestCase {
 
    public void testUmlaute() {
        assertEquals("ä", UnicodeSequences.a2);
        assertEquals("Ä", UnicodeSequences.A2);
        assertEquals("ö", UnicodeSequences.o2);
        assertEquals("Ö", UnicodeSequences.O2);
        assertEquals("ü", UnicodeSequences.u2);
        assertEquals("Ü", UnicodeSequences.U2);
    }

}
