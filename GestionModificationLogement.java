package controleur.modification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Compteur;
import modele.Immeuble;
import modele.Quotter;
import modele.dao.DaoBien;
import modele.dao.DaoCompteur;
import modele.dao.DaoImmeuble;
import modele.dao.DaoQuotter;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionCompteur;
import vue.insertion.Fenetre_InsertionQuotite;
import vue.modification.Fenetre_ModificationLogement;

public class GestionModificationLogement implements ActionListener {

	private Fenetre_ModificationLogement modificationLogement;
	private DaoBien daoBien;
	private DaoImmeuble daoImmeuble;
	private DaoCompteur daoCompteur;
	private DaoQuotter daoQuotter;
	private Immeuble immeubleSauvegarde;

	public GestionModificationLogement(Fenetre_ModificationLogement modificationLogement) {
		this.modificationLogement = modificationLogement;
		this.daoBien = new DaoBien();
		this.daoImmeuble = new DaoImmeuble();
		this.daoCompteur = new DaoCompteur();
		this.daoQuotter = new DaoQuotter();
		// On créer directement l'immeuble à partir de celui de la sauvegarde pour ne
		// plus en dépendre
		this.immeubleSauvegarde = (Immeuble) Sauvegarde.getItem("Immeuble");
		Sauvegarde.initializeSave();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetrePrincipale = (Fenetre_Accueil) this.modificationLogement.getTopLevelAncestor();

		switch (btn.getText()) {
		case "Ajouter un compteur":
			// Je créer temporairement le bien pour pouvoir récupérer l'ID quand je vais
			// créer le compteur
			Bien bienTemporaire = new Bien(this.modificationLogement.getTextField_IdLogement().getText(),
					Double.parseDouble(this.modificationLogement.getTextField_SurfaceHabitable().getText()),
					Integer.parseInt(this.modificationLogement.getTextField_NbPièces().getText()),
					Integer.parseInt(this.modificationLogement.getTextField_NumEtage().getText()),
					this.modificationLogement.getTextField_DateAcquisition().getText(),
					this.modificationLogement.getComboBox_typeDeLogement().getSelectedItem().toString(),
					this.immeubleSauvegarde);
			// J'ajoute dans le logement la sauvegarde pour réutiliser
			Sauvegarde.deleteItem("Logement");
			Sauvegarde.addItem("Logement", bienTemporaire);

			// On enleve l'immeuble de la sauvegarde pour éviter d'avoir l'id immeuble dans
			// la création du compteur donc constraint check UU
			Sauvegarde.deleteItem("Immeuble");

			Fenetre_InsertionCompteur fenetreCompteur = new Fenetre_InsertionCompteur();
			fenetrePrincipale.getLayeredPane().add(fenetreCompteur);
			fenetreCompteur.setVisible(true);
			fenetreCompteur.moveToFront();
			break;

		case "Ajouter une quotité":
			// Je créer temporairement le bien pour pouvoir récupérer l'ID quand je vais
			// créer la quotité
			Bien bienTemporaireQ = new Bien(this.modificationLogement.getTextField_IdLogement().getText(),
					Double.parseDouble(this.modificationLogement.getTextField_SurfaceHabitable().getText()),
					Integer.parseInt(this.modificationLogement.getTextField_NbPièces().getText()),
					Integer.parseInt(this.modificationLogement.getTextField_NumEtage().getText()),
					this.modificationLogement.getTextField_DateAcquisition().getText(),
					this.modificationLogement.getComboBox_typeDeLogement().getSelectedItem().toString(),
					this.immeubleSauvegarde);
			// J'ajoute dans le logement la sauvegarde pour réutiliser
			Sauvegarde.deleteItem("Logement");
			Sauvegarde.addItem("Logement", bienTemporaireQ);

			Fenetre_InsertionQuotite fenetreQuotite = new Fenetre_InsertionQuotite();
			fenetrePrincipale.getLayeredPane().add(fenetreQuotite);
			fenetreQuotite.setVisible(true);
			fenetreQuotite.moveToFront();
			break;
		case "Modifier":
			Bien logement = null;
			try {
				String typeLogement = this.modificationLogement.getComboBox_typeDeLogement().getSelectedItem()
						.toString();

				logement = new Bien(this.modificationLogement.getTextField_IdLogement().getText(),
						Double.parseDouble(this.modificationLogement.getTextField_SurfaceHabitable().getText()),
						Integer.parseInt(this.modificationLogement.getTextField_NbPièces().getText()),
						Integer.parseInt(this.modificationLogement.getTextField_NumEtage().getText()),
						this.modificationLogement.getTextField_DateAcquisition().getText(), typeLogement,
						this.immeubleSauvegarde);

				this.daoBien.update(logement);

				// Si il y a un compteur à ajouter
				if (Sauvegarde.onSave("Compteur")) {
					this.daoCompteur.create((Compteur) Sauvegarde.getItem("Compteur"));
					Sauvegarde.deleteItem("Compteur");
				}

				// Si il y a une quotité à ajouter
				if (Sauvegarde.onSave("Quotter")) {
					this.daoQuotter.create((Quotter) Sauvegarde.getItem("Quotter"));
					Sauvegarde.deleteItem("Quotter");
				}

				this.modificationLogement.dispose();

				// Afficher un message de réussite
				JOptionPane.showMessageDialog(this.modificationLogement, "Modifé avec succès !", "Succès",
						JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		case "Annuler":
			this.modificationLogement.dispose();
			break;
		default:
			break;
		}
	}

}
