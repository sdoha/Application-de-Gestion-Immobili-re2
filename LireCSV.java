package controleur.outils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import modele.dao.CictOracleDataSource;
import modele.dao.requetes.sousProgramme.SousProgrammeInsertFacture;

public class LireCSV {

	public LireCSV() {
		//TODO
	}

	/**Methode pour extraire un fichier CSV pour pouvoir l'inserer dans la BD
	 * @param chemin (String) : prend le chemin ou ce trouve le fichier CSV 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void lireCSV(String chemin) throws FileNotFoundException, IOException {
		Connection cn = CictOracleDataSource.getConnectionBD();
		SousProgrammeInsertFacture insertFacture = new SousProgrammeInsertFacture();

		try (Reader reader = new FileReader(chemin); CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {
			boolean premiereLigne = true;

			for (CSVRecord csvRecord : csvParser) {
				// Vérifiez si c'est la première ligne
				if (premiereLigne) {
					premiereLigne = false;
					continue; // Passe à la prochaine itération pour ignorer la première ligne
				}
				try (PreparedStatement prSt = cn.prepareStatement(insertFacture.appelSousProgramme())) {
					insertFacture.parametresCSV(prSt, csvRecord.get(0), csvRecord.get(1), csvRecord.get(2),
							csvRecord.get(3), csvRecord.get(4), csvRecord.get(5), csvRecord.get(6), csvRecord.get(7),
							csvRecord.get(8));
					prSt.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

















// Ce sera moi le dernier commit m'en fou