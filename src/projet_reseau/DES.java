package projet_reseau;

import java.util.ArrayList;
import java.util.Random;

public class DES {

	private static final int taille_bloc = 64;
	private static final int nb_sous_bloc = 32;
	private static final int nb_ronde = 1;
	private static final int[] tab_decalage = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
	private static final int[] perm_initiale = new int[64];
	private static final int[][] pc1 = new int[28][28];
	private static final int[] pc2 = new int[48];
	private static final int[] s = new int[32];
	private static final int[] e = new int[48];
	
	
	private int[] masterKey = new int[64];
	private ArrayList<Integer> tab_cles;
	
	
	public DES() {
		Random ran = new Random();
		for (int i=0; i<this.masterKey.length; i++) {
			this.masterKey[i]=ran.nextInt(2);
		}
		tab_cles = new ArrayList<Integer>();
		
	}
	
	public int[] crypte() {
		
	}
	public static void main(String[] args) {
		DES d = new DES();
	}
}
