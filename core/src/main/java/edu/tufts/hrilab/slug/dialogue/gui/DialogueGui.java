/*
 * Copyright © Thinking Robots, Inc., Tufts University, and others 2024.
 */

package edu.tufts.hrilab.slug.dialogue.gui;

import edu.tufts.hrilab.fol.Term;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import edu.tufts.hrilab.slug.common.Utterance;
import edu.tufts.hrilab.slug.dialogue.DiaLog;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogueGui extends JFrame {

  private DefaultListModel dialogueHistory_listModel = new DefaultListModel();
  private DefaultListModel semanticHistory_listModel = new DefaultListModel();
  private DefaultListModel offRecordSemanticHistory_listModel = new DefaultListModel();
  private DiaLog diaLog;

  public DialogueGui(DiaLog diaLog) {
    super("DialogueGUI");
    this.diaLog = diaLog;

    dialogueHistory_list.setModel(dialogueHistory_listModel);
    directSemantics_list.setModel(semanticHistory_listModel);
    indirectSemantics_list.setModel(offRecordSemanticHistory_listModel);

    this.add(dialoguePanel);
    this.setSize(700, 500);
    this.setVisible(true);
  }

  public void updateGui() {

    // update dialogue history
    List<Utterance> dialogueHistory = diaLog.getDialogueHistory();
    if (dialogueHistory.size() != dialogueHistory_listModel.size()) {
      dialogueHistory_listModel.clear();
      Utterance utt;
      for (int i = dialogueHistory.size() - 1; i >= 0; --i) {
        utt = dialogueHistory.get(i);
        dialogueHistory_listModel.addElement(utt);
      }
    }

    // update semantic history
    List<List<Term>> semanticHistory = diaLog.getSemanticHistory();
    if (semanticHistory.size() != semanticHistory_listModel.size()) {
      semanticHistory_listModel.clear();
      for (int i = semanticHistory.size() - 1; i >= 0; --i) {
        semanticHistory_listModel.addElement(semanticHistory.get(i));
      }
    }

    // update off record semantic history
    List<List<Term>> offRecordSemanticHistory = diaLog.getOffRecSemanticHistory();
    if (offRecordSemanticHistory.size() != offRecordSemanticHistory_listModel.size()) {
      offRecordSemanticHistory_listModel.clear();
      for (int i = offRecordSemanticHistory.size() - 1; i >= 0; --i) {
        offRecordSemanticHistory_listModel.addElement(offRecordSemanticHistory.get(i));
      }
    }
  }

  private JList dialogueHistory_list;
  private JList directSemantics_list;
  private JList indirectSemantics_list;
  private JPanel dialoguePanel;
  private JScrollPane dialogueHistory_scrollPn;
  private JLabel dialogueHistory_lbl;
  private JScrollPane directSemantics_scrollPn;
  private JLabel directSemantics_lbl;
  private JScrollPane indirectSemantics_scrollPn;
  private JLabel indirectSemantics_lbl;

  {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    dialoguePanel = new JPanel();
    dialoguePanel.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
    dialogueHistory_scrollPn = new JScrollPane();
    dialoguePanel.add(dialogueHistory_scrollPn, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    dialogueHistory_list = new JList();
    dialogueHistory_scrollPn.setViewportView(dialogueHistory_list);
    dialogueHistory_lbl = new JLabel();
    dialogueHistory_lbl.setText("Dialogue History");
    dialoguePanel.add(dialogueHistory_lbl, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    directSemantics_scrollPn = new JScrollPane();
    dialoguePanel.add(directSemantics_scrollPn, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    directSemantics_list = new JList();
    directSemantics_scrollPn.setViewportView(directSemantics_list);
    directSemantics_lbl = new JLabel();
    directSemantics_lbl.setText("Direct Semantics");
    dialoguePanel.add(directSemantics_lbl, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    indirectSemantics_scrollPn = new JScrollPane();
    dialoguePanel.add(indirectSemantics_scrollPn, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    indirectSemantics_list = new JList();
    indirectSemantics_scrollPn.setViewportView(indirectSemantics_list);
    indirectSemantics_lbl = new JLabel();
    indirectSemantics_lbl.setText("Indirect Semantics");
    dialoguePanel.add(indirectSemantics_lbl, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
  }

  public JComponent $$$getRootComponent$$$() {
    return dialoguePanel;
  }

}