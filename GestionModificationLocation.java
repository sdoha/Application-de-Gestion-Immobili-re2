package controleur.modification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Louer;
import modele.dao.DaoLouer;
import vue.modification.Fenetre_ModificationLocation;

public class GestionModificationLocation implements ActionListener {

	private Fenetre_ModificationLocation fml;
	private DaoLouer daoLouer;
	private Louer louerBD;

	public GestionModificationLocation(Fenetre_ModificationLocation fml) {
		this.fml = fml;
		this.daoLouer = new DaoLouer();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();

		switch (btn.getText()) {
		case "Modifier":
			// Récupération de l'objet Louer depuis la sauvegarde
			try {
				Louer louer = (Louer) Sauvegarde.getItem("Louer");
				// Recherche de l'objet Louer dans la base de données
				louerBD = this.daoLouer.findById(louer.getBien().getIdBien(), louer.getLocataire().getIdLocataire());

				// Création d'un nouvel objet Louer avec les modifications
				Louer louerM = new Louer(louerBD.getLocataire(), louerBD.getBien(),
						fml.getTextField_date_debut().getText(), louerBD.getNbMois(),
						Double.parseDouble(fml.getTextField_loyer_TCC().getText()),
						Double.parseDouble(fml.getTextField_provision_chargeMens_TTC().getText()),
						Double.parseDouble(fml.getTextField_caution_TTC().getText()), louerBD.getBail(),
						louerBD.getEtat_lieux(), fml.getTextField_date_derniere_regularisation().getText(),
						louerBD.getLoyerPaye(), louerBD.getIcc(),
						Double.parseDouble(fml.getTextField_montant_reel_paye().getText()));

				// Mise à jour de l'objet Louer dans la base de données
				this.daoLouer.update(louerM);

				// Fermeture de la fenêtre de modification
				this.fml.dispose();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			break;
		case "Annuler":
			// Annulation de la modification et fermeture de la fenêtre
			this.fml.dispose();
			break;
		default:
			break;
		}
	}
}