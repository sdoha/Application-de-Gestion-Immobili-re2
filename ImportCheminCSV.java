package controleur.outils;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImportCheminCSV {

	private String selectedFilePath;

	/**Lorsque vous utilisez comme pour par exemple un bouton 
	 * cela va ouvrir une petite fenetre avec votre repertoire ou il sera possible de prendre
	 * seulement un CSV. 
	 * Avec ce chemin on pour extraire un fichier CSV
	 * 
	 */
	public void choisirChemin() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

		// Utilisez null comme parent si cette classe n'est pas un composant Swing
		int result = fileChooser.showSaveDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			selectedFilePath = selectedFile.getAbsolutePath(); // stocke le chemin
		}
	}

	public String getSelectedFilePath() {
		return this.selectedFilePath;
	}
}
