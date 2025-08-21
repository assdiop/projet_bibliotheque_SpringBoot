package sn.unchk.bibliotheque.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.unchk.bibliotheque.dto.EmpruntDTO;
import sn.unchk.bibliotheque.entity.Emprunt;
import sn.unchk.bibliotheque.entity.Livre;
import sn.unchk.bibliotheque.entity.Utilisateur;
import sn.unchk.bibliotheque.enums.StatutEmprunt;
import sn.unchk.bibliotheque.repository.EmpruntRepository;
import sn.unchk.bibliotheque.repository.LivreRepository;
import sn.unchk.bibliotheque.repository.UtilisateurRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional

public class EmpruntService {

    @Autowired
    private EmpruntRepository empruntRepository;

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * Emprunter un livre par un utilisateur
     * @param utilisateurId L'ID de l'utilisateur
     * @param livreId L'ID du livre à emprunter
     * @return L'emprunt créé sous forme de DTO
     * @throws RuntimeException si l'utilisateur ou le livre n'existe pas, ou si le livre n'est pas disponible
     */
    public EmpruntDTO emprunterLivre(Long utilisateurId, Long livreId) {
        return emprunterLivre(utilisateurId, livreId, LocalDate.now());
    }

    /**
     * Emprunter un livre par un utilisateur avec une date spécifique
     * @param utilisateurId L'ID de l'utilisateur
     * @param livreId L'ID du livre à emprunter
     * @param dateEmprunt La date d'emprunt
     * @return L'emprunt créé sous forme de DTO
     * @throws RuntimeException si l'utilisateur ou le livre n'existe pas, ou si le livre n'est pas disponible
     */
    public EmpruntDTO emprunterLivre(Long utilisateurId, Long livreId, LocalDate dateEmprunt) {
        // Vérifier que l'utilisateur existe
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur avec l'ID " + utilisateurId + " introuvable"));

        // Vérifier que le livre existe
        Livre livre = livreRepository.findById(livreId)
                .orElseThrow(() -> new RuntimeException("Livre avec l'ID " + livreId + " introuvable"));

        // Vérifier que le livre est disponible
        if (!livre.estDisponible()) {
            throw new RuntimeException("Le livre '" + livre.getTitre() + "' n'est pas disponible pour l'emprunt");
        }

        // Vérifier que l'utilisateur n'a pas déjà emprunté ce livre
        boolean dejaEmprunte = empruntRepository.existsEmpruntActif(utilisateurId, livreId, StatutEmprunt.EN_COURS);
        if (dejaEmprunte) {
            throw new RuntimeException("L'utilisateur a déjà emprunté ce livre");
        }

        // Créer l'emprunt
        Emprunt emprunt = new Emprunt(utilisateur, livre);
        emprunt.setDateEmprunt(dateEmprunt);

        // Marquer le livre comme non disponible
        livre.marquerCommeEmprunte();

        // Sauvegarder l'emprunt
        emprunt = empruntRepository.save(emprunt);
        livreRepository.save(livre);

        return convertirEnDTO(emprunt);
    }

    /**
     * Retourner un livre emprunté
     * @param empruntId L'ID de l'emprunt
     * @return L'emprunt mis à jour sous forme de DTO
     * @throws RuntimeException si l'emprunt n'existe pas ou n'est pas en cours
     */
    public EmpruntDTO retournerLivre(Long empruntId) {
        return retournerLivre(empruntId, LocalDate.now());
    }

    /**
     * Retourner un livre emprunté avec une date spécifique
     * @param empruntId L'ID de l'emprunt
     * @param dateRetour La date de retour
     * @return L'emprunt mis à jour sous forme de DTO
     * @throws RuntimeException si l'emprunt n'existe pas ou n'est pas en cours
     */
    public EmpruntDTO retournerLivre(Long empruntId, LocalDate dateRetour) {
        Emprunt emprunt = empruntRepository.findById(empruntId)
                .orElseThrow(() -> new RuntimeException("Emprunt avec l'ID " + empruntId + " introuvable"));

        if (emprunt.getStatut() != StatutEmprunt.EN_COURS) {
            throw new RuntimeException("Cet emprunt n'est pas en cours et ne peut pas être retourné");
        }

        // Effectuer le retour
        emprunt.setDateRetour(dateRetour);
        emprunt.setStatut(StatutEmprunt.RETOURNE);

        // Marquer le livre comme disponible
        Livre livre = emprunt.getLivre();
        livre.marquerCommeDisponible();

        // Sauvegarder les modifications
        empruntRepository.save(emprunt);
        livreRepository.save(livre);

        return convertirEnDTO(emprunt);
    }

    /**
     * Obtenir tous les emprunts d'un utilisateur
     * @param utilisateurId L'ID de l'utilisateur
     * @return Liste des emprunts sous forme de DTO
     */
    @Transactional(readOnly = true)
    public List<EmpruntDTO> getEmpruntsParUtilisateur(Long utilisateurId) {
        List<Emprunt> emprunts = empruntRepository.findByUtilisateur_IdAndStatut(utilisateurId, StatutEmprunt.EN_COURS);
        return emprunts.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir tous les emprunts en cours
     * @return Liste des emprunts en cours sous forme de DTO
     */
    @Transactional(readOnly = true)
    public List<EmpruntDTO> getLivresEmpruntes() {
        List<Emprunt> emprunts = empruntRepository.findByStatut(StatutEmprunt.EN_COURS);
        return emprunts.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir tous les livres disponibles (non empruntés)
     * @return Liste des livres disponibles
     */
    @Transactional(readOnly = true)
    public List<Livre> getLivresDisponibles() {
        return livreRepository.findByDisponibleTrue();
    }

    /**
     * Obtenir les emprunts en retard
     * @return Liste des emprunts en retard sous forme de DTO
     */
    @Transactional(readOnly = true)
    public List<EmpruntDTO> getEmpruntsEnRetard() {
        LocalDate aujourdhui = LocalDate.now();
        List<Emprunt> emprunts = empruntRepository.findEmpruntsEnRetard(StatutEmprunt.EN_COURS, aujourdhui.minusWeeks(2));
        return emprunts.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les emprunts à rendre bientôt (dans les X jours)
     * @param nombreJours Nombre de jours avant la date limite
     * @return Liste des emprunts à rendre bientôt sous forme de DTO
     */
    @Transactional(readOnly = true)
    public List<EmpruntDTO> getEmpruntsARendreBientot(int nombreJours) {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dateLimiteDebut = aujourdhui.minusWeeks(2);
        LocalDate dateLimiteFin = aujourdhui.minusWeeks(2).plusDays(nombreJours);

        List<Emprunt> emprunts = empruntRepository.findEmpruntsARendreBientot(
                StatutEmprunt.EN_COURS, dateLimiteDebut, dateLimiteFin);
        return emprunts.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir un emprunt par son ID
     * @param empruntId L'ID de l'emprunt
     * @return L'emprunt sous forme de DTO ou null si non trouvé
     */
    @Transactional(readOnly = true)
    public EmpruntDTO getEmpruntById(Long empruntId) {
        Optional<Emprunt> emprunt = empruntRepository.findById(empruntId);
        return emprunt.map(this::convertirEnDTO).orElse(null);
    }

    /**
     * Obtenir tous les emprunts
     * @return Liste de tous les emprunts sous forme de DTO
     */
    @Transactional(readOnly = true)
    public List<EmpruntDTO> getAllEmprunts() {
        List<Emprunt> emprunts = empruntRepository.findAll();
        return emprunts.stream()
                .map(this::convertirEnDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les statistiques d'emprunts par mois
     * @return Liste des statistiques [Année, Mois, Nombre d'emprunts]
     */
    @Transactional(readOnly = true)
    public List<Object[]> getStatistiquesEmpruntsParMois() {
        return empruntRepository.getStatistiquesEmpruntsParMois();
    }

    /**
     * Obtenir le nombre d'emprunts actifs par utilisateur
     * @return Liste des statistiques [Utilisateur, Nombre d'emprunts actifs]
     */
    @Transactional(readOnly = true)
    public List<Object[]> getNombreEmpruntsActifsParUtilisateur() {
        return empruntRepository.getNombreEmpruntsActifsParUtilisateur(StatutEmprunt.EN_COURS);
    }

    /**
     * Convertir une entité Emprunt en DTO
     * @param emprunt L'entité à convertir
     * @return Le DTO correspondant
     */
    private EmpruntDTO convertirEnDTO(Emprunt emprunt) {
        EmpruntDTO dto = new EmpruntDTO();
        dto.setId(emprunt.getId());
        dto.setUtilisateurId(emprunt.getUtilisateur().getId());
        dto.setLivreId(emprunt.getLivre().getId());
        dto.setDateEmprunt(emprunt.getDateEmprunt());
        dto.setDateRetour(emprunt.getDateRetour());
        dto.setStatut(emprunt.getStatut());

        // Champs pour l'affichage
        dto.setNomUtilisateur(emprunt.getUtilisateur().getNom() );
        dto.setTitreLivre(emprunt.getLivre().getTitre());
        dto.setNomAuteur(emprunt.getLivre().getAuteur().getNom());
        dto.setDateLimiteRetour(emprunt.getDateLimiteRetour());
        dto.setEnRetard(emprunt.estEnRetard());
        dto.setNombreJoursRetard(emprunt.getNombreJoursRetard());

        return dto;
    }
}
