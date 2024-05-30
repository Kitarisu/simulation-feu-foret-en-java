import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;



//----------------------- COMMENTAIRE ------------------------

// j'ai parfaitement conscience que ca n'est pas parfait, 
// j'ai fait de mon mieux pour faire un truc qui marche, 
// et surtout pour comprendre comment ca marche
// C'est litteralement le premier truc que je fais en java

// le fichier config.txt est composé de 6 lignes (minimum) :
// h : hauteur de la foret
// l : largeur de la foret
// p : probabilité de propagation du feu
// n : nombre d'arbres en feu initialement
// n lignes suivantes : coordonnées x puis y des arbres en feu initialement

//------------------------------------------------------------


public class ForetGUI extends JFrame {
    private Foret foret;
    private DessinPanel dessinPanel;
    private final JButton bouton; //j'ai une erreur si je mets pas final, je sais pas encore pourquoi

    public ForetGUI(int h, int l, double p, List<int[]> arbresEnFeuInit){
        foret = new Foret(h, l);
        foret = foret.iniForet(foret, arbresEnFeuInit);

        setTitle("Simulation de Forêt");
        setSize(h * 25, (l * 25)+50); //22 car 20 pour la taille des cellules et 2 pour l'espace entre elles, 20 en + pour la taille du bouton
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dessinPanel = new DessinPanel();
        getContentPane().add(dessinPanel, BorderLayout.CENTER);

        bouton = new JButton("Actualiser");
        bouton.addActionListener((ActionEvent e) -> {
            foret.actualiser(p); // Par exemple, 20% de probabilité de propagation du feu
            dessinPanel.repaint();
        });
        getContentPane().add(bouton, BorderLayout.SOUTH);
    }

    private class DessinPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int cellSize = 20; // Taille des cellules
            int spacing = 2; // Espace entre les cellules
            for (int i = 0; i < foret.h; i++) {
                for (int j = 0; j < foret.l; j++) {
                    switch (foret.terrain[i][j].etat) {
                        case "o" -> g.setColor(Color.GREEN);
                        case "X" -> g.setColor(Color.RED);
                        case "." -> g.setColor(Color.GRAY);
                    }
                    g.fillRect(j * (cellSize + spacing), i * (cellSize + spacing), cellSize, cellSize);

                    if ("X".equals(foret.terrain[i][j].etat)) {
                        g.setColor(Color.BLACK);
                        List<int[]> directions = foret.directionsPossibles(i, j);
                        for (int[] direction : directions) {
                            drawArrow(g, j * (cellSize + spacing)+10, i * (cellSize + spacing)+10, direction[1] * (cellSize + spacing)+10, direction[0] * (cellSize + spacing)+10);
                        }
                    }
                }
            }
        }

        // Dessine une flèche entre deux arbres
        private void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {

            //System.out.println("x1 = "+x1+" y1 = "+y1+" x2 = "+x2+" y2 = "+y2);
            
            int dx = x2 - x1;
            int dy = y2 - y1;
            double D = Math.sqrt(dx * dx + dy * dy); // distance entre les 2 points
            
            // Taille des segments de la flèche (ajuster si nécessaire)
            double arrowHeadLength = 10; 
            double arrowHeadWidth = 10; 
        
            // Points de base pour la tête de flèche (avant rotation)
            double xm = x2 - (arrowHeadLength / D) * dx;
            double ym = y2 - (arrowHeadLength / D) * dy;
  
            // Calcul des angles de rotation
            double sin = dy / D; 
            double cos = dx / D;
        
            // Points après rotation pour obtenir la tête de flèche
            double x1ArrowHead = xm + (arrowHeadWidth / 2) * (-sin);
            double y1ArrowHead = ym + (arrowHeadWidth / 2) * cos;
            double x2ArrowHead = xm - (arrowHeadWidth / 2) * (-sin);
            double y2ArrowHead = ym - (arrowHeadWidth / 2) * cos;
            //on affiche les points de la tete de la fleche
            
            System.out.println("x1ArrowHead = "+x1ArrowHead+" y1ArrowHead = "+y1ArrowHead+" x2ArrowHead = "+x2ArrowHead+" y2ArrowHead = "+y2ArrowHead);
        
            // Conversion en entier pour les points de la tête de flèche
            int[] xpoints = {x2, (int) x1ArrowHead, (int) x2ArrowHead};
            int[] ypoints = {y2, (int) y1ArrowHead, (int) y2ArrowHead};
        
            // Dessiner la ligne principale de la flèche
            g.drawLine(x1, y1, x2, y2);
        
            // Dessiner la tête de la flèche
            g.fillPolygon(xpoints, ypoints, 3);
        }
        
        
    }

    public static void main(String[] args) throws FileNotFoundException {
        int h,l,n,x,y;
        double p;

        File file = new File("config.txt");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        List<int[]> arbresEnFeuInit = new ArrayList<>();

        String line;
        try {
            //on lit les valeurs de h, l et p dans le fichier config.txt
            line = br.readLine();
            h = Integer.parseInt(line);
            line = br.readLine();
            l = Integer.parseInt(line);
            line = br.readLine();
            p = Double.parseDouble(line);

            System.out.println("h = "+h+" l = "+l+" p = "+p);

            line = br.readLine();
            n = Integer.parseInt(line);

            System.out.println("n = "+n);

            for(int i = 0; i < n; i++){
                line = br.readLine();
                x = Integer.parseInt(line);
                line = br.readLine();
                y = Integer.parseInt(line);
                arbresEnFeuInit.add(new int[]{x, y});
                //System.out.println("x = "+x+" y = "+y);
            }

            if(h <= 0 || l <= 0 || p < 0 || p > 1){
                System.out.println("Les valeurs de h, l et p doivent être positives et p doit être compris entre 0 et 1");
                System.exit(1);
            }

        } catch (FileNotFoundException e) {
            //si le fichier n'existe pas on prend les valeurs par défaut
            System.out.println("Fichier config.txt introuvable ! On prend les valeurs par défaut (20x20, p = 0.5)");
            h = 20;
            l = 20;
            p = 0.5;
        } catch (IOException e) {
            //si on a une erreur on prend les valeurs par défaut
            h = 20;
            l = 20;
            p = 0.5;
        }
        //c'est la qu'on lance la fenetre
        new ForetGUI(h,l,p,arbresEnFeuInit).setVisible(true);

        try {
            br.close();
            fr.close();
        } catch (IOException e) {
            System.out.println("Erreur lors de la fermeture du fichier");
        }
        
    }
}

class Arbre{
    String etat;

    public Arbre(){
        this.etat = "o"; 
        // o : arbre vivant |  . : arbre mort  |  X : arbre en feu
        // jsp si on peut faire de l'affichage avec ca on verra bien (update : utile pr la version terminal pr tester)
    }

    void prendsFeu(){
        this.etat = "X";
    }

    void meurt(){
        this.etat = ".";
    }
}

class Foret{
    Integer h;
    Integer l;
    Arbre[][] terrain;

    public Foret(Integer h, Integer l) {
        int i, j;

        this.h = h;
        this.l = l;
        this.terrain = new Arbre[h][l];

        for( i = 0; i < h; i++){ // initialisation de la foret avec des arbres vivants
            for( j = 0; j < l; j++){
                this.terrain[i][j] = new Arbre();
            }
        }
    }

    List<int[]> arbresEnFeu() { //renvoie la liste des arbres en feu sous forme de coordonnees
        List<int[]> enFeu = new ArrayList<>();
        for (int i = 0; i < this.h; i++) {
            for (int j = 0; j < this.l; j++) {
                if (this.terrain[i][j].etat.equals("X")) {
                    enFeu.add(new int[]{i, j});
                }
            }
        }
        return enFeu;
    }

    //renvoie une liste des coordonnees des arbres vivants autour de l'arbre en feu
    public List<int[]> directionsPossibles(int i, int j) {
        List<int[]> directions = new ArrayList<>();

        //on traite les 4 directions independamment

        if (i > 0 && "o".equals(this.terrain[i - 1][j].etat)) {
            directions.add(new int[]{i - 1, j});
        }
        if (i < this.h - 1  && "o".equals(this.terrain[i + 1][j].etat)) {
            directions.add(new int[]{i + 1, j});
        }
        if (j > 0  && "o".equals(this.terrain[i][j - 1].etat)) {
            directions.add(new int[]{i, j - 1});
        }
        if (j < this.l - 1 && "o".equals(this.terrain[i][j + 1].etat)) {
            directions.add(new int[]{i, j + 1});
        }

        return directions;
    }

    public void afficher(){  //jsp si ca a du sens de faire ca en java mais au moins c'est fait (update : utile pr la version terminal pr tester)
        int i, j;

        for( i = 0; i < this.h; i++){
            System.out.print("- ");
        }
        System.out.println();

        for( i = 0; i < this.h; i++){
            for( j = 0; j < this.l; j++){
                System.out.print(this.terrain[i][j].etat+" ");
            }
            System.out.println();
        }
        for( i = 0; i < this.h; i++){
            System.out.print("- ");
        }
        System.out.println();
    }

    public void actualiser(Double p){
        int i, j;

        Random r = new Random();
        /*System.out.println(r.nextDouble());*/

        List<int[]> enFeu = arbresEnFeu(); //recupere les arbres en feu

        for (int[] coords : enFeu) {
            i = coords[0];
            j = coords[1];

            this.terrain[i][j].meurt(); // d'abord on eteint l'arbre en feu

            // on traite les 4 directions independamment (jaurai pu faire un seul if mais c'etait plus clair comme ca je trouve)

            if (i > 0 && r.nextDouble() < p) {
                if("o".equals(this.terrain[i - 1][j].etat))
                    this.terrain[i - 1][j].prendsFeu();
            }
            if (i < this.h - 1 && r.nextDouble() < p) {
                if("o".equals(this.terrain[i + 1][j].etat))
                    this.terrain[i + 1][j].prendsFeu();
            }
            if (j > 0 && r.nextDouble() < p) {
                if("o".equals(this.terrain[i][j - 1].etat))
                    this.terrain[i][j - 1].prendsFeu();
            }
            if (j < this.l - 1 && r.nextDouble() < p) {
                if("o".equals(this.terrain[i][j + 1].etat))
                    this.terrain[i][j + 1].prendsFeu();
            }
        }

    }

    public Foret iniForet(Foret f, List<int[]> arbresEnFeuInit){
        for(int[] coords : arbresEnFeuInit){
            if(coords[0] < f.h && coords[1] < f.l){
                f.terrain[coords[0]][coords[1]].prendsFeu();
            }
            else{
                System.out.println("Coordonnees d'arbre en feu initialement incorrectes : "+coords[0]+" "+coords[1]);
        
            }
        }
        return f;
    }
}

