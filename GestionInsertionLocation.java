package controleur.insertion;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controleur.outils.PDFImporter;
import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.ICC;
import modele.Locataire;
import modele.Louer;
import modele.dao.DaoBien;
import modele.dao.DaoICC;
import modele.dao.DaoLocataire;
import modele.dao.DaoLouer;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionICC;
import vue.insertion.Fenetre_InsertionLocation;

public class GestionInsertionLocation implements ActionListener, MouseListener {

    // Attributs
    private Fenetre_InsertionLocation fil;
    private DaoBien daoBien;
    private DaoICC daoICC;
    private DaoLocataire daoLocataire;
    private DaoLouer daoLouer;
    private String bail;
    private String etatLieux;

    // Constructeur
	// Constructeur prenant en paramètre la fenêtre d'insertion d'une location
    public GestionInsertionLocation(Fenetre_InsertionLocation fil) {
        this.fil = fil;
        
		// Initialisation de l'accès à la base de données pour l'entité Bien
        this.daoBien = new DaoBien();
        
		// Initialisation de l'accès à la base de données pour l'entité ICC
        this.daoICC = new DaoICC();
        
		// Initialisation de l'accès à la base de données pour l'entité Locataire
        this.daoLocataire = new DaoLocataire();
        
		// Initialisation de l'accès à la base de données pour l'entité Louer
        this.daoLouer = new DaoLouer();
        
        this.bail = " ";
        this.etatLieux = " ";
    }

    // Gestion des actions
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source instanceof JButton) {
            JButton btn = (JButton) source;

            Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fil.getTopLevelAncestor();
            switch (btn.getText()) {
                case "Ajouter un bail":
                    try {
                    	// Ajouter un PDF pour le bail
                        String cheminOrigineBail = PDFImporter.getInstance().importPDFCheminString();
                        this.bail = cheminOrigineBail;

                        if (cheminOrigineBail != null && !cheminOrigineBail.isEmpty()) {
                            JOptionPane.showMessageDialog(fil, "Le fichier a été bien ajouté.", "Succès",
                                    JOptionPane.INFORMATION_MESSAGE);

                            fil.getLblBail().setText(cheminOrigineBail);
                        } else {
                            JOptionPane.showMessageDialog(fil, "Aucun fichier PDF sélectionné.", "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (NullPointerException ex) {
                        JOptionPane.showMessageDialog(fil, "Annulation de l'insertion du fichier.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;

                case "Ajouter l'état des lieux":
                    try {
                    	// Ajouter un PDF pour l'état des lieux
                        String cheminOrigineEtatLieux = PDFImporter.getInstance().importPDFCheminString();
                        this.etatLieux = cheminOrigineEtatLieux;

                        if (cheminOrigineEtatLieux != null && !cheminOrigineEtatLieux.isEmpty()) {
                            JOptionPane.showMessageDialog(fil, "Le fichier a été bien ajouté.", "Succès",
                                    JOptionPane.INFORMATION_MESSAGE);

                            fil.getLblNomEtatDesLieux().setText(cheminOrigineEtatLieux);
                        } else {
                            JOptionPane.showMessageDialog(fil, "Aucun fichier PDF sélectionné.", "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (NullPointerException ex) {
                        JOptionPane.showMessageDialog(fil, "Annulation de l'insertion du fichier.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;

                case "Ajouter":
                	// Création de la location de type Louer à null
                    Louer location = null;
                    try {
                    	// Récuperer le bien  et l'icc placés dans la sauvegarde
                        Bien bienSauvegarde = (Bien) Sauvegarde.getItem("Logement");
                        ICC icc = (ICC) Sauvegarde.getItem("ICC");
                        
        				// Création de l'objet Locataire avec les données de la fenêtre d'insertion
                        Locataire nouveauLocataire = new Locataire(this.fil.getTextField_IdLocataire().getText(),
                                this.fil.getTextField_Nom().getText(), this.fil.getTextField_Prenom().getText(),
                                this.fil.getTextField_tel().getText(), this.fil.getTextField_e_mail().getText(),
                                this.fil.getTextField_Date_de_naissance().getText());

                        this.daoLocataire.create(nouveauLocataire);

        				// Création de l'objet Louer avec les données de la fenêtre d'insertion
                        location = new Louer(nouveauLocataire, bienSauvegarde,
                                this.fil.getTextField_date_arrivee().getText(), 0, // Nombre de mois
                                Double.parseDouble(this.fil.getTextField_loyer().getText()),
                                Double.parseDouble(this.fil.getTextField_provision_sur_charges().getText()),
                                Double.parseDouble(this.fil.getTextField_caution().getText()), this.bail, this.etatLieux,
                                null, // Date de départ
                                0, // Loyer payé
                                icc, 0 // Montant réel payé
                        );

                        this.daoLouer.create(location);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
    				// Fermeture de la fenêtre d'insertion après l'ajout de la location
                    this.fil.dispose();
                    break;

                case "Ajouter ICC":
                	// Ajouter un ICC, donc ouverture de la fenêtre d'insertion d'ICC
                    Fenetre_InsertionICC insertionICC = new Fenetre_InsertionICC();
                    fenetre_Principale.getLayeredPane().add(insertionICC);
                    insertionICC.setVisible(true);
                    insertionICC.moveToFront();
                    break;

                case "Charger ICC":
                    try {
                    	// Utilise la méthode chargerICC afin de charger le tableau d'icc de la fenêtre
                        this.chargerICC();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    break;

                case "Annuler":
        			// Fermeture de la fenêtre d'insertion
                    this.fil.dispose();
                    break;
            }
        } else if (source instanceof JComboBox) {
        	// Permet de filtrer les logements par immeuble
            this.filtreLogementByImmeuble();
        }
    }

    // Gestion des clics de souris
    @Override
    public void mouseClicked(MouseEvent e) {
        JLabel source = (JLabel) e.getSource();

        if (source == fil.getLblNomEtatDesLieux() || source == fil.getLblBail()) {
            ouvrirPDF(source.getText());
        }
    }
    
    // Autres méthodes de l'interface MouseListener

 	@Override
 	public void mousePressed(MouseEvent e) {
 		// TODO Auto-generated method stub
 	}

 	@Override
 	public void mouseReleased(MouseEvent e) {
 		// TODO Auto-generated method stub	
 	}

 	@Override
 	public void mouseEntered(MouseEvent e) {
 		// TODO Auto-generated method stub		
 	}

 	@Override
 	public void mouseExited(MouseEvent e) {
 		// TODO Auto-generated method stub	
 	}
 

    /**
     * Méthode permettant d'écrire une ligne dans la table des logements
     * 
     * @param numeroLigne (int) : correspond au numéro de la ligne courante dans la
	 *                    table des logements
     * @param bien (Bien) : correspond à l'entreprise courante
     */
    public void ecrireLigneTableLogements(int numeroLigne, Bien bien) {
        JTable tableLogements = this.fil.getTable_id_logements();
        DefaultTableModel modeleTable = (DefaultTableModel) tableLogements.getModel();

        modeleTable.setValueAt(bien.getIdBien(), numeroLigne, 0);
    }

    /**
     * Méthode pour mettre à jour la table de logements pour un bien spécifique
     * 
	 * @param idLocataire (String) : correspond à l'identifiant du logement
	 * @throws SQLException
     */
    private void updateTableLogementForBien(String idLogement) throws SQLException {
        List<Bien> biens = this.daoBien.findBiensparImmeuble(idLogement);

        DefaultTableModel modeleTable = (DefaultTableModel) this.fil.getTable_id_logements().getModel();
        modeleTable.setRowCount(biens.size());

        for (int i = 0; i < biens.size(); i++) {
            Bien b = biens.get(i);
            this.ecrireLigneTableLogements(i, b);
        }
    }

    /**
     *  Méthode pour filtrer les logements par immeuble
     */
    private void filtreLogementByImmeuble() {
        JComboBox<String> comboBox_MesBiens = this.fil.getComboBox_bien();
        // Récupère l'immeuble selectionné dans la comboBox
        String idImmeubleSelectionne = (String) comboBox_MesBiens.getSelectedItem();

        if (!idImmeubleSelectionne.equals("Biens")) {
            try {
                this.updateTableLogementForBien(idImmeubleSelectionne);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * // Méthode pour ouvrir un fichier PDF
	 *
     * @param chemin (String) : correspond au chemin du fichier PDF
     */
    public void ouvrirPDF(String chemin) {
        String cheminFichierPDF = chemin;
        File fichierPDF = new File(cheminFichierPDF);

        if (fichierPDF.exists()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(fichierPDF);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(fil, "Erreur lors de l'ouverture du fichier PDF : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(fil, "Le fichier PDF n'existe pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Méthode permettant d'écrire une ligne dans la table ICC
     * 
     * @param numeroLigne (int) : correspond au numéro de la ligne courante dans la
	 *                    table des ICC
	 * @param icc    (ICC) : correspond à l'ICC courant
     * @throws SQLException
     */
    public void ecrireLigneTableICC(int numeroLigne, ICC icc) throws SQLException {
        JTable tableImmeuble = this.fil.getTable_liste_ICC();
        DefaultTableModel modeleTable = (DefaultTableModel) tableImmeuble.getModel();

        modeleTable.setValueAt(icc.getAnnee(), numeroLigne, 0);
        modeleTable.setValueAt(icc.getTrimestre(), numeroLigne, 1);
        modeleTable.setValueAt(icc.getIndice(), numeroLigne, 2);
    }

    /**
     * Méthode permettant de charger les ICC dans la table
     * @throws SQLException
     */
    private void chargerICC() throws SQLException {
    	// Récupère tout les ICC
        List<ICC> iccs = this.daoICC.findAll();
        DefaultTableModel modeleTable = (DefaultTableModel) this.fil.getTable_liste_ICC().getModel();
        modeleTable.setRowCount(iccs.size());

        for (int i = 0; i < iccs.size(); i++) {
            ICC icc = iccs.get(i);
            this.ecrireLigneTableICC(i, icc);
        }
    }

    
}
