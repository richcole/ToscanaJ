package net.sourceforge.toscanaj.model;

import  net.sourceforge.toscanaj.model.XML_Serializable;
import  java.lang.Exception;
import  java.io.FileInputStream;
import java.io.File;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.adapters.DOMAdapter;
import org.jdom.input.DOMBuilder;

/**
 * This class provides a mechanism to read an XML_Serializable object from a
 * filename.  
 */
public class XML_Reader 
{
    /** 
     * The constructor does the reading from a file and throws if there is a *
     * problem.  
     */
    public XML_Reader(File file, XML_Serializable object) throws
	XML_SyntaxError
    {
	try {
	    // open stream on file
	    FileInputStream in;
        in = new FileInputStream(file);

	    DOMAdapter domAdapter = new org.jdom.adapters.XercesDOMAdapter();
	    org.w3c.dom.Document w3cdoc = domAdapter.getDocument(in, false);
	    
	    // create JDOM document
	    DOMBuilder builder =
		new DOMBuilder( "org.jdom.adapters.XercesDOMAdapter" );
	    Document   document = builder.build( w3cdoc );
	    Element    element  = document.getRootElement();
	    object.readXML(element);
	}
	catch( XML_SyntaxError ex ) {
	    throw ex;
	}
	catch( Exception ex ) {
	    throw new XML_SyntaxError(ex.getMessage());
	}
    }
    
}
