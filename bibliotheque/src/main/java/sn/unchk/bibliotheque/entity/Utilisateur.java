package sn.unchk.bibliotheque.entity;

import jakarta.persistence.*;
import sn.unchk.bibliotheque.enums.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//import jakarta.validation.constraints.*;


// ================= USER ENTITY =================
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String motDePasse;

    @Column(nullable = false)
    private LocalDate dateInscription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    //LES RELATIONS ENTRE LES TABLES
    //FetchType.LAZY Évite de charger inutilement les données de l'utilisateur
    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Emprunt> emprunts = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();


    // Constructeurs
    public Utilisateur() {
        this.dateInscription = LocalDate.now();
        this.role = Role.LECTEUR;
    }

    public Utilisateur(String nom, String email, String motDePasse) {
        this();
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public List<Emprunt> getEmprunts() { return emprunts; }
    public void setEmprunts(List<Emprunt> emprunts) { this.emprunts = emprunts; }

    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }
}

