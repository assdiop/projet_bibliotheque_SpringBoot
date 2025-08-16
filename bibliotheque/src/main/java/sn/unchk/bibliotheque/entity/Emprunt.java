package sn.unchk.bibliotheque.entity;

import jakarta.persistence.*;
import sn.unchk.bibliotheque.enums.StatutEmprunt;

import java.time.LocalDate;

@Entity
@Table(name = "emprunts") // mapping de la table "emprunts"
public class Emprunt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_emprunt", nullable = false)
    private LocalDate dateEmprunt;

    @Column(name = "date_retour")
    private LocalDate dateRetour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livre_id", nullable = false)
    private Livre livre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEmprunt statut;

    // Constructeurs
    public Emprunt() {
        this.dateEmprunt = LocalDate.now();
        this.statut = StatutEmprunt.EN_COURS;
    }

    public Emprunt(Utilisateur utilisateur, Livre livre) {
        this();
        this.utilisateur = utilisateur;
        this.livre = livre;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; }

    public LocalDate getDateRetour() { return dateRetour; }
    public void setDateRetour(LocalDate dateRetour) { this.dateRetour = dateRetour; }

    public Utilisateur getUtilisateur() { return utilisateur; }
    public void setUtilisateur(Utilisateur utilisateur) { this.utilisateur = utilisateur; }

    public Livre getLivre() { return livre; }
    public void setLivre(Livre livre) { this.livre = livre; }

    public StatutEmprunt getStatut() { return statut; }
    public void setStatut(StatutEmprunt statut) { this.statut = statut; }

    // Méthodes métier
    public LocalDate getDateLimiteRetour() {
        return this.dateEmprunt.plusWeeks(2); // 2 semaines d'emprunt
    }

    public boolean estEnRetard() {
        return this.statut == StatutEmprunt.EN_COURS &&
                LocalDate.now().isAfter(getDateLimiteRetour());
    }

    public void retourner() {
        this.dateRetour = LocalDate.now();
        this.statut = StatutEmprunt.RETOURNE;
        if (this.livre != null) {
            this.livre.marquerCommeDisponible();
        }
    }

    public long getNombreJoursRetard() {
        if (!estEnRetard()) {
            return 0;
        }
        return getDateLimiteRetour().until(LocalDate.now()).getDays();
    }
}
