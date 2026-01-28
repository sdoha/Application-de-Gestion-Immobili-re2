package controleur.suppression;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Louer;
import modele.dao.DaoLouer;
import vue.Fenetre_Accueil;
import vue.suppression.Fenetre_SupprimerLocation;

public class GestionSuppressionLocation implements ActionListener {

	private Fenetre_SupprimerLocation supprimerLocation;
	private DaoLouer daoLouer;

	// Constructeur prenant en paramètre la fenêtre de suppression d'une location
	public GestionSuppressionLocation(Fenetre_SupprimerLocation supprimerLocation) {
		this.supprimerLocation = supprimerLocation;
		
		// Initialisation de l'accès à la base de données pour l'entité Louer
		this.daoLouer = new DaoLouer();
		Sauvegarde.initializeSave();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.supprimerLocation.getTopLevelAncestor();
		switch (btn.getText()) {
			// Suppression d'une location
			case "Supprimer":
		        // Récuperer la location de type Louer de la sauvegarde
				Louer louer_supp = (Louer) Sauvegarde.getItem("Louer");
				try {
					// Récupération de la location à supprimer
					Louer louer = this.daoLouer.findById(louer_supp.getBien().getIdBien(),
							louer_supp.getLocataire().getIdLocataire(), louer_supp.getDateDebut());
					
					// Suppression de la location dans la base de données
					this.daoLouer.deleteVrai(louer);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				// Fermeture de la fenêtre de suppression de location
				this.supprimerLocation.dispose();
				break;
				
			// Annulation de la suppression
			case "Annuler":
                // Fermeture de la fenêtre de suppression
				this.supprimerLocation.dispose();
				break;
		}
	}
}
