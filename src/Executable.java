import org.xmldb.api.base.XMLDBException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Mat on 20/05/2016.
 */
public class Executable {

    static private DbDao dao;
    static  DaoDao dsd;
    public static void main(String[] args) throws JAXBException, XMLDBException, InstantiationException, IllegalAccessException, IOException {
        Scanner teclat = new Scanner (System.in);
        dao = new DbDao("localhost", "8080", "admin", "marc");
        dsd= new DaoDao();
        int menu = 9;

        dao.openConn();
        dsd.jaxbInit();
        dao.creaColection("dadesPracticaDaoDao");

        while(menu != 0) {

            System.out.println("\n--------MENU--------");
            System.out.println("1-Alta empleats");
            System.out.println("2-Alta clients");
            System.out.println("3-Eliminar empleats");
            System.out.println("4-Eliminar clients");
            System.out.println("5-Consultat empleats");
            System.out.println("6-Consultar factures d'un client");
            System.out.println("0-Fi del programa");

            menu = teclat.nextInt();
            switch (menu) {
                case 1:
                    dsd.afeguirEmpleat();
                    dao.afegeixRecurs("dadesPracticaDaoDao", "dades.xml");
                    break;

                case 2:
                    dsd.afeguirClients();
                    dao.afegeixRecurs("dadesPracticaDaoDao", "dades.xml");
                    break;

                case 3:
                    if (dsd.eliminarEmpleat()) {
                        System.out.println("...empleat eliminat");
                        dao.afegeixRecurs("dadesPracticaDaoDao", "dades.xml");
                    } else {
                        System.out.println("...empleat inexistent");
                    }
                    break;

                case 4:
                    if (dsd.eliminarClients()) {
                        System.out.println("...client eliminat");
                        dao.afegeixRecurs("dadesPracticaDaoDao", "dades.xml");
                    } else {
                        System.out.println("...client inexistent");
                    }
                    break;

                case 5:
                    dsd.consultarEmpleats();
                    break;

                case 6:
                    dsd.consultarFacturesDeUnClient();
                    break;

                case 7:
                    System.out.println("Fi del programa");
                    break;
            }
        }

        dao.closeConexion();

    }

}
