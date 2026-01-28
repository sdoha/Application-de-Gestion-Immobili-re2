package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import modele.Entreprise;
import modele.dao.DaoEntreprise;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionEntreprise;

public class GestionInsertionEntreprise implements ActionListener {

    private Fenetre_InsertionEntreprise fie;
    private DaoEntreprise daoEntreprise;

	// Constructeur prenant en paramètre la fenêtre d'insertion d'une entreprise
    public GestionInsertionEntreprise(Fenetre_InsertionEntreprise fie) {
        this.fie = fie;
        
		// Initialisation de l'accès à la base de données pour l'entité Entreprise
        this.daoEntreprise = new DaoEntreprise();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fie.getTopLevelAncestor();

        switch (btn.getText()) {
            case "Ajouter":
            	// Création de l'objet Entreprise null
                Entreprise entreprise = null;
                try {
                    // Créer une nouvelle entreprise avec les informations fournies dans la fenêtre d'insertion
                    entreprise = new Entreprise(
                            this.fie.getTextField_SIRET().getText(),
                            this.fie.getTextField_Nom().getText(),
                            this.fie.getTextField_Adresse().getText(),
                            this.fie.getTextField_CP().getText(),
                            this.fie.getTextField_Ville().getText(),
                            this.fie.getTextField_Mail().getText(),
                            this.fie.getTextField_Telephone().getText(),
                            this.fie.getTextField_IBAN().getText());

                    // Ajouter la nouvelle entreprise à la base de données
                    this.daoEntreprise.create(entreprise);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                // Fermer la fenêtre d'insertion après l'ajout
                this.fie.dispose();
                break;

            case "Annuler":
                // Fermeture de la fenêtre d'insertion d'une entreprise
                this.fie.dispose();
                break;
        }
    }
}
