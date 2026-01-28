package controleur;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Immeuble;
import modele.dao.CictOracleDataSource;
import modele.dao.DaoBien;
import modele.dao.DaoImmeuble;
import modele.dao.requetes.VerificationOccupation;
import vue.Fenetre_Accueil;

public class GestionBienLogement implements ListSelectionListener {

	private Fenetre_Accueil fenetreAccueil;
	private DaoImmeuble daoImmeuble;
	private DaoBien daoBien;
	private VerificationOccupation verificationOccupation;

	// Constructeur prenant en paramètre la fenêtre d'acceuil
	public GestionBienLogement(Fenetre_Accueil fenetreAccueil) {
		this.fenetreAccueil = fenetreAccueil;
		this.daoImmeuble = new DaoImmeuble();
		this.daoBien = new DaoBien();
		this.verificationOccupation = new VerificationOccupation(CictOracleDataSource.getConnectionBD());
		Sauvegarde.initializeSave();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			 // Récupération de la ligne sélectionnée dans la table des biens
            int selectedRow = fenetreAccueil.getTableBiens().getSelectedRow();

            if (selectedRow > -1) {
                // Récupération de la table des biens et recherche de l'immeuble associé à la ligne sélectionnée
                JTable tableBiens = fenetreAccueil.getTableBiens();
                Immeuble immeuble = null;
                try {
                    immeuble = daoImmeuble.findById(tableBiens.getValueAt(selectedRow, 0).toString(),
                            tableBiens.getValueAt(selectedRow, 1).toString(),
                            tableBiens.getValueAt(selectedRow, 2).toString());
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

                if (immeuble != null) {
                    // Ajout de l'immeuble associé aux logements dans le tableau de sauvegarde
                    Sauvegarde.deleteItem("Logement");
                    Sauvegarde.deleteItem("Immeuble");
                    Sauvegarde.addItem("Immeuble", immeuble);

                    // Récupération de la liste des biens associés à l'immeuble
                    List<Bien> biens = null;
                    try {
                        biens = daoBien.findBiensparImmeuble(immeuble.getImmeuble());
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }

                    // Récupération de la table des logements par bien
                    JTable logements = fenetreAccueil.getTableLogementsParBien();
                    // Vidage de la table des logements
                    GestionAccueil.viderTable(logements);

                    // Déselectionner la ligne de la table logement quand on change de bien
                    logements.clearSelection();

                    DefaultTableModel model = (DefaultTableModel) logements.getModel();

                    int numColonnes = 6; // Nombre de colonnes dans la table des logements
                    int numLignesNecessaires = biens.size(); // Nombre de lignes nécessaires dans la table
                    int numLigneActuel = model.getRowCount();

                    // Ajout de nouvelles lignes si nécessaire dans la table des logements
                    for (int i = 0; i < numLignesNecessaires - numLigneActuel; i++) {
                        model.addRow(new Object[numColonnes]);
                    }

                    // Remplissage de la table des logements avec les informations des biens associés
                    for (int i = 0; i < biens.size(); i++) {
                        Bien bien = biens.get(i);
                        if (bien != null) {
                            // Récupération des informations du bien
                            String nom = bien.getIdBien();
                            double surface = bien.getSurfaceHabitable();
                            int nbPieces = bien.getNbPieces();
                            int etage = bien.getNumEtage();
                            String date = bien.getDateAcquisition();
                            String type = bien.getType_bien();

                            // Utilisation de la méthode estLoué pour vérifier si le logement est loué
                            boolean estLoue = false;
                            try {
                                estLoue = verificationOccupation.estLoue(bien.getIdBien());
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                            String occupe = estLoue ? "Oui" : "Non";
                            // Remplissage des cellules de la table des logements
                            model.setValueAt(nom, i, 0);
                            model.setValueAt(surface, i, 1);
                            model.setValueAt(nbPieces, i, 2);
                            model.setValueAt(etage, i, 3);
                            model.setValueAt(date, i, 4);
                            model.setValueAt(occupe, i, 5);
                            model.setValueAt(type, i, 6);
                        }
                    }
                }
            }
        }
    }
}