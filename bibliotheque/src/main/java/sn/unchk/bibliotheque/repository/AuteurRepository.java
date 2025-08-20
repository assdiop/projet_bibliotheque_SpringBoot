package sn.unchk.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sn.unchk.bibliotheque.entity.Auteur;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuteurRepository extends JpaRepository<Auteur, Long> {

    // Recherche par nom (insensible à la casse)
    List<Auteur> findByNomContainingIgnoreCase(String nom);

    // Recherche exacte par nom
    Optional<Auteur> findByNomIgnoreCase(String nom);

    // Vérifier si un auteur existe
    boolean existsByNomIgnoreCase(String nom);

    // Auteurs nés dans une période
    List<Auteur> findByDateNaissanceBetween(LocalDate debut, LocalDate fin);

    // Auteurs ayant écrit au moins un livre
    @Query("SELECT DISTINCT a FROM Auteur a WHERE SIZE(a.livres) > 0")
    List<Auteur> findAuteursAvecLivres();

    // Compter le nombre de livres par auteur
    @Query("SELECT a, SIZE(a.livres) FROM Auteur a ORDER BY SIZE(a.livres) DESC")
    List<Object[]> findAuteursAvecNombreLivres();
}
