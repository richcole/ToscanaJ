/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.tockit.swing.preferences.ExtendedPreferences;

public class DescriptionViewer {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(DescriptionViewer.class);

    private static URL baseURL;

    private static class ViewerDialog extends JDialog {
        class Hyperactive implements HyperlinkListener {
            public void hyperlinkUpdate(final HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    final JEditorPane pane = (JEditorPane) e.getSource();
                    if (e instanceof HTMLFrameHyperlinkEvent) {
                        final HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                        final HTMLDocument doc = (HTMLDocument) pane
                                .getDocument();
                        doc.processHTMLFrameHyperlinkEvent(evt);
                    } else {
                        try {
                            pane.setPage(e.getURL());
                        } catch (final Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            }
        }

        private final JEditorPane textArea;

        public ViewerDialog(final Frame frame) {
            // / @todo use HTML title as dialog title
            super(frame, "Description", true);

            final JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    closeDialog();
                }
            });
            getRootPane().setDefaultButton(closeButton);

            // Lay out the buttons from left to right.
            final JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane
                    .setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(closeButton);

            this.textArea = new JEditorPane();
            this.textArea.setContentType("text/html");
            this.textArea.setEditable(false);
            this.textArea.addHyperlinkListener(new Hyperactive());

            final JScrollPane scrollview = new JScrollPane();
            scrollview.getViewport().add(this.textArea);

            // Put everything together, using the content pane's BorderLayout.
            final Container contentPane = getContentPane();
            contentPane.add(scrollview, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);

            this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent e) {
                    closeDialog();
                }
            });
        }

        private void closeDialog() {
            preferences.storeWindowPlacement(this);
            this.setVisible(false);
        }

        private void showDescription(final Element description) {
            if (description.getChild("externalHTML") != null) {
                final String urlAttr = description.getChild("externalHTML")
                        .getAttributeValue("url");
                if (urlAttr != null) {
                    final HTMLDocument doc = (HTMLDocument) this.textArea
                            .getDocument();
                    doc.setBase(baseURL);
                    URL url;
                    try {
                        url = new URL(baseURL, urlAttr);
                        this.textArea.setPage(url);
                    } catch (final MalformedURLException e) {
                        this.textArea.setText("Could not parse the url '"
                                + urlAttr + "':" + e.getMessage());
                    } catch (final IOException e) {
                        this.textArea.setText("Could not open url '" + urlAttr
                                + "':" + e.getMessage());
                    }
                    return;
                }
                this.textArea
                        .setText("Could not find \"url\" attribute on &lt;externalHTML&gt;");
                return;
            }
            if (description.getChild("html") != null) {
                final XMLOutputter outputter = new XMLOutputter();
                outputter.setOmitDeclaration(true);
                this.textArea.setText(outputter.outputString(description
                        .getChild("html")));
                final HTMLDocument doc = (HTMLDocument) this.textArea
                        .getDocument();
                doc.setBase(baseURL);
                return;
            }
            this.textArea
                    .setText("Could not find &lt;externalHTML&gt; or &lt;html&gt; element in the description");
        }
    }

    private DescriptionViewer() {
        // not to be used
    }

    public static void setBaseLocation(final String baseLocation) {
        try {
            baseURL = new URL("file://" + baseLocation);
        } catch (final MalformedURLException e) {
            System.err.println(e.getMessage());
            baseURL = null;
        }
    }

    public static void setBaseURL(final URL baseURL) {
        DescriptionViewer.baseURL = baseURL;
    }

    public static void show(final Frame parent, final Element description) {
        final ViewerDialog dialog = new ViewerDialog(parent);
        preferences.restoreWindowPlacement(dialog, new Rectangle(100, 100, 300,
                300));
        dialog.showDescription(description);
        dialog.setVisible(true);
    }
}
