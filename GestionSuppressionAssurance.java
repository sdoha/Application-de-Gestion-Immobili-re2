package controleur.suppression;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Assurance;
import modele.Echeance;
import modele.dao.DaoAssurance;
import modele.dao.DaoEcheance;
import vue.Fenetre_Accueil;
import vue.suppression.Fenetre_SupprimerAssurance;

public class GestionSuppressionAssurance implements ActionListener {

	private Fenetre_SupprimerAssurance supprimerAssurance;
	private DaoAssurance daoAssurance;
	private DaoEcheance daoEcheance;

	// Constructeur prenant en paramètre la fenêtre de suppression d'une assurance
	public GestionSuppressionAssurance(Fenetre_SupprimerAssurance supprimerAssurance) {
		this.supprimerAssurance = supprimerAssurance;

		// Initialisation de l'accès à la base de données pour l'entité Assurance
		this.daoAssurance = new DaoAssurance();
		
		// Initialisation de l'accès à la base de données pour l'entité Echeance
		this.daoEcheance = new DaoEcheance();
		Sauvegarde.initializeSave();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.supprimerAssurance.getTopLevelAncestor();
		
		switch (btn.getText()) {
			case "Supprimer":
				// Récupération des éléments sauvegardés (l'assurance et l'échéance
				Assurance assurance_sauvegarde = (Assurance) Sauvegarde.getItem("Assurance");
				Echeance echeance_sauvegarde = (Echeance) Sauvegarde.getItem("Echeance");
				
				try {
					// Recherche des instances dans la base de données
					Assurance assurance_supp = this.daoAssurance.findById(assurance_sauvegarde.getNuméroPolice());
					Echeance echeance_supp = this.daoEcheance.findById(echeance_sauvegarde.getAssurance().getNuméroPolice(),
							echeance_sauvegarde.getDateEcheance().substring(0, 10)); 
					
					// Suppression de l'échéance et de l'assurance
					this.daoEcheance.delete(echeance_supp);
					this.daoAssurance.delete(assurance_supp);
					
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				// Fermeture de la fenêtre après de la suppression
				this.supprimerAssurance.dispose();
				break;
			case "Annuler":
				// Fermeture de la fenêtre de suppression
				this.supprimerAssurance.dispose();
				break;
		}
	}
}
