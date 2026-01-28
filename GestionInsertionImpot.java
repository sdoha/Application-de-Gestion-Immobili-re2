package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Imposer;
import modele.Impôt;
import modele.dao.DaoBien;
import modele.dao.DaoImposer;
import modele.dao.DaoImpôt;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionImpot;

public class GestionInsertionImpot implements ActionListener {

	private Fenetre_InsertionImpot fii;
	private DaoBien daoBien;
	private DaoImpôt daoImpot;
	private DaoImposer daoImposer;

	// Constructeur prenant en paramètre la fenêtre d'insertion d'un impôt
	public GestionInsertionImpot(Fenetre_InsertionImpot fii) {
		this.fii = fii;
		
		// Initialisation de l'accès à la base de données pour l'entité Bien
		this.daoBien = new DaoBien();
		
		// Initialisation de l'accès à la base de données pour l'entité Impôt
		this.daoImpot = new DaoImpôt();
		
		// Initialisation de l'accès à la base de données pour l'entité Imposer
		this.daoImposer = new DaoImposer();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fii.getTopLevelAncestor();

		switch (btn.getText()) {
		case "Ajouter":
			// Récuperer le bien placé dans la sauvegarde
			Bien bienSauvegarde = (Bien) Sauvegarde.getItem("Logement");
			// Création de l'objet Impôt à null
			Impôt impot = null;
			// Création de l'objet Imposer à null
			Imposer imposer = null;
			try {
				// Création de l'objet Impôt avec les données de la fenêtre d'insertion
				impot = new Impôt(this.fii.getTextField_nom().getText(),
						Double.parseDouble(this.fii.getTextField_montant().getText()),
						this.fii.getTextField_annee().getText());

				// Ajouter le nouvel impôt dans la base de données
				int idImpotSequence = this.daoImpot.createAvecSequence(impot);

				// Attribue l'id de la séquence à l'impôt
				impot.setIdImpot(idImpotSequence);

				// Création de l'objet Imposer à partir du bien de la sauvegarde et de l'impôt crée juste avant
				imposer = new Imposer(bienSauvegarde, impot);
				this.daoImposer.create(imposer);
				
				// Fermeture de la fenêtre d'insertion après l'ajout
				this.fii.dispose();
				
				// Affichage d'un message de succès
				JOptionPane.showMessageDialog(null, "Impôt ajouté avec succès !", "Succès", JOptionPane.YES_OPTION);

			} catch (Exception e1) {
				e1.printStackTrace();
				// Afficher un message d'erreur à l'utilisateur
				JOptionPane.showMessageDialog(null,
						"Erreur lors de l'ajout de l'impôt dans la base de données. Veuillez réessayer plus tard.",
						"Erreur d'ajout", JOptionPane.ERROR_MESSAGE);
			}
			break;

		case "Annuler":
			// Fermeture de la fenêtre d'insertion d'un impôt
			this.fii.dispose();
			break;
		}
	}
}
