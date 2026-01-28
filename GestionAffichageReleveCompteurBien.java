package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controleur.outils.Sauvegarde;
import modele.Compteur;
import modele.Releve;
import modele.dao.DaoReleve;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_AffichageReleveCompteursBien;

public class GestionAffichageReleveCompteurBien implements ActionListener {

	private Fenetre_AffichageReleveCompteursBien farcb;
	private DaoReleve daoReleve;

	// Constructeur prenant en paramètre la fenêtre d'affichage des relevés pour un
	// bien donné
	public GestionAffichageReleveCompteurBien(Fenetre_AffichageReleveCompteursBien arcb) {
		this.farcb = arcb;
		// Initialisation de l'accès à la base de données pour l'entité Relevé
		this.daoReleve = new DaoReleve();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.farcb.getTopLevelAncestor(); // fenetre dans
																									// laquelle
																									// on ouvre des
																									// internal // frame
		switch (btn.getText()) {
		case "Annuler":
			// Fermeture de la fenêtre
			this.farcb.dispose();
			break;
		}
	}

	/**
	 * Méthode pour écrire une ligne de relevé pour un compteur donné
	 *
	 * @param numeroLigne (int) : correspond au numéro de la ligne courante dans la
	 *                    table des relevés
	 * @param location    (Releve) : correspond au relevé courant
	 * @throws SQLException
	 */
	public void ecrireLigneTableReleveCompteurs(int numeroLigne, Releve e) throws SQLException {
		JTable tableReleveCompteur = this.farcb.getTable_releve_compteur_bien();
		DefaultTableModel modeleTable = (DefaultTableModel) tableReleveCompteur.getModel();

		modeleTable.setValueAt(e.getDate_releve(), numeroLigne, 0);
		modeleTable.setValueAt(e.getIndexComp(), numeroLigne, 1);
	}

	/**
	 * Méthode pour charger les relevés dans la table d'affichage des relevés pour
	 * un compteur donné
	 *
	 * @throws SQLException
	 */
	public void chargerReleveCompteurs() throws SQLException {
		// On récupère le compteur de la sauvegarde
		Compteur compteurSauvegarde = (Compteur) Sauvegarde.getItem("Compteur");

		List<Releve> releve = this.daoReleve.findReleveByCompteur(compteurSauvegarde.getIdCompteur());

		DefaultTableModel modeleTable = (DefaultTableModel) this.farcb.getTable_releve_compteur_bien().getModel();

		modeleTable.setRowCount(releve.size());

		for (int i = 0; i < releve.size(); i++) {
			Releve e = releve.get(i);
			this.ecrireLigneTableReleveCompteurs(i, e);
		}
	}

}