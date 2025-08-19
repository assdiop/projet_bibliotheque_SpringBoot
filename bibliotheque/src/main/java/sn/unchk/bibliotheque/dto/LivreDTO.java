package sn.unchk.bibliotheque.dto;
import jakarta.validation.constraints.*;


import java.time.LocalDate;

// ================= LIVRE DTO =================
public class LivreDTO {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 1, max = 200, message = "Le titre doit contenir entre 1 et 200 caractères")
    private String titre;

    @Size(max = 100, message = "Le genre ne peut pas dépasser 100 caractères")
    private String genre;

    @PastOrPresent(message = "La date de publication ne peut pas être dans le futur")
    private LocalDate datePublication;

    private Boolean disponible;

    @NotNull(message = "L'auteur est obligatoire")
    @Positive(message = "L'ID de l'auteur doit être positif")
    private Long auteurId;

    private String nomAuteur; // Pour l'affichage uniquement

    //Contruteur
    public LivreDTO(){ }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Long getAuteurId() {
        return auteurId;
    }

    public void setAuteurId(Long auteurId) {
        this.auteurId = auteurId;
    }

    public String getNomAuteur() {
        return nomAuteur;
    }

    public void setNomAuteur(String nomAuteur) {
        this.nomAuteur = nomAuteur;
    }
}
