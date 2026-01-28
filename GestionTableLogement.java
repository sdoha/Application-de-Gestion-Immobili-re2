package controleur;

import java.sql.SQLException;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.dao.DaoBien;
import vue.Fenetre_Accueil;

public class GestionTableLogement implements ListSelectionListener {

	private Fenetre_Accueil fenetreAccueil;
	private DaoBien daoBien;

	// Constructeur prenant en paramètre la fenêtre d'acceuil
	public GestionTableLogement(Fenetre_Accueil fenetreAccueil) {
		this.fenetreAccueil = fenetreAccueil;
		this.daoBien = new DaoBien();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			// Vérifie si la sélection dans la table de logements a changé
			int selectedRowLogement = fenetreAccueil.getTableLogementsParBien().getSelectedRow();

			if (selectedRowLogement > -1) {
				// Si une ligne est sélectionnée
				JTable tableLogement = fenetreAccueil.getTableLogementsParBien();
				Bien bien = null;
				try {
					// Récupération de l'objet Bien associé à la ligne sélectionnée
					bien = daoBien.findById(tableLogement.getValueAt(selectedRowLogement, 0).toString());
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				// Mise à jour de la sauvegarde avec l'objet Bien sélectionné
				Sauvegarde.deleteItem("Logement");
				Sauvegarde.addItem("Logement", bien);
			}
		}
	}
}
