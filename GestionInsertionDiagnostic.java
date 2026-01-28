package controleur.insertion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import controleur.outils.Sauvegarde;
import modele.Bien;
import modele.Diagnostics;
import modele.dao.DaoDiagnostic;
import vue.Fenetre_Accueil;
import vue.insertion.Fenetre_InsertionDiagnostic;

public class GestionInsertionDiagnostic implements ActionListener {

	private Fenetre_InsertionDiagnostic fid;
	private DaoDiagnostic daoDiagnostic;

	// Constructeur prenant en paramètre la fenêtre d'insertion d'un diagnostic
	public GestionInsertionDiagnostic(Fenetre_InsertionDiagnostic fid) {
		this.fid = fid;
		
		// Initialisation de l'accès à la base de données pour l'entité Diagnostic
		this.daoDiagnostic = new DaoDiagnostic();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource();
		Fenetre_Accueil fenetre_Principale = (Fenetre_Accueil) this.fid.getTopLevelAncestor();

		switch (btn.getText()) {
		case "Ajouter":
			// Création de l'objet Diagnostic null
			Diagnostics diagnostic = null;
			// Récupérer le Logement placé dans la sauvegarde
			Bien bienSauvegarde = (Bien) Sauvegarde.getItem("Logement");
			try {
				// Créer un nouveau diagnostic avec les données de la fenêtre d'insertion
				diagnostic = new Diagnostics(this.fid.getTextField_Date_Validite().getText(),
						this.fid.getTextField_Type().getText(), bienSauvegarde);

				// Ajouter le nouveau diagnostic dans la base de données
				int idDiagnosticSequence = this.daoDiagnostic.createAvecSequence(diagnostic);
				
				// Attribue l'id de la séquence au diagnostic
				diagnostic.setIdDiagnostic(idDiagnosticSequence);

				// Fermer la fenêtre d'insertion après l'ajout
				this.fid.dispose();

			} catch (Exception e1) {
				e1.printStackTrace();
			}

			break;

		case "Annuler":
			// Fermeture de la fenêtre d'insertion d'un diagnostic
			this.fid.dispose();
			break;
		}
	}
}
