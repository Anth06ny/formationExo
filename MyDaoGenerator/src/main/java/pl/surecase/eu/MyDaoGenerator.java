package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(3, "greendao");

        //Table Eleve
        Entity eleve = schema.addEntity("Eleve");
        eleve.addIdProperty();
        eleve.addStringProperty("Nom");
        eleve.addStringProperty("Prenom");
        new DaoGenerator().generateAll(schema, args[0]);
    }
}
