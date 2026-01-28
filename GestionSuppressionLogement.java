package controleur.suppression;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Assurance;
import modele.Bien;
import modele.Compteur;
import modele.Diagnostics;
import modele.Echeance;
import modele.Facture;
import modele.Imposer;
import modele.Louer;
import modele.Quotter;
import modele.Releve;
import modele.dao.DaoAssurance;
import modele.dao.DaoBien;
import modele.dao.DaoCompteur;
import modele.dao.DaoDiagnostic;
import modele.dao.DaoEcheance;
import modele.dao.DaoFacture;
import modele.dao.DaoImmeuble;
import modele.dao.DaoImposer;
import modele.dao.DaoLouer;
import modele.dao.DaoQuotter;
import modele.dao.DaoReleve;
import vue.Fenetre_Accueil;
import vue.suppression.Fenetre_SupprimerLogement;

public class GestionSuppressionLogement implements ActionListener {

	private Fenetre_SupprimerLogement supprimerLogement;
	private DaoImmeuble daoImmeuble;
	private DaoBien daoBien;
	private DaoAssurance daoAssurance;
	private DaoEcheance daoEcheance;
	private DaoFacture daoFacture;
	private DaoLouer daoLouer;
	private DaoQuotter daoQuotter;
	private DaoImposer daoImposer;
	private DaoCompteur daoCompteur;
	private DaoDiagnostic daoDiagnostic;
	private DaoReleve daoReleve;

	// Constructeur prenant en paramètre la fenêtre de suppression d'une location
	public GestionSuppressionLogement(Fenetre_SupprimerLogement supprimerLogement) {
		this.supprimerLogement = supprimerLogement;
		
		// Initialisation de l'accès à la base de données pour l'entité Immeuble
		this.daoImmeuble = new DaoImmeuble();
		
		// Initialisation de l'accès à la base de données pour l'entité Bien
		this.daoBien = new DaoBien();
		
		// Initialisation de l'accès à la base de données pour l'entité Assurance
		this.daoAssurance = new DaoAssurance();
		
		// Initialisation de l'accès à la base de données pour l'entité Echeance
		this.daoEcheance = new DaoEcheance();
		
		// Initialisation de l'accès à la base de données pour l'entité Louer
		this.daoLouer = new DaoLouer();
		
		// Initialisation de l'accès à la base de données pour l'entité Facture
		this.daoFacture = new DaoFacture();
		
		// Initialisation de l'accès à la base de données pour l'entité Quotter
		this.daoQuotter = new DaoQuotter();
		
		// Initialisation de l'accès à la base de données pour l'entité Imposer
		this.daoImposer = new DaoImposer();
		
		// Initialisation de l'accès à la base de données pour l'entité Compteur
		this.daoCompteur = new DaoCompteur();
		
		// Initialisation de l'accès à la base de données pour l'entité Releve
		this.daoReleve = new DaoReleve();
		
		// Initialisation de l'accès à la base de données pour l'entité Diagnostic
		this.daoDiagnostic = new DaoDiagnostic();
		
		Sauvegarde.initializeSave();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.supprimerLogement.getTopLevelAncestor();
		switch (btn.getText()) {
			// Suppression d'un logement
			case "Supprimer":
				Bien bien_supp = (Bien) Sauvegarde.getItem("Logement");
				try {
					
					// Récupération des assurances, diagnostics, locations, compteurs, quotters,
					// factures et impositions liées au logement
					List<Assurance> assurances = this.daoAssurance.findByLogement(bien_supp.getIdBien());
					List<Diagnostics> diagnostics = this.daoDiagnostic.findDiagnosticByBien(bien_supp.getIdBien());
					List<Louer> louers = this.daoLouer.findLocationByBien(bien_supp.getIdBien());
					List<Compteur> compteurListeBien = this.daoCompteur.findByIdBienListe(bien_supp.getIdBien());
					List<Quotter> quotters = this.daoQuotter.findQuotterByBien(bien_supp.getIdBien());
					List<Facture> factureListeBien = this.daoFacture.findFactureByBien(bien_supp.getIdBien());
					List<Imposer> imposers = this.daoImposer.findImposerByBien(bien_supp.getIdBien());
					// Relevé et échéanceconcernant les compteurs 
					List<Releve> releves;
				
					List<Echeance> echeances;
					
					// Suppression des assurances liées au logement
					for (Assurance assurance : assurances) {
						echeances = this.daoEcheance.findByAssuranceNumPoliceList(assurance.getNuméroPolice());
						// Suppression des echéances liées aux assurances
						for(Echeance echeance : echeances) {
							this.daoEcheance.delete(echeance);
						}
						this.daoAssurance.delete(assurance);
					}
					
					// Suppression des diagnostics liés au logement
					for (Diagnostics diagnostic : diagnostics) {
						this.daoDiagnostic.delete(diagnostic);
					}
					
					// Suppression des locations liées au logement
					for (Louer louer : louers) {
						this.daoLouer.delete(louer);
					}
					
					// Suppression des compteurs et de leurs relevés associés au logement
					if (compteurListeBien != null && !compteurListeBien.isEmpty()) {
						for (Compteur compteur : compteurListeBien) {
							releves = this.daoReleve.findReleveByCompteur(compteur.getIdCompteur());
							for (Releve releve : releves) {
								this.daoReleve.delete(releve);
							}
							this.daoCompteur.delete(compteur);
						}
					}
					
					// Suppression des factures associées au logement
					if (factureListeBien != null && !factureListeBien.isEmpty()) {
						for (Facture facture : factureListeBien) {
							this.daoFacture.delete(facture);
						}
					}
					
					// Suppression des quotters associés au logement
					for (Quotter quotter : quotters) {
						this.daoQuotter.delete(quotter);
					}
					
					// Suppression des impositions associées au logement
					for (Imposer imposer : imposers) {
						this.daoImposer.delete(imposer);
					}
					
					// Suppression du logement
					this.daoBien.delete(bien_supp);
					
					// On charge le tableau après la suppression du logement
					fenetre_Principale.getGestionAccueil().chargerBiens();
					
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				// Fermeture de la fenêtre de suppression de logement
				this.supprimerLogement.dispose();
				
				break;
				
			// Annulation de la suppression
			case "Annuler":
				// Fermeture de la fenêtre de suppression
				this.supprimerLogement.dispose();
				break;
		}
	}
}
