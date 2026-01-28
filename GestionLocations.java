package controleur;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Facture;
import modele.Locataire;
import modele.Louer;
import modele.dao.DaoBien;
import modele.dao.DaoFacture;
import modele.dao.DaoLocataire;
import modele.dao.DaoLouer;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_AffichageInfoLocataire;

public class GestionLocations implements ListSelectionListener {

	private Fenetre_Accueil fenetreAccueil;
	private Fenetre_AffichageInfoLocataire fenetreAffichageLocataire;
	private DaoLouer daoLouer;
	private DaoFacture daoFacture;
	private DaoLocataire daoLocataire;
	private DaoBien daoBien;

	// Constructeur prenant en paramètre la fenêtre d'acceuil
	public GestionLocations(Fenetre_Accueil fenetreAccueil) {
		this.fenetreAccueil = fenetreAccueil;
		this.fenetreAffichageLocataire = new Fenetre_AffichageInfoLocataire();
		this.daoLouer = new DaoLouer();
		this.daoFacture = new DaoFacture();
		this.daoLocataire = new DaoLocataire();
		this.daoBien = new DaoBien();
		Sauvegarde.initializeSave();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			int selectedRow = this.fenetreAccueil.getTableLocations().getSelectedRow();

			if (selectedRow > -1) {
				JTable tableLocations = this.fenetreAccueil.getTableLocations();
				Louer location = null;
				Locataire locataire = null;
				Bien bien = null;

				try {
					// Récupération de l'objet Louer sélectionné dans la table
					location = this.daoLouer.findById(tableLocations.getValueAt(selectedRow, 1).toString(),
							tableLocations.getValueAt(selectedRow, 0).toString());

					// Récupération du locataire associé à la location
					locataire = this.daoLocataire.findById(tableLocations.getValueAt(selectedRow, 0).toString());
					// Ajout du locataire à la sauvegarde
					Sauvegarde.deleteItem("Locataire");
					Sauvegarde.addItem("Locataire", locataire);

					// Récupération du bien associé à la location
					bien = this.daoBien.findById(tableLocations.getValueAt(selectedRow, 1).toString());
					// Ajout du bien à la sauvegarde
					Sauvegarde.deleteItem("Logement");
					Sauvegarde.addItem("Logement", bien);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}

				if (location != null) {
					// Ajout de la location à la sauvegarde
					Sauvegarde.deleteItem("Louer");
					Sauvegarde.addItem("Louer", location);

					// Récupération de la dernière facture de loyer associée à la location
					Facture derniereFactureLoyer = null;
					try {
						derniereFactureLoyer = this.daoFacture.findDerniereFactureLoyer(location.getBien());
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					// Mise à jour des champs de la fenêtre principale avec les informations de la location
					JTextField loyer = this.fenetreAccueil.getTextField_loyer();
					loyer.setText(String.valueOf(location.getLoyerTTC()));

					JTextField dateEmissionField = this.fenetreAccueil.getTextField_dateEmission();
					JTextField dateEcheanceField = this.fenetreAccueil.getTextField_dateEcheance();

					if (derniereFactureLoyer != null) {
						// Si la date d'émission n'est pas nulle, on l'utilise ; sinon, "N/A"
						String dateEmission;
						if (derniereFactureLoyer.getDateEmission() != null) {
							dateEmission = derniereFactureLoyer.getDateEmission();
						} else {
							dateEmission = "N/A";
						}

						// Si la date de paiement n'est pas nulle, on l'utilise ; sinon, "N/A"
						String datePaiement;
						if (derniereFactureLoyer.getDatePaiement() != null) {
							datePaiement = derniereFactureLoyer.getDatePaiement();
						} else {
							datePaiement = "N/A";
						}

						// Définir les champs de texte avec les valeurs formatées
						dateEmissionField.setText(dateEmission);
						dateEcheanceField.setText(datePaiement);
					} else {
						// Si la facture est null, définir les champs de texte sur "N/A"
						dateEmissionField.setText("N/A");
						dateEcheanceField.setText("N/A");
					}

					Facture facture;
					try {
						facture = daoFacture.findDerniereFactureLoyer(bien);

						JTextField montantPaye = this.fenetreAccueil.getTextField_paye();
						montantPaye.setText(String.valueOf(facture.getMontantReelPaye()));
						
						JTextField restantDu = this.fenetreAccueil.getTextField_restantDu();
						restantDu.setText(String.valueOf(location.getLoyerTTC() + location.getProvision_chargeMens_TTC()
								- facture.getMontantReelPaye()));
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					JTextField caution = this.fenetreAccueil.getTextField_caution();
					caution.setText(String.valueOf(location.getCautionTTC()));

					JTextField provision = this.fenetreAccueil.getTextField_provisionCharges();
					provision.setText(String.valueOf(location.getProvision_chargeMens_TTC()));

					// Affichage de la fenêtre d'affichage des informations du locataire
					fenetreAffichageLocataire.setVisible(true);
					fenetreAffichageLocataire.moveToFront();
				}
			}
		}
	}
}
