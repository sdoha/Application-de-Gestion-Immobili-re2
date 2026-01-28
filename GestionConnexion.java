package controleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import modele.dao.CictOracleDataSource;
import vue.Fenetre_Accueil;
import vue.Fenetre_Connexion;

public class GestionConnexion implements ActionListener {

	private Fenetre_Connexion fc;

	// Constructeur prenant en paramètre la fenêtre de connexion
	public GestionConnexion(Fenetre_Connexion fc) {
		this.fc = fc;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton boutonClicke = (JButton) e.getSource();
		switch (boutonClicke.getText()) {
		
		case "Se connecter":
			// Récupération des informations de connexion depuis la fenêtre
			String login = this.fc.getNomUtilisateur();
			String mdp = this.fc.getMdp();
			try {
				// Tentative de création de l'accès avec les informations de connexion
				CictOracleDataSource.creerAcces(login, mdp);

				// Si la connexion réussit, ouvrir la fenêtre principale et fermer la fenêtre de connexion
				Fenetre_Accueil fenetreAccueil = new Fenetre_Accueil();
				fenetreAccueil.setVisible(true);
				//On récupère le gestionnaire de l'accueil pour afficher la page d'accueil et rendre non visible tous le reste 
				fenetreAccueil.getGestionAccueil().rendreVisible(fenetreAccueil.getLayeredPane_Accueil());
				//On utilise le gestionnaire pour utiliser la méthode qui charge les graphiques de statistiques
				fenetreAccueil.getGestionAccueil().chargerAccueil();
				this.fc.dispose();
			} catch (SQLException e1) {
				// En cas d'échec de connexion, afficher un message d'erreur
				JOptionPane.showMessageDialog(this.fc, "Login ou mot de passe incorrect", "Erreur",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
			break;
			
		case "Annuler":
			// En cas d'annulation, fermer la fenêtre de connexion
			this.fc.dispose();
			break;
		}
	}
}
