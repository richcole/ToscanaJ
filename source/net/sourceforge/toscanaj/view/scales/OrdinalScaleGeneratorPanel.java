/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import javax.swing.*;
import javax.swing.event.*;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.context.Attribute;
import net.sourceforge.toscanaj.model.context.BinaryRelationImplementation;
import net.sourceforge.toscanaj.model.context.Context;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAObjectImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAObject;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.Table;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @todo for performance reasons we should start with some extra entry like "<select a column>" instead
 *       of just taking the first column. On a large database querying the average can take a while.
 *       Another option would be querying the min/max/avg in another thread, but this would still put
 *       the load on the RDBMS, while making the code more complex.
 */
public class OrdinalScaleGeneratorPanel extends JPanel {
	boolean result;
	public static final int INTEGER = 0;
	public static final int FLOAT = 1;
	public static final int UNSUPPORTED = -1;

	private JButton addButton;
	private NumberField addField;
	private JList dividersList;
	private JComboBox typeChooser;
	private JComboBox columnChooser;
	private JLabel avgLabel;
	private JLabel minLabel;
	private JLabel maxLabel;

	private DefaultListModel dividersModel;

	private Column column;
	private DatabaseSchema databaseSchema;
	private DatabaseConnection connection;
  
	private static interface ContextGenerator {
		Context createContext(String name, List dividers, Column column);
	}
    
	private static abstract class SingleDimensionScaleGenerator implements ContextGenerator {
		public Context createContext(String name, List dividers, Column column) {
			ContextImplementation context = new ContextImplementation();
			context.setName(name);
			for (int i = -1; i < dividers.size(); i++) {
				String objectData = createSQLClause(column.getSqlExpression(), dividers, i);
				WritableFCAObject object = new FCAObjectImplementation(objectData);
				String attributeName = createAttributeName(dividers, i);
				context.getObjects().add(object);
				if(attributeName != null) {
					context.getAttributes().add(new Attribute(attributeName));
				}
				Iterator it = context.getAttributes().iterator();
				while (it.hasNext()) {
					Attribute attribute = (Attribute) it.next();
					context.getRelationImplementation().insert(object,attribute);
				}
			}
			return context;
		}

		public String createAttributeName(List dividers, int i) {
			if(i == -1){
				return null;
			}
			return getForwardSymbol() + String.valueOf(dividers.get(getPosition(i, dividers.size())));
		}

		public String createSQLClause(String columnName, List dividers, int i) {
			if(i == -1) {
				return "(" + columnName + getBackwardSymbol() + String.valueOf(dividers.get(getPosition(0, dividers.size()))) + ")";
			}
			String retVal = "(" + columnName + getForwardSymbol() + String.valueOf(dividers.get(getPosition(i, dividers.size()))) + ")";
			if (i < dividers.size() - 1) {
				retVal += " AND (" + columnName + getBackwardSymbol() + String.valueOf(dividers.get(getPosition(i + 1, dividers.size()))) + ")";
			}
			return retVal;
		}

		protected abstract int getPosition(int i, int max);
		protected abstract String getForwardSymbol();
		protected abstract String getBackwardSymbol();

	} 
    
	private static class IncreasingExclusiveGenerator extends SingleDimensionScaleGenerator {
		public String toString() {
			return "increasing, exclude bounds";
		}
		protected int getPosition(int i, int max) {
			return i;
		}
		protected String getForwardSymbol() {
			return ">";
		}
		protected String getBackwardSymbol() {
			return "<=";
		}
	}

	private static class IncreasingInclusiveGenerator extends SingleDimensionScaleGenerator {
		public String toString() {
			return "increasing, include bounds";
		}
		protected int getPosition(int i, int max) {
			return i;
		}
		protected String getForwardSymbol() {
			return ">=";
		}
		protected String getBackwardSymbol() {
			return "<";
		}
	}

	private static class DecreasingExclusiveGenerator extends SingleDimensionScaleGenerator {
		public String toString() {
			return "decreasing, exclude bounds";
		}
		protected int getPosition(int i, int max) {
			return max - i -1;
		}
		protected String getForwardSymbol() {
			return "<";
		}
		protected String getBackwardSymbol() {
			return ">=";
		}
	}

	private static class DecreasingInclusiveGenerator extends SingleDimensionScaleGenerator {
		public String toString() {
			return "decreasing, include bounds";
		}
		protected int getPosition(int i, int max) {
			return max - i -1;
		}
		protected String getForwardSymbol() {
			return "<=";
		}
		protected String getBackwardSymbol() {
			return ">";
		}
	}
    
	/**
	 * @todo there is another case, which mirrors this but puts the equals on
	 * the other direction ==> implement.
	 */
	private static abstract class InterordinalGenerator implements ContextGenerator {
		public Context createContext(String name, List dividers, Column column) {
			ContextImplementation context = new ContextImplementation();
			context.setName(name);
			int numDiv = dividers.size();
			Attribute[] upwardsAttributes = new Attribute[numDiv];
			Attribute[] downwardsAttributes = new Attribute[numDiv];
			for (int i = 0; i < numDiv; i++) {
				upwardsAttributes[i] = getUpwardsAttribute(dividers, i);
				downwardsAttributes[i] = getDownwardsAttribute(dividers, i);
				context.getAttributes().add(upwardsAttributes[i]);
				context.getAttributes().add(downwardsAttributes[i]);
			}
			BinaryRelationImplementation relation = context.getRelationImplementation();
			for (int i = -1; i < numDiv; i++) {
				String object;
				if( i == -1) {
					object = column.getSqlExpression() + " " + downwardsAttributes[i+1];
				} else if (i == numDiv - 1) {
					object = column.getSqlExpression() + " " + upwardsAttributes[i];
				} else {
					object = column.getSqlExpression() + " " + upwardsAttributes[i] + " AND " +
							 column.getSqlExpression() + " " + downwardsAttributes[i+1];
				}
				for(int j = 0; j <= i; j++) {
					relation.insert(object, upwardsAttributes[j]);
				}
				for(int j = i + 1; j < numDiv; j++ ) {
					relation.insert(object, downwardsAttributes[j]);
				}
				context.getObjects().add(new FCAObjectImplementation(object));
			}
			return context;
		}

		protected abstract Attribute getUpwardsAttribute(List dividers, int i);
		protected abstract Attribute getDownwardsAttribute(List dividers, int i);
	}
    
	private static class Type1InterordinalGenerator extends InterordinalGenerator {
		protected Attribute getUpwardsAttribute(List dividers, int i) {
			return new Attribute(">= " + dividers.get(i));
		}
		protected Attribute getDownwardsAttribute(List dividers, int i) {
			return new Attribute("< " + dividers.get(i));
		}
		public String toString() {
			return "both, increasing side includes bounds";
		}
	}

	private static class Type2InterordinalGenerator extends InterordinalGenerator {
		protected Attribute getUpwardsAttribute(List dividers, int i) {
			return new Attribute("> " + dividers.get(i));
		}
		protected Attribute getDownwardsAttribute(List dividers, int i) {
			return new Attribute("<= " + dividers.get(i));
		}
		public String toString() {
			return "both, decreasing side includes bounds";
		}
	}

	public OrdinalScaleGeneratorPanel(DatabaseSchema databaseSchema, DatabaseConnection connection) {
		setSize(400,600);
		setLocation(200,100);
		this.databaseSchema = databaseSchema;
		this.connection = connection;
		layoutPanel();
		fillControls();
	}
	
	public void addDividerListListener(ListDataListener listener) {
		this.dividersModel.addListDataListener(listener);	
	}

	private void layoutPanel() {
		setLayout(new GridBagLayout());
        
		add(makeColumnChooserPane(),new GridBagConstraints(
					0,0,1,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		
		add(makeTypeOptionPane(),
				new GridBagConstraints(
						0, 1, 1, 1, 1, 0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(2, 2, 2, 2),
						0, 0
				)
		);

		add(makeDividerPane(), new GridBagConstraints(
					0,2,1,1,1,1,
					GridBagConstraints.CENTER,
					GridBagConstraints.BOTH,
					new Insets(2,2,2,0),
					2,2
		));
	}

	private JPanel makeColumnChooserPane() {
		this.columnChooser = new JComboBox();
		this.columnChooser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				if(column != cb.getSelectedItem()) {
					column = ((TableColumnPair) cb.getSelectedItem()).getColumn();
					if(determineDataType(column.getType()) == FLOAT) {
						addField.setNumberType(NumberField.FLOAT);
					} else {
						addField.setNumberType(NumberField.INTEGER);
					}
					updateRangeInfo();
				}
			}
		});

		return new LabeledPanel("Column:", this.columnChooser, false);
	}
    
	protected void updateRangeInfo() {
		String tail = " FROM " + this.column.getTable().getSqlExpression() + ";";
		String minQu = "SELECT min(" + this.column.getSqlExpression() + ")" + tail;
		String maxQu = "SELECT max(" + this.column.getSqlExpression() + ")" + tail;
		String avgQu = "SELECT avg(" + this.column.getSqlExpression() + ")" + tail;
		try {
			if (determineDataType(this.column.getType()) == FLOAT) {
				double min = this.connection.queryDouble(minQu,1);
				double max = this.connection.queryDouble(maxQu,1);
				double avg = this.connection.queryDouble(avgQu,1);
				this.minLabel.setText("Min: "+createFormattedNumberString(min));
				this.maxLabel.setText("Max: "+createFormattedNumberString(max));
				this.avgLabel.setText("Average: "+createFormattedNumberString(avg));
			} else {
				int min = this.connection.queryInt(minQu,1);
				int max = this.connection.queryInt(maxQu,1);
				double avg = this.connection.queryDouble(avgQu,1);
				this.minLabel.setText("Min: "+createFormattedNumberString(min));
				this.maxLabel.setText("Max: "+createFormattedNumberString(max));
				this.avgLabel.setText("Average: "+ createFormattedNumberString(avg));
			}
		} catch (DatabaseException e) {
			ErrorDialog.showError(this,e,"Database query failed");
			this.minLabel.setText("");
			this.maxLabel.setText("");
			this.avgLabel.setText("");
		}
	}

	private String createFormattedNumberString(double numToRound) {
		if( numToRound< 10 ) {
		    return new DecimalFormat(".######").format(numToRound);
		} else if( numToRound< 100 ) {
			return new DecimalFormat(".####").format(numToRound);
		} else {
			return new DecimalFormat(".##").format(numToRound);
		}
	}

	protected JPanel makeDividerPane() {
		dividersModel = new DefaultListModel();
		
		dividersList = new JList(dividersModel);
		dividersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		
		final JButton removeButton = new JButton("Remove");
		removeButton.setEnabled(hasSelectedDivider());
		
		dividersList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			   public void valueChanged(ListSelectionEvent e) {
				   removeButton.setEnabled(e.getFirstIndex() != -1);
			   }
		   });
		   dividersModel.addListDataListener(new ListDataListener() {
			   private void updateRemoveButton() {
				   removeButton.setEnabled(dividersModel.getSize() > 0 && hasSelectedDivider());
			   }
		
			   public void intervalAdded(ListDataEvent e) {
				   updateRemoveButton();
			   }
		
			   public void intervalRemoved(ListDataEvent e) {
				   updateRemoveButton();
			   }
		
			   public void contentsChanged(ListDataEvent e) {
				   updateRemoveButton();
			   }
		   });
		
		   JPanel removeButtonPane = new JPanel(new GridBagLayout());
		   
		
		   removeButton.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
				   int[] selected = dividersList.getSelectedIndices();
				   for (int i = selected.length; --i >= 0;) {
					   removeDivider(selected[i]);
				   }
			   }
		   });
		
			final JButton removeAllButton = new JButton("Remove All");
			removeAllButton.setEnabled(false);
			dividersModel.addListDataListener(new ListDataListener() {
			  private void updateRemoveButton() {
				  removeAllButton.setEnabled(dividersModel.size() != 0);
			  }

			  public void intervalAdded(ListDataEvent e) {
				  updateRemoveButton();
			  }

			  public void intervalRemoved(ListDataEvent e) {
				  updateRemoveButton();
			  }

			  public void contentsChanged(ListDataEvent e) {
				  updateRemoveButton();
			  }
		  });
				removeAllButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeAllDividers();
				}
			});
			
		removeButtonPane.add(removeButton, new GridBagConstraints(
					0,0,1,1,1.0,0,
					GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		removeButtonPane.add(removeAllButton, new GridBagConstraints(
					1,0,1,1,1.0,0,
					GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		
		JPanel centerPane = new JPanel(new GridBagLayout());
		
		
		
		centerPane.add(makeAddDividerPanel(), new GridBagConstraints(
					0,0,1,2,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,0,2,0),
					2,2
		));
		centerPane.add(new JScrollPane(dividersList), new GridBagConstraints(
					1,0,1,1,1.0,1.0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH,
					new Insets(2,2,2,0),
					2,2
		)); 
		centerPane.add(removeButtonPane, new GridBagConstraints(
					1,1,1,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		centerPane.setBorder(BorderFactory.createEtchedBorder());
		
		return new LabeledPanel("Dividers:", centerPane, false);
	}

	private void fillControls() {
		this.columnChooser.removeAllItems();
		List tables = this.databaseSchema.getTables();
		for (Iterator tableIterator = tables.iterator(); tableIterator.hasNext();) {
			Table table = (Table) tableIterator.next();
			List columns = table.getColumns();
			for (Iterator columnsIterator = columns.iterator(); columnsIterator.hasNext();) {
				Column column = (Column) columnsIterator.next();
				if(determineDataType(column.getType()) != UNSUPPORTED) {
					this.columnChooser.addItem(new TableColumnPair(table, column));
				}
			}
		}
	}

	public void removeDivider(int i) {
		dividersModel.removeElementAt(i);
	}

	private boolean hasSelectedDivider() {
		return dividersList.getSelectedIndex() != -1;
	}

	public void removeAllDividers() {
		dividersModel.removeAllElements();
	}

	private JPanel makeAddDividerPanel() {
		JPanel addPanel = new JPanel();
		JLabel enterValueLabel = new JLabel("Enter Value:");
		addPanel.setLayout(new GridBagLayout());
		addField = new NumberField(10, NumberField.FLOAT);
		addButton = new JButton("Add");
		addButton.setEnabled(false);

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDelimiter();
			}
		});

		addField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				processDocumentEvent(e);
			}

			public void removeUpdate(DocumentEvent e) {
				processDocumentEvent(e);
			}

			public void changedUpdate(DocumentEvent e) {
				processDocumentEvent(e);
			}

			private void processDocumentEvent(DocumentEvent e) {
				addButton.setEnabled(addField.isValid());
			}
		});

		addField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addDelimiter();
			}
		});
        
		this.minLabel = new JLabel();
		this.maxLabel = new JLabel();
		this.avgLabel = new JLabel();
        
		addPanel.add(enterValueLabel, new GridBagConstraints(
					0,0,2,1,0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE,
					new Insets(0,2,0,2),
					2,2
		));

		addPanel.add(addField, new GridBagConstraints(
					0,1,1,1,1,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(0,2,0,2),
					2,2
		));
		addPanel.add(addButton,new GridBagConstraints(
					1,1,1,1,0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE,
					new Insets(0,2,0,2),
					2,2
		));
		addPanel.add(this.minLabel,new GridBagConstraints(
					0,2,2,1,1,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(0,2,0,2),
					2,2
		));
		addPanel.add(this.maxLabel,new GridBagConstraints(
					0,3,2,1,1,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(0,2,0,2),
					2,2
		));
		addPanel.add(this.avgLabel,new GridBagConstraints(
					0,4,2,1,1,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(0,2,0,2),
					2,2
		));
		return addPanel;
	}

	private void addDelimiter() {
		if (determineDataType(this.column.getType()) == FLOAT) {
			addDelimiter(addField.getDoubleValue());
		} else {
			addDelimiter(addField.getIntegerValue());
		}
	}

	public void addDelimiter(double value) {
		int i;
		for (i = 0; i < dividersModel.size(); i++) {
			final double currDivider = ((Double) dividersModel.elementAt(i)).doubleValue();
			if (value == currDivider) {
				return;
			}
			if (value < currDivider) {
				break;
			}
		}
		dividersModel.insertElementAt(new Double(value), i);
		addField.setText("");
	}

	public void addDelimiter(int value) {
		int i;
		for (i = 0; i < dividersModel.size(); i++) {
			final int currDivider = ((Integer) dividersModel.elementAt(i)).intValue();
			if (value == currDivider) {
				return;
			}
			if (value < currDivider) {
				break;
			}
		}
		dividersModel.insertElementAt(new Integer(value), i);
		addField.setText("");
	}

	public java.util.List getDividers() {
		return Arrays.asList(dividersModel.toArray());
	}

	private JPanel makeTypeOptionPane() {
		this.typeChooser = new JComboBox(new ContextGenerator[]{
				new IncreasingExclusiveGenerator(),
				new IncreasingInclusiveGenerator(),
				new DecreasingExclusiveGenerator(),
				new DecreasingInclusiveGenerator(),
				new Type1InterordinalGenerator(),
				new Type2InterordinalGenerator()} );
		return new LabeledPanel("Type:", this.typeChooser, false);
	}

	public static int determineDataType(int columnType) {
		switch (columnType) {
			case Types.DOUBLE:
				return OrdinalScaleGeneratorPanel.FLOAT;
			case Types.FLOAT:
				return OrdinalScaleGeneratorPanel.FLOAT;
			case Types.REAL:
				return OrdinalScaleGeneratorPanel.FLOAT;
			case Types.BIGINT:
				return OrdinalScaleGeneratorPanel.INTEGER;
			case Types.INTEGER:
				return OrdinalScaleGeneratorPanel.INTEGER;
			case Types.SMALLINT:
				return OrdinalScaleGeneratorPanel.INTEGER;
			case Types.TINYINT:
				return OrdinalScaleGeneratorPanel.INTEGER;
			default:
				return OrdinalScaleGeneratorPanel.UNSUPPORTED;
		}
	}
	
	public Context createContext(String title) {
		ContextGenerator generator = (ContextGenerator) this.typeChooser.getSelectedItem();
		return generator.createContext(title, getDividers(), getColumn());
	}
	
	protected JList getDividersList(){
		return this.dividersList;
	}
	
	protected Column getColumn(){
		return this.column;
	}
	
	protected DefaultListModel getDividersModel(){
		return this.dividersModel;
	}
	
	protected JComboBox getTypeChooser(){
		return this.typeChooser;
	}
}
