/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.toscanaj.servlet;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.NormedIntervalSource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import org.tockit.events.EventBroker;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.awt.geom.Point2D;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class ToscanaJDiagrams extends HttpServlet {
    private static double 	viewBoxX;
	private static double 	viewBoxY;
	private static double 	viewBoxWidth;
	private static double 	viewBoxHeight;
	private static double	marginLeft;
	private static double 	marginRight;
	private static double 	marginTop;
	private static double 	marginBottom; 
	
    public void init() throws ServletException 
    {
    		
		//System.out.println("ToscanaJDiagrams: init");
        super.init();
		if(!GlobalVariables.isInitialized()) {
			// get the schema filename from the servlet init parameter
			String inputFile = getInitParameter("schemaFile");        

			// if no filename defined        
			if(inputFile == null) {
				throw new RuntimeException("No file given as parameter");
			}
        
			String servletUrl = getInitParameter("baseURL");
			if (servletUrl == null) {
				throw new ServletException("No baseURL given as init parameter");
			}
			File schemaFile = new File(inputFile);
			
			try {
				GlobalVariables.initialize(schemaFile, servletUrl);
			} catch (Exception e) {
				throw new ServletException("Can not initialize servlet", e);
			}
		}
    }

	public void destroy() {
		super.destroy();
	}

    public void doPost(HttpServletRequest httpServletRequest, 
    		HttpServletResponse httpServletResponse) 
    		throws IOException 
    {
    		
    	// call doGet
        doGet(httpServletRequest, httpServletResponse);
        
    }

    public void doGet (HttpServletRequest req, 
    		HttpServletResponse resp) 
    		throws IOException
    {
    	
		//System.out.println("ToscanaJDiagrams: doGet");		

		// local variable declaration
		DiagramHistory 	diagramHistory;
		PrintWriter 	out;
		HttpSession 	session;
		String 			diagramParameter;
		int			diagramNumber;

		// local variable initialisation
        session 				= req.getSession();
		out 					= resp.getWriter();
        diagramParameter 		= req.getParameter("diagram");
        diagramHistory 			= (DiagramHistory) session.getAttribute("diagramHistory");
		diagramNumber 			= Integer.parseInt(diagramParameter);
		
		// if there is no diagram history found
        if(diagramHistory == null) {
            diagramHistory = new DiagramHistory();
          	// session.setAttribute("diagramHistory", diagramHistory);
        }

        // construct the SVG content and put it into buffer
        StringBuffer svgContent = printDiagram(diagramNumber, diagramHistory, out);
        
        // calculate view box size
        // used to call the viewBoxCalculater. 
        // Is placed wrong here, I reckon, I moved it into printSVGFile. Bastian
        // calculateViewBoxSize();
		
		// translate the svg image
		svgContent.insert(0,"<g transform=\"translate(-" + marginLeft + ",-" 
			+ marginTop + ")\">");
		svgContent.append("</g>");

		// print out the SVG file
		printSVGFile(svgContent, out, resp);
    }

	private void printSVGFile(StringBuffer buffer, PrintWriter out, 
			HttpServletResponse resp) 
	{
		// First create the viewBox
		calculateViewBoxSize();
		
		// set the response content type to be svg image		
		resp.setContentType("image/svg-xml");
        
        // prints the content
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" " +
			"\"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">");
		out.println("<svg preserveAspectRatio=\"xMidYMid\" viewBox=\"" + viewBoxX 
			+ " " + viewBoxY + " " + viewBoxWidth 
			+ " " + viewBoxHeight + "\" xmlns=\"http://www.w3.org/2000/svg\">");      
		printScript(out);
		
		out.println(buffer.toString());
		out.println("</svg>");            
		
	}
	
    private void printScript(PrintWriter out) 
    {
    	
		//System.out.println("ToscanaJDiagrams: PrintScript");
        out.println("<script type=\"text/javascript\">");
        out.println("function openWindow(URL) {");
        out.println("window1 = window.open(URL,\"my_new_window\"," +
        	"\"" + GlobalConstants.PopupOptions + "\")");
        out.println("}");
        out.println("</script>");
        
    }

    private StringBuffer printDiagram(int diagramNumber, 
    		DiagramHistory diagramHistory, PrintWriter out) 
    {
//		System.out.println("ToscanaJDiagrams: printDiagram");
//		minimalDiagramX = 0.0;
//		maximalDiagramX = 0.0;
//		minimalDiagramY = 0.0;
//		maximalDiagramY = 0.0;
//		diagramX = 0.0;
//		diagramY = 0.0;
		
		//	local variables declaration
        Diagram2D 		diagram;			// initialise the diagram
        StringBuffer	buffer;				// initialise the StringBuffer
		int 			numNodes;			// number of nodes
        int 			numLines;			// number of lines 
		double 		radius;				// radius of a node
		double 		gradientPosition;	// position of a gradient
				
		//	local variables initialisation
		buffer			=	new StringBuffer();
		diagram 		= 	GlobalVariables.getConceptualSchema().getDiagram(diagramNumber);
		numLines 		= 	diagram.getNumberOfLines();
		numNodes 		= 	diagram.getNumberOfNodes();

		// initialise the buffered image to sizes
		BufferedImage bi = new BufferedImage(GlobalConstants.IMAGE_WIDTH,
						GlobalConstants.IMAGE_HEIGHT,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		Font font = new Font(GlobalConstants.FONT_FAMILY, Font.PLAIN, 12);
		FontMetrics fm = g2d.getFontMetrics(font);

		// iterate through the lines element			
        for(int i = 0; i < numLines; i ++) 
        {
        	
            DiagramLine line = diagram.getLine(i);
            Point2D from = line.getFromPosition();
            Point2D to = line.getToPosition();
            buffer.append("<path d=\"M " + addXPos(from.getX()) 
            	+ " " + addYPos(from.getY()) + " L " + addXPos(to.getX()) 
            	+ " " + addYPos(to.getY()) 
            	+ "\" stroke=\"black\" stroke-width=\"3\" />");
            buffer.append("\n");
            
        }

		// iterate through the nodes element
        for(int i = 0; i < numNodes; i ++) {

			// gather information about a node        	
            DiagramNode node = diagram.getNode(i);
            Concept concept = node.getConcept();
            ConceptInterpretationContext interpretationContext =
                new ConceptInterpretationContext(diagramHistory, new EventBroker());
            Point2D pos = node.getPosition();

			// update margins for nodes            
            updateMargins(pos, null, 0.0, i, "Node");
            
            // check whether this concept is realised or not
            if( GlobalVariables.getConceptInterpreter().isRealized(concept, interpretationContext)) 
            {
                radius = GlobalConstants.NODE_SIZE;
            }
            else 
            {
                radius = GlobalConstants.NODE_SIZE/3.0;
            }

			// get the gradient position of this node            
            NormedIntervalSource extentSizeIntervalSource = GlobalVariables.getConceptInterpreter().getIntervalSource(ConceptInterpreter.INTERVAL_TYPE_EXTENT);
            gradientPosition = extentSizeIntervalSource.getValue(concept, interpretationContext);
            
            Color nodeColor = GlobalVariables.getDiagramSchema().getGradient().getColor(gradientPosition);
            
            LabelInfo attrLabel = node.getAttributeLabelInfo();

//			buffer.append("<a target=\"diagrams\" xlink:href=\"" 
//				+ GlobalVariables.getServletUrl() + "?diagram=" + diagramNumber 
//				+ "&amp;filterConcept=" + node.getIdentifier() + "\">");
//			buffer.append("\n");
				
			buffer.append("<circle cx=\"" + addXPos(pos.getX()) 
				+ "\" cy=\"" + addYPos(pos.getY()) +"\" r=\"" 
				+ radius + "\" style=\"fill:RGB(" + nodeColor.getRed() 
				+ "," + nodeColor.getGreen() + "," 
				+ nodeColor.getBlue() + ")\" stroke=\"black\" />");
			buffer.append("\n");
			
			
//            buffer.append("</a>");
//			buffer.append("\n");

            if(attrLabel != null) 
            {
            	
                // diagram related info (layout)
                Color backgroundColor = attrLabel.getBackgroundColor();
                Point2D offset = attrLabel.getOffset();
                Color textColor = attrLabel.getTextColor();
                int textAlignment = attrLabel.getTextAlignment();
                
                // lattice related info (content)
                Iterator attrIt = concept.getAttributeContingentIterator();
                List attrList = new ArrayList();
                
                // iterate through the attribute list
                while (attrIt.hasNext()) 
                {
                    FCAElement attribute = (FCAElement) attrIt.next();
                    attrList.add(attribute.getData().toString());
                }
                
                // get the maximum label width using the Font Metrics
                double maxLabelWidth = getWidth(fm, attrList);
                int maxLabelHeight = getHeight(fm);

				// update the margins
				updateMargins(pos, offset, maxLabelWidth, maxLabelHeight, "attrLabel");
				
                if ((textAlignment == 1) && (maxLabelWidth != 0.0)) 
                {
                	
                	buffer.append("<rect width=\"" + maxLabelWidth + "\" height=\"" 
                		+ maxLabelHeight + "\" x=\"" + (addXPos((pos.getX() 
                		+ offset.getX())) - 3.0 - (0.5 * maxLabelWidth)) + "\" y=\"" 
                		+ addYPos((pos.getY() 
                		+ offset.getY() - 10.0 - maxLabelHeight )) 
                		+ "\" style=\"fill:RGB(" + backgroundColor.getRed() 
                		+ "," + backgroundColor.getGreen() + "," 
                		+ backgroundColor.getBlue() +")\" stroke=\"Black\" />");
					buffer.append("\n");
                		
                }
                else if ((textAlignment == 2) && (maxLabelWidth != 0.0)) 
                {
                	
					buffer.append("<rect width=\"" + maxLabelWidth + "\" height=\"" 
						+ maxLabelHeight + "\" x=\"" + (addXPos((pos.getX() 
						+ offset.getX())) - 3.0 - (0.65 * maxLabelWidth)) 
						+ "\" y=\"" + addYPos((pos.getY() + offset.getY() 
						- 10.0 - maxLabelHeight )) + "\" style=\"fill:RGB(" 
						+ backgroundColor.getRed() + "," + backgroundColor.getGreen() 
						+ "," + backgroundColor.getBlue() +")\" stroke=\"Black\" />");
					buffer.append("\n");
					
                }
                else if (maxLabelWidth != 0.0) 
                {
                	
					buffer.append("<rect width=\"" + maxLabelWidth + "\" height=\"" 
						+ maxLabelHeight + "\" x=\"" + (addXPos((pos.getX() 
						+ offset.getX())) - 3.0 - (0.35 * maxLabelWidth)) 
						+ "\" y=\"" + addYPos((pos.getY() + offset.getY() 
						- 10.0 - maxLabelHeight )) + "\" style=\"fill:RGB(" 
						+ backgroundColor.getRed() + "," + backgroundColor.getGreen() 
						+ "," + backgroundColor.getBlue() +")\" stroke=\"Black\" />");
					buffer.append("\n");
					
                }
                else 
                {
                    // do not print the label
                }

                buffer.append(drawLineConnector(
                		out, 
                		pos, 
                		offset, 
                		maxLabelWidth, 
                		maxLabelHeight, 
                		textAlignment
                ));
				buffer.append("\n");

                attrIt = concept.getAttributeContingentIterator();
                
                while (attrIt.hasNext()) {
                    FCAElement attribute = (FCAElement) attrIt.next();
					
                    if (textAlignment == 1) {
						buffer.append("<text font-family=\"" + GlobalConstants.FONT_FAMILY 
							+ "\" font-size=\"" + GlobalConstants.FONT_SIZE + "\" x=\"" 
							+ (addXPos((pos.getX() + offset.getX())) 
							- (0.5 * maxLabelWidth)) + "\" y=\"" 
							+ addYPos((pos.getY() + offset.getY() - 12.0 )) 
							+ "\" style=\"fill:RGB(" + textColor.getRed() 
							+ "," + textColor.getGreen() + "," + textColor.getBlue() 
							+ ")\" >");
						buffer.append("\n");
                    } else if (textAlignment == 2) {
						buffer.append("<text font-family=\"" + GlobalConstants.FONT_FAMILY 
							+ "\" font-size=\"" + GlobalConstants.FONT_SIZE + "\" x=\"" 
							+ (addXPos((pos.getX() + offset.getX())) 
							- (0.65 * maxLabelWidth)) + "\" y=\"" 
							+ addYPos((pos.getY() + offset.getY() - 12.0 )) 
							+ "\" style=\"fill:RGB(" + textColor.getRed() + "," 
							+ textColor.getGreen() + "," + textColor.getBlue() 
							+ ")\" >");
						buffer.append("\n");
                    } else {                   	
						buffer.append("<text font-family=\"" + GlobalConstants.FONT_FAMILY 
							+ "\" font-size=\"" + GlobalConstants.FONT_SIZE + "\" x=\"" 
							+ (addXPos((pos.getX() + offset.getX())) 
							- (0.35 * maxLabelWidth)) + "\" y=\"" 
							+ addYPos((pos.getY() + offset.getY() - 12.0 )) 
							+ "\" style=\"fill:RGB(" + textColor.getRed() 
							+ "," + textColor.getGreen() + "," + textColor.getBlue() 
							+ ")\" >");
						buffer.append("\n");
                    }
					String attributeName = attribute.getData().toString();
					buffer.append(escapeEntities(attributeName));
					buffer.append("\n");
					buffer.append("</text>");
					buffer.append("\n");
                }
            }
            
            LabelInfo objLabel = node.getObjectLabelInfo();

			//System.out.println("checking object label");
            if(objLabel != null) {
            	//System.out.println("object label exists !!!!!!!!!!!!!!!!!!!");
            	
				Color backgroundColor = objLabel.getBackgroundColor();
				Point2D offset = objLabel.getOffset();
				Color textColor = objLabel.getTextColor();
				int textAlignment = objLabel.getTextAlignment(); 
				int objectCount = GlobalVariables.getConceptInterpreter().getObjectCount(concept, 
					interpretationContext);

                if (objectCount != 0) 
                {
					//System.out.println("object count exists");
					
                    
					double maxLabelWidth = fm.stringWidth(String.valueOf(objectCount))
						+ 2 * fm.getLeading() + 2 * fm.getDescent();
					int maxLabelHeight = getHeight(fm);

					//System.out.println("max label height" + maxLabelHeight);
					updateMargins(pos, offset, maxLabelWidth, maxLabelHeight, 
						"labelInfo");

					//System.out.println("textAlignment: "+textAlignment);
                    if ((textAlignment == 0) && (maxLabelWidth != 0.0)) 
                    {

						buffer.append("<a xlink:href=\"" + GlobalVariables.getServletUrl() 
							+ "?concept=" + node.getIdentifier() 
							+ "&amp;diagram=" + diagramNumber 
							+ "\" target=\"objectlabel\">");
						buffer.append("\n");
							
						buffer.append("<rect width=\"" + maxLabelWidth 
							+ "\" height=\"" + maxLabelHeight 
							+ "\" x=\"" + (addXPos((pos.getX() 
//							+ offset.getX())) - 3.0 - (0.5 * maxLabelWidth)) 
							+ offset.getX())) - (0.5 * maxLabelWidth)) 
							+ "\" y=\"" + addYPos((pos.getY()  
//							- 10.0 + maxLabelHeight )) + "\" style=\"fill:RGB(" 
							+ GlobalConstants.NODE_SIZE)) + "\" style=\"fill:RGB(" 
							+ backgroundColor.getRed() + "," 
							+ backgroundColor.getGreen() + "," 
							+ backgroundColor.getBlue() +")\" stroke=\"Black\" />");
						buffer.append("\n");
							
						buffer.append("<text x=\"" + (addXPos(pos.getX() 
							+ offset.getX()) - (0.4 * maxLabelWidth))  
//							+ "\" y=\"" + addYPos((pos.getY() + NODE_SIZE 
//							+ offset.getY() + 5.0)) + "\" font-size=\""
							+ "\" y=\"" + addYPos((pos.getY() + offset.getY() 
							+ (1.5*maxLabelHeight) )) + "\" font-size=\""
							+ GlobalConstants.FONT_SIZE + "\" font-family=\"" + GlobalConstants.FONT_FAMILY 
							+ "\" style=\"fill:RGB(" + textColor.getRed() 
							+ "," + textColor.getGreen() + "," 
							+ textColor.getBlue() +")\">");
						buffer.append("\n");
							
                        buffer.append(drawLineConnector2(
            				out, 
            				pos, 
            				offset, 
            				maxLabelWidth, 
            				maxLabelHeight, 
            				textAlignment
                        ));
						buffer.append("\n");
                        
                        try 
                        {
                            buffer.append("" + objectCount);
                            //System.out.println("" + objectCount);
                        } 
                        
                        catch (Exception e) {
                        	
                            e.printStackTrace(out);
                            
                        }
                        
						buffer.append("</text>");
						buffer.append("\n");
						buffer.append("</a>");
						buffer.append("\n");
						
                    }
                    
                }
                else {
					//System.out.println("object count does exists");

                }
                
            }
            else {
				//System.out.println("object label does not exists");
            }
            
        }

		return buffer;        
		
    }

    private String drawLineConnector (PrintWriter out, Point2D centerOfNode, 
    		Point2D offset, double labelWidth, int labelHeight, 
    		int theTextAlignment) 
    {

		//System.out.println("ToscanaJDiagrams: drawLineConnector");
        if ((theTextAlignment == 1) && (labelWidth != 0.0)) 
        {
        	
            return "<path d=\"M " + addXPos(centerOfNode.getX()) + " " 
            	+ addYPos((centerOfNode.getY() - GlobalConstants.NODE_SIZE)) + " L " 
            	+ addXPos((centerOfNode.getX() + offset.getX())) 
            	+ " " + addYPos((centerOfNode.getY() - GlobalConstants.NODE_SIZE + offset.getY())) 
            	+ "\" stroke=\"black\" stroke-width=\"1\" />";
            
        }
        else if ((theTextAlignment == 2) && (labelWidth != 0.0)) 
        {
        	
            return "<path d=\"M " + addXPos(centerOfNode.getX()) + " " 
            	+ addYPos((centerOfNode.getY() - GlobalConstants.NODE_SIZE)) + " L " 
            	+ (addXPos((centerOfNode.getX() + offset.getX())) - 3.0 
            	- ( 0.5 * ( 0.65 * labelWidth))) + " " + addYPos((centerOfNode.getY() 
            	+ offset.getY() - 10.0)) 
            	+ "\" stroke=\"black\" stroke-width=\"1\" />";
            
        }
        else if (labelWidth != 0.0) 
        {
        	
            return "<path d=\"M " + addXPos(centerOfNode.getX()) + " " 
            	+ addYPos((centerOfNode.getY() - GlobalConstants.NODE_SIZE)) + " L " 
            	+ ((addXPos((centerOfNode.getX() + offset.getX())) - 3.0) 
            	+ ( 0.5 * ( 0.35 * labelWidth))) + " " + addYPos((centerOfNode.getY() 
            	+ offset.getY() - 10.0)) 
            	+ "\" stroke=\"black\" stroke-width=\"1\" />";
            
        }
        else 
        {
        	
            // do not print the line connector
			return "";
			
        }
    }

    private String drawLineConnector2(PrintWriter out, Point2D centerOfNode, Point2D offset, double labelWidth, int labelHeight, int theTextAlignment) {
    	
		//System.out.println("ToscanaJDiagrams: drawLineConnector2");
        if ((theTextAlignment == 1) && (labelWidth != 0.0)) 
        {
        	
            return "<path d=\"M " + addXPos(centerOfNode.getX()) + " " 
            	+ addYPos((centerOfNode.getY() + GlobalConstants.NODE_SIZE)) + " L " 
            	+ addXPos((centerOfNode.getX() + offset.getX())) + " " 
            	+ addYPos((centerOfNode.getY() + GlobalConstants.NODE_SIZE - 5.0 + offset.getY())) 
            	+ "\" stroke=\"black\" stroke-width=\"1\" />";
            
        }
        else if ((theTextAlignment == 2) && (labelWidth != 0.0)) 
        {
        	
            return "<path d=\"M " + addXPos(centerOfNode.getX()) 
            	+ " " + addYPos((centerOfNode.getY() + GlobalConstants.NODE_SIZE)) + " L " 
            	+ (addXPos((centerOfNode.getX() + offset.getX())) - 3.0 
            	- ( 0.5 * ( 0.65 * labelWidth))) + " " 
            	+ addYPos((centerOfNode.getY() + offset.getY() - 10.0)) 
            	+ "\" stroke=\"black\" stroke-width=\"1\" />";
            
        }
        else if (labelWidth != 0.0) 
        {
        	
            return "<path d=\"M " + addXPos(centerOfNode.getX()) + " " 
            	+ addYPos((centerOfNode.getY() + GlobalConstants.NODE_SIZE)) + " L " 
            	+ ((addXPos((centerOfNode.getX() + offset.getX())) - 3.0) 
            	+ ( 0.5 * ( 0.35 * labelWidth))) + " " + addYPos((centerOfNode.getY() 
            	+ offset.getY() - 10.0)) 
            	+ "\" stroke=\"black\" stroke-width=\"1\" />";
            
        }
        else 
        {
        	
            // do not print the line connector
            return "";
            
        }
        
    }

    private String escapeEntities(String string) 
    {
    	
		//System.out.println("ToscanaJDiagrams: escapeEntities");
        String retVal = "";
        for(int i = 0; i < string.length(); i++) 
        {
            char c = string.charAt(i);
            switch(c) {
                case '<':
                    retVal += "&lt;";
                    break;
                case '>':
                    retVal += "&gt;";
                    break;
                case '&':
                    retVal += "&amp;";
                    break;
                case '\'':
                    retVal += "&apos;";
                    break;
                case '"':
                    retVal += "&quot;";
                    break;
                default:
                    retVal += c;
            }
        }
        return retVal;
        
    }

    private double addXPos(double oldPos) 
    {
    	
        return (oldPos + GlobalConstants.OFFSET_X);
        
    }

    private double addYPos(double oldPos) 
    {
    	
        return (oldPos + GlobalConstants.OFFSET_Y);
        
    }

    /**
     * Calculates the width of the label given a specific font metric.
     *
     * The width is calculated as the maximum string width plus two times the
     * leading and the descent from the font metrics. When drawing the text the
     * horizontal position should be the left edge of the label plus one times
     * the two values (FontMetrics::getLeading() and FontMetrics::getDescent()).
     */
    public double getWidth(FontMetrics fontMetrics, List entries) 
    {
    	
        if (entries.size() == 0) 
        {
            return 0;
        }
        double result = 0;

        // find maximum width of string
        Iterator it = entries.iterator();
        while (it.hasNext()) {
            String cur = it.next().toString();
            double w = fontMetrics.stringWidth(cur);
            if (w > result) {
                result = w;
            }
        }

        // add two leadings and two descents to have some spacing on the left
        // and right side
        result += 2 * fontMetrics.getLeading() + 2 * fontMetrics.getDescent();

        return result;
        
    }

    /**
     * Calculates the height of the label given a specific font metric.
     */
    public int getHeight(FontMetrics fontMetrics) 
    {
    	
        return fontMetrics.getHeight();
        
    }

//	private void calculateViewBoxSize()//double maximalDiagramX, double maximalDiagramY) // braucht jawohl die Diagrammgroesse
	// Bastian: changed the calculation of viewBoxWidth and viewBoxHeight 
    private void calculateViewBoxSize() 
    {
    	viewBoxWidth = 0;
		viewBoxHeight = 0;

		viewBoxX = 0.0;	
		viewBoxY = -60.0; 
//		viewBoxWidth = ( marginRight - marginLeft ) + 40.0;
//		viewBoxHeight = ( marginBottom - marginTop ) + 80.0;

		viewBoxWidth = (marginRight-marginLeft ) + 10; //minimalDiagramX + maximalDiagramX;// + (minimalDiagramX + maximalDiagramX)/20;
		viewBoxHeight = (marginBottom - marginTop) + 75; //minimalDiagramY + maximalDiagramY;
	}
	
	private void updateMargins(Point2D pos, Point2D offset, 
			double maxLabelWidth, int maxLabelHeight, String element) 
	{

		if (element.equals("Node")) 
		{
			// if it is the first node
			if (maxLabelHeight == 1) 
			{
				marginLeft 		= addXPos(pos.getX());
				marginRight 	= addXPos(pos.getX());
				marginTop		= addYPos(pos.getY());
				marginBottom	= addYPos(pos.getY());
			}
			else 
			{			
				// if current node is the left most node in the diagram
				if (addXPos(pos.getX()) < marginLeft) 
				{
					// update left margin
					marginLeft = addXPos(pos.getX());	
				}
				// if current node is not the left most node in the diagram
				else 
				{
					// do not update left margin	
				}
	
				// if current node is the right most node in the diagram
				if (marginRight < addXPos(pos.getX())) 
				{	
					// update right margin
					marginRight = addXPos(pos.getX());	
				}
				// if current node is not the right most node in the diagram
				else 
				{
					// do not update right margin
				}
				// if current node is the upper most node in the diagram
				if (addYPos(pos.getY()) < marginTop) 
				{
					// update top margin
					marginTop = addYPos(pos.getY());
				}
				// if current node is not the upper most node in the diagram
				else 
				{
					// do not update top margin
				}
				// if current node is the bottom most node in the diagram
				if (marginBottom < addYPos(pos.getY())) 
				{
					// update bottom margin
					marginBottom = addYPos(pos.getY());
				}
				else 
				{
					// do not update bottom margin
				}
			}
		}
		else if (element.equals("attrLabel")) 
		{
			// if node label is the right most part of the diagram
			if (marginRight < ((addXPos((pos.getX() + offset.getX())) - 3.0 
				- (0.35 * maxLabelWidth)) + maxLabelWidth)) 
			{
				// update right margin
				marginRight = (addXPos((pos.getX() + offset.getX())) - 3.0 
					- (0.35 * maxLabelWidth)) + maxLabelWidth;
			}			
			else {
				// do not update right margin
			}
			// if node label is the left most part of the diagram
			if ((addXPos((pos.getX() + offset.getX())) - 3.0 
				- (0.65 * maxLabelWidth)) < marginLeft) 
			{
				// update left margin
				marginLeft = addXPos((pos.getX() + offset.getX())) - 3.0 - (0.65 * maxLabelWidth);
			}
			else 
			{	
				// do not update left margin	
			}
			// if the node label is the upper most part of the diagram
			if (addYPos((pos.getY() + offset.getY() - 10.0 
				- maxLabelHeight )) < marginTop) 
			{	
				// update top margin
				marginTop = addYPos((pos.getY() + offset.getY() - 10.0 
					- maxLabelHeight ));	
			}
			else 
			{
				// do not update top margin				
			}
			// if the node label is the bottom most part of the diagram
			if (marginBottom < (addYPos((pos.getY() + offset.getY() - 10.0 
				- maxLabelHeight )) + maxLabelHeight)) 
			{				
				// update bottom margin
				marginBottom = addYPos((pos.getY() + offset.getY() - 10.0 
					- maxLabelHeight )) + maxLabelHeight;				
			}
			else 
			{				
				// do not update bottom margin				
			}			
		}
		else if (element.equals("labelInfo")) 
		{			
			// if this label info is the right most part of the diagram
			if (marginRight < ((addXPos((pos.getX() + offset.getX())) - 3.0 
				- (0.5 * maxLabelWidth)) + maxLabelWidth)) 
			{				
				// update the right margin
				marginRight = (addXPos((pos.getX() + offset.getX())) - 3.0 
					- (0.5 * maxLabelWidth)) + maxLabelWidth;					
			}
			else 
			{				
				// do not update right margin				
			}
			// if this label info is the left most part of the diagram
			if ((addXPos((pos.getX() + offset.getX())) - 3.0 
				- (0.5 * maxLabelWidth)) < marginLeft) 
			{				
				// update margin left
				marginLeft = (addXPos((pos.getX() + offset.getX())) - 3.0 
					- (0.5 * maxLabelWidth));
					
			}
			else 
			{
				
				// do not update left margin
				
			}

			// if this label info is the top most part of the diagram
			if (addYPos((pos.getY() + offset.getY() - 10.0 + maxLabelHeight )) 
				< marginTop) 
			{
				
				// update top margin
				marginTop = addYPos((pos.getY() + offset.getY() - 10.0 
					- maxLabelHeight ));
					
			}
			else 
			{
				
				// do not update top margin
				
			}

			if (marginBottom < (addYPos((pos.getY() + offset.getY() - 10.0 
				+ maxLabelHeight )) + maxLabelHeight)) 
			{
				
				// update bottom margin
				marginBottom = addYPos((pos.getY() + offset.getY() - 10.0 
					+ maxLabelHeight )) + maxLabelHeight;
				
			}
			else 
			{
				
				// do not update bottom margin
				
			}	
					
		}
	}
}
