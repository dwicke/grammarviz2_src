package net.seninp.grammarviz.view;

import com.sun.javafx.collections.ListListenerHelper;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import net.seninp.grammarviz.session.UserSession;
import net.seninp.grammarviz.view.table.RPMTableModel;
import net.seninp.grammarviz.view.table.GrammarvizRulesTableColumns;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by david on 1/23/17.
 */
public class GrammarVizRPMPanel extends JPanel implements ListSelectionListener {

    /** Fancy Serial */
    private static final long serialVersionUID = -6017992967964000474L;

    public static final String FIRING_PROPERTY_RPM = "selectedRow_rpm";

    private UserSession session;

    private RPMTableModel RPMTableModel;

    private JXTable RPMTable;

    private JScrollPane RPMPane;

    private ArrayList<String> selectedResults;

    private boolean acceptListEvents;

    // static block - we instantiate the logger
    //
    private static final Logger LOGGER = LoggerFactory.getLogger(GrammarRulesPanel.class);

    public GrammarVizRPMPanel() {
        super();
        this.RPMTableModel = new RPMTableModel();
        this.RPMTable = new JXTable() {
            private static final long serialVersionUID = 4L;

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JXTableHeader(columnModel) {
                    private static final long serialVersionUID = 2L;

                    @Override
                    public void updateUI() {
                        super.updateUI();
                        // need to do in updateUI to survive toggling of LAF
                        if (getDefaultRenderer() instanceof JLabel) {
                            ((JLabel) getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

                        }
                    }
                };
            }
        };

        this.RPMTable.setModel(this.RPMTableModel);
        this.RPMTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        this.RPMTable.setShowGrid(false);

        @SuppressWarnings("unused")
        org.jdesktop.swingx.renderer.DefaultTableRenderer renderer =
                (org.jdesktop.swingx.renderer.DefaultTableRenderer) RPMTable.getDefaultRenderer(String.class);

        TableRowSorter<RPMTableModel> sorter = new TableRowSorter<RPMTableModel>(this.RPMTableModel);
        this.RPMTable.setRowSorter(sorter);

        this.RPMPane = new JScrollPane(this.RPMTable);
    }

    public void resetPanel() {
        // cleanup all the content
        this.removeAll();
        this.add(this.RPMPane);
        this.validate();
        this.repaint();
    }

    public RPMTableModel getPeriodicityTableModel() {return this.RPMTableModel; }

    public JTable getAnomalyTable() {return this.RPMTable; }

    @Override
    public void valueChanged(ListSelectionEvent arg) {
        if (!arg.getValueIsAdjusting() && this.acceptListEvents) {
            int[] rows = this.RPMTable.getSelectedRows();
            LOGGER.debug("Selected ROWS: " + Arrays.toString(rows));
            ArrayList<String> rules = new ArrayList<String>(rows.length);
            for (int i = 0; i < rows.length; i++) {
                int ridx = rows[i];
                String rule = String.valueOf(
                        this.RPMTable.getValueAt(ridx, GrammarvizRulesTableColumns.RULE_NUMBER.ordinal()));
                rules.add(rule);
            }
            this.firePropertyChange(FIRING_PROPERTY_RPM, this.selectedResults, rules);
        }

    }

    public void updateRPMStatistics() {
        this.acceptListEvents = false;
        this.RPMTableModel.update(this.session.rpmHandler.getResults());
        this.acceptListEvents = true;
    }

    public void clear() {
        this.acceptListEvents = false;
        this.removeAll();
        this.session = null;
        RPMTableModel.update(null);
        this.validate();;
        this.repaint();
        this.acceptListEvents = true;
    }

    public void setClassificationResults(UserSession session) {this.session = session; }

}
