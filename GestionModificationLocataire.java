package controleur.modification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Locataire;
import modele.dao.DaoLocataire;
import vue.insertion.Fenetre_AffichageInfoLocataire;

public class GestionModificationLocataire implements ActionListener {

	private Fenetre_AffichageInfoLocataire modificationLocataire;
	private DaoLocataire daoLocataire;

	public GestionModificationLocataire(Fenetre_AffichageInfoLocataire modificationLocataire) {
		this.modificationLocataire = modificationLocataire;
		this.daoLocataire = new DaoLocataire();
		Sauvegarde.initializeSave();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();

		switch (btn.getText()) {
		case "Modifier":
			// Appel de la méthode pour effectuer la modification du locataire
			modifierLocataire();
			break;
		case "Retour":
			// Fermeture de la fenêtre de modification
			this.modificationLocataire.dispose();
			break;
		default:
			break;
		}
	}

	/**
	 * Méthode privée pour effectuer la modification du locataire
	 *
	 */
	private void modifierLocataire() {
		Locataire locataire = creerLocataireAPartirDesChamps();
		try {
			// Appel de la méthode de mise à jour dans la base de données
			this.daoLocataire.update(locataire);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Fermeture de la fenêtre de modification après la mise à jour
		this.modificationLocataire.dispose();
	}

	/**
	 * Méthode privée pour créer un objet Locataire à partir des champs de saisie
	 *
	 * @return
	 */
	private Locataire creerLocataireAPartirDesChamps() {
		return new Locataire(this.modificationLocataire.getTextField_Id().getText(),
				this.modificationLocataire.getTextField_Nom().getText(),
				this.modificationLocataire.getTextField_Prenom().getText(),
				this.modificationLocataire.getTextField_Telephone().getText(),
				this.modificationLocataire.getTextField_Mail().getText(),
				this.modificationLocataire.getTextField_DateN().getText());
	}
}
