package controleur;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import controleur.outils.ImportCheminCSV;
import controleur.outils.LireCSV;
import controleur.outils.Sauvegarde;
import modele.Assurance;
import modele.Bien;
import modele.Echeance;
import modele.Entreprise;
import modele.Facture;
import modele.Immeuble;
import modele.Imposer;
import modele.Impôt;
import modele.Locataire;
import modele.Louer;
import modele.MoyenneLoyer;
import modele.MoyenneMediane;
import modele.ProvisionAnnee;
import modele.dao.DaoAssurance;
import modele.dao.DaoBien;
import modele.dao.DaoEcheance;
import modele.dao.DaoEntreprise;
import modele.dao.DaoFacture;
import modele.dao.DaoImmeuble;
import modele.dao.DaoImposer;
import modele.dao.DaoImpôt;
import modele.dao.DaoLocataire;
import modele.dao.DaoLouer;
import rapport.CreerAnnexe;
import vue.Fenetre_Accueil;
import vue.archiver.Fenetre_ArchiverFacture;
import vue.archiver.Fenetre_ArchiverLocation;
import vue.insertion.Fenetre_AffichageCompteursBien;
import vue.insertion.Fenetre_AffichageCompteursLogement;
import vue.insertion.Fenetre_AffichageInfoLocataire;
import vue.insertion.Fenetre_InsertionAssurance;
import vue.insertion.Fenetre_InsertionBien;
import vue.insertion.Fenetre_InsertionDiagnostic;
import vue.insertion.Fenetre_InsertionImpot;
import vue.insertion.Fenetre_InsertionLocation;
import vue.insertion.Fenetre_InsertionLogement;
import vue.insertion.Fenetre_InsertionPaiementBien;
import vue.insertion.Fenetre_InsertionPaiementLogement;
import vue.modification.Fenetre_ModificationAssurance;
import vue.modification.Fenetre_ModificationBien;
import vue.modification.Fenetre_ModificationFactureChargeLogement;
import vue.modification.Fenetre_ModificationLocation;
import vue.modification.Fenetre_ModificationLogement;
import vue.modification.Fenetre_ModificationTravauxImmeuble;
import vue.suppression.Fenetre_SupprimerAssurance;
import vue.suppression.Fenetre_SupprimerBien;
import vue.suppression.Fenetre_SupprimerFactureCharge;
import vue.suppression.Fenetre_SupprimerLocation;
import vue.suppression.Fenetre_SupprimerLogement;
import vue.suppression.Fenetre_SupprimerTravaux;

public class GestionAccueil implements ActionListener {

	private Fenetre_Accueil fenetreAccueil;
	private DaoImmeuble daoImmeuble;
	private DaoLouer daoLouer;
	private DaoBien daoBien;
	private DaoAssurance daoAssurance;
	private DaoEcheance daoEcheance;
	private DaoEntreprise daoEntreprise;
	private DaoFacture daoFacture;
	private DaoLocataire daoLocataire;
	private DaoImposer daoImposer;
	private DaoImpôt daoImpot;

	public GestionAccueil(Fenetre_Accueil fenetreAccueil) {
		this.fenetreAccueil = fenetreAccueil;
		this.daoImmeuble = new DaoImmeuble();
		this.daoBien = new DaoBien();
		this.daoLouer = new DaoLouer();
		this.daoAssurance = new DaoAssurance();
		this.daoEcheance = new DaoEcheance();
		this.daoEntreprise = new DaoEntreprise();
		this.daoFacture = new DaoFacture();
		this.daoLocataire = new DaoLocataire();
		this.daoImposer = new DaoImposer();
		this.daoImpot = new DaoImpôt();
	}

	/**
	 * Permet de rendre visible/mettre au premier plan
	 * les JLayeredPane de l'application
	 * 
	 * @param visible (JLayeredPane) : la page à rendre visible
	 */
	public void rendreVisible(JLayeredPane visible) {
		this.fenetreAccueil.getLayeredPane_Accueil().setVisible(false);
		this.fenetreAccueil.getLayeredPane_MesBiens().setVisible(false);
		this.fenetreAccueil.getLayeredPane_MesTravaux().setVisible(false);
		this.fenetreAccueil.getLayeredPane_MesChargesLocatives().setVisible(false);
		this.fenetreAccueil.getLayeredPane_MesLocations().setVisible(false);
		this.fenetreAccueil.getLayeredPane_MesAssurances().setVisible(false);
		this.fenetreAccueil.getLayeredPane_MesDocuments().setVisible(false);
		this.fenetreAccueil.getLayeredPane_MesArchives().setVisible(false);
		visible.setVisible(true);
		this.fenetreAccueil.getContentPane().add(visible, BorderLayout.CENTER);
	}
	
	
	/**
	 * Permet de vider un JTable tableau
	 * 
	 * @param table (JTable) :  La table à vider
	 */
	public static void viderTable(JTable table) {
		DefaultTableModel modeleTable = (DefaultTableModel) table.getModel();
		int rowCount = modeleTable.getRowCount();
		int columnCount = modeleTable.getColumnCount();

		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < columnCount; col++) {
				modeleTable.setValueAt(null, row, col);
			}
		}
	}	

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// LAYERED ACCUEIL
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Permet de vider le contenu d'un panel
	 * 
	 * @param panel (Container) : panel à vider
	 */
	private void viderPanel(Container panel) {
		panel.removeAll();
		panel.repaint();
		panel.revalidate();
	}
	
	/**
	 * Permet de vider les panels de la page d'accueil
	 * pour recalculer les statistiques qui seront 
	 * mises à jour à la prochaine visite sur le panel
	 */
	public void viderAccueil() {
		this.viderPanel(this.fenetreAccueil.getPanel_Accueil_graphiqueBasDroite());
		this.viderPanel(this.fenetreAccueil.getPanel_Accueil_graphiqueHautDroite());
		this.viderPanel(this.fenetreAccueil.getPanel_Accueil_mediane());
		this.viderPanel(this.fenetreAccueil.getPanel_Accueil_moyenne());
	}

	/**
	 * Crée et retourne un jeu de données (dataset) par défaut pour faire des statistiques
	 *
	 * @return dataset (DefaultCategoryDataset) : les données pour les statistiques
	 */
	private DefaultCategoryDataset créerJeuDeDonnées() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		try {
			List<ProvisionAnnee> provisions = this.daoLouer.findProvisions();
			for (ProvisionAnnee provision : provisions) {
				dataset.addValue(provision.getSommeProvision(), "Provisions", provision.getAnnee());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataset;
	}
	
	/**
	 * Crée et retourne un graphique à barres représentant l'évolution des provisions pour charges par année
	 *
	 * @param dataset (DefaultCategoryDataset) : données à afficher sur le graphique
	 * @return (JFreeChart) le graphique à barres
	 */
	private JFreeChart créerGraphiqueBarresProvisions(DefaultCategoryDataset dataset) {
		return ChartFactory.createBarChart(
				"Évolution des provisions pour charges par année", // Titre du graphique
				"Année", // Axe des abscisses (X)
				"Provision pour charges", // Axe des ordonnées (Y)
				dataset, // Jeu de données
				PlotOrientation.VERTICAL, // Orientation du graphique
				true, // Inclure la légende : oui
				true, // Générer des tooltips (infos-bulles)
				false // Générer des URLs : non
		);
	}

	/**
	 * Crée et retourne un jeu de données (dataset) pour le graphique de la moyenne des loyers par locataire
	 *
	 * @return dataset (DefaultCategoryDataset): données pour le graphique de moyenne des loyers
	 */
	private DefaultCategoryDataset créerJeuDeDonnéesMoyenneLoyer() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		try {
			List<MoyenneLoyer> moyennes = this.daoLouer.findMoyenneLoyer();
			for (MoyenneLoyer moyenne : moyennes) {
				dataset.addValue(moyenne.getLoyer(), "Moyenne Loyer", moyenne.getLocataire());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataset;
	}
	
	/**
	 * Crée et retourne un graphique à barres représentant la moyenne des loyers par locataire
	 *
	 * @param dataset Un objet DefaultCategoryDataset contenant les données à afficher sur le graphique.
	 * @return Un objet JFreeChart représentant le graphique à barres de la moyenne des loyers par locataire.
	 */
	private JFreeChart créerGraphiqueBarresMoyenneLoyer(DefaultCategoryDataset dataset) {
		return ChartFactory.createBarChart(
				"Moyenne des loyers par locataire", // Titre du graphique
				"Locataire", // Axe des abscisses (X)
				"Moyenne", // Axe des ordonnées (Y)
				dataset, // Jeu de données 
				PlotOrientation.VERTICAL, // Orientation
				true, // Inclure la légende
				true, // Générer des tooltips
				false // Générer des URLs
		);
	}

	/**
	 * Calcule et retourne la moyenne et la médiane des loyers.
	 * 
	 * @return (MoyenneMediane) : les valeurs de la moyenne et de la médiane des loyers
	 * @throws SQLException
	 */
	private MoyenneMediane loyerMoyenneMediane() throws SQLException {
		return this.daoLouer.findMoyenneMediane();
	}	
	
	/**
	 * Charge la page d'accueil en générant et affichant 
	 * les graphiques des provisions et des moyennes de loyer
	 *
	 * @throws SQLException
	 */
	public void chargerAccueil() throws SQLException {
		// Créer les jeux de données
	    DefaultCategoryDataset datasetProvisions = this.créerJeuDeDonnées();
	    DefaultCategoryDataset datasetMoyenneLoyer = this.créerJeuDeDonnéesMoyenneLoyer();

	    // Créer les graphiques
	    JFreeChart chartProvisions = this.créerGraphiqueBarresProvisions(datasetProvisions);
	    JFreeChart chartMoyenneLoyer = this.créerGraphiqueBarresMoyenneLoyer(datasetMoyenneLoyer);

	    // Créer les ChartPanels avec les tailles appropriées pour les graphiques de provisions
	    ChartPanel chartPanelProvisions = new ChartPanel(chartProvisions);
	    this.fenetreAccueil.getPanel_Accueil_graphiqueBasDroite().setPreferredSize(
	            new Dimension(this.fenetreAccueil.getPanel_Accueil_graphiqueBasDroite().getWidth(),
	                          this.fenetreAccueil.getPanel_Accueil_graphiqueBasDroite().getHeight()));
	    this.fenetreAccueil.getPanel_Accueil_graphiqueBasDroite().setLayout(new BorderLayout());
	    this.fenetreAccueil.getPanel_Accueil_graphiqueBasDroite().add(chartPanelProvisions, BorderLayout.CENTER);

	    // Créer les ChartPanels avec les tailles appropriées pour les graphiques de moyenne de loyer
	    ChartPanel chartPanelMoyenneLoyer = new ChartPanel(chartMoyenneLoyer);
	    this.fenetreAccueil.getPanel_Accueil_graphiqueHautDroite().setPreferredSize(
	            new Dimension(this.fenetreAccueil.getPanel_Accueil_graphiqueHautDroite().getWidth(),
	                          this.fenetreAccueil.getPanel_Accueil_graphiqueHautDroite().getHeight()));
	    this.fenetreAccueil.getPanel_Accueil_graphiqueHautDroite().setLayout(new BorderLayout());
	    this.fenetreAccueil.getPanel_Accueil_graphiqueHautDroite().add(chartPanelMoyenneLoyer, BorderLayout.CENTER);

	    // Ajouter des labels pour afficher la moyenne et la médiane des loyers
	    JLabel labelMoyenne = new JLabel("Moyenne des loyers : " + this.loyerMoyenneMediane().getMoyenne() + " €");
	    Font nouvellePoliceMo = new Font(labelMoyenne.getFont().getName(), Font.PLAIN, 20);
	    labelMoyenne.setFont(nouvellePoliceMo);
	    
	    this.fenetreAccueil.getPanel_Accueil_mediane().add(labelMoyenne, BorderLayout.NORTH);
	    JLabel labelMediane = new JLabel("Médiane des loyers : " + this.loyerMoyenneMediane().getMediane() + " €");
	    Font nouvellePolice = new Font(labelMediane.getFont().getName(), Font.PLAIN, 20);
	    labelMediane.setFont(nouvellePolice);
	    this.fenetreAccueil.getPanel_Accueil_moyenne().add(labelMediane, BorderLayout.SOUTH);

	    // Rafraîchir la fenêtre d'accueil
	    this.fenetreAccueil.revalidate();
	    this.fenetreAccueil.repaint();
	}

	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// LAYERED MES BIENS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Met à jour une ligne spécifique de la table biens avec les informations de l'immeuble donné
	 *
	 * @param numeroLigne (int) : le numéro de la ligne dans la table à mettre à jour
	 * @param immeuble (Immeuble) : Immeuble contenant les informations à afficher dans la ligne
	 * @throws SQLException
	 */
	public void ecrireLigneTableBiens(int numeroLigne, Immeuble immeuble) throws SQLException {
		JTable tableImmeuble = this.fenetreAccueil.getTableBiens();
		DefaultTableModel modeleTable = (DefaultTableModel) tableImmeuble.getModel();

		modeleTable.setValueAt(immeuble.getImmeuble(), numeroLigne, 0);
		modeleTable.setValueAt(immeuble.getAdresse() + "\n" + immeuble.getCp() + " " + immeuble.getVille(), numeroLigne,
				1);

		int nb = this.daoImmeuble.getNombreLogementsDansImmeuble(immeuble.getImmeuble());
		modeleTable.setValueAt(nb, numeroLigne, 2);// rajouter une méthode count pour le nb logement
		modeleTable.setValueAt(immeuble.getType_immeuble(), numeroLigne, 3);
	}

	
	public void chargerBiens() throws SQLException {
		List<Immeuble> immeubles = this.daoImmeuble.findAll();
		
		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableBiens().getModel();
		modeleTable.setRowCount(immeubles.size());
		for (int i = 0; i < immeubles.size(); i++) {
			Immeuble immeuble = immeubles.get(i);
			this.ecrireLigneTableBiens(i, immeuble);
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// LAYERED MES LOCATIONS
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Met à jour une ligne spécifique de la table des locations avec les informations de la location et du bien associé
	 *
	 * @param numeroLigne (int) : le numéro de la ligne dans la table à mettre à jour.
	 * @param location (Louer) : L'objet Louer contenant les informations de la location.
	 * @param bien (Bien) : Bien associé à la location
	 */
	public void ecrireLigneTableLocations(int numeroLigne, Louer location, Bien bien) {
		JTable tableLocations = this.fenetreAccueil.getTableLocations();
		
		DefaultTableModel modeleTable = (DefaultTableModel) tableLocations.getModel();
		modeleTable.setValueAt(location.getLocataire(), numeroLigne, 0);
		modeleTable.setValueAt(location.getBien(), numeroLigne, 1);
		modeleTable.setValueAt(bien.getType_bien(), numeroLigne, 2);
		modeleTable.setValueAt(location.getDateDebut(), numeroLigne, 3);
		modeleTable.setValueAt(location.getDateDerniereRegularisation(), numeroLigne, 4);
	}

	/**
	 * Charge et affiche les informations sur les locations dans la table dédiée sur la fenêtre d'accueil
	 * 
	 * @throws SQLException
	 */
	private void chargerLocations() throws SQLException {
		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableLocations().getModel();
		modeleTable.setRowCount(0);
		List<Bien> biens = this.daoBien.findAll();
		List<Louer> locations = new ArrayList<>();
		for (Bien b : biens) {
			locations.addAll(this.daoLouer.findLocationByBien(b.getIdBien()));
		}
		for (int i = 0; i < locations.size(); i++) {
			Louer location = locations.get(i);
			Bien bien = location.getBien();
			int row = modeleTable.getRowCount(); // Obtenir le nb de lignes
			modeleTable.addRow(new Object[0]); // Ajouter une nouvelle ligne
			this.ecrireLigneTableLocations(row, location, bien);
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// LAYERED MES TRAVAUX
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Table travaux pour des immeubles
	/**
	 * Charge et affiche les informations sur les travaux effectués dans les immeubles 
	 * dans la table dédiée sur la fenêtre d'accueil
	 * 
	 * @throws SQLException
	 */
	private void chargerTravauxImmeubles() throws SQLException {
		List<Facture> factures = this.daoFacture.findFactureTravaux();
		
		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableTravaux().getModel();
		modeleTable.setRowCount(0); // Efface toutes les lignes existantes
		for (int i = 0; i < factures.size(); i++) {
			Facture f = factures.get(i);
			if (f != null && f.getImmeuble() != null) {
				Entreprise entreprise = this.daoEntreprise.findById(f.getEntreprise().getSiret());
				modeleTable.addRow(new Object[] { f.getNumero(), f.getImmeuble().getImmeuble(), f.getDateEmission(),
						f.getMontant(), f.getDatePaiement(), entreprise.getNom(),
						entreprise.getAdresse() + " " + entreprise.getCp() + " " + entreprise.getVille() });
			}
		}
	}

	// Table travaux pour des logements
	/**
	 * Charge et affiche les informations sur les travaux effectués dans les logements 
	 * dans la table dédiée sur la fenêtre d'accueil
	 *
	 * @throws SQLException
	 */
	private void chargerTravauxLogements() throws SQLException {
		List<Facture> factures = this.daoFacture.findFactureTravaux();
		
		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableTravaux().getModel();
		modeleTable.setRowCount(0); // Efface toutes les lignes existantes
		for (int i = 0; i < factures.size(); i++) {
			Facture f = factures.get(i);
			if (f != null && f.getBien() != null) {
				Entreprise entreprise = this.daoEntreprise.findById(f.getEntreprise().getSiret());
				modeleTable.addRow(new Object[] { f.getNumero(), f.getBien().getIdBien(), f.getDateEmission(),
						f.getMontant(), f.getDatePaiement(), entreprise.getNom(),
						entreprise.getAdresse() + " " + entreprise.getCp() + " " + entreprise.getVille() });
			}
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// LAYERED MES CHARGES LOCATIVES
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Écrit les informations d'une charge locative dans une ligne spécifique
	 * de la table des charges locatives sur la fenêtre d'accueil
	 *
	 * @param numeroLigne Le numéro de la ligne dans la table à mettre à jour.
	 * @param charge L'objet Facture représentant les informations de la charge locative.
	 */
	public void ecrireLigneTableChargesLocatives(int numeroLigne, Facture charge) {
		JTable tableChargesLocatives = this.fenetreAccueil.getTableChargesLocatives();
		
		DefaultTableModel modeleTable = (DefaultTableModel) tableChargesLocatives.getModel();
		modeleTable.setValueAt(charge.getBien().getIdBien(), numeroLigne, 0);
		modeleTable.setValueAt(charge.getNumero(), numeroLigne, 1);
		modeleTable.setValueAt(charge.getDesignation(), numeroLigne, 2);
		modeleTable.setValueAt(charge.getDateEmission(), numeroLigne, 3);
		modeleTable.setValueAt(charge.getDatePaiement(), numeroLigne, 4);
		if (charge.getImputableLocataire() == 1) {
			modeleTable.setValueAt("Oui", numeroLigne, 5);
		} else {
			modeleTable.setValueAt("Non", numeroLigne, 5);
		}
		modeleTable.setValueAt(charge.getMontant(), numeroLigne, 6);
		modeleTable.setValueAt(charge.getMontantReelPaye(), numeroLigne, 7);
		modeleTable.setValueAt(charge.getMontant() - charge.getMontantReelPaye(), numeroLigne, 8);
	}

	/**
	 * Charge et affiche les informations sur les charges locatives des logements 
	 * dans la table dédiée sur la fenêtre d'accueil
	 *
	 * @throws SQLException
	 */
	private void chargerChargesLogement() throws SQLException {
		List<Facture> factures = this.daoFacture.findFactureCharge();
		
		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableChargesLocatives().getModel();
		modeleTable.setRowCount(0);
		for (int i = 0; i < factures.size(); i++) {
			Facture f = factures.get(i);
			String imputable = (f.getImputableLocataire() == 1) ? "Oui" : "Non";
			if (f != null && f.getBien() != null) {
				// Ajouter une nouvelle ligne à la table avec les informations de la charge
				modeleTable.addRow(new Object[] { f.getBien().getIdBien(), f.getNumero(), f.getDesignation(),
						f.getDateEmission(), f.getDatePaiement(), imputable, f.getMontant(), f.getMontantReelPaye(),
						f.getMontant() - f.getMontantReelPaye(), });
			}
		}
	}

	/**
	 * Met à jour la table des charges locatives pour un logement spécifique avec les informations des factures associées.
	 *
	 * @param idLogement L'identifiant du logement pour lequel mettre à jour la table des charges locatives.
	 * @throws SQLException En cas d'erreur lors de l'accès aux données.
	 */
	private void updateTableChargesForLogement(String idLogement) throws SQLException {
		List<Facture> factures = this.daoFacture.findFactureChargeByLogement(idLogement);

		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableChargesLocatives().getModel();
		modeleTable.setRowCount(factures.size());

		// Mettre à jour la table avec les informations des charges pour le logement
		// spécifié
		for (int i = 0; i < factures.size(); i++) {
			Facture f = factures.get(i);
			this.ecrireLigneTableChargesLocatives(i, f);
		}
	}
	
	/**
	 * Filtrer la table des charges locatives en fonction de l'ID 
	 * du logement sélectionné dans la ComboBox
	 */
	private void filtreChargesByLogement() {
		JComboBox<String> comboBox_MesCharges = this.fenetreAccueil.getComboBox_MesChargesLocatives();
		
		String idLogementSelectionne = comboBox_MesCharges.getSelectedItem().toString();
		// Si l'ID sélectionné est différent de "ID du logement", filtrer la table
		if (!idLogementSelectionne.equals("ID du logement")) {
			try {
				this.updateTableChargesForLogement(idLogementSelectionne);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// LAYERED MES ASSURANCES
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Table mes assurances
	/**
	 * Écrit les informations d'une assurance dans une ligne spécifique de la table des assurances 
	 * sur la fenêtre d'accueil.
	 *
	 * @param numeroLigne (int): Le numéro de la ligne dans la table à mettre à jour
	 * @param assurance (Assurance) : informations de l'assurance
	 * @param entreprise (Entreprise) : informations de l'entreprise associée à l'assurance
	 * @param echeance (Echeance) : informations de l'échéance associée à l'assurance
	 */
	public void ecrireLigneTableAssurances(int numeroLigne, Assurance assurance, Entreprise entreprise,
			Echeance echeance) {
		JTable tableAssurances = this.fenetreAccueil.getTableAssurances();
		DefaultTableModel modeleTable = (DefaultTableModel) tableAssurances.getModel();
		
		modeleTable.setValueAt(assurance.getBien().getIdBien(), numeroLigne, 0);
		modeleTable.setValueAt(assurance.getNuméroPolice(), numeroLigne, 1);
		modeleTable.setValueAt(assurance.getMontant(), numeroLigne, 2);
		modeleTable.setValueAt(echeance.getDateEcheance(), numeroLigne, 3);
		if (entreprise != null) {
			modeleTable.setValueAt(entreprise.getNom(), numeroLigne, 4);
			modeleTable.setValueAt(entreprise.getAdresse() + " " + entreprise.getCp() + " " + entreprise.getVille(),
					numeroLigne, 5);
			modeleTable.setValueAt(entreprise.getTelephone(), numeroLigne, 6);
		} else {
			// Si l'entreprise est null
			modeleTable.setValueAt("N/A", numeroLigne, 3);
			modeleTable.setValueAt("N/A", numeroLigne, 4);
			modeleTable.setValueAt("N/A", numeroLigne, 5);
		}
	}

	
	/**
	 * Charge et affiche les informations sur les assurances
	 * dans la table dédiée sur la fenêtre d'accueil
	 * 
	 * @throws SQLException
	 */
	private void chargerAssurances() throws SQLException {
		List<Assurance> assurances = this.daoAssurance.findAll();

		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableAssurances().getModel();
		modeleTable.setRowCount(assurances.size());

		for (int i = 0; i < assurances.size(); i++) {
			Assurance a = assurances.get(i);
			Entreprise entreprise = this.daoEntreprise.findById(a.getEntreprise().getSiret());
			Echeance echeance = this.daoEcheance.findByAssuranceNumPolice(a.getNuméroPolice());
			echeance.setDateEcheance(echeance.getDateEcheance().substring(0, 10)); 
			this.ecrireLigneTableAssurances(i, a, entreprise, echeance);
		}
	}
	
	/**
	 * Met à jour la table des assurances pour un logement spécifique 
	 * avec les informations des assurances associées.
	 *
	 * @param idLogement (String) : L'identifiant du logement pour lequel mettre à jour la table des assurances
	 * @throws SQLException
	 */
	private void updateTableAssurancesForLogement(String idLogement) throws SQLException {
		List<Assurance> assurancesLogement = this.daoAssurance.findByLogement(idLogement);

		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableAssurances().getModel();
		modeleTable.setRowCount(assurancesLogement.size());

		for (int i = 0; i < assurancesLogement.size(); i++) {
			Assurance assurance = assurancesLogement.get(i);
			Entreprise entreprise = this.daoEntreprise.findById(assurance.getEntreprise().getSiret());
			Echeance echeance = this.daoEcheance.findByAssuranceNumPolice(assurance.getNuméroPolice());
			echeance.setDateEcheance(echeance.getDateEcheance().substring(0, 10));
			this.ecrireLigneTableAssurances(i, assurance, entreprise, echeance);
		}
		Bien bien = this.daoBien.findById(idLogement);
		Sauvegarde.deleteItem("Logement");
		Sauvegarde.addItem("Logement", bien);
	}

	/**
	 * Filtrer la table des assurances en fonction de l'ID du logement sélectionné dans la ComboBox
	 */
	private void filtreAssuranceByLogement() {
		JComboBox<String> comboBox_MesAssurances = this.fenetreAccueil.getComboBox_MesAssurances();
		
		String idLogementSelectionne = comboBox_MesAssurances.getSelectedItem().toString();
		// Si l'ID selectionne est différent de "ID du logement", filtrez la table des assurances
		if (!idLogementSelectionne.equals("ID du logement")) {
			try {
				this.updateTableAssurancesForLogement(idLogementSelectionne);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
// LAYERED MES DOCUMENTS
//////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Écrit les informations d'un impôt dans une ligne spécifique de la table 
	 * des documents sur la fenêtre d'accueil
	 *
	 * @param numeroLigne (int) : Le numéro de la ligne dans la table à mettre à jour
	 * @param impot (Impôt) : les informations de l'impôt
	 * @throws SQLException
	 */
	public void ecrireLigneTableDocuments(int numeroLigne, Impôt impot) throws SQLException {
		JTable tableDocuments = this.fenetreAccueil.getTableDocuments();
		DefaultTableModel modeleTable = (DefaultTableModel) tableDocuments.getModel();

		modeleTable.setValueAt(impot.getNom(), numeroLigne, 0);
		modeleTable.setValueAt(impot.getMontant(), numeroLigne, 1);
		modeleTable.setValueAt(impot.getAnnee(), numeroLigne, 2);
	}

	
	/**
	 * Charge la table des documents avec les informations des impôts depuis la base de données
	 * 
	 * @throws SQLException
	 */
	public void chargerImpot() throws SQLException {
		List<Impôt> impots = this.daoImpot.findAll();
		
		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableDocuments().getModel();
		modeleTable.setRowCount(impots.size());
		for (int i = 0; i < impots.size(); i++) {
			Impôt impot = impots.get(i);
			modeleTable.addRow(new Object[0]); // Ajouter une nouvelle ligne
			this.ecrireLigneTableDocuments(i, impot);
		}
	}
	

	/**
	 * Filtrer la table des documents (Impôts) en fonction de l'ID 
	 * du logement sélectionné dans la ComboBox.
	 */
	private void filtreImpotByLogement() {
		JComboBox<String> comboBox_Logement = this.fenetreAccueil.getComboBox_MesDocuments();
		String idLogementSelectionne = comboBox_Logement.getSelectedItem().toString();
		// Si l'ID selectionne est différent de "ID du logement", filtrez la table des assurances
		if (!idLogementSelectionne.equals("ID du logement")) {
			try {
				this.updateTableDocumentsForLogement(idLogementSelectionne);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	
	/**
	 * Met à jour la table des documents (Impôts) pour un logement spécifique 
	 * avec les informations des impôts associés
	 *
	 * @param idLogement (String) : L'identifiant du logement pour lequel mettre à jour la table des documents (Impôts)
	 * @throws SQLException
	 */
	private void updateTableDocumentsForLogement(String idLogement) throws SQLException {
		List<Imposer> impotLogement = this.daoImposer.findImposerByBien(idLogement);

		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTableDocuments().getModel();
		modeleTable.setRowCount(impotLogement.size());

		for (int i = 0; i < impotLogement.size(); i++) {
			Imposer imposer = impotLogement.get(i);
			Impôt impot = this.daoImpot.findById(String.valueOf(imposer.getImpot().getIdImpot()));
			this.ecrireLigneTableDocuments(i, impot);
		}
		Bien bien = this.daoBien.findById(idLogement);
		Sauvegarde.deleteItem("Logement");
		Sauvegarde.addItem("Logement", bien);
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////
// LAYERED MES DOCUMENTS
//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Écrit les informations d'une location archivée dans une ligne spécifique 
	 * de la table des archives de locations sur la fenêtre d'accueil
	 *
	 * @param numeroLigne (int) : Le numéro de la ligne dans la table à mettre à jour
	 * @param louer (Louer) : les informations de la location archivée
	 * @throws SQLException
	 */
	public void ecrireLigneTableArchiveLouer(int numeroLigne, Louer louer) throws SQLException {
		JTable tableLouer = this.fenetreAccueil.getTable_MesArchives_Louer();
		DefaultTableModel modeleTable = (DefaultTableModel) tableLouer.getModel();

		System.out.println(louer.getLocataire().getIdLocataire());
		System.out.println(louer.getBien().getIdBien());
		modeleTable.setValueAt(louer.getLocataire().getIdLocataire(), numeroLigne, 0);
		modeleTable.setValueAt(louer.getBien().getIdBien(), numeroLigne, 1);
		modeleTable.setValueAt(louer.getDateDebut(), numeroLigne, 2);
		modeleTable.setValueAt(louer.getLoyerTTC(), numeroLigne, 3);
		modeleTable.setValueAt(louer.getProvision_chargeMens_TTC(), numeroLigne, 4);
	}

	/**
	 * Charge la table des archives de locations sur la fenêtre d'accueil 
	 * avec les informations des locations archivées depuis la base de données
	 *
	 * @throws SQLException
	 */
	public void chargerTableArchiveLouer() throws SQLException {
		List<Louer> louers = this.daoLouer.findAllArchive();
		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTable_MesArchives_Louer().getModel();
		modeleTable.setRowCount(louers.size());

		for (int i = 0; i < louers.size(); i++) {
			Louer louer = louers.get(i);
			modeleTable.addRow(new Object[0]); // Ajouter une nouvelle ligne
			this.ecrireLigneTableArchiveLouer(i, louer);
		}
	}

	/**
	 * Écrit les informations d'une facture archivée dans une ligne spécifique 
	 * de la table des archives de factures sur la fenêtre d'accueil
	 *
	 * @param numeroLigne (int) : Le numéro de la ligne dans la table à mettre à jour.
	 * @param facture (Facture) : informations de la facture archivée
	 * @throws SQLException
	 */
	public void ecrireLigneTableArchiveFacture(int numeroLigne, Facture facture) throws SQLException {
		JTable tableFacture = this.fenetreAccueil.getTable_MesArchives_Facture();
		DefaultTableModel modeleTable = (DefaultTableModel) tableFacture.getModel();

		modeleTable.setValueAt(facture.getNumero(), numeroLigne, 0);
		modeleTable.setValueAt(facture.getDateEmission(), numeroLigne, 1);
		modeleTable.setValueAt(facture.getMontantReelPaye(), numeroLigne, 2);
		modeleTable.setValueAt(facture.getMontant(), numeroLigne, 3);
		modeleTable.setValueAt(facture.getModePaiement(), numeroLigne, 4);
	}


	/**
	 * Charge la table des archives de factures sur la fenêtre d'accueil 
	 * avec les informations des factures archivées depuis la base de données
	 * 
	 * @throws SQLException
	 */
	public void chargerTableArchiveFacture() throws SQLException {
		List<Facture> factures = this.daoFacture.findAllArchive();
		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTable_MesArchives_Facture().getModel();

		modeleTable.setRowCount(factures.size());
		for (int i = 0; i < factures.size(); i++) {
			Facture facture = factures.get(i);
			modeleTable.addRow(new Object[0]);
			this.ecrireLigneTableArchiveFacture(i, facture);
		}
	}
	
	/**
	 * Écrit les informations d'un locataire archivé dans une ligne spécifique 
	 * de la table des archives de locataires sur la fenêtre d'accueil.
	 *
	 * @param numeroLigne (int) : Le numéro de la ligne dans la table à mettre à jour.
	 * @param loc (Locataire) : informations du locataire archivé
	 * @throws SQLException
	 */
	public void ecrireLigneTableArchiveLocataire(int numeroLigne, Locataire loc) throws SQLException {
		JTable tableLocataire = this.fenetreAccueil.getTable_MesArchives_Locataire();
		DefaultTableModel modeleTable = (DefaultTableModel) tableLocataire.getModel();

		if (loc != null) {
			modeleTable.setValueAt(loc.getIdLocataire(), numeroLigne, 0);
			modeleTable.setValueAt(loc.getNom(), numeroLigne, 1);
			modeleTable.setValueAt(loc.getPrenom(), numeroLigne, 2);
			modeleTable.setValueAt(loc.getTelephone(), numeroLigne, 3);
			modeleTable.setValueAt(loc.getMail(), numeroLigne, 4);
		} else {
			System.err.println("Error: loc is null at line " + numeroLigne);
		}
	}

	/**
	 * Charge la table des archives de locataires sur la fenêtre d'accueil 
	 * avec les informations des locataires archivés depuis la base de données
	 *
	 * @throws SQLException
	 */
	public void chargerTableArchiveLoccataire() throws SQLException {
		List<Locataire> locataires = this.daoLocataire.findAllArchive();
		DefaultTableModel modeleTable = (DefaultTableModel) this.fenetreAccueil.getTable_MesArchives_Locataire().getModel();

		modeleTable.setRowCount(locataires.size());
		for (int i = 0; i < locataires.size(); i++) {
			Locataire locataire = locataires.get(i);
			modeleTable.addRow(new Object[0]);
			this.ecrireLigneTableArchiveLocataire(i, locataire);
		}
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// NAVIGATION
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JButton) { // Cas de clics sur des JButton
			JButton btn = (JButton) source;
			switch (btn.getName()) { // a partir du nom des boutons

			// NAVIGATION ENTRE LES LAYEREDPANE
			case "btnAccueil":
				this.rendreVisible(this.fenetreAccueil.getLayeredPane_Accueil());
				try {
					this.viderAccueil();
					this.chargerAccueil(); // Mettre à jour les statistiques
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				break;
				
			case "btnMesBiens":
				this.rendreVisible(this.fenetreAccueil.getLayeredPane_MesBiens());
				break;
				
			case "btnMesLocations":
				this.rendreVisible(this.fenetreAccueil.getLayeredPane_MesLocations());
				break;
				
			case "btnMesTravaux":
				this.rendreVisible(this.fenetreAccueil.getLayeredPane_MesTravaux());
				break;
				
			case "btnMesChargesLocatives":
				this.rendreVisible(this.fenetreAccueil.getLayeredPane_MesChargesLocatives());
				break;
				
			case "btnMesAssurances":
				this.rendreVisible(this.fenetreAccueil.getLayeredPane_MesAssurances());
				break;
				
			case "btnMesDocuments":
				this.rendreVisible(this.fenetreAccueil.getLayeredPane_MesDocuments());
				break;
				
			case "btnMesArchives":
				this.rendreVisible(this.fenetreAccueil.getLayeredPane_MesArchives());
				break;
			////////////////////////////////////////////////////////////////////////////////////
			// LAYERED ACCUEIL
			////////////////////////////////////////////////////////////////////////////////////
			case "importCSV":
				ImportCheminCSV chemin = new ImportCheminCSV();
				LireCSV lire = new LireCSV();
				chemin.choisirChemin();
				try {
					lire.lireCSV(chemin.getSelectedFilePath());
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				break;
			/////////////////////////////////////////////////////////////////////////////////////
			// LAYERED MES BIENS
			/////////////////////////////////////////////////////////////////////////////////////
			case "btnMesBiens_Charger":
				try {
					this.chargerBiens();
				} catch (SQLException e1) {
					e1.printStackTrace();
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(null,
							"Erreur lors du chargement des logements. Veuillez réessayer plus tard.",
							"Erreur de chargement", JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btnMesBiens_Supprimer":
				// Cas de la suppression d'un immeuble
				if (Sauvegarde.onSave("Immeuble") == true && !Sauvegarde.onSave("Logement")) {
					Fenetre_SupprimerBien supp_bien = new Fenetre_SupprimerBien();
					this.fenetreAccueil.getLayeredPane().add(supp_bien); // Afficher la page de suppression d'un immeuble
					supp_bien.setVisible(true);
					supp_bien.moveToFront();
				// Cas de la suppression d'un logement
				} else if (Sauvegarde.onSave("Logement") == true) {
					Fenetre_SupprimerLogement supp_logement = new Fenetre_SupprimerLogement();
					this.fenetreAccueil.getLayeredPane().add(supp_logement); // Afficher la page de suppression d'un logement
					supp_logement.setVisible(true);
					supp_logement.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil,
							"Veuillez sélectionner un bien/logement pour supprimer", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}

				break;

			case "btnMesBiens_Modifier":
				// Cas de la modification d'un logement
				if (Sauvegarde.onSave("Logement") == true) {
					Fenetre_ModificationLogement modif_logement = new Fenetre_ModificationLogement();
					this.fenetreAccueil.getLayeredPane().add(modif_logement);
					modif_logement.setVisible(true);
					modif_logement.moveToFront();
					// On recupère le logement de la sauvegarde
					Bien logementSauvegarde = (Bien) Sauvegarde.getItem("Logement");
					Bien logementCourant;
					try {
						logementCourant = this.daoBien.findById(logementSauvegarde.getIdBien());
						modif_logement.getTextField_IdLogement().setText(logementCourant.getIdBien());
						modif_logement.getTextField_SurfaceHabitable()
								.setText(Double.toString(logementCourant.getSurfaceHabitable()));
						modif_logement.getTextField_NbPièces().setText(Integer.toString(logementCourant.getNbPieces()));
						modif_logement.getTextField_DateAcquisition().setText(logementCourant.getDateAcquisition());
						modif_logement.getTextField_NumEtage().setText(Integer.toString(logementCourant.getNumEtage()));
						modif_logement.getComboBox_typeDeLogement().setSelectedItem(logementCourant.getType_bien());
					} catch (SQLException e1) {
						e1.printStackTrace();
						// Afficher un message d'erreur à l'utilisateur
						JOptionPane.showMessageDialog(null,
								"Erreur lors de la recherche du logement dans la base de données. Veuillez réessayer plus tard.",
								"Erreur de recherche", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					// Cas de la modification d'un immeuble
					// Premier test : s'il n'y a aucun immeuble sélectionné alors erreur
					if (Sauvegarde.onSave("Immeuble") == false) {
						// Afficher un message d'erreur à l'utilisateur
						JOptionPane.showMessageDialog(this.fenetreAccueil,
								"Veuillez sélectionner un bien/logement pour modifier !", "Erreur",
								JOptionPane.ERROR_MESSAGE);
					} else {
						// On ouvre la fenêtre de modification
						Fenetre_ModificationBien modif_bien = new Fenetre_ModificationBien();
						this.fenetreAccueil.getLayeredPane().add(modif_bien); 
						modif_bien.setVisible(true);
						modif_bien.moveToFront();
						// Permet de recuperer les infos sur l'immeuble courant pour les afficher
						// On récupère l'immeuble de la sauvegarde
						Immeuble immeubleSauvegarde = (Immeuble) Sauvegarde.getItem("Immeuble");
						Immeuble immeubleCourant;
						try {
							// A partir de l'ID de l'immeuble dans la sauvegarde on utilise la BD pour
							// récuperer l'immeuble le plus récent correspondant
							immeubleCourant = this.daoImmeuble.findById(immeubleSauvegarde.getImmeuble());
							// Afficher les informations stockées dans les champs correspondant
							modif_bien.getTextField_IdImmeuble().setText(immeubleCourant.getImmeuble());
							modif_bien.getTextField_adresse().setText(immeubleCourant.getAdresse());
							modif_bien.getTextField_codePostal().setText(immeubleCourant.getCp());
							modif_bien.getTextField_ville().setText(immeubleCourant.getVille());
							modif_bien.getTextField_periodeDeConstruction()
									.setText(immeubleCourant.getPeriodeConstruction());
							modif_bien.getComboBox_typeDeBien().setSelectedItem(immeubleCourant.getType_immeuble());
						} catch (SQLException e1) {
							e1.printStackTrace();
							// Afficher un message d'erreur à l'utilisateur
							JOptionPane.showMessageDialog(null,
									"Erreur lors de la recherche du bien dans la base de données. Veuillez réessayer plus tard.",
									"Erreur de recherche", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				break;

			case "btnMesBiens_AjouterBien":
				Fenetre_InsertionBien insertion_bien = new Fenetre_InsertionBien();
				this.fenetreAccueil.getLayeredPane().add(insertion_bien); // Afficher la page pour inserer un immeuble
				insertion_bien.setVisible(true);
				insertion_bien.moveToFront();
				break;
				
			case "btnMesBiens_AjouterPaiements":
				if (Sauvegarde.onSave("Immeuble") == true) {
					Fenetre_InsertionPaiementBien paiement_bien = new Fenetre_InsertionPaiementBien();
					this.fenetreAccueil.getLayeredPane().add(paiement_bien); // Afficher la page des paiements pour un immeuble
					paiement_bien.setVisible(true);
					paiement_bien.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un bien !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btnMesBiens_AfficherCompteurs_Bien":
				if (Sauvegarde.onSave("Immeuble") == true) {
					Fenetre_AffichageCompteursBien affichage_compteursBien = new Fenetre_AffichageCompteursBien();
					this.fenetreAccueil.getLayeredPane().add(affichage_compteursBien); // Afficher la page des compteurs pour un immeuble
					// Afficher les compteurs au chargement de la page
					try {
						affichage_compteursBien.getGestionAffichage().chargerCompteurs();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					affichage_compteursBien.setVisible(true);
					affichage_compteursBien.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un bien !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btnMesBiens_AjouterLogement":
				if (Sauvegarde.onSave("Immeuble") == true) {
					Fenetre_InsertionLogement insertion_logement = new Fenetre_InsertionLogement();
					this.fenetreAccueil.getLayeredPane().add(insertion_logement); // Afficher la page pour inserer un logement
					insertion_logement.setVisible(true);
					insertion_logement.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un bien !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btnMesBiens_AjouterDiagnostic_Logements":
				if (Sauvegarde.onSave("Logement") == true) {
					Fenetre_InsertionDiagnostic diagnostic_logement = new Fenetre_InsertionDiagnostic();
					this.fenetreAccueil.getLayeredPane().add(diagnostic_logement); // Afficher la page des diagnostics pour un logement
					diagnostic_logement.setVisible(true);
					diagnostic_logement.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un logement !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btnMesBiens_AjouterPaiements_Logements":
				if (Sauvegarde.onSave("Logement") == true) {
					Fenetre_InsertionPaiementLogement paiement_logement = new Fenetre_InsertionPaiementLogement(false);
					this.fenetreAccueil.getLayeredPane().add(paiement_logement); // Afficher la page des paiements pour un logement
					paiement_logement.setVisible(true);
					paiement_logement.moveToFront();
				} else {
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un logement !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btnMesBiens_AfficherCompteurs_Logement":
				if (Sauvegarde.onSave("Logement")) {
					Fenetre_AffichageCompteursLogement affichage_compteursLogement = new Fenetre_AffichageCompteursLogement();
					this.fenetreAccueil.getLayeredPane().add(affichage_compteursLogement); // Afficher la page des compteurs pour un logement
					// Afficher les compteurs au chargement de la page
					try {
						affichage_compteursLogement.getGestionAffichage().chargerCompteurs();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					affichage_compteursLogement.setVisible(true);
					affichage_compteursLogement.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un logement !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			//////////////////////////////////////////////////////////////////////////////////////////////
			// LAYERED MES LOCATIONS
			/////////////////////////////////////////////////////////////////////////////////////////////
			case "btn_MesLocations_Charger":
				try {
					this.chargerLocations();
				} catch (SQLException e1) {
					e1.printStackTrace();
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(null,
							"Erreur lors du chargement des locations. Veuillez réessayer plus tard.",
							"Erreur de chargement", JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btn_MesLocations_Modifier":
				if (Sauvegarde.onSave("Louer") == true) {
					Fenetre_ModificationLocation fml = new Fenetre_ModificationLocation();
					this.fenetreAccueil.getLayeredPane().add(fml); // Afficher la fenêtre de modification pour une location
					fml.setVisible(true);
					fml.moveToFront();
					Louer locSauvegarde = (Louer) Sauvegarde.getItem("Louer");
					// Pré-remplir la page avec les informations de la sauvegarde
					try {
						Louer louerBD = this.daoLouer.findById(locSauvegarde.getBien().getIdBien(),
								locSauvegarde.getLocataire().getIdLocataire());
						fml.getTextField_caution_TTC().setText(String.valueOf(louerBD.getCautionTTC()));
						fml.getTextField_date_debut().setText(louerBD.getDateDebut());
						fml.getTextField_IdImmeuble()
								.setText(String.valueOf(louerBD.getBien().getImmeuble().getImmeuble()));
						fml.getTextField_provision_chargeMens_TTC()
								.setText(String.valueOf(louerBD.getProvision_chargeMens_TTC()));
						fml.getTextField_montant_reel_paye().setText(String.valueOf(louerBD.getMontantReelPaye()));
						fml.getTextField_Id_Locataire().setText(louerBD.getLocataire().getIdLocataire());
						fml.getTextField_loyer_TCC().setText(String.valueOf(louerBD.getLoyerTTC()));
						// Si la date de la derniere regularisation est nulle
						if (louerBD.getDateDerniereRegularisation() == null)
							fml.getTextField_date_derniere_regularisation().setText("N/A");
						else
							fml.getTextField_date_derniere_regularisation()
									.setText(louerBD.getDateDerniereRegularisation());

					} catch (SQLException e1) {
						e1.printStackTrace();
						// Afficher un message d'erreur à l'utilisateur
						JOptionPane.showMessageDialog(null,
								"Erreur lors de la recherche de la location dans la base de données. Veuillez réessayer plus tard.",
								"Erreur de recherche", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil,
							"Veuillez sélectionner une location pour la modifier !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btn_MesLocations_Inserer":
				Fenetre_InsertionLocation location = new Fenetre_InsertionLocation();
				this.fenetreAccueil.getLayeredPane().add(location); // Afficher la fenêtre d'insertion pour une location
				location.setVisible(true);
				location.moveToFront();
				break;

			case "btn_MesLocations_Supprimer":
				if (Sauvegarde.onSave("Louer") == true) {
					Fenetre_SupprimerLocation loc_supp = new Fenetre_SupprimerLocation();
					this.fenetreAccueil.getLayeredPane().add(loc_supp); // Afficher la fenêtre de suppression pour une location
					loc_supp.setVisible(true);
					loc_supp.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil,
							"Veuillez sélectionner une location pour supprimer !", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btn_mesLocations_InfoLocataire":
				if (Sauvegarde.onSave("Locataire") == true) {
					Fenetre_AffichageInfoLocataire infos_locataire = new Fenetre_AffichageInfoLocataire();
					this.fenetreAccueil.getLayeredPane().add(infos_locataire); // Afficher la fenêtre d'informations propres à un locataire
					infos_locataire.setVisible(true);
					infos_locataire.moveToFront();

					// On recupere le locataire de la sauvegarde
					Locataire locataireSauvgarde = (Locataire) Sauvegarde.getItem("Locataire");
					Locataire locataireCourant;
					// Pré-remplir les champs avec les données de la sauvegarde
					try {
						locataireCourant = this.daoLocataire.findById(locataireSauvgarde.getIdLocataire());
						infos_locataire.getTextField_Id().setText(locataireCourant.getIdLocataire());
						infos_locataire.getTextField_Nom().setText(locataireCourant.getNom());
						infos_locataire.getTextField_Prenom().setText(locataireCourant.getPrenom());
						infos_locataire.getTextField_Telephone().setText(locataireCourant.getTelephone());
						infos_locataire.getTextField_Mail().setText(locataireCourant.getMail());
						infos_locataire.getTextField_DateN().setText(locataireCourant.getDateNaissance());
					} catch (SQLException e1) {
						e1.printStackTrace();
						// Afficher un message d'erreur à l'utilisateur
						JOptionPane.showMessageDialog(null,
								"Erreur lors de la recherche du locataire dans la base de données. Veuillez réessayer plus tard.",
								"Erreur de recherche", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner une location !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;
				
			case "btn_mesLocations_AjouterFacture":
				if (Sauvegarde.onSave("Logement") == true) {
					Fenetre_InsertionPaiementLogement insertion_facture = new Fenetre_InsertionPaiementLogement(true);
					this.fenetreAccueil.getLayeredPane().add(insertion_facture); // Afficher la fenêtre d'insertion d'un loyer pour une location
					insertion_facture.setVisible(true);
					insertion_facture.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner une location !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "btn_MesLocations_Archiver":
				if (Sauvegarde.onSave("Louer") == true) {
					Fenetre_ArchiverLocation archiver_location = new Fenetre_ArchiverLocation();
					this.fenetreAccueil.getLayeredPane().add(archiver_location); // Afficher la fenêtre d'archivage pour une location
					archiver_location.setVisible(true);
					archiver_location.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner une location !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			//////////////////////////////////////////////////////////////////////////////////////////////
			// LAYERED MES TRAVAUX
			//////////////////////////////////////////////////////////////////////////////////////////////
			case "btn_Travaux_Modifier":
				if (Sauvegarde.onSave("Facture")) {
					Fenetre_ModificationTravauxImmeuble modif_travaux = new Fenetre_ModificationTravauxImmeuble();
					this.fenetreAccueil.getLayeredPane().add(modif_travaux); // Afficher la fenêtre de modification pour des travaux
					modif_travaux.setVisible(true);
					modif_travaux.moveToFront();

					// Recuperer la facture du travaux de la sauvegarde
					Facture travauxSauvegarde = (Facture) Sauvegarde.getItem("Facture");
					Facture travauxCourant;
					// Pré-remplir les champs avec les données de la suvegarde
					try {
						travauxCourant = this.daoFacture.findFactureTravauxById(travauxSauvegarde.getNumero());
						modif_travaux.getTextField_Numero().setText(travauxCourant.getNumero());
						modif_travaux.getTextField_designation().setText(travauxCourant.getDesignation());
						modif_travaux.getTextField_dateEmission().setText(travauxCourant.getDateEmission());
						modif_travaux.getTextField_montant().setText(Double.toString(travauxCourant.getMontant()));
						if (travauxCourant.getDatePaiement() != null) {
							modif_travaux.getTextField_paye().setText(travauxCourant.getDatePaiement());
						} else {
							modif_travaux.getTextField_paye().setText("N/A");
						}
						modif_travaux.getTextField_prestataire().setText(travauxCourant.getEntreprise().getNom());
						modif_travaux.getTextField_adresse().setText(travauxCourant.getEntreprise().getAdresse());
						if (travauxCourant.getImmeuble() != null) {
							modif_travaux.getTextField_Bien_Logement()
									.setText(travauxCourant.getImmeuble().getImmeuble());
						} else {
							modif_travaux.getTextField_Bien_Logement().setText(travauxCourant.getBien().getIdBien());
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
						// Afficher un message d'erreur à l'utilisateur
						JOptionPane.showMessageDialog(null,
								"Erreur lors de la recherche du travau dans la base de données. Veuillez réessayer plus tard.",
								"Erreur de recherche", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un travaux !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btn_Travaux_Supprimer":
				if (Sauvegarde.onSave("Facture")) {
					Fenetre_SupprimerTravaux supp_travaux = new Fenetre_SupprimerTravaux();
					this.fenetreAccueil.getLayeredPane().add(supp_travaux); // Afficher la fenêtre de suppression pour des travaux
					supp_travaux.setVisible(true);
					supp_travaux.moveToFront();
					break;
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un travaux !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			/////////////////////////////////////////////////////////////////////////////////////////////
			// LAYERED MES FACTURES
			/////////////////////////////////////////////////////////////////////////////////////////////
			case "btn_MesFactures_Modifier":
				// Premier test si il n'y a aucune charge sélectionnée alors erreur
				if (Sauvegarde.onSave("Charge") == false) {
					JOptionPane.showMessageDialog(this.fenetreAccueil,
							"Veuillez sélectionner une facture pour modifier !", "Erreur", JOptionPane.ERROR_MESSAGE);
				} else {
					// On ouvre la fenêtre
					Fenetre_ModificationFactureChargeLogement modif_charge = new Fenetre_ModificationFactureChargeLogement();
					this.fenetreAccueil.getLayeredPane().add(modif_charge); // Afficher la fenêtre de modification pour des factures
					modif_charge.setVisible(true);
					modif_charge.moveToFront();
					// Permet de récupérer les infos sur la facture/charge courante pour les afficher
					// On la récupère de la sauvegarde
					Facture chargeSauvegarde = (Facture) Sauvegarde.getItem("Charge");
					Facture chargeCourante;
					try {
						// À partir du numéro dans la sauvegarde, utilisez la BD pour
						// récupérer la plus récente correspondante
						chargeCourante = this.daoFacture.findById(chargeSauvegarde.getNumero());
						// Afficher les infos dans la page de modification
						modif_charge.getTextField_Numero().setText(chargeCourante.getNumero());
						modif_charge.getTextField_date_paiement().setText(chargeCourante.getDatePaiement());
						modif_charge.getTextField_date_emission().setText(chargeCourante.getDateEmission());

						if (chargeCourante.getNumeroDevis() != null) {
							modif_charge.getTextField_numeroDevis().setText(chargeCourante.getNumeroDevis());
						} else {
							modif_charge.getTextField_numeroDevis().setText("N/A");
						}

						modif_charge.getTextField_accompteVerse()
								.setText(String.valueOf(chargeCourante.getMontantReelPaye()));
						modif_charge.getTextField_montant().setText(String.valueOf(chargeCourante.getMontant()));

						// Mise à jour des boutons radio
						if (chargeCourante.getImputableLocataire() == 1) {
							modif_charge.getRdbtnOui().setSelected(true);
						} else {
							modif_charge.getRdbtnNon().setSelected(true);
						}

						// Mise à jour du JComboBox_Designation
						modif_charge.getComboBox_Designation().setSelectedItem(chargeCourante.getDesignation());

					} catch (SQLException e1) {
						// Afficher un message d'erreur à l'utilisateur
						JOptionPane.showMessageDialog(null,
								"Erreur lors de la recherche de la facture dans la base de données. Veuillez réessayer plus tard.",
								"Erreur de recherche", JOptionPane.ERROR_MESSAGE);
					}
				}

				break;

			case "btn_MesFactures_Supprimer":
				if (Sauvegarde.onSave("Charge") == true) {
					Fenetre_SupprimerFactureCharge supp_charge = new Fenetre_SupprimerFactureCharge();
					this.fenetreAccueil.getLayeredPane().add(supp_charge); // Afficher la fenêtre de suppression pour des factures
					supp_charge.setVisible(true);
					supp_charge.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil,
							"Veuillez sélectionner une facture pour supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btn_MesFactures_Archiver":
				if (Sauvegarde.onSave("Charge") == true) {
					Fenetre_ArchiverFacture arch_charge = new Fenetre_ArchiverFacture();
					this.fenetreAccueil.getLayeredPane().add(arch_charge); // Afficher la fenêtre d'archivage pour des factures
					arch_charge.setVisible(true);
					arch_charge.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil,
							"Veuillez sélectionner une facture pour supprimer", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				break;

			//////////////////////////////////////////////////////////////////////////////////////////////
			// LAYERED MES ASSURANCES
			/////////////////////////////////////////////////////////////////////////////////////////////
			case "btn_MesAssurances_Charger":
				try {
					this.chargerAssurances();
				} catch (SQLException e1) {
					e1.printStackTrace();
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(null,
							"Erreur lors du chargement des assurance. Veuillez réessayer plus tard.",
							"Erreur de chargement", JOptionPane.ERROR_MESSAGE);
				}
				break;
			case "btn_MesAssurances_Modifier":
				if (Sauvegarde.onSave("Assurance")) { // Vérifie s'il y a des données d'assurance sauvegardées
					// Crée une nouvelle fenêtre de modification d'assurance
					Fenetre_ModificationAssurance modification_assurance = new Fenetre_ModificationAssurance();
					this.fenetreAccueil.getLayeredPane().add(modification_assurance); // Afficher la fenêtre de modification pour une assurance
					modification_assurance.setVisible(true);
					modification_assurance.moveToFront();
					Assurance assuranceSauvegarde = (Assurance) Sauvegarde.getItem("Assurance");
					Assurance assuranceCourante;
					Echeance echeance = (Echeance) Sauvegarde.getItem("Echeance");
					// Pré-remplir les champs avec les données de la sauvegarde
					try {
						assuranceCourante = this.daoAssurance.findById(assuranceSauvegarde.getNuméroPolice());
						modification_assurance.getTextField_numPolice().setText(assuranceCourante.getNuméroPolice());
						modification_assurance.getTextField_dateEcheance()
								.setText(echeance.getDateEcheance().substring(0, 10));
						modification_assurance.getTextField_montant()
								.setText(Double.toString(assuranceCourante.getMontant()));

					} catch (SQLException e1) {
						e1.printStackTrace();
						// Afficher un message d'erreur à l'utilisateur
						JOptionPane.showMessageDialog(null,
								"Erreur lors de la recherche de l'assurance dans la base de données. Veuillez réessayer plus tard.",
								"Erreur de recherche", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil,
							"Veuillez sélectionner une assurance pour modifier !", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				break;

			case "btn_MesAssurances_Inserer":
				// Vérifie s'il y a des données d'un logement sauvegardées
				if (Sauvegarde.onSave("Logement") == true) {
					Fenetre_InsertionAssurance insertion_assurance = new Fenetre_InsertionAssurance();
					this.fenetreAccueil.getLayeredPane().add(insertion_assurance);
					insertion_assurance.setVisible(true);
					insertion_assurance.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un logement !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;
				
			case "btn_MesAssurances_Supprimer":
				if (Sauvegarde.onSave("Assurance") == true) {
					Fenetre_SupprimerAssurance supp_assurance = new Fenetre_SupprimerAssurance();
					this.fenetreAccueil.getLayeredPane().add(supp_assurance); // Afficher la fenêtre de suppression pour une assurance
					supp_assurance.setVisible(true);
					supp_assurance.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil,
							"Veuillez sélectionner une assurance pour supprimer !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;

			////////////////////////////////////////////////////////////////////////////////////////////
			// LAYERED MES DOCUMENTS
			///////////////////////////////////////////////////////////////////////////////////////////
			case "btn_MesDocuments_Inserer_Impots":
				// Vérifie s'il y a des données d'un logement sauvegardées
				if (Sauvegarde.onSave("Logement") == true) {
					Fenetre_InsertionImpot insertion_impot = new Fenetre_InsertionImpot();
					this.fenetreAccueil.getLayeredPane().add(insertion_impot); // Afficher la fenêtre d'insertion pour un impôt
					insertion_impot.setVisible(true);
					insertion_impot.moveToFront();
				} else {
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Veuillez sélectionner un logement !", "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
				break;
				
			case "btn_MesDocuments_Charger":
				try {
					this.chargerImpot();
				} catch (SQLException e1) {
					e1.printStackTrace();
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(null,
							"Erreur lors du chargement des assurance. Veuillez réessayer plus tard.",
							"Erreur de chargement", JOptionPane.ERROR_MESSAGE);
				}
				break;
				
			case "btn_MesDocuments_generer_annexe":
				try {
					CreerAnnexe.main(new String[] {});
					// Afficher un message de réussite à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Annexe 2044 générée avec succès !", "Succès",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception ex) {
					ex.printStackTrace();
					// Afficher un message d'erreur à l'utilisateur
					JOptionPane.showMessageDialog(this.fenetreAccueil, "Erreur lors de la génération de l'annexe.",
							"Erreur", JOptionPane.ERROR_MESSAGE);
				}
				break;

			/////////////////////////////////////////////////////////////////////////////////////////////
			// LAYERED MES ARCHIVES
			/////////////////////////////////////////////////////////////////////////////////////////////
			case "btn_MesArchives_Louer":
				try {
					this.chargerTableArchiveLouer();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				break;

			case "btn_MesArchives_Facture":
				try {
					this.chargerTableArchiveFacture();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				break;
				
			case "btn_MesArchives_Locataire":
				try {
					this.chargerTableArchiveLoccataire();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				break;
			}

		} else if (source instanceof JToggleButton) { // Cas de clics sur des ToggleButton
			JToggleButton btnToggle = (JToggleButton) source;
			switch (btnToggle.getName()) {

			/////////////////////////////////////////////////////////////////////////////////////////////
			// LAYERED MES TRAVAUX
			/////////////////////////////////////////////////////////////////////////////////////////////
			case "tglbtn_Travaux_immeubles":
				// Permet de trier le tableau de travaux en n'affichant que celles concernant les immeubles
				try {
					this.chargerTravauxImmeubles();
				} catch (SQLException e1) {
					// Afficher un message d'erreur à l'utilisateur
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Erreur lors du chargement des charges des immeubles. Veuillez réessayer plus tard.",
							"Erreur de chargement", JOptionPane.ERROR_MESSAGE);
				}

				break;
				
			case "tglbtn_Travaux_logements":
				// Permet de trier le tableau de travaux en n'affichant que celles concernant les logements
				try {
					this.chargerTravauxLogements();
				} catch (SQLException e1) {
					// Afficher un message d'erreur à l'utilisateur
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Erreur lors du chargement des travaux des logements. Veuillez réessayer plus tard.",
							"Erreur de chargement", JOptionPane.ERROR_MESSAGE);
				}
				break;

			/////////////////////////////////////////////////////////////////////////////////////////////
			// LAYERED MES FACTURES
			/////////////////////////////////////////////////////////////////////////////////////////////
			case "tglbtn_FactureCharge_biens":
				// Permet de trier le tableau de factures en n'affichant que celles concernant les immeubles
				try {
					this.chargerChargesLogement();
				} catch (SQLException e1) {
					// Afficher un message d'erreur à l'utilisateur
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null,
							"Erreur lors du chargement des charges de logement. Veuillez réessayer plus tard.",
							"Erreur de chargement", JOptionPane.ERROR_MESSAGE);
				}
				break;
			}

		}
		// Filtrage pour les comboBox
		this.filtreAssuranceByLogement();
		this.filtreChargesByLogement();
		this.filtreImpotByLogement();
	}
}