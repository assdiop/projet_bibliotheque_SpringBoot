package sn.unchk.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.unchk.bibliotheque.entity.Emprunt;
import sn.unchk.bibliotheque.entity.Livre;
import sn.unchk.bibliotheque.entity.Utilisateur;
import sn.unchk.bibliotheque.enums.StatutEmprunt;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    // Emprunts par utilisateur
    List<Emprunt> findByUtilisateur(Utilisateur utilisateur);

    // Emprunts par utilisateur et statut
    List<Emprunt> findByUtilisateurAndStatut(Utilisateur utilisateur, StatutEmprunt statut);

    // Emprunts en cours d'un utilisateur
    List<Emprunt> findByUtilisateur_IdAndStatut(Long utilisateurId, StatutEmprunt statut);

    // Emprunts par livre
    List<Emprunt> findByLivre(Livre livre);

    // Emprunts par statut
    List<Emprunt> findByStatut(StatutEmprunt statut);

    // Emprunts en retard
    @Query("SELECT e FROM Emprunt e WHERE e.statut = :statut AND " +
            "e.dateEmprunt < :dateLimite")
    List<Emprunt> findEmpruntsEnRetard(@Param("statut") StatutEmprunt statut,
                                       @Param("dateLimite") LocalDate dateLimite);

    // Emprunts à rendre bientôt (dans les X jours)
    @Query("SELECT e FROM Emprunt e WHERE e.statut = :statut AND " +
            "e.dateEmprunt BETWEEN :dateDebut AND :dateFin")
    List<Emprunt> findEmpruntsARendreBientot(@Param("statut") StatutEmprunt statut,
                                             @Param("dateDebut") LocalDate dateDebut,
                                             @Param("dateFin") LocalDate dateFin);

    // Emprunts dans une période
    List<Emprunt> findByDateEmpruntBetween(LocalDate debut, LocalDate fin);

    // Vérifier si un livre est actuellement emprunté par un utilisateur
    @Query("SELECT COUNT(e) > 0 FROM Emprunt e WHERE e.utilisateur.id = :utilisateurId " +
            "AND e.livre.id = :livreId AND e.statut = :statut")
    boolean existsEmpruntActif(@Param("utilisateurId") Long utilisateurId,
                               @Param("livreId") Long livreId,
                               @Param("statut") StatutEmprunt statut);

    // Statistiques d'emprunts par mois
    @Query("SELECT YEAR(e.dateEmprunt), MONTH(e.dateEmprunt), COUNT(e) " +
            "FROM Emprunt e GROUP BY YEAR(e.dateEmprunt), MONTH(e.dateEmprunt) " +
            "ORDER BY YEAR(e.dateEmprunt) DESC, MONTH(e.dateEmprunt) DESC")
    List<Object[]> getStatistiquesEmpruntsParMois();

    // Nombre d'emprunts actifs par utilisateur
    @Query("SELECT e.utilisateur, COUNT(e) FROM Emprunt e WHERE e.statut = :statut " +
            "GROUP BY e.utilisateur")
    List<Object[]> getNombreEmpruntsActifsParUtilisateur(@Param("statut") StatutEmprunt statut);
}
