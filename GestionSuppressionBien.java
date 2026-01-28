package controleur.suppression;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.tools.Diagnostic;

import controleur.outils.Sauvegarde;
import modele.Assurance;
import modele.Bien;
import modele.Compteur;
import modele.Diagnostics;
import modele.Echeance;
import modele.Facture;
import modele.Immeuble;
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
import vue.suppression.Fenetre_SupprimerBien;

public class GestionSuppressionBien implements ActionListener {

	private Fenetre_SupprimerBien supprimerBien;
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

	// Constructeur prenant en paramètre la fenêtre de suppression d'un bien
	public GestionSuppressionBien(Fenetre_SupprimerBien supprimerBien) {
		this.supprimerBien = supprimerBien;
		
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
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.supprimerBien.getTopLevelAncestor();
		
		switch (btn.getText()) {
			case "Supprimer":
				// Récupération de l'Immeuble de la sauvegarde
				Immeuble immeuble_supp = (Immeuble) Sauvegarde.getItem("Immeuble");
				try {
					String idImmeuble = immeuble_supp.getImmeuble();
					// Récupération des Biens liées a l'Immeuble
					List<Bien> bienListe = this.daoBien.findBiensparImmeuble(idImmeuble);
					List<Compteur> compteurListeImmeuble = this.daoCompteur.findByIdImmeubleListe(idImmeuble);
					List<Facture> factureListeImmeuble = this.daoFacture.findFactureImmeuble(idImmeuble);
					
					// Relevé concernant les compteurs 
					List<Releve> releves;
					
					// Echeances concernant les assurnces
					List<Echeance> echeances;
					
					// Suppression des Relevés et des Compteurs de l'Immeuble
					if (compteurListeImmeuble != null && !compteurListeImmeuble.isEmpty()) {
						for (Compteur compteur : compteurListeImmeuble) {
							releves = this.daoReleve.findReleveByCompteur(compteur.getIdCompteur());
							for (Releve releve : releves) {
								this.daoReleve.delete(releve);
							}
							this.daoCompteur.delete(compteur);
						}
					}
					
					// Suppression des Factures liées à l'Immeuble
					if (factureListeImmeuble != null && !factureListeImmeuble.isEmpty()) {
						for (Facture facture : factureListeImmeuble) {
							this.daoFacture.delete(facture);
						}
					}
					
					// Suppression des Biens liés à l'Immeuble
					for (Bien bien : bienListe) {
						List<Assurance> assurances = this.daoAssurance.findByLogement(bien.getIdBien());
						List<Diagnostics> diagnostics = this.daoDiagnostic.findDiagnosticByBien(bien.getIdBien());
						List<Louer> louers = this.daoLouer.findLocationByBien(bien.getIdBien());
						List<Compteur> compteurListeBien = this.daoCompteur.findByIdBienListe(bien.getIdBien());
						List<Quotter> quotters = this.daoQuotter.findQuotterByBien(bien.getIdBien());
						List<Facture> factureListeBien = this.daoFacture.findFactureByBien(bien.getIdBien());
						List<Imposer> imposers = this.daoImposer.findImposerByBien(bien.getIdBien());
						
						// Suppression des assurances liées au Bien
						for (Assurance assurance : assurances) {
							echeances = this.daoEcheance.findByAssuranceNumPoliceList(assurance.getNuméroPolice());
							// Suppression des echéances liées aux assurances
							for(Echeance echeance : echeances) {
								this.daoEcheance.delete(echeance);
							}
							this.daoAssurance.delete(assurance);
						}
						
						// Suppression des Diagnostics liés au Bien
						for (Diagnostics diagnostic : diagnostics) {
							this.daoDiagnostic.delete(diagnostic);
						}
						
						// Suppression des Locations liées au Bien
						for (Louer louer : louers) {
							this.daoLouer.delete(louer);
						}
						
						// Suppression des Relevés liés aux Compteurs du Bien
						if (compteurListeBien != null && !compteurListeBien.isEmpty()) {
							for (Compteur compteur : compteurListeBien) {
								releves = this.daoReleve.findReleveByCompteur(compteur.getIdCompteur());
								for (Releve releve : releves) {
									this.daoReleve.delete(releve);
								}
								this.daoCompteur.delete(compteur);
							}
						}
						
						// Suppression des Factures liées au Bien
						if (factureListeBien != null && !factureListeBien.isEmpty()) {
							for (Facture facture : factureListeBien) {
								this.daoFacture.delete(facture);
							}
						}
						
						// Suppression des Quotters liés au Bien
						for (Quotter quotter : quotters) {
							this.daoQuotter.delete(quotter);
						}
						
						// Suppression des Impositions liées au Bien
						for (Imposer imposer : imposers) {
							this.daoImposer.delete(imposer);
						}
						
						// Suppression du Bien
						this.daoBien.delete(bien);
					}
					
					// Suppression de l'Immeuble
					this.daoImmeuble.delete(immeuble_supp);
					
					// On charge le tableau après la suppression de l'immeuble
					fenetre_Principale.getGestionAccueil().chargerBiens();
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				// Fermeture de la fenêtre après de la suppression
				this.supprimerBien.dispose();
				break;
				
			case "Annuler":
				// Fermeture de la fenêtre de suppression
				this.supprimerBien.dispose();
				break;
		}
	}
}
