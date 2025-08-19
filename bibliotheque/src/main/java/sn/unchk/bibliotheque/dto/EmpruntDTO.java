package sn.unchk.bibliotheque.dto;
import jakarta.validation.constraints.*;
import sn.unchk.bibliotheque.enums.StatutEmprunt;


import java.time.LocalDate;


// ================= EMPRUNT DTO =================
public class EmpruntDTO {

    private Long id;

    @NotNull(message = "L'utilisateur est obligatoire")
    @Positive(message = "L'ID de l'utilisateur doit être positif")
    private Long utilisateurId;

    @NotNull(message = "Le livre est obligatoire")
    @Positive(message = "L'ID du livre doit être positif")
    private Long livreId;

    private LocalDate dateEmprunt;
    private LocalDate dateRetour;
    private StatutEmprunt statut;

    // Champs pour l'affichage
    private String nomUtilisateur;
    private String titreLivre;
    private String nomAuteur;
    private LocalDate dateLimiteRetour;
    private boolean enRetard;
    private long nombreJoursRetard;

    //Contruteur
    public EmpruntDTO(){}

    public EmpruntDTO(Long utilisateurId, Long livreId) {
        this.utilisateurId = utilisateurId;
        this.livreId = livreId;
    }

    //Getter & Setter

    public Long getUtilisateurId() {
        return utilisateurId;
    }

    public void setUtilisateurId(Long utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    public Long getLivreId() {
        return livreId;
    }

    public void setLivreId(Long livreId) {
        this.livreId = livreId;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public StatutEmprunt getStatut() {
        return statut;
    }

    public void setStatut(StatutEmprunt statut) {
        this.statut = statut;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getTitreLivre() {
        return titreLivre;
    }

    public void setTitreLivre(String titreLivre) {
        this.titreLivre = titreLivre;
    }

    public String getNomAuteur() {
        return nomAuteur;
    }

    public void setNomAuteur(String nomAuteur) {
        this.nomAuteur = nomAuteur;
    }

    public LocalDate getDateLimiteRetour() {
        return dateLimiteRetour;
    }

    public void setDateLimiteRetour(LocalDate dateLimiteRetour) {
        this.dateLimiteRetour = dateLimiteRetour;
    }

    public boolean isEnRetard() {
        return enRetard;
    }

    public void setEnRetard(boolean enRetard) {
        this.enRetard = enRetard;
    }

    public long getNombreJoursRetard() {
        return nombreJoursRetard;
    }

    public void setNombreJoursRetard(long nombreJoursRetard) {
        this.nombreJoursRetard = nombreJoursRetard;
    }
}
