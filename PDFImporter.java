package controleur.outils;

import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import modele.dao.CictOracleDataSource;

public class PDFImporter extends JFrame {

    private static PDFImporter instance;
    private String selectedFilePath;  // Attribut pour stocker le chemin du fichier

    private PDFImporter() {
        this.setTitle("PDF Importer");
        this.setSize(300, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.initializeComponents();
    }

    /**Methode pour pouvoir en faire un patern Singleton pour ne pas le crée a chaque utilsation
     * @return
     */
    public static PDFImporter getInstance() {
        if (instance == null) {
            instance = new PDFImporter();
        }
        return instance;
    }
    
    // Importe le fichier PDF dans la base de données avec l'id et le nom spécifiés
    /**
     * @param id (int) : id a inserer dans la BD
     * @param nom (String) : nom a mettre dans l'insertion de la BD
     * @throws FileNotFoundException
     * @throws SQLException
     */
    public void importPDFBD(int id, String nom) throws FileNotFoundException, SQLException {
    	
    	Connection cn = CictOracleDataSource.getConnectionBD();
    	
        String sql = "INSERT INTO documents (id, nom, fichier_pdf) VALUES (?, ?, ?)";
        PreparedStatement pstmt = cn.prepareStatement(sql);

        File pdfFile = new File(this.getSelectedFilePath());
        FileInputStream input = new FileInputStream(pdfFile);

        pstmt.setInt(1, id);
        pstmt.setString(2, nom);
        pstmt.setBinaryStream(3, input, (int) pdfFile.length());
        pstmt.executeUpdate();
        pstmt.close();
    }
    

    /**Récupère le nom du fichier PDF
     * @return
     */
    public String getPDFFileName() {
        if (selectedFilePath != null && !selectedFilePath.isEmpty()) {
            return new File(selectedFilePath).getName();
        }
        return null;
    }

    /**Initialise les composants de l'interface graphique
     * 
     */
    private void initializeComponents() {
        JButton btnImportPDF = new JButton("Import PDF");
        btnImportPDF.addActionListener(e -> importPDFChemin());
        this.setLayout(new FlowLayout());
        this.add(btnImportPDF);
    }

    /**Permet à l'utilisateur de choisir un fichier PDF à importer
     * 
     */
    public void importPDFChemin() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedFilePath = selectedFile.getAbsolutePath();  
        }
    }

    /**Retourne le chemin du fichier PDF choisi par l'utilisateur
     * @return 
     */
    public String importPDFCheminString() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath(); 
        }
        return null; 
    }

    /** Retourne le chemin du fichier PDF sélectionné
     * @return
     */
    public String getSelectedFilePath() {
        return selectedFilePath;
    }
}
