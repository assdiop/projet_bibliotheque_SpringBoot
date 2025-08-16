package sn.unchk.bibliotheque.enums;

public enum StatutEmprunt {
    EN_COURS("En cours"),
    RETOURNE("Retourné");

    private final String libelle;

    StatutEmprunt(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

}
