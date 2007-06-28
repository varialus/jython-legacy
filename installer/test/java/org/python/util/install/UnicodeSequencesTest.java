package org.python.util.install;

import junit.framework.TestCase;

public class UnicodeSequencesTest extends TestCase {
 
    public void testUmlaute() {
        assertEquals("�", UnicodeSequences.a2);
        assertEquals("�", UnicodeSequences.A2);
        assertEquals("�", UnicodeSequences.o2);
        assertEquals("�", UnicodeSequences.O2);
        assertEquals("�", UnicodeSequences.u2);
        assertEquals("�", UnicodeSequences.U2);
    }

}
