package sn.unchk.bibliotheque.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import sn.unchk.bibliotheque.dto.AuteurDTO;
import sn.unchk.bibliotheque.entity.Auteur;
import sn.unchk.bibliotheque.exception.BusinessException;
import sn.unchk.bibliotheque.repository.AuteurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AuteurService {
    @Autowired
    private AuteurRepository auteurRepository;

    /**
     * Créer un nouveau auteur
     * Cette méthode utilise automatiquement save() qui génère l'INSERT
     */
      /**
     *  avec gestion d'erreur plus fine
     */
    public Auteur creerAuteurAvecValidation(AuteurDTO auteurDTO) {
        // Validation des données d'entrée
        if (auteurDTO.getNom() == null || auteurDTO.getNom().trim().isEmpty()) {
            throw new BusinessException("Le nom de l'auteur est obligatoire");
        }

        // Vérifier unicité du nom
        Optional<Auteur> auteurExistant = auteurRepository.findByNomIgnoreCase(auteurDTO.getNom());
        if (auteurExistant.isPresent()) {
            throw new BusinessException("Un auteur avec ce nom existe déjà : " + auteurDTO.getNom());
        }

        // Validation de la date de naissance
        if (auteurDTO.getDateNaissance() != null && auteurDTO.getDateNaissance().isAfter(LocalDate.now())) {
            throw new BusinessException("La date de naissance ne peut pas être dans le futur");
        }

        // Créer et sauvegarder
        Auteur auteur = new Auteur();
        auteur.setNom(auteurDTO.getNom().trim());
        auteur.setBiographie(auteurDTO.getBiographie() != null ? auteurDTO.getBiographie().trim() : null);
        auteur.setDateNaissance(auteurDTO.getDateNaissance());

        // L'insertion se fait ici automatiquement
        Auteur auteurSauve = auteurRepository.save(auteur);

        // Log pour le debug (optionnel)
        System.out.println("Nouvel auteur créé avec l'ID : " + auteurSauve.getId());

        return auteurSauve;
    }

    /**
     * Obtenir tous les auteurs
     */
    public List<Auteur> obtenirTousLesAuteurs() {
        return auteurRepository.findAll();
    }

    /**
     * Obtenir un auteur par ID
     */
    public Auteur obtenirAuteurParId(Long id) {
        return auteurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé avec l'ID : " + id));
    }

    /**
     * Rechercher des auteurs par nom
     */
    public List<Auteur> rechercherAuteurs(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return auteurRepository.findAll();
        }
        return auteurRepository.findByNomContainingIgnoreCase(nom.trim());
    }

    /**
     * Mettre à jour un auteur

    public Auteur mettreAJourAuteur(Long id, AuteurDTO auteurDTO) {
        Auteur auteur = obtenirAuteurParId(id);

        // Vérifier si le nouveau nom n'est pas déjà pris par un autre auteur
        if (!auteur.getNom().equalsIgnoreCase(auteurDTO.getNom())) {
            Optional<Auteur> auteurExistant = auteurRepository.findByNomIgnoreCase(auteurDTO.getNom());
            if (auteurExistant.isPresent() && !auteurExistant.get().getId().equals(id)) {
                throw new BusinessException("Un autre auteur avec ce nom existe déjà");
            }
        }

        // Mettre à jour les champs
        auteur.setNom(auteurDTO.getNom());
        auteur.setBiographie(auteurDTO.getBiographie());
        auteur.setDateNaissance(auteurDTO.getDateNaissance());

        // save() détecte que l'entité existe déjà et fait un UPDATE
        return auteurRepository.save(auteur);
    }   */

    public Auteur mettreAJourAuteur(Long id, AuteurDTO auteurDTO) {
        // Validations d'entrée
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        if (auteurDTO == null) {
            throw new IllegalArgumentException("Les données auteur ne peuvent pas être null");
        }

        // Validation des champs requis
        if (auteurDTO.getNom() == null || auteurDTO.getNom().trim().isEmpty()) {
            throw new BusinessException("Le nom de l'auteur est obligatoire");
        }

        Auteur auteur = obtenirAuteurParId(id);

        // Vérification unicité (logique existante - correcte)
        if (!auteur.getNom().equalsIgnoreCase(auteurDTO.getNom())) {
            Optional<Auteur> auteurExistant = auteurRepository.findByNomIgnoreCase(auteurDTO.getNom());
            if (auteurExistant.isPresent() && !auteurExistant.get().getId().equals(id)) {
                throw new BusinessException("Un autre auteur avec ce nom existe déjà");
            }
        }

        // Mise à jour avec nettoyage des données
        auteur.setNom(auteurDTO.getNom().trim());
        auteur.setBiographie(auteurDTO.getBiographie() != null ? auteurDTO.getBiographie().trim() : null);
        auteur.setDateNaissance(auteurDTO.getDateNaissance());

        try {
            return  auteurRepository.save(auteur);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Erreur lors de la mise à jour de l'auteur");
        }
    }

    /**
     * Supprimer un auteur
     */
    public void supprimerAuteur(Long id) {
        Auteur auteur = obtenirAuteurParId(id);

        // Vérifier si l'auteur a des livres
        if (!auteur.getLivres().isEmpty()) {
            throw new BusinessException("Impossible de supprimer un auteur qui a des livres associés");
        }

        auteurRepository.delete(auteur);
    }

}
