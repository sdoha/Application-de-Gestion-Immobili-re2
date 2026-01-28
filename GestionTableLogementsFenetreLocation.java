package controleur;

import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.dao.DaoBien;
import vue.insertion.Fenetre_InsertionLocation;

public class GestionTableLogementsFenetreLocation implements ListSelectionListener {

    private Fenetre_InsertionLocation fenetreInsertionLocation;
    private DaoBien daoBien;

    // Constructeur prenant en paramètre la fenêtre d'insertion de location
    public GestionTableLogementsFenetreLocation(Fenetre_InsertionLocation fenetreInsertionLocation) {
        this.fenetreInsertionLocation = fenetreInsertionLocation;
        this.daoBien = new DaoBien();
        Sauvegarde.initializeSave();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // Vérifie si la sélection dans la table de logements a changé
            int selectedRowLogement = this.fenetreInsertionLocation.getTable_liste_logements().getSelectedRow();

            if (selectedRowLogement > -1) {
                // Si une ligne est sélectionnée
                JTable tableLogement = this.fenetreInsertionLocation.getTable_liste_logements();
                Bien bien = null;
                try {
                    // Récupération de l'objet Bien associé à la ligne sélectionnée
                    bien = this.daoBien.findById(tableLogement.getValueAt(selectedRowLogement, 0).toString());
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                // Mise à jour de la sauvegarde avec l'objet Bien sélectionné
                Sauvegarde.deleteItem("Logement");
                Sauvegarde.addItem("Logement", bien);
            }
        }
    }
}
