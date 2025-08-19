package sn.unchk.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.unchk.bibliotheque.entity.Utilisateur;
import sn.unchk.bibliotheque.enums.Role;
import sn.unchk.bibliotheque.enums.StatutEmprunt;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Recherche par email (pour l'authentification)
    Optional<Utilisateur> findByEmail(String email);

    // Vérifier si un email existe déjà
    boolean existsByEmail(String email);

    // Recherche par nom (insensible à la casse)
    List<Utilisateur> findByNomContainingIgnoreCase(String nom);

    // Recherche par rôle
    List<Utilisateur> findByRole(Role role);

    // Utilisateurs inscrits dans une période
    List<Utilisateur> findByDateInscriptionBetween(LocalDate debut, LocalDate fin);

    // Utilisateurs avec des emprunts en cours
    @Query("SELECT DISTINCT u FROM Utilisateur u JOIN u.emprunts e WHERE e.statut = :statut")
    List<Utilisateur> findUtilisateursAvecEmprunts(@Param("statut") StatutEmprunt statut);
}
