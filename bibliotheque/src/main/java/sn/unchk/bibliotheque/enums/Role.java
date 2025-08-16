package sn.unchk.bibliotheque.enums;

import lombok.Getter;

@Getter
public enum Role {

        ADMIN("Administrateur"),
        LECTEUR("Lecteur");

     //Champ et constructeur
        private final String libelle;

        Role(String libelle) {
            this.libelle = libelle;
        }

}
