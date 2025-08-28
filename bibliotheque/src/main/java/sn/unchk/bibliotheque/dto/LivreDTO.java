package sn.unchk.bibliotheque.dto;
import jakarta.validation.constraints.*;


import java.time.LocalDate;

// ================= LIVRE DTO =================
public class LivreDTO {

    private Long id;

    @NotBlank(message = "Le titre du livre est obligatoire")
    @Size(min = 1, max = 200, message = "Le titre doit contenir entre 1 et 200 caractères")
    private String titre;

    @Size(max = 100, message = "Le genre ne peut pas dépasser 100 caractères")
    private String genre;

    @PastOrPresent(message = "La date de publication ne peut pas être dans le futur")
    private LocalDate datePublication;

    // Support pour les deux formats : ID ou nom de l'auteur
    private Long auteurId;

    @Size(min = 2, max = 150, message = "Le nom de l'auteur doit contenir entre 2 et 150 caractères")
    private String nomAuteur;

    private Boolean disponible;

    // Constructeurs
    public LivreDTO() {}

    public LivreDTO(String titre, String genre, LocalDate datePublication, Long auteurId) {
        this.titre = titre;
        this.genre = genre;
        this.datePublication = datePublication;
        this.auteurId = auteurId;
    }

    public LivreDTO(String titre, String genre, LocalDate datePublication, String nomAuteur) {
        this.titre = titre;
        this.genre = genre;
        this.datePublication = datePublication;
        this.nomAuteur = nomAuteur;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public LocalDate getDatePublication() { return datePublication; }
    public void setDatePublication(LocalDate datePublication) { this.datePublication = datePublication; }

    public Long getAuteurId() { return auteurId; }
    public void setAuteurId(Long auteurId) { this.auteurId = auteurId; }

    public String getNomAuteur() { return nomAuteur; }
    public void setNomAuteur(String nomAuteur) { this.nomAuteur = nomAuteur; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    // Méthodes utilitaires
    public boolean hasAuteurId() { return auteurId != null; }
    public boolean hasNomAuteur() { return nomAuteur != null && !nomAuteur.trim().isEmpty(); }
    public boolean hasAuteurInfo() { return hasAuteurId() || hasNomAuteur(); }

    @Override
    public String toString() {
        return "LivreDTO{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", genre='" + genre + '\'' +
                ", datePublication=" + datePublication +
                ", auteurId=" + auteurId +
                ", nomAuteur='" + nomAuteur + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}
