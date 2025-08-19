package sn.unchk.bibliotheque.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;



// ================= NOTIFICATION DTO =================
public class NotificationDTO {

    private Long id;

    @NotBlank(message = "Le contenu de la notification est obligatoire")
    @Size(max = 2000, message = "Le contenu ne peut pas dépasser 2000 caractères")
    private String contenu;

    private LocalDate dateEnvoi;

    @NotNull(message = "L'utilisateur est obligatoire")
    @Positive(message = "L'ID de l'utilisateur doit être positif")
    private Long utilisateurId;

    private Boolean lue;
    private String nomUtilisateur; // Pour l'affichage

    // Constructeurs et getters/setters
    public NotificationDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDate getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDate dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public Long getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Long utilisateurId) { this.utilisateurId = utilisateurId; }

    public Boolean getLue() { return lue; }
    public void setLue(Boolean lue) { this.lue = lue; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

}
