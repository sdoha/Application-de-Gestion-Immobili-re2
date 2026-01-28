package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Compteur;
import modele.Immeuble;
import modele.Quotite;
import modele.Quotter;
import modele.dao.DaoBien;
import modele.dao.DaoCompteur;
import modele.dao.DaoQuotite;
import modele.dao.DaoQuotter;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionCompteur;
import vue.insertion.Fenetre_InsertionLogement;
import vue.insertion.Fenetre_InsertionQuotite;

public class GestionInsertionLogement implements ActionListener {

	private Fenetre_InsertionLogement fil;
	private DaoBien daoBien;
	private DaoCompteur daoCompteur;
	private DaoQuotter daoQuotter;
	private Immeuble immeubleSauvegarde;

	// Constructeur prenant en paramètre la fenêtre d'insertion d'un logement
	public GestionInsertionLogement(Fenetre_InsertionLogement fil) {
		this.fil = fil;
		
		// Initialisation de l'accès à la base de données pour l'entité Bien
		this.daoBien = new DaoBien();
		
		// Initialisation de l'accès à la base de données pour l'entité Compteur
		this.daoCompteur = new DaoCompteur();
		
		// Initialisation de l'accès à la base de données pour l'entité Quotter
		this.daoQuotter = new DaoQuotter();
		
		//On créer directement l'immeuble à partir de celui de la sauvegarde pour ne plus en dépendre
		this.immeubleSauvegarde = (Immeuble) Sauvegarde.getItem("Immeuble");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fil.getTopLevelAncestor(); // fenetre dans laquelle
																								// on ouvre des internal
		
		switch (btn.getText()) {
			// Action lors du clic sur "Ajouter une quotité"
			case "Ajouter une quotité":
				// Je créer temporairement le bien pour pouvoir récupérer l'ID quand je vais
				// créer la quotité
				Bien bienTemporaireQ = new Bien (this.fil.getTextField_IdLogement().getText(), 
						Double.parseDouble(this.fil.getTextField_SurfaceHabitable().getText()),
						Integer.parseInt(this.fil.getTextField_NbPièces().getText()),
						Integer.parseInt(this.fil.getTextField_NumEtage().getText()),
						this.fil.getTextField_DateAcquisition().getText(),
						this.fil.getComboBox_typeDeLogement().getSelectedItem().toString(),
						this.immeubleSauvegarde);	
				
				// J'ajoute  dans le logement la sauvegarde pour réutiliser
				Sauvegarde.deleteItem("Logement");
				Sauvegarde.addItem("Logement", bienTemporaireQ);
				
				// Ouverture de la fenêtre d'insertion d'une quotité
				Fenetre_InsertionQuotite insertion_quotite = new Fenetre_InsertionQuotite();
				fenetre_Principale.getLayeredPane().add(insertion_quotite);
				insertion_quotite.setVisible(true);
				insertion_quotite.moveToFront();
				break;

			// Action lors du clic sur "Ajouter un compteur"
			case "Ajouter un compteur":
				// Je créer temporairement le bien pour pouvoir récupérer l'ID quand je vais
				// créer le compteur
				Bien bienTemporaire = new Bien (this.fil.getTextField_IdLogement().getText(), 
						Double.parseDouble(this.fil.getTextField_SurfaceHabitable().getText()),
						Integer.parseInt(this.fil.getTextField_NbPièces().getText()),
						Integer.parseInt(this.fil.getTextField_NumEtage().getText()),
						this.fil.getTextField_DateAcquisition().getText(),
						this.fil.getComboBox_typeDeLogement().getSelectedItem().toString(),
						this.immeubleSauvegarde);	
				// J'ajoute  dans le logement la sauvegarde pour réutiliser
				Sauvegarde.deleteItem("Logement");
				Sauvegarde.addItem("Logement", bienTemporaire);
				
				// On enleve l'immeuble de la sauvegarde pour éviter d'avoir l'id immeuble dans la création du compteur donc constraint check UU
				Sauvegarde.deleteItem("Immeuble");
				
				// Ouverture de la fenêtre d'insertion d'un compteur
				Fenetre_InsertionCompteur insertion_compteur = new Fenetre_InsertionCompteur();
				fenetre_Principale.getLayeredPane().add(insertion_compteur);
				insertion_compteur.setVisible(true);
				insertion_compteur.moveToFront();
				break;

			// Action lors du clic sur "Ajouter"
			case "Ajouter":
				Bien logement = null;
				try {
					// Récupération des informations du logement depuis l'interface graphique
					String typeLogement = this.fil.getComboBox_typeDeLogement().getSelectedItem().toString();
					logement = new Bien(
							this.fil.getTextField_IdLogement().getText(),
							Double.parseDouble(this.fil.getTextField_SurfaceHabitable().getText()),
							Integer.parseInt(this.fil.getTextField_NbPièces().getText()),
							Integer.parseInt(this.fil.getTextField_NumEtage().getText()),
							this.fil.getTextField_DateAcquisition().getText(),
							typeLogement,
							this.immeubleSauvegarde // Récupération de l'Immeuble associé au logement
					);
					
					// Enregistrement du logement dans la base de données
					this.daoBien.create(logement);
										
					// Vérifier  qu'il y a un compteur à ajouter
					if (Sauvegarde.onSave("Compteur")) {
						this.daoCompteur.create((Compteur) Sauvegarde.getItem("Compteur"));
						Sauvegarde.clearSave();
					}
					
					// Vérifier qu'il y a une quotité à ajouter
					if (Sauvegarde.onSave("Quotter")) {
						this.daoQuotter.create((Quotter) Sauvegarde.getItem("Quotter"));    
						Sauvegarde.clearSave();
					}
					
					 // Afficher un message de réussite
                    JOptionPane.showMessageDialog(this.fil, "Ajouté avec succès !", "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// Fermeture de la fenêtre d'insertion de logement après ajout
				this.fil.dispose();
				
				break;
				
			// Action lors du clic sur "Annuler"
			case "Annuler":
				// Fermeture de la fenêtre d'insertion de logement sans ajout
				this.fil.dispose();
				break;
		}
	}
}
