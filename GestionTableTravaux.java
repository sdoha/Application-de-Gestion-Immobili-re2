package controleur;

import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.Facture;
import modele.dao.DaoFacture;
import vue.Fenetre_Accueil;

public class GestionTableTravaux implements ListSelectionListener {

	private Fenetre_Accueil fenetreAccueil;
	private DaoFacture daoFacture;
	// Constructeur prenant en paramètre la fenêtre d'acceuil
	public GestionTableTravaux(Fenetre_Accueil fenetreAccueil) {
		this.fenetreAccueil = fenetreAccueil;
		this.daoFacture = new DaoFacture();
		Sauvegarde.initializeSave();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			// Vérifie si la sélection dans la table des travaux a changé
			int selectedRowTravaux = this.fenetreAccueil.getTableTravaux().getSelectedRow();

			if (selectedRowTravaux > -1) {
				// Si une ligne est sélectionnée
				JTable tableTravaux = this.fenetreAccueil.getTableTravaux();
				Facture travaux = null;
				try {
					// Récupération de l'objet Facture associé à la ligne sélectionnée
					travaux = this.daoFacture.findById(tableTravaux.getValueAt(selectedRowTravaux, 0).toString());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				// Mise à jour de la sauvegarde avec l'objet Facture sélectionné
				Sauvegarde.deleteItem("Facture");
				Sauvegarde.addItem("Facture", travaux);
			}
		}
	}
}
