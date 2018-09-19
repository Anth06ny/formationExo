package anthony.com.smsmmsbomber;

public class Constants {

    //Code en dur  pour la premiere url
    public static final String URL_SERVER_CONSOLE = "https://console.push-sms.co/API/";

    public static final String URL_GET_IP = "https://api.ipify.org";
    //Delai d'action du service
    public static final int DELAI_SERVICE = 10000;

    //Code Erreur du serveur
    public static final int SERVEUR_CODE_ERROR = 550;

    //Delai de suppression des broadcast non utilisé
    public static final int DELETE_BR_DELAY = 1000 * 60 * 20;

    public static final int DELAY_MMS_ERROR = 2; //temps en minute  a partir duquel un mms est concidéré en erreur s'il n'a pas transmis la valeur
}
