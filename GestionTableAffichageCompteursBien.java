package controleur.insertion;

import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Compteur;
import modele.dao.DaoCompteur;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_AffichageCompteursBien;

public class GestionTableAffichageCompteursBien implements ListSelectionListener {
	
	private Fenetre_AffichageCompteursBien fenetreAffichageCompteursBiens;
	private DaoCompteur daoCompteur;

	// Constructeur prenant en paramètre la fenêtre d'affichage des compteurs pour un bien
	public GestionTableAffichageCompteursBien(Fenetre_AffichageCompteursBien fenetreAffichageCompteursBiens) {
		this.fenetreAffichageCompteursBiens = fenetreAffichageCompteursBiens;
		
		// Initialisation de l'accès à la base de données pour l'entité Compteur
		this.daoCompteur = new DaoCompteur();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			// Vérifie si la sélection dans la table de Compteur a changé
			int selectedRowCompteur = fenetreAffichageCompteursBiens.getTable_compteurs().getSelectedRow();

			if (selectedRowCompteur > -1) {
				// Si une ligne est sélectionnée
				JTable tableCompteur = fenetreAffichageCompteursBiens.getTable_compteurs();
				Compteur compteur = null;
				try {
					// Récupération de l'objet Compteur associé à la ligne sélectionnée
					compteur = daoCompteur.findById(tableCompteur.getValueAt(selectedRowCompteur, 0).toString());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				// Mise à jour de la sauvegarde avec l'objet Compteur sélectionné
				Sauvegarde.deleteItem("Compteur");
				Sauvegarde.addItem("Compteur", compteur);
			}
		}
	}

}
