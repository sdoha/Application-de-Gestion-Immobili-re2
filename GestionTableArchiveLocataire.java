package controleur;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controleur.outils.Sauvegarde;
import modele.Bien;
import vue.Fenetre_Accueil;

public class GestionTableArchiveLocataire implements ListSelectionListener {

	private Fenetre_Accueil fenetreAccueil;

	// Constructeur prenant en paramètre la fenêtre d'acceuil
	public GestionTableArchiveLocataire(Fenetre_Accueil fenetreAccueil) {
		this.fenetreAccueil = fenetreAccueil;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			// Vérifie si la sélection dans la table de logements a changé
			int selectedRowLogement = fenetreAccueil.getTableLogementsParBien().getSelectedRow();

			if (selectedRowLogement > -1) {
				Bien bien = null;
				Sauvegarde.deleteItem("Logement");
				Sauvegarde.addItem("Logement", bien);
			}
		}
	}
}
