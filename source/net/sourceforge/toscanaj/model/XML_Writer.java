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
    /**
     * The constructor does the reading from a file and throws if there is a *
     * problem.
     */
    public XML_Writer(File file, XML_Serializable object, String rootElementName)
            throws Exception
    {
        // open stream on file
        FileOutputStream out = new FileOutputStream(file);
        XMLOutputter outputter = new XMLOutputter();

        Document document = new Document(new Element(rootElementName));
        object.writeXML(document.getRootElement());
        outputter.output(document, out);
    }

}
