package controleur.suppression;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Facture;
import modele.dao.DaoFacture;
import vue.Fenetre_Accueil;
import vue.suppression.Fenetre_SupprimerTravaux;

public class GestionSuppressionTravaux implements ActionListener {

    private Fenetre_SupprimerTravaux supprimerTravaux;
    private DaoFacture daoFacture;

    // Constructeur prenant en paramètre la fenêtre de suppression d'un travaux
    public GestionSuppressionTravaux(Fenetre_SupprimerTravaux supprimerTravaux) {
        this.supprimerTravaux = supprimerTravaux;
        
		// Initialisation de l'accès à la base de données pour l'entité Facture
        this.daoFacture = new DaoFacture();
        Sauvegarde.initializeSave();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.supprimerTravaux.getTopLevelAncestor();
        
        switch (btn.getText()) {
            // Suppression d'une facture de travaux
            case "Supprimer":
				// Récupération de la facture de la sauvegarde
                Facture travaux_supp = (Facture) Sauvegarde.getItem("Facture");
                try {
                    Facture travaux = this.daoFacture.findById(travaux_supp.getNumero());
                    this.daoFacture.delete(travaux);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                // Fermeture de la fenêtre de suppression de travaux
                this.supprimerTravaux.dispose();
                break;
                
            // Annulation de la suppression
            case "Annuler":
				// Fermeture de la fenêtre de suppression
                this.supprimerTravaux.dispose();
                break;
        }
    }
}
