package anthony.com.smsmmsbomber.model.wsbeans;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.Constants;
import anthony.com.smsmmsbomber.utils.exceptions.TechnicalException;

public class GenericAnswerBean {

    private AnswerStatusBean status;
    private ArrayList<String> error;

    public void checkError(String nomRequete) throws TechnicalException {
        if (status == null) {
            throw new TechnicalException(nomRequete + " : status vide");
        }
        else if (status.getCode() == Constants.SERVEUR_CODE_ERROR) {

            if (error != null && !error.isEmpty()) {
                throw new TechnicalException(error.get(0));
            }
            else {
                throw new TechnicalException(nomRequete + " : Erreur serveur");
            }
        }
        else if (status.getCode() != 200) {
            throw new TechnicalException("Erreur serveur lors de la réponse du " + nomRequete);
        }
    }

    public String getErrorMessage() {
        if (error != null && !error.isEmpty()) {
            return error.get(0);
        }
        else {
            return "Aucun erreur transmise";
        }
    }

    public AnswerStatusBean getStatus() {
        return status;
    }

    public void setStatus(AnswerStatusBean status) {
        this.status = status;
    }

    public ArrayList<String> getError() {
        return error;
    }

    public void setError(ArrayList<String> error) {
        this.error = error;
    }
}
