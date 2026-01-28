package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Compteur;
import modele.Immeuble;
import modele.dao.DaoCompteur;
import modele.dao.DaoImmeuble;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionCompteur;

public class GestionInsertionCompteur implements ActionListener {

    private Fenetre_InsertionCompteur fic;
    private DaoCompteur daoCompteur;

	// Constructeur prenant en paramètre la fenêtre d'insertion d'un compteur
    public GestionInsertionCompteur(Fenetre_InsertionCompteur fic) {
        this.fic = fic;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fic.getTopLevelAncestor();
        
        switch (btn.getText()) {
            case "Ajouter":
                try {
                    // Création d'un nouveau compteur avec les données de la fenêtre d'insertion
                    Compteur compteur = new Compteur(
                            this.fic.getTextField_IdCompteur().getText(),
                            this.fic.getComboBox_typeDeCompteur().getSelectedItem().toString(),
                            Double.parseDouble(this.fic.getTextField_textFieldPrixAbo().getText()),
                            (Bien) Sauvegarde.getItem("Logement"),
                            (Immeuble) Sauvegarde.getItem("Immeuble")
                    );
                    
                    // Supprimer l'ancien compteur de la sauvegarde et ajouter le nouveau
                    Sauvegarde.deleteItem("Compteur");
                    Sauvegarde.addItem("Compteur", compteur);
                    
                    // Fermer la fenêtre d'insertion
                    fic.dispose();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                break;

            case "Annuler":
                // Fermeture de la fenêtre d'insertion d'un compteur
                this.fic.dispose();
                break;
        }
    }
}
