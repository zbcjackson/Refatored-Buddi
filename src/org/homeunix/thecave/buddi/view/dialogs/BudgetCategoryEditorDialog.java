/*
 * Created on Aug 6, 2007 by wyatt
 */
package org.homeunix.thecave.buddi.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import org.homeunix.thecave.buddi.Const;
import org.homeunix.thecave.buddi.i18n.BuddiKeys;
import org.homeunix.thecave.buddi.i18n.keys.BudgetFrameKeys;
import org.homeunix.thecave.buddi.i18n.keys.ButtonKeys;
import org.homeunix.thecave.buddi.model.BudgetCategory;
import org.homeunix.thecave.buddi.model.BudgetCategoryType;
import org.homeunix.thecave.buddi.model.Document;
import org.homeunix.thecave.buddi.model.impl.BudgetCategoryTypeMonthly;
import org.homeunix.thecave.buddi.model.impl.ModelFactory;
import org.homeunix.thecave.buddi.model.prefs.PrefsModel;
import org.homeunix.thecave.buddi.plugin.api.exception.ModelException;
import org.homeunix.thecave.buddi.util.InternalFormatter;
import org.homeunix.thecave.buddi.view.MainFrame;
import org.homeunix.thecave.buddi.view.swing.TranslatorListCellRenderer;
import org.homeunix.thecave.moss.data.list.CompositeList;
import org.homeunix.thecave.moss.swing.MossDialog;
import org.homeunix.thecave.moss.swing.MossHintTextArea;
import org.homeunix.thecave.moss.swing.MossHintTextField;
import org.homeunix.thecave.moss.util.Log;
import org.homeunix.thecave.moss.util.OperatingSystemUtil;

public class BudgetCategoryEditorDialog extends MossDialog implements ActionListener {

	public static final long serialVersionUID = 0;

	private final MossHintTextField name;
	private final JComboBox parent;
	private final JComboBox budgetPeriodType;
	private final JRadioButton income;
	private final JRadioButton expense;
	private final MossHintTextArea notes;

	private final JButton ok;
	private final JButton cancel;

	private final ParentComboBoxModel parentComboBoxModel;

	private final BudgetCategory selected;

	private final Document model;

	public BudgetCategoryEditorDialog(MainFrame frame, Document model, BudgetCategory selected) {
		super(frame);

		this.selected = selected;
		this.model = model;

		name = new MossHintTextField(PrefsModel.getInstance().getTranslator().get(BuddiKeys.HINT_NAME));
		parentComboBoxModel = new ParentComboBoxModel(model);
		parent = new JComboBox(parentComboBoxModel);
		budgetPeriodType = new JComboBox(Const.BUDGET_PERIOD_TYPES); 
		income = new JRadioButton(PrefsModel.getInstance().getTranslator().get(BudgetFrameKeys.BUDGET_EDITOR_INCOME));
		expense = new JRadioButton(PrefsModel.getInstance().getTranslator().get(BudgetFrameKeys.BUDGET_EDITOR_EXPENSE));
		notes = new MossHintTextArea(PrefsModel.getInstance().getTranslator().get(BuddiKeys.HINT_NOTES));

		ok = new JButton(PrefsModel.getInstance().getTranslator().get(ButtonKeys.BUTTON_OK));
		cancel = new JButton(PrefsModel.getInstance().getTranslator().get(ButtonKeys.BUTTON_CANCEL));
	}

	public void init() {
		super.init();
		JPanel textPanel = new JPanel(new BorderLayout());
		JPanel textPanelLeft = new JPanel(new GridLayout(0, 1));
		JPanel textPanelRight = new JPanel(new GridLayout(0, 1));
		textPanel.add(textPanelLeft, BorderLayout.WEST);
		textPanel.add(textPanelRight, BorderLayout.EAST);

		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BudgetFrameKeys.BUDGET_EDITOR_NAME)));
		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BudgetFrameKeys.BUDGET_EDITOR_PARENT)));
		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BudgetFrameKeys.BUDGET_EDITOR_BUDGET_PERIOD_TYPE)));		
		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BudgetFrameKeys.BUDGET_EDITOR_TYPE)));
		textPanelLeft.add(new JLabel(""));
//		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BudgetFrameKeys.BUDGET_EDITOR_NOTES)));

		textPanelRight.add(name);
		textPanelRight.add(parent);
		textPanelRight.add(budgetPeriodType);
		textPanelRight.add(income);
		textPanelRight.add(expense);
//		textPanelRight.add(new JScrollPane(notes));
		
		JScrollPane notesScroller = new JScrollPane(notes);
		notesScroller.setPreferredSize(new Dimension(150, 75));		
		textPanel.add(notesScroller, BorderLayout.SOUTH);


		ButtonGroup group = new ButtonGroup();
		group.add(income);
		group.add(expense);

		ok.setPreferredSize(InternalFormatter.getButtonSize(ok));
		cancel.setPreferredSize(InternalFormatter.getButtonSize(cancel));

		ok.addActionListener(this);
		cancel.addActionListener(this);
		parent.addActionListener(this);

		budgetPeriodType.setRenderer(new TranslatorListCellRenderer());

		name.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);

				updateButtons();
			}
		});

		FocusListener focusListener = new FocusListener(){
			public void focusGained(FocusEvent e) {}
			public void focusLost(FocusEvent e) {
				updateButtons();
			}
		};

		ok.addFocusListener(focusListener);
		cancel.addFocusListener(focusListener);
		name.addFocusListener(focusListener);
		parent.addFocusListener(focusListener);
		budgetPeriodType.addFocusListener(focusListener);
		income.addFocusListener(focusListener);
		expense.addFocusListener(focusListener);
		notes.addFocusListener(focusListener);


		parent.setRenderer(new DefaultListCellRenderer(){
			private static final long serialVersionUID = 0;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				if (value == null)
					this.setText(PrefsModel.getInstance().getTranslator().get(BuddiKeys.NO_PARENT));
				else if (value instanceof BudgetCategory)
					this.setText(PrefsModel.getInstance().getTranslator().get(((BudgetCategory) value).getFullName()));
				else
					this.setText(" ");

				return this;
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		if (OperatingSystemUtil.isMac()){
			buttonPanel.add(cancel);			
			buttonPanel.add(ok);
		}
		else {
			buttonPanel.add(ok);
			buttonPanel.add(cancel);
		}

		this.getRootPane().setDefaultButton(ok);
		this.setLayout(new BorderLayout());
		this.add(textPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	public void updateButtons() {
		super.updateButtons();

		ok.setEnabled(name.getValue() != null && name.getValue().toString().length() > 0);

		budgetPeriodType.setEnabled(parent.getSelectedItem() == null);
		income.setEnabled(parent.getSelectedItem() == null);
		expense.setEnabled(parent.getSelectedItem() == null);

		if (parent.getSelectedItem() != null){
			BudgetCategory bc = (BudgetCategory) parent.getSelectedItem();
			budgetPeriodType.setSelectedItem(bc.getBudgetPeriodType());
			income.setSelected(bc.isIncome());
			expense.setSelected(!bc.isIncome());
		}
	}

	public void updateContent() {
		super.updateContent();

		if (selected == null){
			name.setValue("");
			expense.setSelected(true);
			parent.setSelectedItem(null);
			budgetPeriodType.setSelectedItem(new BudgetCategoryTypeMonthly());
			notes.setValue("");
		}
		else {
			name.setValue(PrefsModel.getInstance().getTranslator().get(selected.getName()));
			if (selected.isIncome())
				income.setSelected(true);
			else
				expense.setSelected(true);
			budgetPeriodType.setSelectedItem(selected.getBudgetPeriodType());
			parent.setSelectedItem(selected.getParent());
			notes.setValue(PrefsModel.getInstance().getTranslator().get(selected.getNotes()));
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(ok)){
			BudgetCategory bc;
			try {
				if (selected == null){
					bc = ModelFactory.createBudgetCategory(name.getValue().toString(), (BudgetCategoryType) budgetPeriodType.getSelectedItem(), income.isSelected());
					bc.setParent((BudgetCategory) parent.getSelectedItem());
					bc.setNotes(notes.getValue().toString());
					Log.debug("Created new BudgetCategory " + bc);

					model.addBudgetCategory(bc);
				}
				else {
					bc = selected;
					bc.setName(name.getValue().toString());
					bc.setParent((BudgetCategory) parent.getSelectedItem());
					bc.setPeriodType((BudgetCategoryType) budgetPeriodType.getSelectedItem());
					bc.setIncome(income.isSelected());
					bc.setNotes(notes.getValue().toString());
				}
			}
			catch (ModelException me){
				Log.error("Error creating budget category", me);
			}

			closeWindow();
		}
		else if (e.getSource().equals(cancel)){
			closeWindow();
		}
		else if (e.getSource().equals(parent)){
			updateButtons();
		}
	}

	private class ParentComboBoxModel extends DefaultComboBoxModel {
		private static final long serialVersionUID = 0; 

		private final List<BudgetCategory> availableParents;
		private int selectedIndex = 0;

		public ParentComboBoxModel(Document model) {
			List<BudgetCategory> blank = new LinkedList<BudgetCategory>();
			blank.add(null);
			List<List<? extends BudgetCategory>> allLists = new LinkedList<List<? extends BudgetCategory>>();
			allLists.add(blank);
			allLists.add(model.getBudgetCategories());
			availableParents = new CompositeList<BudgetCategory>(true, true, allLists);
		}

		public Object getSelectedItem() {
			return availableParents.get(selectedIndex);
		}

		public void setSelectedItem(Object arg0) {
			selectedIndex = availableParents.indexOf(arg0);
		}

		public Object getElementAt(int arg0) {
			return availableParents.get(arg0);
		}

		public int getSize() {
			return availableParents.size();
		}
	}
}
