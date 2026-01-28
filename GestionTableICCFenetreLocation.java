package controleur;

import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.ICC;
import modele.dao.DaoICC;
import vue.insertion.Fenetre_InsertionLocation;

public class GestionTableICCFenetreLocation implements ListSelectionListener {

    private Fenetre_InsertionLocation fil;
    private DaoICC daoICC;

    public GestionTableICCFenetreLocation(Fenetre_InsertionLocation fil) {
        this.fil = fil;
        this.daoICC = new DaoICC();
        Sauvegarde.initializeSave(); // Initialisation de la sauvegarde
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) { // Vérification pour éviter de réagir à des événements indésirables

            int selectedRowICC = this.fil.getTable_liste_ICC().getSelectedRow();

            if (selectedRowICC > -1) { // Vérification si une ligne est effectivement sélectionnée
                JTable tableICC = this.fil.getTable_liste_ICC();
                ICC icc = null;
                try {
                    // Récupération de l'objet ICC à partir des valeurs de la ligne sélectionnée
                    icc = this.daoICC.findById(tableICC.getValueAt(selectedRowICC, 0).toString(),
                            tableICC.getValueAt(selectedRowICC, 1).toString(),
                            tableICC.getValueAt(selectedRowICC, 2).toString());
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                Sauvegarde.deleteItem("ICC"); // Suppression de l'ancien ICC de la sauvegarde
                Sauvegarde.addItem("ICC", icc); // Ajout du nouvel ICC à la sauvegarde
            }
        }
    }
}
