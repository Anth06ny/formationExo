package anthony.com.ultimatewarmup;

/**
 * Created by Anthony on 04/04/2018.
 */

public enum ExerciceBean {

    Mollet("Mollet", "Mollet", "Monter sur la pointe des pieds", MainActivity.DUREE, MainActivity.NB_REPETITION, "Pied droit", "Pied gauche"),
    Quadriceps("Quadriceps", "Quadriceps", "Monter sur un pied de la position accroupie à debout ", MainActivity.DUREE, MainActivity.NB_REPETITION, "Pied droit", "Pied gauche"),
    EsquioConcentrique("Esquio", "Concentrique esquio", "On replie le bas de la jambe", MainActivity.DUREE, 10, "Jambe droite", "Jambe gauche"),
    FessierConcentrique("Fessier", "Concentrique fessier", "On lève toute la jambe", MainActivity.DUREE, MainActivity.NB_REPETITION, "Jambe droite", "Jambe gauche"),
    EsquioExcentrique("Esquio", "Excentrique esquio", "On déplie le bas de la jambe", MainActivity.DUREE, MainActivity.NB_REPETITION, "Jambe droite", "Jambe gauche"),
    FessierExcentrique("Fessier", "Excentrique fessier", "On descend toute la jambe", MainActivity.DUREE, MainActivity.NB_REPETITION, "Jambe droite", "Jambe gauche"),
    Abduceteur("Abducteur", "Abducteur", "On rapproche les jambes en créant une opposition", MainActivity.DUREE, 10, null, null),
    Psoas("Psoas", "Psoas", "Coup de genou sans toucher sa main", MainActivity.DUREE, MainActivity.NB_REPETITION, "Genou droit", "Genou gauche");

    public String muscle;
    public String textCheckBox;
    public String textExplication;
    public int tempsEnS;
    public int nbRepetition;

    public String nomCote1;
    public String nomCote2;

    ExerciceBean(String muscle, String textCheckBox, String textExplication, int tempsEnS, int nbRepetition, String nomCote1, String nomCote2) {
        this.muscle = muscle;
        this.textCheckBox = textCheckBox;
        this.textExplication = textExplication;
        this.tempsEnS = tempsEnS;
        this.nbRepetition = nbRepetition;
        this.nomCote1 = nomCote1;
        this.nomCote2 = nomCote2;
    }
}
