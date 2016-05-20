import generated.ClientType;
import generated.DadesType;
import generated.EmpleatType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.Serializable;
import java.util.Scanner;

/**
 * Created by Mat on 20/05/2016.
 */
public class DaoDao implements Serializable{

    private static final File dadesXmlFitxer = new File("dades.xml");
    private static DadesType dadesType;
    private static JAXBContext jaxbContext;


    /**
     * Configuració jaxB
     */
    public static void jaxbInit(){

        try {
            jaxbContext = JAXBContext.newInstance(DadesType.class);
            Unmarshaller UMS = jaxbContext.createUnmarshaller();
            dadesType = (DadesType) UMS.unmarshal(dadesXmlFitxer);
        } catch (JAXBException e) {
        }

    }

    /**
     * afeguirEmpleat Metode per afeguir clients a xml
     */
    public static void afeguirEmpleat() throws JAXBException {

        Scanner input = new Scanner(System.in);

        System.out.println("Id del empleat: ");
        String id= input.nextLine();
        System.out.println("Nom del empleat: ");
        String nom = input.nextLine();
        System.out.println("Cognom del empleat: ");
        String cognom = input.nextLine();
        System.out.println("Sou del empleat: ");
        String sou = input.nextLine();
        System.out.println("Anys treballats: ");
        String anys_treballats = input.nextLine();


        EmpleatType empleatType = new EmpleatType();

        empleatType.setNom(nom);
        empleatType.setId(id);
        empleatType.setCognom(cognom);
        empleatType.setSou(sou);
        empleatType.setAnysTreballats(anys_treballats);
        dadesType.getEmpleats().getEmpleat().add(empleatType);

        Marshaller MS = jaxbContext.createMarshaller();
        MS.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        MS.marshal(dadesType, dadesXmlFitxer);

    }

    /**
     * Afegueix clients a la bbdd
     * @throws JAXBException
     */
    public static void afeguirClients() throws JAXBException {

        Scanner input = new Scanner(System.in);

        System.out.println("Dni del client:");
        String dni= input.nextLine();
        System.out.println("Nom del client:");
        String nom = input.nextLine();
        System.out.println("Cognom del client:");
        String cognom = input.nextLine();


        ClientType clientType = new ClientType();
        clientType.setDni(dni);
        clientType.setNom(nom);
        clientType.setCognom(cognom);

        dadesType.getClients().getClient().add(clientType);

        Marshaller MS = jaxbContext.createMarshaller();
        MS.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        MS.marshal(dadesType, dadesXmlFitxer);
    }

    /***
     * elimina clients de la bbdd
     * @return boolean true-eliminat false-no eliminat
     */
    public static boolean eliminarClients(){
        boolean eliminat = false;
        Scanner input = new Scanner(System.in);
        System.out.println("Dni del client  : ");
        String dni= input.nextLine();

        for(int i = 0; i < dadesType.getClients().getClient().size(); i++ ){
            if(dadesType.getClients().getClient().get(i).getDni().equals(dni)){
                ClientType clientType = dadesType.getClients().getClient().get(i);
                dadesType.getClients().getClient().remove(clientType);
                eliminat=true;
                break;
            }
        }
        return eliminat;
    }

    /**
     *  eliminar un empleat de la bbdd
     * @return boolean true si ha estat eliminat algun dels empleats false en cas contrari
     */
    public static boolean eliminarEmpleat(){
        boolean eliminat = false;
        Scanner input = new Scanner(System.in);
        System.out.println("Id del empleat: ");
        String id= input.nextLine();
        for(int cont = 0; cont < dadesType.getEmpleats().getEmpleat().size(); cont++ ){

            if(dadesType.getEmpleats().getEmpleat().get(cont).getId().equals(id)){
                EmpleatType empleatType = dadesType.getEmpleats().getEmpleat().get(cont);
                dadesType.getEmpleats().getEmpleat().remove(empleatType);
                System.out.println("remove ok");
                eliminat = true;

                break;
            }
        }
        return eliminat;
    }

    /**
     * Metode per consultar factures d'un client.
     */
    public static void consultarFacturesDeUnClient(){
        System.out.println("Factures de clients:\n");
        System.out.println("Dni del client: ");
        Scanner input = new Scanner(System.in);

        String dni = input.nextLine();
        System.out.println("Factures de " + dni + ":");
        for(int cont = 0; cont < dadesType.getFactures().getFactura().size(); cont++){
            if(dadesType.getFactures().getFactura().get(cont).getDniClient().equals(dni)){
                System.out.println("Article: " + dadesType.getFactures().getFactura().get(cont).getIdProducte()
                        + "\nPreu unitat: " + dadesType.getFactures().getFactura().get(cont).getPreuUnitat()
                        +"\nPreu total:" + dadesType.getFactures().getFactura().get(cont).getPreuTotal()
                        +"\niva " + dadesType.getFactures().getFactura().get(cont).getIva() + " %");
            }
        }
    }

    /**
     * consulta dades d'empleats
     */
    public static void consultarEmpleats(){
        Scanner input = new Scanner(System.in);
        System.out.println("\t1-Consulta per id");
        System.out.println("\t2-Consulta per anys treballats");
        System.out.println("\t3-Consulta per sou");

        String menu = input.nextLine();
        switch (menu){
            case "1":
                System.out.println("\nEntra id:");
                String id = input.nextLine();
                for(int cont = 0; cont < dadesType.getEmpleats().getEmpleat().size(); cont++ ){
                    if(dadesType.getEmpleats().getEmpleat().get(cont).getId().equals(id)){
                        System.out.println(" nom :" + dadesType.getEmpleats().getEmpleat().get(cont).getNom() + " "
                                + dadesType.getEmpleats().getEmpleat().get(cont).getCognom()
                                +" sou: " + dadesType.getEmpleats().getEmpleat().get(cont).getSou()
                                +" anys treballats : " + dadesType.getEmpleats().getEmpleat().get(cont).getAnysTreballats());
                        break;
                    }
                }
                break;

            case "2":
                System.out.println("\nEntra anys treballats:");
                String anysTreballats = input.nextLine();
                for(int cont = 0; cont < dadesType.getEmpleats().getEmpleat().size(); cont++ ){

                    if(dadesType.getEmpleats().getEmpleat().get(cont).getAnysTreballats().equals(anysTreballats)){
                        System.out.println(dadesType.getEmpleats().getEmpleat().get(cont).getId()
                                +" nom :" + dadesType.getEmpleats().getEmpleat().get(cont).getNom() + " "
                                + dadesType.getEmpleats().getEmpleat().get(cont).getCognom()
                                + " sou: " + dadesType.getEmpleats().getEmpleat().get(cont).getSou());

                        break;
                    }
                }
                break;

            case "3":
                System.out.println("\nEntra sou:");
                String sou = input.nextLine();
                for(int cont = 0; cont < dadesType.getEmpleats().getEmpleat().size(); cont++ ){

                    if(dadesType.getEmpleats().getEmpleat().get(cont).getSou().equals(sou)){
                        System.out.println(dadesType.getEmpleats().getEmpleat().get(cont).getId()
                                + " nom :" + dadesType.getEmpleats().getEmpleat().get(cont).getNom() + " "
                                + dadesType.getEmpleats().getEmpleat().get(cont).getCognom()
                                + " anys treballats: " + dadesType.getEmpleats().getEmpleat().get(cont).getAnysTreballats());
                        break;
                    }
                }
                break;

            default:
                System.out.println("...opció inexistent");
                break;
        }
    }


}
