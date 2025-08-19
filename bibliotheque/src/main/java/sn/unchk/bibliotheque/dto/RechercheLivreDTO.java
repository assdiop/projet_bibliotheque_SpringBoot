package sn.unchk.bibliotheque.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;


// ================= RECHERCHE LIVRE DTO =================
public class RechercheLivreDTO {

    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;

    @Size(max = 150, message = "Le nom de l'auteur ne peut pas dépasser 150 caractères")
    private String auteur;

    @Size(max = 100, message = "Le genre ne peut pas dépasser 100 caractères")
    private String genre;

    private Boolean disponible;

    private LocalDate datePublicationDebut;
    private LocalDate datePublicationFin;

    //Contructeur
    public RechercheLivreDTO() {    }

    //Getter & Setter
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public LocalDate getDatePublicationDebut() {
        return datePublicationDebut;
    }

    public void setDatePublicationDebut(LocalDate datePublicationDebut) {
        this.datePublicationDebut = datePublicationDebut;
    }

    public LocalDate getDatePublicationFin() {
        return datePublicationFin;
    }

    public void setDatePublicationFin(LocalDate datePublicationFin) {
        this.datePublicationFin = datePublicationFin;
    }
}
