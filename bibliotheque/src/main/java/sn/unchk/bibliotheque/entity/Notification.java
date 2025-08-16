package sn.unchk.bibliotheque.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)   //Crée une colonne utilisateur_id (clé étrangère) liée à la table Utilisateur
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private Boolean lue = false;   // status de lecture

    // Constructeurs
    public Notification() {
        this.dateEnvoi = LocalDateTime.now();
        this.lue = false;
    }

    public Notification(String contenu, Utilisateur utilisateur) {
        this();
        this.contenu = contenu;
        this.utilisateur = utilisateur;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Boolean getLue() {
        return lue;
    }

    public void setLue(Boolean lue) {
        this.lue = lue;
    }

    // Méthodes utiles
    public void marquerCommeLue() {
        this.lue = true;
    }

    public boolean estNouvelle() {
        return !this.lue;
    }
}
