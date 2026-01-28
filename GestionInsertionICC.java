package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import modele.ICC;
import modele.dao.DaoICC;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionICC;

public class GestionInsertionICC implements ActionListener {

    private Fenetre_InsertionICC fii;
    private DaoICC daoICC;

	// Constructeur prenant en paramètre la fenêtre d'insertion d'un ICC
    public GestionInsertionICC(Fenetre_InsertionICC fii) {
        this.fii = fii;
        
		// Initialisation de l'accès à la base de données pour l'entité ICC
        this.daoICC = new DaoICC();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fii.getTopLevelAncestor();
        
        // Gérer les actions en fonction du bouton cliqué
        switch (btn.getText()) {
            case "Ajouter":
            	// Création de l'objet ICC null
                ICC icc = null;
                try {
                    // Créer un nouvel ICC avec les informations fournies dans la fenêtre d'insertion
                    icc = new ICC(
                            this.fii.getTextField_Annee().getText(),
                            this.fii.getTextField_Trimestre().getText(),
                            Double.parseDouble(this.fii.getTextField_indice().getText()));

                    // Ajouter le nouvel ICC à la base de données
                    this.daoICC.create(icc);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                // Fermer la fenêtre d'insertion après l'ajout
                this.fii.dispose();
                break;
                
            case "Annuler":
                // Fermeture de la fenêtre d'insertion d'un ICC
                this.fii.dispose();
                break;
        }
    }
}
