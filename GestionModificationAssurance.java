package controleur.modification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controleur.outils.Sauvegarde;
import modele.Assurance;
import modele.Bien;
import modele.Echeance;
import modele.Entreprise;
import modele.dao.DaoAssurance;
import modele.dao.DaoBien;
import modele.dao.DaoEcheance;
import modele.dao.DaoEntreprise;
import vue.insertion.Fenetre_InsertionEntreprise;
import vue.modification.Fenetre_ModificationAssurance;
import vue.modification.Fenetre_ModificationEntreprise;

public class GestionModificationAssurance implements ActionListener {

	private Fenetre_ModificationAssurance modificationAssurance;
	private DaoAssurance daoAssurance;
	private DaoBien daoBien;
	private DaoEcheance daoEcheance;
	private DaoEntreprise daoEntreprise;
	private Entreprise entreprise;

	/**
	 * @param modificationAssurance Constructeur de la classe qui va servir a gerer
	 *                              la page de la modification de l'assurance
	 */
	public GestionModificationAssurance(Fenetre_ModificationAssurance modificationAssurance) {
		// Initialisation du gestionnaire pour la fenêtre de modification d'assurance
		this.modificationAssurance = modificationAssurance;
		// Initialisation de l'accès aux opérations de la base de données pour
		// l'assurance, le bien, l'entreprise et l'échéance
		this.daoAssurance = new DaoAssurance();
		this.daoBien = new DaoBien();
		this.daoEcheance = new DaoEcheance();
		this.daoEntreprise = new DaoEntreprise();

		// Initialisation de la sauvegarde (potentiellement non nécessaire ici, dépend
		// du contexte)
		Sauvegarde.initializeSave();
		this.entreprise = (Entreprise) Sauvegarde.getItem("Entreprise"); // On fait sa car le sonarLite declare une
																			// duplication de code
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Récupération du bouton source de l'événement
		JButton btn = (JButton) e.getSource();

		switch (btn.getText()) {
		case "Modifier":
			Assurance assurance = null;
			Echeance echeance = null;

			try {
				// Récupération du Bien et de l'Entreprise sauvegardés précédemment
				Bien bienSauvegarde = (Bien) Sauvegarde.getItem("Logement");
				Entreprise entrepriseSauvegarde = this.entreprise; //

				// Recherche du Bien par son identifiant
				Bien bien = daoBien.findById(bienSauvegarde.getIdBien());

				// Création de l'objet Assurance avec les données de la fenêtre de modification
				assurance = new Assurance(this.modificationAssurance.getTextField_numPolice().getText(),
						Float.parseFloat(this.modificationAssurance.getTextField_montant().getText()), bien,
						entrepriseSauvegarde);

				// Création de l'objet Echeance avec les données de la fenêtre de modification
				echeance = new Echeance(assurance, this.modificationAssurance.getTextField_dateEcheance().getText());

				// Mise à jour des données de l'assurance dans la base de données
				this.daoAssurance.update(assurance);
				// Mise à jour des données de l'échéance dans la base de données
				this.daoEcheance.update(echeance);

				// Fermeture de la fenêtre de modification après la mise à jour
				this.modificationAssurance.dispose();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			break;

		case "Annuler":
			// Fermeture de la fenêtre de modification sans effectuer de modification
			this.modificationAssurance.dispose();
			break;
		case "Charger":
			try {
				this.chargerEntreprise();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			break;

		case "Insérer":
			// Ouverture de la fenêtre d'insertion d'entreprise
			Fenetre_InsertionEntreprise insertionEntreprise = new Fenetre_InsertionEntreprise();
			modificationAssurance.getLayeredPane().add(insertionEntreprise);
			insertionEntreprise.setVisible(true);
			insertionEntreprise.moveToFront();
			break;

		case "Modifier ":
			// Si on a dans la sauvegarde l'element Entreprise on peut modifier la fenetre
			if (Sauvegarde.onSave("Entreprise")) {
				Fenetre_ModificationEntreprise modificationEntreprise = new Fenetre_ModificationEntreprise();
				modificationAssurance.getLayeredPane().add(modificationEntreprise);
				modificationEntreprise.setVisible(true);
				modificationEntreprise.moveToFront();

				// On récupère l'entreprise de la sauvegarde
				Entreprise entrepriseSauvegarde = this.entreprise;
				Entreprise entrepriseCourante;

				try {
					entrepriseCourante = this.daoEntreprise.findById(entrepriseSauvegarde.getSiret());
					modificationEntreprise.getTextField_SIRET().setText(entrepriseCourante.getSiret());
					modificationEntreprise.getTextField_Nom().setText(entrepriseCourante.getNom());
					modificationEntreprise.getTextField_Adresse().setText(entrepriseCourante.getAdresse());
					modificationEntreprise.getTextField_CP().setText(entrepriseCourante.getCp());
					modificationEntreprise.getTextField_Ville().setText(entrepriseCourante.getVille());
					modificationEntreprise.getTextField_Mail().setText(entrepriseCourante.getMail());
					modificationEntreprise.getTextField_Telephone().setText(entrepriseCourante.getTelephone());
					modificationEntreprise.getTextField_IBAN().setText(entrepriseCourante.getIban());

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				break; 
			}
		default:
			break;

		}

	}

	/**
	 * Méthode pour écrire une ligne d'entreprise dans la table d'entreprise
	 *
	 * @param numeroLigne (int) : prend le numero de la ligne du tableau
	 * @param e           (Entreprise) : prend une Entreprise pour l'inserer dans le
	 *                    tableau
	 *
	 */
	public void ecrireLigneTableEntreprise(int numeroLigne, Entreprise e) {
		JTable tableEntreprise = this.modificationAssurance.getTable_entreprise();
		DefaultTableModel modeleTable = (DefaultTableModel) tableEntreprise.getModel();

		modeleTable.setValueAt(e.getSiret(), numeroLigne, 0);
		modeleTable.setValueAt(e.getNom(), numeroLigne, 1);
	}

	/**
	 * Méthode pour charger les entreprises dans la table d'entreprise
	 *
	 * @throws SQLException
	 */
	private void chargerEntreprise() throws SQLException {
		List<Entreprise> entreprises = this.daoEntreprise.findAll();

		DefaultTableModel modeleTable = (DefaultTableModel) this.modificationAssurance.getTable_entreprise().getModel();

		modeleTable.setRowCount(entreprises.size());

		for (int i = 0; i < entreprises.size(); i++) {
			Entreprise e = entreprises.get(i);
			this.ecrireLigneTableEntreprise(i, e);
		}
	}
}
