package net.sourceforge.toscanaj.view.dialogs;

import net.sourceforge.toscanaj.controller.ConfigurationManager;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class DescriptionViewer
{
    private static URL baseURL;
    
    private static class ViewerDialog extends JDialog
    {
        class Hyperactive implements HyperlinkListener {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    JEditorPane pane = (JEditorPane) e.getSource();
                    if (e instanceof HTMLFrameHyperlinkEvent) {
                        HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
                        HTMLDocument doc = (HTMLDocument)pane.getDocument();
                        doc.processHTMLFrameHyperlinkEvent(evt);
                    } else {
                        try {
                            pane.setPage(e.getURL());
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            }
        }

        private JEditorPane textArea;

        public ViewerDialog( Frame frame )
        {
            /// @todo use HTML title as dialog title
            super( frame, "Description", true );

            final JButton closeButton = new JButton("Close");
            final ViewerDialog dialog = this;
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ConfigurationManager.storePlacement("DescriptionViewerDialog", dialog);
                    dialog.setVisible(false);
                }
            });
            getRootPane().setDefaultButton(closeButton);

            //Lay out the buttons from left to right.
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout( new BoxLayout( buttonPane, BoxLayout.X_AXIS) );
            buttonPane.setBorder( BorderFactory.createEmptyBorder(0, 10, 10, 10) );
            buttonPane.add( Box.createHorizontalGlue() );
            buttonPane.add( closeButton );

            this.textArea = new JEditorPane();
            this.textArea.setContentType("text/html");
            this.textArea.setEditable(false);
            this.textArea.addHyperlinkListener(new Hyperactive());

            JScrollPane scrollview = new JScrollPane();
            scrollview.getViewport().add(this.textArea);

            //Put everything together, using the content pane's BorderLayout.
            Container contentPane = getContentPane();
            contentPane.add( scrollview, BorderLayout.CENTER );
            contentPane.add( buttonPane, BorderLayout.SOUTH );
        }

        private void showDescription(Element description, URL baseURL)
        {
            Element elem = description.getChild("externalHTML");
            if(elem != null) {
                String urlAttr = elem.getAttributeValue("url");
                String fileAttr = elem.getAttributeValue("file");
                if(urlAttr != null) {
                    HTMLDocument doc = (HTMLDocument) this.textArea.getDocument();
                    doc.setBase(baseURL);
                    try {
                        this.textArea.setPage(urlAttr);
                    }
                    catch (IOException e) {
                        this.textArea.setText("Could not open url \"" + urlAttr +"\":" + e.getMessage());
                    }
                    return;
                }
                if(fileAttr != null) {
                    URL url;
                    try {
                        url = new URL(baseURL, fileAttr);
                        this.textArea.setPage(url);
                    }
                    catch (MalformedURLException e) {
                        this.textArea.setText("Could not create url for file '" + fileAttr +"'");
                    }
                    catch (IOException e) {
                        this.textArea.setText("Could not open file \"" + fileAttr +"\":" + e.getMessage());
                    }
                    return;
                }
                this.textArea.setText("Could neither find \"url\" nor \"file\" attribute on &lt;externalHTML&gt;");
                return;
            }
            elem = description.getChild("html");
            if(elem != null) {
                XMLOutputter outputter = new XMLOutputter();
                outputter.setOmitDeclaration(true);
                this.textArea.setText(outputter.outputString(elem));
                HTMLDocument doc = (HTMLDocument) this.textArea.getDocument();
                doc.setBase(baseURL);
                return;
            }
            this.textArea.setText("Could not find &lt;externalHTML&gt; or &lt;html&gt; element in the description");
        }
    }

    private DescriptionViewer()
    {
    }
    
    public static void setBaseLocation(String baseLocation)
    {
        try {
            baseURL = new URL("file://" + baseLocation);
        }
        catch (MalformedURLException e) {
            System.err.println(e.getMessage());
            baseURL = null;
        }
    }

    public static void setBaseURL(URL baseURL)
    {
        DescriptionViewer.baseURL = baseURL;
    }

    public static void show(Frame parent, Element description)
    {
        ViewerDialog dialog = new ViewerDialog(parent);
        ConfigurationManager.restorePlacement("DescriptionViewerDialog", dialog, new Rectangle(100,100,300,300));
        dialog.showDescription(description, baseURL);
        dialog.setVisible(true);
    }
}
