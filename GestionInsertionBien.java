package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import controleur.outils.Sauvegarde;
import modele.Compteur;
import modele.Immeuble;
import modele.dao.DaoCompteur;
import modele.dao.DaoImmeuble;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionBien;
import vue.insertion.Fenetre_InsertionCompteur;

public class GestionInsertionBien implements ActionListener {

    private Fenetre_InsertionBien insertionBien;
    private DaoImmeuble daoImmeuble;
    private DaoCompteur daoCompteur;

	// Constructeur prenant en paramètre la fenêtre d'insertion d'un bien
    public GestionInsertionBien(Fenetre_InsertionBien insertionBien) {
        this.insertionBien = insertionBien;
        
		// Initialisation de l'accès à la base de données pour l'entité Immeuble
        this.daoImmeuble = new DaoImmeuble();
        
		// Initialisation de l'accès à la base de données pour l'entité Compteur
        this.daoCompteur = new DaoCompteur();
        Sauvegarde.initializeSave();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.insertionBien.getTopLevelAncestor();
        
        switch (btn.getText()) {
            case "Ajouter un compteur":
                // Ouvrir la fenêtre d'insertion de compteur
                Fenetre_InsertionCompteur fenetreCompteur = new Fenetre_InsertionCompteur();
                
                // Créer temporairement l'immeuble pour pouvoir récupérer l'ID lors de la création du compteur
                Immeuble immeubleTemporaire = new Immeuble(
                        this.insertionBien.getTextField_IdImmeuble().getText(),
                        this.insertionBien.getTextField_adresse().getText(),
                        this.insertionBien.getTextField_codePostal().getText(),
                        this.insertionBien.getTextField_ville().getText(),
                        this.insertionBien.getTextField_periodeDeConstruction().getText(),
                        this.insertionBien.getComboBox_typeDeBien().getSelectedItem().toString());
                
                // Ajouter l'immeuble dans la sauvegarde pour réutilisation
                Sauvegarde.deleteItem("Immeuble");
                Sauvegarde.addItem("Immeuble", immeubleTemporaire);
                
                // On enlève le logement de la sauvegarde pour éviter d'avoir l'id immeuble dans la création du compteur donc constraint check UU
				Sauvegarde.deleteItem("Logement");
                
                fenetre_Principale.getLayeredPane().add(fenetreCompteur);
                fenetreCompteur.setVisible(true);
                fenetreCompteur.moveToFront();
                break;
                
            case "Ajouter":
                try {
                    // Créer un nouvel immeuble avec les données de la fenêtre d'insertion
                    Immeuble nouvelImmeuble = new Immeuble(
                            this.insertionBien.getTextField_IdImmeuble().getText(),
                            this.insertionBien.getTextField_adresse().getText(),
                            this.insertionBien.getTextField_codePostal().getText(),
                            this.insertionBien.getTextField_ville().getText(),
                            this.insertionBien.getTextField_periodeDeConstruction().getText(),
                            this.insertionBien.getComboBox_typeDeBien().getSelectedItem().toString());

                    // Ajouter l'immeuble avant d'ajouter le compteur, sinon on ne peut pas créer un compteur sur un immeuble inexistant
                    this.daoImmeuble.create(nouvelImmeuble);
                    
                    // Ajouter le compteur de la sauvegarde qui est déjà relié à l'immeuble courant
                    if (Sauvegarde.onSave("Compteur")) {
                        this.daoCompteur.create((Compteur) Sauvegarde.getItem("Compteur"));
                        Sauvegarde.clearSave();
                    }
                    
                    // Fermer la fenêtre d'insertion après l'ajout
                    this.insertionBien.dispose();
                    
                    // Afficher un message de réussite
                    JOptionPane.showMessageDialog(insertionBien, "Bien ajouté avec succès !", "Succès",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                break;

            case "Annuler":
                // Fermeture la fenêtre d'insertion
                this.insertionBien.dispose();
                break;
        }
    }
}
