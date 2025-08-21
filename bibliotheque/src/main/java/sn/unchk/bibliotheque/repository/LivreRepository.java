package sn.unchk.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.unchk.bibliotheque.entity.Auteur;
import sn.unchk.bibliotheque.entity.Livre;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {
    // Recherche par titre (insensible à la casse)
    List<Livre> findByTitreContainingIgnoreCase(String titre);

    // Recherche par genre
    List<Livre> findByGenreContainingIgnoreCase(String genre);

    // Recherche par auteur
    List<Livre> findByAuteur(Auteur auteur);

    // Recherche par nom d'auteur
    List<Livre> findByAuteur_NomContainingIgnoreCase(String nomAuteur);

    // Livres disponibles
    List<Livre> findByDisponible(Boolean disponible);

    // Livres disponibles par genre
    List<Livre> findByDisponibleAndGenreContainingIgnoreCase(Boolean disponible, String genre);

    // Livres publiés dans une période
    List<Livre> findByDatePublicationBetween(LocalDate debut, LocalDate fin);

    // Recherche multi-critères
    @Query("SELECT l FROM Livre l WHERE " +
            "(:titre IS NULL OR LOWER(l.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) AND " +
            "(:auteur IS NULL OR LOWER(l.auteur.nom) LIKE LOWER(CONCAT('%', :auteur, '%'))) AND " +
            "(:genre IS NULL OR LOWER(l.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) AND " +
            "(:disponible IS NULL OR l.disponible = :disponible)")
    List<Livre> rechercherLivres(@Param("titre") String titre,
                                 @Param("auteur") String auteur,
                                 @Param("genre") String genre,
                                 @Param("disponible") Boolean disponible);

    // Livres les plus empruntés
    @Query("SELECT l, COUNT(e) as nbEmprunts FROM Livre l LEFT JOIN l.emprunts e " +
            "GROUP BY l ORDER BY COUNT(e) DESC")
    List<Object[]> findLivresLesPlusEmpruntes();

    // Genres distincts
    @Query("SELECT DISTINCT l.genre FROM Livre l WHERE l.genre IS NOT NULL ORDER BY l.genre")
    List<String> findDistinctGenres();

    List<Livre> findByDisponibleTrue();
}
