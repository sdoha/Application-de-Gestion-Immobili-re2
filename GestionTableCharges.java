package controleur;

import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Facture;
import modele.dao.DaoBien;
import modele.dao.DaoFacture;
import vue.Fenetre_Accueil;

public class GestionTableCharges implements ListSelectionListener {

	private Fenetre_Accueil fenetreAccueil;
	private DaoFacture daoFacture;
	private DaoBien daoBien;

	// Constructeur prenant en paramètre la fenêtre d'acceuil
	public GestionTableCharges(Fenetre_Accueil fenetreAccueil) {
		this.fenetreAccueil = fenetreAccueil;
		this.daoFacture = new DaoFacture();
		this.daoBien = new DaoBien();
		Sauvegarde.initializeSave();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			// Récupère l'indice de la ligne sélectionnée dans la table des charges
			// locatives
			int selectedRowCharge = this.fenetreAccueil.getTableChargesLocatives().getSelectedRow();

			// Vérifie si une ligne est effectivement sélectionnée
			if (selectedRowCharge > -1) {
				// Récupère la référence à la table des charges locatives
				JTable tableFacturesCharges = this.fenetreAccueil.getTableChargesLocatives();
				// Initialise une référence à l'objet Facture
				Facture facture = null;
				Bien bien = null;

				try {
					// Récupère l'objet Facture à partir des données de la ligne sélectionnée dans
					// la table
					facture = this.daoFacture.findById(tableFacturesCharges.getValueAt(selectedRowCharge, 1).toString() // numéro
																														// de
																														// facture
					);

					// Récupère l'objet Bien à partir des données de la ligne sélectionnée dans la
					// table
					bien = this.daoBien.findById(tableFacturesCharges.getValueAt(selectedRowCharge, 0).toString() // Id_Bien
					);

				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				// Supprimer l'élément CHARGE précédemment sauvegardé et sauvegarder le nouvel
				// élément
				Sauvegarde.deleteItem("Charge");
				Sauvegarde.addItem("Charge", facture);
				// Supprimer l'élément LOGEMENT précédemment sauvegardé et sauvegarder le nouvel
				// élément
				Sauvegarde.deleteItem("Logement");
				Sauvegarde.addItem("Logement", bien);
			}
		}
	}

}
