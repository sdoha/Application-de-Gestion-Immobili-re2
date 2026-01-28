package controleur.insertion;

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
import modele.dao.DaoEntreprise;
import modele.dao.DaoFacture;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionEntreprise;
import vue.insertion.Fenetre_InsertionPaiementLogement;

public class GestionInsertionPaiementLogement implements ActionListener {

	private Fenetre_InsertionPaiementLogement fipl;
	private DaoFacture daoFacture;
	private DaoEntreprise daoEntreprise;

	// Constructeur prenant en paramètre la fenêtre d'insertion de paiement de
	// logement
	public GestionInsertionPaiementLogement(Fenetre_InsertionPaiementLogement fit) {
		this.fipl = fit;
		// Initialisation de l'accès à la base de données pour l'entité Facture
		this.daoFacture = new DaoFacture();
		// Initialisation de l'accès à la base de données pour l'entité Entreprise
		this.daoEntreprise = new DaoEntreprise();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JButton) {
			JButton bouton = (JButton) source;
			Fenetre_Accueil fenetrePrincipale = (Fenetre_Accueil) this.fipl.getTopLevelAncestor();

			// Gestion des actions en fonction du bouton cliqué
			switch (bouton.getText()) {
			case "Ajouter":
				// Création d'un objet Facture à null
				Facture facture = null;
				// Récupérer le Logement de la sauvegarde
				Bien bienSauvegarde = (Bien) Sauvegarde.getItem("Logement");
				// Récupérer l'entreprise de la sauvegarde
				Entreprise entrepriseSauvegarde = (Entreprise) Sauvegarde.getItem("Entreprise");

				// Savoir si le locataire est imputable
				int imputable = 0;
				if (this.fipl.getRdbtnOui().isSelected()) {
					imputable = 1;
				}

				// Si la désignation de la facture est "Loyer", on insère null pour l'entreprise
				// car celui qui génère la facture c'est le propriétaire du bien
				String selectedDesignation = this.fipl.getComboBox_Designation().getSelectedItem().toString();

				if ("Loyer".equals(selectedDesignation)) {
					try {
						// Création d'un objet Facture à partir des données saisies dans la fenêtre
						facture = new Facture(this.fipl.getTextField_Numero().getText(),
								this.fipl.getTextField_date_emission().getText(),
								this.fipl.getTextField_date_paiement().getText(),
								this.fipl.getComboBox_modePaiement().getSelectedItem().toString(),
								this.fipl.getTextField_numeroDevis().getText(),
								this.fipl.getComboBox_Designation().getSelectedItem().toString(),
								Double.parseDouble(this.fipl.getTextField_accompteVerse().getText()),
								Double.parseDouble(this.fipl.getTextField_montant().getText()), imputable, null,
								bienSauvegarde, null);
						// Enregistrement de la facture dans la base de données
						this.daoFacture.create(facture);
					} catch (Exception e1) {
						e1.printStackTrace();
						System.err.println("Erreur lors de l'ajout de la facture : " + e1.getMessage());
					}
					// Sinon, on crée une facture avec l'entreprise sauvegardée, qu'on a
					// sélectionnée auparavant
				} else {
					try {
				

						// Création d'un objet Facture à partir des données saisies dans la fenêtre
						facture = new Facture(this.fipl.getTextField_Numero().getText(),
								this.fipl.getTextField_date_emission().getText(),
								this.fipl.getTextField_date_paiement().getText(),
								this.fipl.getComboBox_modePaiement().getSelectedItem().toString(),
								this.fipl.getTextField_numeroDevis().getText(),
								this.fipl.getComboBox_Designation().getSelectedItem().toString(),
								Double.parseDouble(this.fipl.getTextField_accompteVerse().getText()),
								Double.parseDouble(this.fipl.getTextField_montant().getText()), imputable, null,
								bienSauvegarde, entrepriseSauvegarde);
						// Enregistrement de la facture dans la base de données
						this.daoFacture.create(facture);
					} catch (Exception e1) {
						e1.printStackTrace();
						System.err.println("Erreur lors de l'ajout de la facture : " + e1.getMessage());
					}
				}
				// Fermeture de la fenêtre d'insertion
				this.fipl.dispose();
				break;

			case "Annuler":
				// Fermeture de la fenêtre d'insertion en cas d'annulation
				this.fipl.dispose();
				break;

			case "Charger":
				try {
					// Utilisation de la méthode chargerEntreprise afin de charger le tableau des entreprises
					this.chargerEntreprise();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				break;

			case "Insérer":
				// Ouverture de la fenêtre d'une entreprise
				Fenetre_InsertionEntreprise insertionEntreprise = new Fenetre_InsertionEntreprise();
				fenetrePrincipale.getLayeredPane().add(insertionEntreprise);
				insertionEntreprise.setVisible(true);
				insertionEntreprise.moveToFront();
				break;
			}

			// Méthode appelée lorsque l'état de l'élément du JComboBox change
			updateEntrepriseComponents();

		} else if (source instanceof JComboBox) {
			// Méthode appelée lorsque l'état de l'élément du JComboBox change
			updateEntrepriseComponents();
		}
	}

	
	/**
	 * Méthode pour mettre à jour les composants liés à l'entreprise en fonction de la désignation
	 * 
	 */
	private void updateEntrepriseComponents() {
		// Récupère dans une variable l'objet selectionné dans la comboBox
		Object selectedObject = this.fipl.getComboBox_Designation().getSelectedItem();

		if (selectedObject != null) {
			String selectedDesignation = selectedObject.toString();

			switch (selectedDesignation) {
			case "Loyer":
				// Masquer les composants liés à l'entreprise pour les autres options
				this.fipl.getBtn_ajouter_entreprise().setVisible(false);
				this.fipl.getBtn_charger_entreprise().setVisible(false);
				this.fipl.getScrollPane_table_entreprise().setVisible(false);
				this.fipl.getTable_entreprise().setVisible(false);
				this.fipl.getLbl_Entreprise().setVisible(false);
				break;
			// Afficher les composants liés à l'entreprise pour d'autres options
			default:
				this.fipl.getBtn_ajouter_entreprise().setVisible(true);
				this.fipl.getBtn_charger_entreprise().setVisible(true);
				this.fipl.getScrollPane_table_entreprise().setVisible(true);
				this.fipl.getTable_entreprise().setVisible(true);
				this.fipl.getLbl_Entreprise().setVisible(true);
				break;
			}
		}
	}

	/**
	 * Méthode permettant d'écrire une ligne d'entreprise dans la table d'entreprise
	 * 
	 * @param numeroLigne (int) : correspond au numéro de la ligne courante dans la
	 *                    table des entreprises
	 * @param e (Entreprise) : correspond à l'entreprise courante
	 * @throws SQLException
	 */
	public void ecrireLigneTableEntreprise(int numeroLigne, Entreprise e) throws SQLException {
		JTable tableEntreprise = this.fipl.getTable_entreprise();
		DefaultTableModel modeleTable = (DefaultTableModel) tableEntreprise.getModel();

		modeleTable.setValueAt(e.getSiret(), numeroLigne, 0);
		modeleTable.setValueAt(e.getNom(), numeroLigne, 1);
	}

	/**
	 * Méthode permettant de charger les entreprises dans la table d'entreprise
	 * @throws SQLException
	 */
	private void chargerEntreprise() throws SQLException {
		List<Entreprise> entreprises = this.daoEntreprise.findAll();

		DefaultTableModel modeleTable = (DefaultTableModel) this.fipl.getTable_entreprise().getModel();

		modeleTable.setRowCount(entreprises.size());

		for (int i = 0; i < entreprises.size(); i++) {
			Entreprise e = entreprises.get(i);
			this.ecrireLigneTableEntreprise(i, e);
		}
	}
}
