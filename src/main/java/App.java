import javax.swing.JFrame;

public class App 
{
    public static void main( String[] args )
    {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;

        int width = columnCount * tileSize;
        int height = rowCount * tileSize;

        // Create a new window
        JFrame frame = new JFrame( "PacMan");

        frame.setResizable(false);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacMan packmanGame = new PacMan();
        frame.add(packmanGame);
        packmanGame.requestFocus();
        frame.setVisible(true);
    }
}
