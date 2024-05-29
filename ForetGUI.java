import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;


public class ForetGUI extends JFrame {
    private Foret foret;
    private DessinPanel dessinPanel;
    private JButton bouton;

    public ForetGUI(int h, int l, double p) {
        foret = new Foret(h, l);
        foret.terrain[0][0].prendsFeu(); // On met le premier arbre en feu pour lancer la simulation

        setTitle("Simulation de Forêt");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dessinPanel = new DessinPanel();
        getContentPane().add(dessinPanel, BorderLayout.CENTER);

        bouton = new JButton("Actualiser");
        bouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                foret.actualiser(p); // Par exemple, 20% de probabilité de propagation du feu
                dessinPanel.repaint();
            }
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
                        List<int[]> directions = foret.directionsPossibles(i, j, 1.0);
                        for (int[] direction : directions) {
                            drawArrow(g, j * (cellSize + spacing), i * (cellSize + spacing), direction[1] * (cellSize + spacing), direction[0] * (cellSize + spacing), cellSize);
                        }
                    }
                }
            }
        }

        private void drawArrow(Graphics g, int x1, int y1, int x2, int y2, int cellSize) {
            int arrowSize = 10;
            int dx = x2 - x1;
            int dy = y2 - y1;
            double D = Math.sqrt(dx*dx + dy*dy);
            double xm = x1 + (cellSize / 2.0) + (dx / D) * (cellSize / 2.0);
            double ym = y1 + (cellSize / 2.0) + (dy / D) * (cellSize / 2.0);
            double xn = xm;
            double yn = ym;
            double x;
            double sin = dy / D;
            double cos = dx / D;
        
            x = xm * cos - ym * sin;
            ym = xm * sin + ym * cos;
            xm = x;
        
            x = xn * cos - yn * sin;
            yn = xn * sin + yn * cos;
            xn = x;
        
            int[] xpoints = {x2, (int) xm, (int) xn};
            int[] ypoints = {y2, (int) ym, (int) yn};
        
            g.drawLine(x1, y1, x2, y2);
            g.fillPolygon(xpoints, ypoints, 3);
        }
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ForetGUI(20, 20,0.5).setVisible(true);
            }
        });
    }
}
class Arbre{
    String etat;

    public Arbre(){
        this.etat = "o"; 
        // o : arbre vivant |  . : arbre mort  |  X : arbre en feu
        // jsp si on peut faire de l'affichage avec ca on verra bien
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

    List<int[]> arbresEnFeu() { //renvoie la liste des arbres en feu ()
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

    public List<int[]> directionsPossibles(int i, int j, double p) {
        List<int[]> directions = new ArrayList<>();

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

    public void afficher(){  //jsp si ca a du sens de faire ca en java mais au moins c'est fait
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

            // propagation du feu a faire gaffe si au bord de la foret
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

}

