package controleur.modification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Entreprise;
import modele.Facture;
import modele.Immeuble;
import modele.dao.DaoBien;
import modele.dao.DaoEntreprise;
import modele.dao.DaoFacture;
import modele.dao.DaoImmeuble;
import vue.Fenetre_Accueil;
import vue.modification.Fenetre_ModificationTravauxImmeuble;

public class GestionModificationTravauxImmeuble implements ActionListener {

	private Fenetre_ModificationTravauxImmeuble modificationTravauxImmeuble;
	private DaoFacture daoTravaux;
	private DaoEntreprise daoEntreprise;
	private DaoImmeuble daoImmeuble;
	private DaoBien daoBien;
	private Facture facture;
	private Entreprise entreprise;
	private Immeuble immeuble;
	private Bien bien;

	public GestionModificationTravauxImmeuble(Fenetre_ModificationTravauxImmeuble modificationTravauxImmeuble) {
		this.modificationTravauxImmeuble = modificationTravauxImmeuble;
		this.daoTravaux = new DaoFacture();
		this.daoEntreprise = new DaoEntreprise();
		this.daoBien = new DaoBien();
		this.daoImmeuble = new DaoImmeuble();
		Sauvegarde.initializeSave();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetrePrincipale = (Fenetre_Accueil) this.modificationTravauxImmeuble.getTopLevelAncestor();

		switch (btn.getText()) {
		case "Modifier":
			try {
				// Récupération des objets associés à la facture depuis la base de données
				this.facture = this.daoTravaux
						.findById(this.modificationTravauxImmeuble.getTextField_Numero().getText());
				this.entreprise = this.daoEntreprise.findById(this.facture.getEntreprise().getSiret());
				this.bien = this.daoBien
						.findById(this.modificationTravauxImmeuble.getTextField_Bien_Logement().getText());
				this.immeuble = this.daoImmeuble
						.findById(this.modificationTravauxImmeuble.getTextField_Bien_Logement().getText());

				// Création d'un nouvel objet Facture avec les modifications
				Facture nouvelFacture = new Facture(this.modificationTravauxImmeuble.getTextField_Numero().getText(),
						this.modificationTravauxImmeuble.getTextField_dateEmission().getText(),
						facture.getDatePaiement(), facture.getModePaiement(), facture.getNumeroDevis(),
						this.modificationTravauxImmeuble.getTextField_designation().getText(),
						facture.getMontantReelPaye(),
						Double.parseDouble(this.modificationTravauxImmeuble.getTextField_montant().getText()),
						facture.getImputableLocataire(), this.immeuble, this.bien, entreprise);

				// Mise à jour de l'objet Facture dans la base de données
				this.daoTravaux.update(nouvelFacture);

				// Fermeture de la page après la modification
				this.modificationTravauxImmeuble.dispose();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		case "Annuler":
			// Annulation de la modification et fermeture de la fenêtre
			this.modificationTravauxImmeuble.dispose();
			break;
		default:
			break;
		}
	}
}
