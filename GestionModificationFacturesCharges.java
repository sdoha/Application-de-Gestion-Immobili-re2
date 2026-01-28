package controleur.modification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Entreprise;
import modele.Facture;
import modele.dao.DaoBien;
import modele.dao.DaoEntreprise;
import modele.dao.DaoFacture;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionEntreprise;
import vue.modification.Fenetre_ModificationFactureChargeLogement;

public class GestionModificationFacturesCharges implements ActionListener {

	private Fenetre_ModificationFactureChargeLogement modificationFacturesCharge;
	private DaoFacture daoFacture;
	private DaoEntreprise daoEntreprise;

	public GestionModificationFacturesCharges(Fenetre_ModificationFactureChargeLogement modificationCharge) {
		// Initialisation du gestionnaire avec la fenêtre de modification des factures
		// et charges
		this.modificationFacturesCharge = modificationCharge;
		// Initialisation des objets d'accès aux données (DAO)
		this.daoFacture = new DaoFacture();
		new DaoBien();
		this.daoEntreprise = new DaoEntreprise();
		// Initialisation de la sauvegarde (peut être utilisée pour stocker des données
		// temporaires)
		Sauvegarde.initializeSave();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JButton) {
			// Gérer les événements des boutons
			JButton btn = (JButton) source;
			Fenetre_Accueil fenetrePrincipale = (Fenetre_Accueil) this.modificationFacturesCharge.getTopLevelAncestor();

			switch (btn.getText()) {
			case "Modifier":
				// Gérer l'action du bouton "Modifier"
				try {
					int imputable = 0;
					if (this.modificationFacturesCharge.getRdbtnOui().isSelected()) {
						imputable = 1;
					}

					Bien bienSauvegarde = (Bien) Sauvegarde.getItem("Logement");
					Entreprise entrepriseSauvegarde = (Entreprise) Sauvegarde.getItem("Entreprise");

					Facture nouvelleCharge = new Facture(
							this.modificationFacturesCharge.getTextField_Numero().getText(),
							this.modificationFacturesCharge.getTextField_date_emission().getText(),
							this.modificationFacturesCharge.getTextField_date_paiement().getText(),
							this.modificationFacturesCharge.getComboBox_modePaiement().getSelectedItem().toString(),
							this.modificationFacturesCharge.getTextField_numeroDevis().getText(),
							this.modificationFacturesCharge.getComboBox_Designation().getSelectedItem().toString(),
							Double.parseDouble(this.modificationFacturesCharge.getTextField_accompteVerse().getText()),
							Double.parseDouble(this.modificationFacturesCharge.getTextField_montant().getText()),
							imputable, null, bienSauvegarde, entrepriseSauvegarde);

					this.daoFacture.update(nouvelleCharge);

					this.modificationFacturesCharge.dispose(); // Fermer la page après l'ajout

				} catch (Exception e1) {
					e1.printStackTrace();
				}
				break;

			case "Annuler":
				// Gérer l'action du bouton "Annuler"
				this.modificationFacturesCharge.dispose();
				break;
			case "Charger":
				// Gérer l'action du bouton "Charger"
				try {
					this.chargerEntreprise();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				break;

			case "Insérer":
				// Gérer l'action du bouton "Insérer"
				Fenetre_InsertionEntreprise insertionEntreprise = new Fenetre_InsertionEntreprise();
				modificationFacturesCharge.getLayeredPane().add(insertionEntreprise);
				insertionEntreprise.setVisible(true);
				insertionEntreprise.moveToFront();
				break;
			}

		} else if (source instanceof JComboBox) {
			// Gérer les événements de JComboBox
			JComboBox<?> comboBox = (JComboBox<?>) source;

		} else {
			// Gérer les autres sources d'événements inattendues
			System.out.println("Unexpected event source: " + source);
		}
		// Méthode appelée lorsque l'état de l'élément du JComboBox change
		updateEntrepriseComponents();
	}

	/**
	 * Méthode pour mettre à jour les composants liés à l'entreprise en fonction de
	 * la désignation
	 *
	 */
	private void updateEntrepriseComponents() {
		Object selectedObject = this.modificationFacturesCharge.getComboBox_Designation().getSelectedItem();

		if (selectedObject != null) {
			String selectedDesignation = selectedObject.toString();

			if (selectedDesignation.equals("Loyer")) {
				// Masquer les composants liés à l'entreprise pour les autres options
				this.modificationFacturesCharge.getBtn_ajouter_entreprise().setVisible(false);
				this.modificationFacturesCharge.getBtn_charger_entreprise().setVisible(false);
				this.modificationFacturesCharge.getScrollPane_table_entreprise().setVisible(false);
				this.modificationFacturesCharge.getTable_entreprise().setVisible(false);
				this.modificationFacturesCharge.getLbl_Entreprise().setVisible(false);
				// Afficher les composants liés à l'entreprise pour d'autres options
			} else {
				this.modificationFacturesCharge.getBtn_ajouter_entreprise().setVisible(true);
				this.modificationFacturesCharge.getBtn_charger_entreprise().setVisible(true);
				this.modificationFacturesCharge.getScrollPane_table_entreprise().setVisible(true);
				this.modificationFacturesCharge.getTable_entreprise().setVisible(true);
				this.modificationFacturesCharge.getLbl_Entreprise().setVisible(true);
			}

		}
	}

	// Méthode pour écrire une ligne d'entreprise dans la table d'entreprise
	/**
	 * @param numeroLigne (int) : prend en parametre le numero de la ligne du
	 *                    tableau
	 * @param e           (Entreprise) : prend en parametre l'entreprise pour
	 *                    l'inserer dans le tableau
	 */
	public void ecrireLigneTableEntreprise(int numeroLigne, Entreprise e) {
		JTable tableEntreprise = this.modificationFacturesCharge.getTable_entreprise();
		DefaultTableModel modeleTable = (DefaultTableModel) tableEntreprise.getModel();

		modeleTable.setValueAt(e.getSiret(), numeroLigne, 0);
		modeleTable.setValueAt(e.getNom(), numeroLigne, 1);
	}

	//
	/**
	 * Méthode pour charger les entreprises dans la table d'entreprise
	 */
	private void chargerEntreprise() throws SQLException {
		List<Entreprise> entreprises = this.daoEntreprise.findAll();

		DefaultTableModel modeleTable = (DefaultTableModel) this.modificationFacturesCharge.getTable_entreprise()
				.getModel();

		modeleTable.setRowCount(entreprises.size());

		for (int i = 0; i < entreprises.size(); i++) {
			Entreprise e = entreprises.get(i);
			this.ecrireLigneTableEntreprise(i, e);
		}
	}
}
