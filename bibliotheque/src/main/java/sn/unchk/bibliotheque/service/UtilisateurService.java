package sn.unchk.bibliotheque.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.stylesheets.LinkStyle;
import sn.unchk.bibliotheque.dto.AuteurDTO;
import sn.unchk.bibliotheque.dto.UtilisateurDTO;
import sn.unchk.bibliotheque.entity.Auteur;
import sn.unchk.bibliotheque.entity.Utilisateur;
import sn.unchk.bibliotheque.enums.Role;
import sn.unchk.bibliotheque.exception.BusinessException;
import sn.unchk.bibliotheque.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Vérifier si l'utilisateur connecté a le rôle admin
     */
    private void verifierDroitsAdmin(Long idUtilisateurConnecte) {
        Utilisateur utilisateurConnecte = obtenirUtilisateurParId(idUtilisateurConnecte);

        if (utilisateurConnecte.getRole() == null ||
                !utilisateurConnecte.getRole().equals(Role.ADMIN)) {
            throw new BusinessException("Accès refusé : seuls les administrateurs peuvent effectuer cette opération");
        }
    }

    /**
     * Créer un nouveau utilisateur
     * Cette méthode utilise automatiquement save() qui génère l'INSERT
     * avec gestion d'erreur plus fine
     * RESTREINT AUX ADMINS SEULEMENT
     */
    public Utilisateur creerUtilisateurAvecValidation(UtilisateurDTO utilisateurDTO, Long idUtilisateurConnecte) {

        // Vérifier les droits d'administration
        verifierDroitsAdmin(idUtilisateurConnecte);

        // Validation des données d'entrée
        if (utilisateurDTO.getNom() == null || utilisateurDTO.getNom().trim().isEmpty()) {
            throw new BusinessException("Le nom de l'utilisateur est obligatoire");
        }

        if (utilisateurDTO.getEmail() == null || utilisateurDTO.getEmail().trim().isEmpty()) {
            throw new BusinessException("L'email de l'utilisateur est obligatoire");
        }

        if (utilisateurDTO.getMotDePasse() == null || utilisateurDTO.getMotDePasse().trim().isEmpty()) {
            throw new BusinessException("Le mot de passe est obligatoire");
        }

        if (utilisateurDTO.getMotDePasse().length() < 6) {
            throw new BusinessException("Le mot de passe doit contenir au moins 6 caractères");
        }

        // Vérifier unicité du nom et de l'email
        Optional<Utilisateur> utilisateurExistantNom = utilisateurRepository.findByNomIgnoreCase(utilisateurDTO.getNom());
        if (utilisateurExistantNom.isPresent()) {
            throw new BusinessException("Un utilisateur avec ce nom existe déjà : " + utilisateurDTO.getNom());
        }

        // Vérifier unicité de l'email
        Optional<Utilisateur> utilisateurExistantEmail = utilisateurRepository.findByEmailIgnoreCase(utilisateurDTO.getEmail());
        if (utilisateurExistantEmail.isPresent()) {
            throw new BusinessException("Un utilisateur avec cet email existe déjà : " + utilisateurDTO.getEmail());
        }

        // verifier si la date d'inscription est null
        if (utilisateurDTO.getDateInscription() == null ) {
            throw new BusinessException("La date d'inscription ne peut pas être null ");
        }

        // Créer et sauvegarder
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(utilisateurDTO.getNom().trim());
        utilisateur.setEmail(utilisateurDTO.getEmail().trim().toLowerCase());
        utilisateur.setDateInscription(utilisateurDTO.getDateInscription());
        utilisateur.setRole(utilisateurDTO.getRole());

        // Hasher le mot de passe avant de le sauvegarder
        String motDePasseHash = passwordEncoder.encode(utilisateurDTO.getMotDePasse());
        utilisateur.setMotDePasse(motDePasseHash);

        // L'insertion se fait ici automatiquement
        Utilisateur utilisateurSauve = utilisateurRepository.save(utilisateur);

        //Log pour le debug (optionnel)
        System.out.println("Nouvel utilisateur créé avec l'ID : " + utilisateurSauve.getId());

        return utilisateurSauve;
    }

    /**
     * Obtenir tous les utilisateurs
     */
    public List<Utilisateur> obtenirTousLesUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Obtenir un utilisateur par ID
     */
    public Utilisateur obtenirUtilisateurParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
    }

    /**
     * Rechercher des utilisateurs par nom
     */
    public List<Utilisateur> rechercherUtilisateurs(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return utilisateurRepository.findAll();
        }
        return utilisateurRepository.findByNomContainingIgnoreCase(nom.trim());
    }

    /**
     * Mettre à jour un utilisateur
     * RESTREINT AUX ADMINS SEULEMENT
     */
    public Utilisateur mettreAJourUtilisateur(Long id, UtilisateurDTO utilisateurDTO, Long idUtilisateurConnecte) {

        // Vérifier les droits d'administration
        verifierDroitsAdmin(idUtilisateurConnecte);

        Utilisateur utilisateur = obtenirUtilisateurParId(id);

        // Vérifier si le nouveau nom n'est pas déjà pris par un autre utilisateur
        if (!utilisateur.getNom().equalsIgnoreCase(utilisateurDTO.getNom())) {
            Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findByNomIgnoreCase(utilisateurDTO.getNom());
            if (utilisateurExistant.isPresent() && !utilisateurExistant.get().getId().equals(id)) {
                throw new BusinessException("Un utilisateur avec ce nom existe déjà");
            }
        }

        // Vérifier si le nouvel email n'est pas déjà pris par un autre utilisateur
        if (!utilisateur.getEmail().equalsIgnoreCase(utilisateurDTO.getEmail())) {
            Optional<Utilisateur> utilisateurExistantEmail = utilisateurRepository.findByEmailIgnoreCase(utilisateurDTO.getEmail());
            if (utilisateurExistantEmail.isPresent() && !utilisateurExistantEmail.get().getId().equals(id)) {
                throw new BusinessException("Un utilisateur avec cet email existe déjà");
            }
        }

        // Mettre à jour les champs dans la table Utilisateur
        utilisateur.setDateInscription(utilisateurDTO.getDateInscription());
        utilisateur.setEmail(utilisateurDTO.getEmail().trim().toLowerCase());
        utilisateur.setNom(utilisateurDTO.getNom().trim());
        utilisateur.setRole(utilisateurDTO.getRole());

        // Si un nouveau mot de passe est fourni, le hasher
        if (utilisateurDTO.getMotDePasse() != null && !utilisateurDTO.getMotDePasse().trim().isEmpty()) {
            if (utilisateurDTO.getMotDePasse().length() < 6) {
                throw new BusinessException("Le mot de passe doit contenir au moins 6 caractères");
            }
            String motDePasseHash = passwordEncoder.encode(utilisateurDTO.getMotDePasse());
            utilisateur.setMotDePasse(motDePasseHash);
        }

        // save() détecte que l'entité existe déjà et fait un UPDATE
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Supprimer un utilisateur
     * RESTREINT AUX ADMINS SEULEMENT
     */
    public void supprimerUtilisateur(Long id, Long idUtilisateurConnecte) {

        // Vérifier les droits d'administration
        verifierDroitsAdmin(idUtilisateurConnecte);

        Utilisateur utilisateur = obtenirUtilisateurParId(id);

        // Vérifier si l'utilisateur a des emprunts
        if (!utilisateur.getEmprunts().isEmpty()) {
            throw new BusinessException("Impossible de supprimer un utilisateur qui a des emprunts");
        }

        utilisateurRepository.delete(utilisateur);
    }

    /**
     * Méthode pour permettre à un utilisateur de modifier ses propres informations (limitées)
     * Un utilisateur peut modifier ses propres informations de base mais pas son rôle
     */
    public Utilisateur modifierSesPropresInformations(Long idUtilisateur, UtilisateurDTO utilisateurDTO, Long idUtilisateurConnecte) {

        // Vérifier que l'utilisateur modifie bien ses propres informations
        if (!idUtilisateur.equals(idUtilisateurConnecte)) {
            throw new BusinessException("Vous ne pouvez modifier que vos propres informations");
        }

        Utilisateur utilisateur = obtenirUtilisateurParId(idUtilisateur);

        // Vérifier si le nouveau nom n'est pas déjà pris par un autre utilisateur
        if (!utilisateur.getNom().equalsIgnoreCase(utilisateurDTO.getNom())) {
            Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findByNomIgnoreCase(utilisateurDTO.getNom());
            if (utilisateurExistant.isPresent() && !utilisateurExistant.get().getId().equals(idUtilisateur)) {
                throw new BusinessException("Un utilisateur avec ce nom existe déjà");
            }
        }

        // Mettre à jour seulement les champs autorisés (pas le rôle ni la date d'inscription)
        utilisateur.setEmail(utilisateurDTO.getEmail().trim().toLowerCase());
        utilisateur.setNom(utilisateurDTO.getNom().trim());

        // Permettre à l'utilisateur de changer son propre mot de passe
        if (utilisateurDTO.getMotDePasse() != null && !utilisateurDTO.getMotDePasse().trim().isEmpty()) {
            if (utilisateurDTO.getMotDePasse().length() < 6) {
                throw new BusinessException("Le mot de passe doit contenir au moins 6 caractères");
            }
            String motDePasseHash = passwordEncoder.encode(utilisateurDTO.getMotDePasse());
            utilisateur.setMotDePasse(motDePasseHash);
        }

        // Note: le rôle et la date d'inscription ne sont pas modifiables par l'utilisateur lui-même

        return utilisateurRepository.save(utilisateur);
    }

}
