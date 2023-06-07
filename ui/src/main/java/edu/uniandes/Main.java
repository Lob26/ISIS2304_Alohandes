package edu.uniandes;

import edu.uniandes.business.AlohandesB;
import edu.uniandes.persistence.AlohandesP;
import edu.uniandes.view.AlohandesV;

public class Main {
    public static void main(String[] args)
            throws Exception {
        AlohandesP p = AlohandesP.getInstance();
        AlohandesB b = new AlohandesB(p);
        AlohandesV v = new AlohandesV(b);
        UIConfig.run(v);
        v.setVisible(true);
    }
}
