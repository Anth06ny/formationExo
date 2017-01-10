package com.example.anthony.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

@Entity()
public class Eleve  {

    private String nom;
    private String prenom;
    private boolean sexe;

    @Id(autoincrement = true)
    private Long id;
}
