package controleur.suppression;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Facture;
import modele.dao.DaoFacture;
import vue.Fenetre_Accueil;
import vue.suppression.Fenetre_SupprimerFactureCharge;

public class GestionSuppressionFactureCharge implements ActionListener {

    private Fenetre_SupprimerFactureCharge supprimerCharges;
    private DaoFacture daoFacture;

	// Constructeur prenant en paramètre la fenêtre de suppression d'une facture
    public GestionSuppressionFactureCharge(Fenetre_SupprimerFactureCharge supprimerCharges) {
        this.supprimerCharges = supprimerCharges;
        
		// Initialisation de l'accès à la base de données pour l'entité Facture
        this.daoFacture = new DaoFacture();
        Sauvegarde.initializeSave();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.supprimerCharges.getTopLevelAncestor();
        
        // Récuperer la facture dans la sauvegarde
        Facture charge_supp = (Facture) Sauvegarde.getItem("Charge");

        switch (btn.getText()) {
            // Action lors du clic sur "Supprimer"
            case "Supprimer":
                try {
                    // Recherche de la facture à supprimer par son numéro
                    Facture charge = this.daoFacture.findFactureChargeById(charge_supp.getNumero());
                    
                    // Suppression de la facture
                    this.daoFacture.delete(charge); 
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                
                // Fermeture de la fenêtre de suppression
                this.supprimerCharges.dispose(); 
                break;

            // Action lors du clic sur "Annuler"
            case "Annuler":
            	// Fermeture de la fenêtre de suppression sans action
                this.supprimerCharges.dispose(); 
                break;
        }
    }
}
