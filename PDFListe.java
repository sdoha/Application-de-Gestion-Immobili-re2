package controleur.outils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PDFListe extends JPanel {

    private ArrayList<File> pdfFiles;
    private JPanel pdfListPanel;


    public PDFListe() {
        pdfFiles = new ArrayList<>();
        initializeComponents();
    }

    /**
     * Initialise les composant de PDFListe
     */
    private void initializeComponents() {
        this.setLayout(new BorderLayout());

        pdfListPanel = new JPanel();
        pdfListPanel.setLayout(new BoxLayout(pdfListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(pdfListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Ajoute un fichier PDF à la liste et crée un bouton pour celui ci
     *
     * @param pdfFile Le fichier PDF à ajouter à la liste.
     */
    public void addPDFFile(File pdfFile) {
        pdfFiles.add(pdfFile);
        JButton pdfButton = new JButton(pdfFile.getName());
        pdfButton.addActionListener(e -> openPDF(pdfFile));
        pdfListPanel.add(pdfButton);
        pdfListPanel.revalidate();
        pdfListPanel.repaint();
    }

    /**
     * Ouvre le fichier PDF sélectionne en utilisant l'application systeme par défaut
     *
     * @param pdfFile (File): pdfFile Le fichier PDF à ouvrire
     */
    private void openPDF(File pdfFile) {
        try {
            Desktop.getDesktop().open(pdfFile);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ouverture du fichier : " + e.getMessage());
        }
    }
}
