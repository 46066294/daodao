import net.xqj.exist.ExistXQDataSource;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XPathQueryService;
import org.xmldb.api.modules.XQueryService;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Mat on 20/05/2016.
 */
public class DbDao implements Serializable{

    //private static final long serialVersionUID = 1L;
    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";


    private XQDataSource xqs;
    private XQConnection xconn;

    private String ip;
    private String port;
    private String user;
    private String pass;

    private String path;

    private FileWriter fileWriter;
    private String log;

    /**
     * Constructor sense parametres
     */
    public DbDao() {
    }

    /**
     * Constructor amb parametres
     *
     * @param ip Ip del servidor
     * @param port Port del servidor
     * @param user
     * @param password
     */
    public DbDao(String ip, String port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pass = password;
    }



    /**
     * Obre la conexio de la bbdd
     *
     * @return 	true-oberta   false-tancada
     *
     */
    public boolean openConn() {

        setURI(ip, port);
        xqs = new ExistXQDataSource();

        try {
            fileWriter = new FileWriter("DAOexistDB.log");
            xqs.setProperty("serverName", ip);
            xqs.setProperty("port", port);
            xconn = xqs.getConnection();
            log = "Conexió establerta a ip: " + ip + ":" + port;
            escriuLog(log);
            return true;

        } catch (XQException e) {
            log = "...error a l'obrir la connexió" + e;
            escriuLog(log);
            return false;

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Configuracio del path per a la bbdd
     *
     * @param ip Ip de la conexio
     * @param port 	Port de la conexio
     */
    private void setURI(String ip, String port) {
        this.path = "xmldb:exist://" + ip + ":" + port + "/exist/xmlrpc";
    }

    /**
     * Tanca la conexió
     */
    public boolean closeConexion() throws IOException {
        try {
            xconn.close();
            log = "...conexio tancada";
            escriuLog(log);
            fileWriter.close();

            return true;

        } catch (XQException e) {
            log = "...error tancant la conexio" + e;
            escriuLog(log);
            fileWriter.close();

            return false;
        }
    }

    /**
     * Configuracio del log
     *
     * @param log missatge a grabar
     *
     */
    private void escriuLog(String log) {
        System.out.println(this.log);
        try {
            fileWriter.write(this.log + "\n");
        } catch (IOException e) {
            System.out.println("...error log: " + e);
        }
    }

    /**
     * Crea la coleccio
     *
     *
     * @param coleccion  Nom de la coleccio
     * @return 	True-creada
     *
     */
    public boolean creaColection(String coleccion) throws XMLDBException, IllegalAccessException, InstantiationException {

        try {
            Class<?> cl = Class.forName(DRIVER);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            Collection col = DatabaseManager.getCollection(path + "/db",user, pass);

            if (col == null) {
                log = "coleccio inexistent";
                escriuLog(log);

                return false;
            }

            CollectionManagementService collectionManagementService =
                    (CollectionManagementService) col.getService("CollectionManagementService", "1.0");
            collectionManagementService.createCollection(coleccion);
            log ="Col·lecció: " + coleccion + " creada";
            escriuLog(log);

            return true;

        } catch (ClassNotFoundException e) {
            log ="...no s'ha pogut crear la col·lecció" + e;
            escriuLog(log);
            return false;
        }
    }

    /**
     * Elimina una coleccio existent
     *
     * @param coleccion  coleccio a eliminar
     * @return 	True-eliminada
     *
     */
    public boolean deleteColeccion(String coleccion) throws XMLDBException, IllegalAccessException, InstantiationException {

        try {
            Class<?> cl = Class.forName(DRIVER);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            Collection col = DatabaseManager.getCollection(path + "/db",user, pass);

            if (col == null) {
                log ="No existeix la coleccio";
                escriuLog(log);
                return false;
            }

            CollectionManagementService colService = (CollectionManagementService) col.getService("CollectionManagementService", "1.0");
            colService.removeCollection(coleccion);
            log ="...eliminada la coleccio:" + coleccion;
            escriuLog(log);

            return true;

        } catch (ClassNotFoundException e) {
            log ="No s'ha borrat la col·leccio " + e;
            escriuLog(log);
            return false;
        }
    }


    public boolean afegeixRecurs(String coleccion, String recurs) throws XMLDBException, IllegalAccessException, InstantiationException {

        try {
            Class<?> cl = Class.forName(DRIVER);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            File fichero = new File(recurs);
            Collection col = DatabaseManager.getCollection(this.path + "/db/" + coleccion,user, pass);

            if (col == null) {
                log ="no existeix la col·leccio";
                escriuLog(log);
                return false;
            }

            Resource recurso = col.createResource(recurs, "XMLResource");
            recurso.setContent(fichero);
            col.storeResource(recurso);
            log ="Recurs modificat: " + recurs;
            escriuLog(log);

            return true;

        } catch (ClassNotFoundException e) {
            log ="Error al crear el recurs " + e;
            escriuLog(log);
            return false;
        }

    }


    public boolean eliminaRecurs(String coleccion, String urlRecurso) throws XMLDBException, IllegalAccessException, InstantiationException {

        try {
            Class<?> cl = Class.forName(DRIVER);
            Database database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            Collection col = DatabaseManager.getCollection(path + "/db/" + coleccion,user, pass);

            if (col == null) {
                log ="no existeix la col·leccio";
                escriuLog(log);
                return false;
            }

            Resource recurso = col.getResource(urlRecurso);
            col.removeResource(recurso);
            log ="Eliminat el recurs: " + urlRecurso;
            escriuLog(log);
            return true;

        } catch (ClassNotFoundException e) {
            return false;
        }

    }

    /**
     * consulta XPATH
     *
     * @param colec
     * @param xPath
     * @return
     *
     */
    public String consultaXPATH(String colec, String xPath) throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        String result = null;
        Collection col = null;

        try {
            Class<?> cl = Class.forName(DRIVER);
            Database database;
            database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            col = DatabaseManager.getCollection(path + "/db/" + colec,user, pass);

            if (col == null) {
                log = "Consulta mal feta o col·leccio inexistent";
                escriuLog(log);
                return (log);
            }

            XPathQueryService xp = (XPathQueryService) col.getService("XPathQueryService","1.0");
            ResourceSet rs = xp.query (xPath);

            ResourceIterator resourceIterator = rs.getIterator();
            if (resourceIterator.hasMoreResources()) {
                Resource res1;
                while (resourceIterator.hasMoreResources()) {
                    res1 = resourceIterator.nextResource();
                    result = result + res1.getContent() + "\n";
                }
                log = "XPATH:" + xPath ;
                escriuLog(log);

            } else {
                result =  ("...no hi han resultats");
                escriuLog(result);
            }

        } catch (XMLDBException e) {
            result = "...consulta xpath erronea" + e;
            escriuLog(result);
        }

        return result;

    }

    public String consultaXQUERY(String coleccion, String sentenciaXQUERY) throws IllegalAccessException, InstantiationException, ClassNotFoundException {

        String result = null;

        Collection col = null;

        try {

            Class<?> cl = Class.forName(DRIVER);
            Database database;
            database = (Database) cl.newInstance();
            database.setProperty("create-database", "true");
            DatabaseManager.registerDatabase(database);
            col = DatabaseManager.getCollection(path + "/db/" + coleccion, user, pass);

            if (col == null) {
                log = "no existe la coleccion indicada en la consulta XQUERY";
                escriuLog(log);
                return (log);
            }

            XQueryService xpqs = (XQueryService) col.getService("XQueryService","1.0");
            ResourceSet rs = xpqs.query (sentenciaXQUERY);

            // Comprobamos y mostramos resultado ejecucion consulta

            ResourceIterator resourceIterator = rs.getIterator();
            if (resourceIterator.hasMoreResources()) {
                Resource res1;
                while (resourceIterator.hasMoreResources()) {
                    res1 = resourceIterator.nextResource();
                    result = result + (String)res1.getContent() + "\n";
                }
                log = "xQuery: " + sentenciaXQUERY;
                escriuLog(log);


            } else {
                result =  ("...sense resultats");
                escriuLog(result);
            }

        } catch (XMLDBException e) {
            result = "...error consulta xquery" + e;
            escriuLog(result);
        }

        return result;


    }

    //getters setters
    public XQDataSource getXqs() {
        return xqs;
    }

    public void setXqs(XQDataSource xqs) {
        this.xqs = xqs;
    }

    public XQConnection getXconn() {
        return xconn;
    }

    public void setXconn(XQConnection xconn) {
        this.xconn = xconn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setAdmin(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


}
