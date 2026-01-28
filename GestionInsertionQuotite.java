package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Quotite;
import modele.Quotter;
import modele.dao.DaoQuotite;
import modele.dao.DaoQuotter;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionQuotite;

public class GestionInsertionQuotite implements ActionListener {

	private Fenetre_InsertionQuotite fiq;
	private DaoQuotite daoQuotite;
	private DaoQuotter daoQuotter; 

	// Constructeur prenant en paramètre la fenêtre d'insertion d'une quotité
	public GestionInsertionQuotite(Fenetre_InsertionQuotite fiq) {
		this.fiq = fiq;
		
		// Initialisation de l'accès à la base de données pour l'entité Quotité
		this.daoQuotite = new DaoQuotite();
		
		// Initialisation de l'accès à la base de données pour l'entité Quotter
		this.daoQuotter = new DaoQuotter();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fiq.getTopLevelAncestor();
		
		switch (btn.getText()) {
			// Action lors du clic sur "Ajouter"
			case "Ajouter":
				// Création d'un objet Quotité à null
				Quotite quotite_type = null;
				// Création d'un objet Quotter à null
				Quotter quotter = null;
				try {
					// Récupération du logement par la sauvegarde 
					Bien bien_sauvegarde = (Bien) Sauvegarde.getItem("Logement");
					
					// Récupération du type de quotité depuis l'interface graphique
					String typeQuotite = (String) this.fiq.getComboBox_typeDeCompteur().getSelectedItem();
					
					// Récupération du type de quotité correspondant depuis la base de donnée 
					quotite_type = this.daoQuotite.findById(typeQuotite);
					
					// Récupération du pourcentage depuis l'interface graphique
					Double pourcentage = Double.parseDouble(this.fiq.getTextField_Pourcentage().getText());
					
					// Création de l'objet java quotter 
					quotter = new Quotter(bien_sauvegarde, quotite_type, pourcentage);

					// Ajout dans la sauvegarde pour le créer dans la page voulue
					Sauvegarde.deleteItem("Quotter");
					Sauvegarde.addItem("Quotter", quotter);

				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// Fermeture de la fenêtre d'insertion de quotité après ajout
				this.fiq.dispose();
				break;
				
			// Action lors du clic sur "Annuler"
			case "Annuler":
				// Fermeture de la fenêtre d'insertion de quotité sans ajout
				this.fiq.dispose();
				break;
		}
	}
}
