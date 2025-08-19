package sn.unchk.bibliotheque.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
// ================= AUTEUR DTO =================
public class AuteurDTO {
    private Long id;

    @NotBlank(message = "Le nom de l'auteur est obligatoire")
    @Size(min = 2, max = 150, message = "Le nom doit contenir entre 2 et 150 caractères")
    private String nom;

    @Size(max = 2000, message = "La biographie ne peut pas dépasser 2000 caractères")
    private String biographie;

    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    //Contructeur
    public AuteurDTO(){ }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getBiographie() {
        return biographie;
    }

    public void setBiographie(String biographie) {
        this.biographie = biographie;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
}
