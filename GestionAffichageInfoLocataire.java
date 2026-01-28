package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Locataire;
import modele.Louer;
import modele.dao.DaoBien;
import modele.dao.DaoLocataire;
import modele.dao.DaoLouer;
import vue.Fenetre_Accueil;
import vue.archiver.Fenetre_ArchiverLocataire;
import vue.insertion.Fenetre_AffichageInfoLocataire;

public class GestionAffichageInfoLocataire implements ActionListener {

	private Fenetre_AffichageInfoLocataire fail;
	private DaoLocataire daoLocataire;
	private DaoLouer daoLouer;
	private DaoBien daoBien;
	private Louer location;

	// Constructeur prenant en paramètre la fenêtre d'affichage des informations des
	// locataires
	public GestionAffichageInfoLocataire(Fenetre_AffichageInfoLocataire fail) {
		this.fail = fail;

		// Initialisation de l'accès à la base de données pour l'entité Locataire
		this.daoLocataire = new DaoLocataire();

		// Initialisation de l'accès à la base de données pour l'entité Louer
		this.daoLouer = new DaoLouer();

		// Initialisation de l'accès à la base de données pour l'entité Bien
		this.daoBien = new DaoBien();

		// Récupère la Location selectionnée dans le tableau qui est dans sauvegarde
		this.location = (Louer) Sauvegarde.getItem("Louer");

	}

	/**
	 * Méthode pour écrire une ligne de Solde Tout compte dans la table des solde
	 * tout de compte
	 *
	 * @param numeroLigne (int) : correspond au numéro de la ligne courante dans la
	 *                    table des soldes de tout compte
	 * @param location    (Louer) : correspond à la location courante
	 * @param bien        (Bien) : correspond au bien courant
	 * @throws SQLException
	 */
	public void ecrireLigneTableSoldeToutCompte(int numeroLigne, Louer location, Bien bien) throws SQLException {
		JTable tableSoldeToutCompte = this.fail.getTable_soldeToutCompte();
		DefaultTableModel modeleTable = (DefaultTableModel) tableSoldeToutCompte.getModel();

		// Charges réelles
		double chargesReellesBien = daoLouer.totalChargesRéelles(location);
		// Ordures ménagères
		double orduresMenageres = this.daoLouer.totalOrduresMenageres(location);

		double totalCharges = chargesReellesBien + orduresMenageres;
		modeleTable.setValueAt(totalCharges, numeroLigne, 2);

		// Travaux imputables
		double travauxImputables = daoLouer.travauxImputables(location);
		modeleTable.setValueAt(travauxImputables, numeroLigne, 6);

		// Total des provisions sur charges
		double totalProvisions = daoLouer.totalProvisions(location);
		modeleTable.setValueAt(totalProvisions, numeroLigne, 0);

		// Caution
		modeleTable.setValueAt(location.getCautionTTC(), numeroLigne, 4);

		// Restant du Loyers
		double restantDuLoyers = this.daoLouer.restantDuLoyers(location);
		modeleTable.setValueAt(restantDuLoyers, numeroLigne, 8);

		// Reste
		double soldeToutCompte = daoLouer.soldeToutCompte(location);

		modeleTable.setValueAt(soldeToutCompte, numeroLigne, 10);

		modeleTable.setValueAt("-", numeroLigne, 1);
		modeleTable.setValueAt("+", numeroLigne, 3);
		modeleTable.setValueAt("-", numeroLigne, 5);
		modeleTable.setValueAt("-", numeroLigne, 7);
		modeleTable.setValueAt("=", numeroLigne, 9);
	}

	/**
	 * Méthode qui permet de charger les soldes de tout compte dans la table de
	 * solde de tout compte
	 *
	 * @throws SQLException
	 */
	private void chargerSoldeToutCompte() throws SQLException {
		List<Louer> locations = this.daoLouer.findLocationByBien(location.getBien().getIdBien());
		DefaultTableModel modeleTable = (DefaultTableModel) this.fail.getTable_soldeToutCompte().getModel();
		modeleTable.setRowCount(1);

		for (int i = 0; i < locations.size(); i++) {
			Louer l = locations.get(i);
			Bien b = this.daoBien.findById(l.getBien().getIdBien()); // les 10 premiers pour enlever
			// "HH:MM"
			this.ecrireLigneTableSoldeToutCompte(i, l, b);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fail.getTopLevelAncestor();

		switch (btn.getText()) {
		case "Régularisation des charges":
			// Récupère le locataire placé dans la sauvegarde
			Locataire locataire_save = (Locataire) Sauvegarde.getItem("Locataire");
			String idLocataire = locataire_save.getIdLocataire();
			try {
				this.updateTableRegularisationsForLocataire(idLocataire);
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
			break;

		case "Solde tout compte":
			try {
				// Vérifie si la location est dans la sauvegarde
				if (Sauvegarde.onSave("Louer") == true) {
					// Récupère le locataire placé dans la sauvegarde
					Louer locSauvegarde = (Louer) Sauvegarde.getItem("Louer");
					Fenetre_ArchiverLocataire archiver_locataire = new Fenetre_ArchiverLocataire();
					this.fail.getLayeredPane().add(archiver_locataire);
					archiver_locataire.setVisible(true);
					archiver_locataire.moveToFront();
				} else {
					JOptionPane.showMessageDialog(this.fail, "Veuillez sélectionner une location !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				this.chargerSoldeToutCompte();
			} catch (SQLException e1) {
				// Afficher un message d'erreur à l'utilisateur
				e1.printStackTrace();
				// Affichage d'une boîte de dialogue avec le message d'erreur
				JOptionPane.showMessageDialog(null,
						"Erreur lors du chargement des soldes de tout compte. Veuillez réessayer plus tard.",
						"Erreur de chargement", JOptionPane.ERROR_MESSAGE);
			}
			break;
		}
	}

	////////////////////////////////////////////
	////////////// REGULARISATION //////////////
	////////////////////////////////////////////

	/**
	 * Méthode pour écrire une ligne dans la table des régularisations de charges
	 *
	 * @param numeroLigne (int) : correspond au numéro de la ligne courante dans la
	 *                    table des régularisations de charges
	 * @param location    (Louer) : correspond à la location courante
	 * @throws SQLException
	 */
	public void ecrireLigneTableRegularisation(int numeroLigne, Louer location) throws SQLException {
		JTable tableRegularisation = this.fail.getTableRegularisation();
		DefaultTableModel modeleTable = (DefaultTableModel) tableRegularisation.getModel();
		// Periode du
		modeleTable.setValueAt(location.getBien().getIdBien(), numeroLigne, 0);
		modeleTable.setValueAt(location.getDateDebut(), numeroLigne, 1);
		// au
		if (location.getDateDerniereRegularisation() != null) {
			modeleTable.setValueAt(location.getDateDerniereRegularisation(), numeroLigne, 2);
		} else {
			modeleTable.setValueAt("N/A", numeroLigne, 2);
		}
		// Charges réelles
		double chargesReellesBien = this.daoLouer.totalChargesRéelles(location);
		modeleTable.setValueAt(chargesReellesBien, numeroLigne, 3);
		// Ordures ménagères
		double orduresMenageres = this.daoLouer.totalOrduresMenageres(location);
		modeleTable.setValueAt(orduresMenageres, numeroLigne, 4);
		// TOTAL charges
		double totalCharges = chargesReellesBien + orduresMenageres;
		modeleTable.setValueAt(totalCharges, numeroLigne, 5);
		// Restant du Loyers
		double restantDuLoyers = this.daoLouer.restantDuLoyers(location);
		modeleTable.setValueAt(restantDuLoyers, numeroLigne, 6);
		// Total des provisions sur charges
		double totalProvisions = this.daoLouer.totalProvisions(location);
		modeleTable.setValueAt(totalProvisions, numeroLigne, 7);
		// TOTAL
		double regularisationCharges = this.daoLouer.regularisationCharges(location);
		modeleTable.setValueAt(regularisationCharges, numeroLigne, 8);

	}

	/**
	 * Met à jour la table des régularisations pour un locataire donné.
	 *
	 * @param idLocataire (String) : correspond à l'identifiant du locataire pour
	 *                    lequel les régularisations doivent être affichées.
	 * @throws SQLException
	 */
	private void updateTableRegularisationsForLocataire(String idLocataire) throws SQLException {
		// Récupère le logement qui est dans la sauvegarde
		Bien bien = (Bien) Sauvegarde.getItem("Logement");
		Louer location = this.daoLouer.findById(bien.getIdBien(), idLocataire);

		DefaultTableModel modeleTable = (DefaultTableModel) this.fail.getTableRegularisation().getModel();
		modeleTable.setRowCount(1); // Toujours une seule ligne puisque il s'agit d'un locataire et de son bien pour
									// une location
		this.ecrireLigneTableRegularisation(0, location); // Ecrit la ligne

	}

}