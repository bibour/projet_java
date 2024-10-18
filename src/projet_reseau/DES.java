package projet_reseau;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

public class DES {

	private static final int taille_bloc = 64;
	private static final int nb_sous_bloc = 32;
	private static final int nb_ronde = 1;
	private static final int[] tab_decalage = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
	private static final int[] perm_initiale = {57,49,41,33,25,17,9,1,59,51,43,35,27,19,11,3,61,53,45,37,29,21,13,5,63,55,47,39,31,23,15,7,56,48,40,32,24,16,8,0,58,50,42,34,26,18,10,2,60,52,44,36,28,20,12,4,62,54,46,38,30,22,14,6};
	private static final int[]pc1 = {56,48,40,32,24,16,8,0,5,0,57,49,41,33,25,17,9,1,58,50,42,34,26,18,10,2,59,51,43,35,62,54,46,38,30,22,14,6,61,53,45,37,29,21,13,5,60,52,44,36,28,20,12,4,27,19,11,3};

	private static final int[] pc2 = {13,16,10,23,0,4,2,27,14,5,20,9,22,18,11,3,25,7,15,6,26,19,12,1,40,51,30,36,46,54,29,39,50,44,32,47,43,48,38,55,33,52,45,41,49,35,28,31};
	
	private static final int[][] s = {{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7}, {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8}, {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0}, {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}};
	private static final int[] e = {31,0,1,2,3,4,3,4,5,6,7,8,7,8,9,10,11,12,11,12,13,14,15,16,15,16,17,18,19,20,19,20,21,22,23,24,23,24,25,26,27,28,27,28,29,30,31,0};
	
	
	private int[] masterKey = new int[64];
	private ArrayList<int[]> tab_cles;
	
	
	public DES() {
		Random ran = new Random();
		for (int i=0; i<this.masterKey.length; i++) {
			this.masterKey[i]=ran.nextInt(2);
		}
		tab_cles = new ArrayList<int[]>();
		
	}
	
	public int[] stringToBits(String message) {
		byte[] bytes = message.getBytes();
		int[] bit = new int[message.length()*8];
		int i = 0;
		for (byte b : bytes) {
			int val = b;
			for (int j=0; j<8; j++) {
				if (i<64) {
				bit[i] = (val & 128) == 0 ? 0 : 1;
				val <<= 1;
				i ++;
				}
			}
		}
		
		return bit;
	}
	
	public String bitToString(int[] bloc) {
		byte[] b = new byte[8];
		
		for (int i=0; i<8; i++) {
			int somme = 0;
			for (int j=0; j<8; j++) {
				somme += bloc[i*8+j]*(Math.pow(2,(7-j)));
				
			}
			
			b[i] = (byte) somme;
		}
		String s = new String(b, StandardCharsets.UTF_8);

		
		return s;
		
	}
	
	public int[] genereMasterKey() {
		int[] mk = new int[64];
		Random ran = new Random();
		for (int i=0; i<mk.length; i++) {
			mk[i] = ran.nextInt(1);
		}
		return mk;
	}
	
	public int[] permutation(int[] tab_permutation, int[] bloc) {
		int longueur = tab_permutation.length;
		int[] perm = new int[longueur];
		for (int i=0; i<longueur; i++) {
			perm[tab_permutation[i]] = bloc[i];
		}
		return perm;
	}
	
	public int[] invPermutation(int[] tab_permutation, int[] bloc) {
		int longueur = tab_permutation.length;
		int[] perm = new int[longueur];
		for (int i=0; i<longueur; i++) {
			perm[i] = bloc[tab_permutation[i]];
		}
		return perm;
	}
	
	public int[][] decoupage(int[] bloc, int tailleBlocs){
		int longueur_decoupage = bloc.length/this.taille_bloc +1;
		int[][] decoup = new int[longueur_decoupage][tailleBlocs];
		for (int i=0; i<longueur_decoupage; i++) {
			for (int j=0; j<tailleBlocs; j++) {
				decoup[i][j] = bloc[i*tailleBlocs+j];
			}
		}
		return decoup;
	}
	
	public int[] recollage_bloc(int[][] blocs) {
		int long_recollage_bloc = blocs[0].length * blocs.length;
		int[] recollage = new int[long_recollage_bloc];
		for (int i=0; i<blocs.length; i++) {
			for (int j=0; j<blocs[1].length; j++){
				recollage[i*blocs.length + j]= blocs[i][j];
			}
		}
		return recollage;
	}
	
	public int[] decalle_gauche(int[] bloc, int nbCran) {
		int[] decalle = new int[bloc.length];
		for (int i=nbCran; i<bloc.length-2; i++) {
			decalle[i-nbCran] = bloc[i];
		}
		for (int i = 0; i<nbCran; i++) {
			decalle[decalle.length-nbCran+i] = bloc[i];
		}
		return decalle;
	}
	
	public int[] xor(int[] tab1, int[] tab2) {
		int taille = tab1.length;
		int[] tab_xor = new int[taille];
		for (int i=0; i<taille; i++) {
			if (tab1[i] + tab2[i] == 2) {
				tab_xor[i] = 0;
			}
			else {
				tab_xor[i] = tab1[i] + tab2[i];
			}
		}
		return tab_xor;
	}
	
	public void genereCle(int n) {
		int[][] pc1 = decoupage(permutation(this.pc1, masterKey), 28);
		for (int i=0; i<pc1.length; i++) {
			pc1[i] = decalle_gauche(pc1[i], this.tab_decalage[nb_ronde]);
		}
		//Continuer en recollant les blocs et faire pc2
		int[] pc1_recolle = this.recollage_bloc(pc1);
		int[] pc2 = this.permutation(this.pc2, pc1_recolle);
		
		this.tab_cles.add(pc2);
	}
	
	public int[] fonction_S(int[] tab) {
		int ligne = tab[0]*2 + tab[5];
		int colonne = 0;
		for  (int i=1; i<5; i++) {
			colonne += tab[i]*Math.pow(2, 4-i);
		}
		String sauv = Integer.toBinaryString(this.s[ligne][colonne]);
		int res[] = new int[4];
		for (int i=0; i<sauv.length(); i++) {
			res[0] = Integer.parseInt(String.valueOf(sauv.charAt(i)));
		}
		return res;
	}
	
	
	public int[] fonction_F(int[] unD) {
		int[] d_permut = this.permutation(this.e, unD);
		int[] d_xor = this.xor(d_permut, this.tab_cles.get(-1));
	}
	
	
	public static void main(String[] args) {
		DES d = new DES();
		
		int[] test = d.stringToBits("lala");
		for (int i=0; i<test.length; i++) {
			System.out.print(test[i]);
		}
		System.out.println(d.bitToString(test));
	}
}
