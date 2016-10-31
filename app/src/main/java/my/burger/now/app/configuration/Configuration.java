package my.burger.now.app.configuration;

/**
 * Created by Jack on 19/02/2016.
 */
public class Configuration {
    //tout les donnée static
    /*public static String IPWEB = "https://dev.myburgernow.com";
    public static String IPSAILS = "https://www.myburgernow.com:1339";*/
    public static String IPWEB = "https://www.myburgernow.com";
    public static String IPSAILS = "https://www.myburgernow.com:1337";



    //Les etapes de livraisons

    public static int EN_ATTENTE = 0;
    public static int EN_ATTENTE_PAYEMANT = 1;
    public static int EN_VERS_RESTAURANT = 2;
    public static int EN_VERS_CLIENT = 3;
    public static int ARRIVE_A_DESTINATION = 4;



}
