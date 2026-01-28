package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import controleur.outils.Sauvegarde;
import modele.Compteur;
import modele.Releve;
import modele.dao.DaoReleve;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionReleve;

public class GestionInsertionReleve implements ActionListener {

	private Fenetre_InsertionReleve fiv;
	private DaoReleve daoReleve;

	// Constructeur prenant en paramètre la fenêtre d'insertion d'un relevé
	public GestionInsertionReleve(Fenetre_InsertionReleve fiv) {
		this.fiv = fiv;
		
		// Initialisation de l'accès à la base de données pour l'entité Relevé
		this.daoReleve = new DaoReleve();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fiv.getTopLevelAncestor();
		
		switch (btn.getText()) {
			// Action lors du clic sur "Ajouter"
			case "Ajouter":
				// Récupération de l'id du compteur cliqué depuis la sauvegarde
				Compteur compteur_sauvegarde = (Compteur) Sauvegarde.getItem("Compteur");
				// Création d'un objet Releve à null
				Releve releve = null;
				try {

					// Création de l'objet relevé à partir des données insérées dans la fenêtre
					releve = new Releve(compteur_sauvegarde, this.fiv.getTextField_dateReleve().getText() , Double.parseDouble(this.fiv.getTextField_indiceCompteur().getText()));
					
					// Création du relevé dans la base de données
					this.daoReleve.create(releve);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// Fermeture de la fenêtre d'insertion du relevé après ajout
				this.fiv.dispose();
				
				JOptionPane.showMessageDialog(fenetre_Principale, "Relevé ajouté avec succès", "Succès",
						JOptionPane.INFORMATION_MESSAGE);
				break;
				
			// Action lors du clic sur "Annuler"
			case "Annuler":
				// Fermeture de la fenêtre d'insertion du relevé sans ajout
				this.fiv.dispose();
				break;
		}
	}
}
