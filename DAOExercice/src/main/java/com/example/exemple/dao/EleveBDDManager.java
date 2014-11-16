package com.example.exemple.dao;

import com.example.exemple.bean.Eleve;

import java.util.ArrayList;

/**
 * Classe de gestion de la table eleve
 */
public class EleveBDDManager {

    public void insertOrUpdate(Eleve eleve) {
         //TODO insertion ou modification de l'eleve en base
    }

    public void clearEleve() {
    }

    public void deleteEleveWithId(long id) {
        //TODO suppression de l'eleve en base
    }

    public Eleve getEleveForId(long id) {
        //TODO
        return null;
    }

    public ArrayList<Eleve> getAllEleve() {
        //TODO retrouver la liste de l'ensemble des élèves
        return new ArrayList<Eleve>();
    }

}
