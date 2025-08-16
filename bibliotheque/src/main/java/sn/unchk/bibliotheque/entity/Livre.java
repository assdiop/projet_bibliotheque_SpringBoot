package sn.unchk.bibliotheque.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "livres")
public class Livre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(length = 100)
    private String genre;

    @Column(name = "date_publication")
    private LocalDate datePublication;

    @Column(nullable = false)
    private Boolean disponible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private Auteur auteur;

    @OneToMany(mappedBy = "livre", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Emprunt> emprunts = new ArrayList<>();

    // Constructeurs
    public Livre() {}

    public Livre(String titre, String genre, LocalDate datePublication, Auteur auteur) {
        this.titre = titre;
        this.genre = genre;
        this.datePublication = datePublication;
        this.auteur = auteur;
        this.disponible = true;
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

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public Auteur getAuteur() { return auteur; }
    public void setAuteur(Auteur auteur) { this.auteur = auteur; }

    public List<Emprunt> getEmprunts() { return emprunts; }
    public void setEmprunts(List<Emprunt> emprunts) { this.emprunts = emprunts; }

    // Méthodes utiles
    public boolean estDisponible() {
        return this.disponible != null && this.disponible;
    }

    public void marquerCommeEmprunte() {
        this.disponible = false;
    }

    public void marquerCommeDisponible() {
        this.disponible = true;
    }
}

