package net.sourceforge.toscanaj.model;

import  net.sourceforge.toscanaj.model.XML_Serializable;
import  java.lang.Exception;
import  java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.adapters.DOMAdapter;
import org.jdom.input.DOMBuilder;

/**
 * This class provides a mechanism to read an XML_Serializable object from a
 * filename.
 */
public class XML_Writer
{
    public static void write(File file, XML_Serializable object)
            throws Exception
    {
        // open stream on file
        FileOutputStream out = new FileOutputStream(file);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setIndent(true);
        outputter.setNewlines(true);

        Document document = new Document(object.toXML());
        outputter.output(document, out);
    }

}
