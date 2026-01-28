package controleur.outils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Sauvegarde {
	
	/*
	 * Cette classe est un outil pour notre code, elle permet de stocker 
	 * temporairement des éléments avant l'insertion
	 * lors de l'utilisation de plusieurs fenêtres
	 * pour les assembler à la fin.
	 * 
	 * Par exemple lors de l'ajout d'un immeuble on souhaite ajouter un compteur pour
	 * un immeuble qui n'existe pas encore.
	 * Avec cette classe, on pourra stocker les informations du compteur ici avant de l'inserer plus tard quand l'objet immeuble sera créée.
	 * On accède à chaque valeur avec son nom grace à la Map("nom","objet")
	 */
		private static Map<String, Object> sauvegarde;
		

		/**
		 * Initialisation de la sauvegarde
		 */
		public static void initializeSave() {
			if (sauvegarde == null) {
				sauvegarde = new HashMap<>();
			}
		}

		/**Vidage de la sauvegarde
		 * 
		 */
		public static void clearSave() {
			if (sauvegarde != null && !(sauvegarde.isEmpty())) {
				sauvegarde.clear();
			}
		}
		
		/**Verifie si une instance existe dans la Map exemple si une Entreprise
		 * a été sauvegardé auparavant elle enregistrée
		 * @param name (String) : nom de la Table ou Classe
		 * @return true si elle est dans la map sinon false
		 */
		public static boolean onSave(String name) {
			return sauvegarde.containsKey(name);
		}
		
		/**Accès a la sauvegarde
		 * @return
		 */
		public static Map<String, Object> getSave() {
			if (sauvegarde != null) {
				return sauvegarde;
			}
			return Collections.emptyMap();
		}
		

		/**Ajout d'élément a la sauvegarde
		 * @param nom (String) : nom de l'item a ajouté
		 * @param item (Object) : la classe de l'objet a ajouté 
		 */
		public static void addItem(String nom, Object item) {
			if (sauvegarde != null) {
				sauvegarde.put(nom, item);
			}
		}
		

		/**Obtention d'un élément de la sauvegarde
		 * @param name (String) : nom de l'item
		 * @return l'objet demandée
		 */
		public static Object getItem(String name) {
			if (sauvegarde != null && sauvegarde.containsKey(name)) {
					return sauvegarde.get(name);
			}
			return null;
		}

		/**Supression d'un élément de la sauvegarde
		 * @param name (String) : nom de l'item a supprimé
		 */
		public static void deleteItem(String name) {
			if (sauvegarde != null && sauvegarde.containsKey(name)) {
				sauvegarde.remove(name);
			}
		}


}
