package controleur;

import java.sql.SQLException;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.Louer;
import modele.dao.DaoLouer;
import vue.Fenetre_Accueil;

public class GestionTableArchiveLouer implements ListSelectionListener {

	private Fenetre_Accueil fenetreAccueil;
	private DaoLouer daoLouer;

	// Constructeur prenant en paramètre la fenêtre d'acceuil
	public GestionTableArchiveLouer(Fenetre_Accueil fenetreAccueil) {
		this.fenetreAccueil = fenetreAccueil;
		daoLouer = new DaoLouer();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			// Vérifie si la sélection dans la table de logements a changé
			int selectedRowLogement = fenetreAccueil.getTable_MesArchives_Louer().getSelectedRow();

			if (selectedRowLogement > -1) {
				// Si une ligne est sélectionnée
				Louer louer = (Louer) Sauvegarde.getItem("Louer");
				Louer louer_sauv = null;
				try {
					louer_sauv = this.daoLouer.findByIdArchive(louer.getBien().getIdBien(),
							louer.getLocataire().getIdLocataire(), louer.getDateDebut());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				Sauvegarde.deleteItem("Louer");
				Sauvegarde.addItem("Louer", louer_sauv);
			}
		}
	}
}
