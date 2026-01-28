package controleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import controleur.outils.ImportChemin;
import controleur.outils.PDFImporter;
import vue.insertion.Fenetre_InsertionLocation;

public class GestionPDF extends JInternalFrame {

	private JPanel contentPanePDF;
	private JTextField textFieldPDF;
	private JTextField nom_bis;
	private JButton btnPDF;
	private JLabel nomPdf;
	private int id;
	private String nom;
	private Fenetre_InsertionLocation fil;
	private PDFImporter pdf;
	private ImportChemin chemin;

	/**
	 * Creation du gestionnaire de PDF
	 */
	public GestionPDF(Fenetre_InsertionLocation fil) {
		this.fil = fil;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPanePDF = new JPanel();
		contentPanePDF.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPanePDF);
		contentPanePDF.setLayout(null);

		JLabel lblPDF = new JLabel("id");
		lblPDF.setBounds(55, 14, 8, 13);
		contentPanePDF.add(lblPDF);

		textFieldPDF = new JTextField();
		textFieldPDF.setBounds(68, 11, 96, 19);
		contentPanePDF.add(textFieldPDF);
		textFieldPDF.setColumns(10);

		nomPdf = new JLabel("");
		nomPdf.setBounds(163, 81, 200, 13);
		contentPanePDF.add(nomPdf);

		btnPDF = new JButton("pdf");
		btnPDF.setBounds(68, 77, 85, 21);
		contentPanePDF.add(btnPDF);
		
		btnPDF.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				// Déclarez id et nom comme variables locales si elles ne sont pas des variables d'instance
				id = Integer.parseInt(textFieldPDF.getText());
				nom = nom_bis.getText();
				if (pdf == null) {
					pdf = PDFImporter.getInstance();
				}
				chemin.choisirChemin();

				if (pdf.getPDFFileName() != null) {
					nomPdf.setText(pdf.getPDFFileName());
				}
			} catch (NumberFormatException ex) {
				// Gérer l'exception si l'ID n'est pas un entier
				ex.printStackTrace();
			}
		}
		});

		JLabel lblNewLabel_1 = new JLabel("Nom");
		lblNewLabel_1.setBounds(259, 14, 21, 13);
		contentPanePDF.add(lblNewLabel_1);

		nom_bis = new JTextField();
		nom_bis.setBounds(285, 11, 96, 19);
		contentPanePDF.add(nom_bis);
		nom_bis.setColumns(10);

		nomPdf = new JLabel("");
		nomPdf.setBounds(163, 81, 200, 13);
		contentPanePDF.add(nomPdf);

		JButton btnNewButton_1 = new JButton("Ajoutez");
		btnNewButton_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					pdf.getInstance().importPDFBD(id, nom);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(79, 211, 85, 21);
		contentPanePDF.add(btnNewButton_1);

		JButton btnAnnuler = new JButton("Annuler");
		btnAnnuler.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnAnnuler.setBounds(270, 211, 85, 21);
		contentPanePDF.add(btnAnnuler);
	}
}
