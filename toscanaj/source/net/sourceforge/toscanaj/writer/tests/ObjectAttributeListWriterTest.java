/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.writer.tests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Reader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.parser.ObjectAttributeListParser;
import net.sourceforge.toscanaj.tests.ContextSetups;
import net.sourceforge.toscanaj.writer.ObjectAttributeListWriter;

import org.tockit.context.model.Context;

public class ObjectAttributeListWriterTest extends TestCase {
    final static Class THIS = ObjectAttributeListWriterTest.class;

    public ObjectAttributeListWriterTest(final String s) {
        super(s);
    }

    public static Test suite() {
        return new TestSuite(THIS);
    }

    public void testAnimalContext() throws IOException, DataFormatException {
        final Context inContext = ContextSetups.createCompleteAnimalContext();
        final PipedOutputStream out = new PipedOutputStream();
        final Reader in = new InputStreamReader(new PipedInputStream(out));

        final Runnable writeTask = new Runnable() {
            public void run() {
                ObjectAttributeListWriter.writeObjectAttributeList(inContext,
                        new PrintStream(out));
            }
        };

        final Thread thread = new Thread(writeTask);
        thread.start();

        final Context resultContext = ObjectAttributeListParser
                .importOALFromReader(in, inContext.getName());

        assertEquals(inContext.getAttributes().size(), resultContext
                .getAttributes().size());
        assertEquals(inContext.getObjects().size(), resultContext.getObjects()
                .size());
    }
}