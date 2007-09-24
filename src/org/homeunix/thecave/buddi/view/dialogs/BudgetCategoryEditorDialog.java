/*
 * Created on Aug 6, 2007 by wyatt
 */
package org.homeunix.thecave.buddi.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Arrays;

import javax.swing.ButtonGroup;
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
import org.homeunix.thecave.buddi.i18n.keys.BudgetCategoryTypes;
import org.homeunix.thecave.buddi.i18n.keys.ButtonKeys;
import org.homeunix.thecave.buddi.model.BudgetCategory;
import org.homeunix.thecave.buddi.model.Document;
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
import org.homeunix.thecave.moss.swing.MossScrollingComboBox;
import org.homeunix.thecave.moss.swing.model.BackedComboBoxModel;
import org.homeunix.thecave.moss.util.Log;
import org.homeunix.thecave.moss.util.OperatingSystemUtil;

public class BudgetCategoryEditorDialog extends MossDialog implements ActionListener {

	public static final long serialVersionUID = 0;

	private final MossHintTextField name;
	private final JComboBox parent;
	private final JComboBox budgetCategoryType;
	private final JRadioButton income;
	private final JRadioButton expense;
	private final MossHintTextArea notes;

	private final JButton ok;
	private final JButton cancel;

	private final BudgetCategory selected;

	private final Document model;

	@SuppressWarnings("unchecked")
	public BudgetCategoryEditorDialog(MainFrame frame, Document model, BudgetCategory selected) {
		super(frame);

		this.selected = selected;
		this.model = model;

		name = new MossHintTextField(PrefsModel.getInstance().getTranslator().get(BuddiKeys.HINT_NAME));
		parent = new MossScrollingComboBox(new BackedComboBoxModel<BudgetCategory>(new CompositeList<BudgetCategory>(true, true, Arrays.asList(new BudgetCategory[]{null}), model.getBudgetCategories())));
		if (parent.getModel().getSize() > 0)
			parent.setSelectedIndex(0);
		budgetCategoryType = new JComboBox(BudgetCategoryTypes.values()); 
		income = new JRadioButton(PrefsModel.getInstance().getTranslator().get(BuddiKeys.BUDGET_EDITOR_INCOME));
		expense = new JRadioButton(PrefsModel.getInstance().getTranslator().get(BuddiKeys.BUDGET_EDITOR_EXPENSE));
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

		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BuddiKeys.BUDGET_EDITOR_NAME)));
		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BuddiKeys.BUDGET_EDITOR_PARENT)));
		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BuddiKeys.BUDGET_EDITOR_BUDGET_PERIOD_TYPE)));		
		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BuddiKeys.BUDGET_EDITOR_TYPE)));
		textPanelLeft.add(new JLabel(""));
//		textPanelLeft.add(new JLabel(PrefsModel.getInstance().getTranslator().get(BuddiKeys.BUDGET_EDITOR_NOTES)));

		textPanelRight.add(name);
		textPanelRight.add(parent);
		textPanelRight.add(budgetCategoryType);
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

		budgetCategoryType.setRenderer(new TranslatorListCellRenderer());

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
		budgetCategoryType.addFocusListener(focusListener);
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
				else if (value instanceof BudgetCategory){
					BudgetCategory bc = (BudgetCategory) value;
					this.setText(PrefsModel.getInstance().getTranslator().get(bc.getFullName()));

					if (isSelected)
						this.setForeground(Color.WHITE);
					else
						this.setForeground((bc.isIncome() ? Const.COLOR_JLIST_UNSELECTED_TEXT : Color.RED));
				}
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

		ok.setEnabled(name.getText() != null && name.getText().length() > 0);

		budgetCategoryType.setEnabled(parent.getSelectedItem() == null);
		income.setEnabled(parent.getSelectedItem() == null);
		expense.setEnabled(parent.getSelectedItem() == null);

		if (parent.getSelectedItem() != null){
			BudgetCategory bc = (BudgetCategory) parent.getSelectedItem();
			budgetCategoryType.setSelectedItem(bc.getBudgetPeriodType());
			income.setSelected(bc.isIncome());
			expense.setSelected(!bc.isIncome());
		}
	}

	public void updateContent() {
		super.updateContent();

		if (selected == null){
			name.setText("");
			expense.setSelected(true);
			parent.setSelectedItem(null);
			budgetCategoryType.setSelectedItem(ModelFactory.getBudgetCategoryType(BudgetCategoryTypes.BUDGET_CATEGORY_TYPE_MONTH));
			notes.setText("");
		}
		else {
			name.setText(PrefsModel.getInstance().getTranslator().get(selected.getName()));
			if (selected.isIncome())
				income.setSelected(true);
			else
				expense.setSelected(true);
			budgetCategoryType.setSelectedItem(BudgetCategoryTypes.valueOf(selected.getBudgetPeriodType().getName()));
			parent.setSelectedItem(selected.getParent());
			notes.setText(PrefsModel.getInstance().getTranslator().get(selected.getNotes()));
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(ok)){
			BudgetCategory bc;
			try {
				if (selected == null){
					bc = ModelFactory.createBudgetCategory(name.getText(), ModelFactory.getBudgetCategoryType(budgetCategoryType.getSelectedItem().toString()), income.isSelected());
					bc.setParent((BudgetCategory) parent.getSelectedItem());
					bc.setNotes(notes.getText());
					Log.debug("Created new BudgetCategory " + bc);

					model.addBudgetCategory(bc);
				}
				else {
					bc = selected;
					bc.setName(name.getText());
					bc.setParent((BudgetCategory) parent.getSelectedItem());
					bc.setPeriodType(ModelFactory.getBudgetCategoryType(budgetCategoryType.getSelectedItem().toString()));
					bc.setIncome(income.isSelected());
					bc.setNotes(notes.getText());
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
}
