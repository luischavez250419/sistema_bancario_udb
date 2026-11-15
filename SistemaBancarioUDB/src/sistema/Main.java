package sistema;

import vistas.VentMenu;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args){
        
        SwingUtilities.invokeLater(() -> {
            VentMenu menu = new VentMenu();
            menu.setLocationRelativeTo(null);
            menu.setVisible(true);
        });   
    }
    
}
        