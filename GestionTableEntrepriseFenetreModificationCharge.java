package controleur;

import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.Entreprise;
import modele.dao.DaoEntreprise;
import vue.modification.Fenetre_ModificationFactureChargeLogement;

public class GestionTableEntrepriseFenetreModificationCharge implements ListSelectionListener {

	private Fenetre_ModificationFactureChargeLogement fmfcl;
	private DaoEntreprise daoEntreprise;

	// Constructeur prenant en paramètre la fenêtre d'insertion de paiement de logement
	public GestionTableEntrepriseFenetreModificationCharge(Fenetre_ModificationFactureChargeLogement fmfcl) {
		this.fmfcl = fmfcl;
		this.daoEntreprise = new DaoEntreprise();
		Sauvegarde.initializeSave();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			// Récupère l'indice de la ligne sélectionnée dans la table d'entreprise
			int selectedRowEntreprise = this.fmfcl.getTable_entreprise().getSelectedRow();

			// Vérifie si une ligne est effectivement sélectionnée
			if (selectedRowEntreprise > -1) {
				// Récupère la référence à la table d'entreprise
				JTable tableEntreprise = this.fmfcl.getTable_entreprise();
				// Initialise une référence à l'objet Entreprise
				Entreprise entreprise = null;

				try {
					// Récupère l'objet Entreprise à partir des données de la ligne sélectionnée
					// dans la table
					entreprise = this.daoEntreprise.findById(
							tableEntreprise.getValueAt(selectedRowEntreprise, 0).toString(),
							tableEntreprise.getValueAt(selectedRowEntreprise, 1).toString());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				// Supprime l'élément Entreprise précédemment sauvegardé et sauvegarde le nouvel
				// élément
				Sauvegarde.deleteItem("Entreprise");
				Sauvegarde.addItem("Entreprise", entreprise);
			}
		}
	}
}
