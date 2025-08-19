package sn.unchk.bibliotheque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sn.unchk.bibliotheque.entity.Notification;
import sn.unchk.bibliotheque.entity.Utilisateur;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Notifications par utilisateur
    List<Notification> findByUtilisateur(Utilisateur utilisateur);

    // Notifications par utilisateur triées par date
    List<Notification> findByUtilisateurOrderByDateEnvoiDesc(Utilisateur utilisateur);

    // Notifications non lues d'un utilisateur
    List<Notification> findByUtilisateurAndLue(Utilisateur utilisateur, Boolean lue);

    // Notifications non lues par ID utilisateur
    List<Notification> findByUtilisateur_IdAndLue(Long utilisateurId, Boolean lue);

    // Compter les notifications non lues
    int countByUtilisateurAndLue(Utilisateur utilisateur, Boolean lue);

    // Notifications dans une période
    List<Notification> findByDateEnvoiBetween(LocalDate debut, LocalDate fin);

    // Marquer toutes les notifications d'un utilisateur comme lues
    @Query("UPDATE Notification n SET n.lue = true WHERE n.utilisateur = :utilisateur AND n.lue = false")
    void marquerToutesCommeLues(@Param("utilisateur") Utilisateur utilisateur);

    // Supprimer les anciennes notifications lues
    @Query("DELETE FROM Notification n WHERE n.lue = true AND n.dateEnvoi < :dateLimite")
    void supprimerAnciennesNotificationsLues(@Param("dateLimite") LocalDate dateLimite);

    // Notifications récentes (X derniers jours)
    @Query("SELECT n FROM Notification n WHERE n.dateEnvoi >= :dateDebut ORDER BY n.dateEnvoi DESC")
    List<Notification> findNotificationsRecentes(@Param("dateDebut") LocalDate dateDebut);
}
