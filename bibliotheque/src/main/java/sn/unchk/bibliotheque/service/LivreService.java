package sn.unchk.bibliotheque.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sn.unchk.bibliotheque.dto.LivreDTO;
import sn.unchk.bibliotheque.entity.Auteur;
import sn.unchk.bibliotheque.entity.Livre;
import sn.unchk.bibliotheque.entity.Utilisateur;
import sn.unchk.bibliotheque.enums.Role;
import sn.unchk.bibliotheque.exception.BusinessException;
import sn.unchk.bibliotheque.repository.AuteurRepository;
import sn.unchk.bibliotheque.repository.LivreRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LivreService {
    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private AuteurService auteurService;

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Vérifier si l'utilisateur connecté a le rôle admin
     */
    private void verifierDroitsAdmin(Long idUtilisateurConnecte) {
        Utilisateur utilisateurConnecte = utilisateurService.obtenirUtilisateurParId(idUtilisateurConnecte);

        if (utilisateurConnecte.getRole() == null ||
                !utilisateurConnecte.getRole().equals(Role.ADMIN)) {
            throw new BusinessException("Accès refusé : seuls les administrateurs peuvent effectuer cette opération");
        }
    }

    /**
     * Obtenir un auteur existant par nom
     */
    private Auteur obtenirAuteurParNom(String nomAuteur) {
        if (nomAuteur == null || nomAuteur.trim().isEmpty()) {
            throw new BusinessException("Le nom de l'auteur est obligatoire");
        }

        // Rechercher parmi les auteurs existants
        List<Auteur> auteurs = auteurService.rechercherAuteurs(nomAuteur.trim());

        // Recherche exacte (insensible à la casse)
        Optional<Auteur> auteurTrouve = auteurs.stream()
                .filter(auteur -> auteur.getNom().equalsIgnoreCase(nomAuteur.trim()))
                .findFirst();

        if (!auteurTrouve.isPresent()) {
            throw new BusinessException("Auteur non trouvé : " + nomAuteur +
                    ". Veuillez d'abord créer l'auteur avant d'ajouter le livre.");
        }

        return auteurTrouve.get();
    }

    // ==================== MÉTHODES ADMIN (CRUD) ====================

    /**
     * Ajouter un nouveau livre (ADMIN seulement)
     */
    public Livre ajouterLivre(LivreDTO livreDTO, Long idUtilisateurConnecte) {
        // Vérifier les droits d'administration
        verifierDroitsAdmin(idUtilisateurConnecte);

        // Validation des données d'entrée
        if (livreDTO.getTitre() == null || livreDTO.getTitre().trim().isEmpty()) {
            throw new BusinessException("Le titre du livre est obligatoire");
        }

        if (livreDTO.getNomAuteur() == null || livreDTO.getNomAuteur().trim().isEmpty()) {
            throw new BusinessException("Le nom de l'auteur est obligatoire");
        }

        // Vérifier que l'auteur existe
        Auteur auteur = obtenirAuteurParNom(livreDTO.getNomAuteur());
        List<Livre> livresExistants = livreRepository.findByAuteur(auteur);

        boolean livreExiste = livresExistants.stream()
                .anyMatch(livre -> livre.getTitre().equalsIgnoreCase(livreDTO.getTitre().trim()));

        if (livreExiste) {
            throw new BusinessException("Un livre avec ce titre et cet auteur existe déjà");
        }

        // Créer le nouveau livre
        Livre nouveauLivre = new Livre();
        nouveauLivre.setTitre(livreDTO.getTitre().trim());
        nouveauLivre.setAuteur(auteur);
        nouveauLivre.setGenre(livreDTO.getGenre() != null ? livreDTO.getGenre().trim() : null);
        nouveauLivre.setDatePublication(livreDTO.getDatePublication());
        nouveauLivre.setDisponible(true); // Par défaut, un nouveau livre est disponible

        Livre livreSauve = livreRepository.save(nouveauLivre);

        System.out.println("Nouveau livre créé avec l'ID : " + livreSauve.getId());
        return livreSauve;
    }

    /**
     * Modifier les détails d'un livre (ADMIN seulement)
     */
    public Livre modifierLivre(Long idLivre, LivreDTO livreDTO, Long idUtilisateurConnecte) {
        // Vérifier les droits d'administration
        verifierDroitsAdmin(idUtilisateurConnecte);

        // Récupérer le livre existant
        Livre livre = livreRepository.findById(idLivre)
                .orElseThrow(() -> new EntityNotFoundException("Livre non trouvé avec l'ID : " + idLivre));

        // Validation des données d'entrée
        if (livreDTO.getTitre() == null || livreDTO.getTitre().trim().isEmpty()) {
            throw new BusinessException("Le titre du livre est obligatoire");
        }

        if (livreDTO.getNomAuteur() == null || livreDTO.getNomAuteur().trim().isEmpty()) {
            throw new BusinessException("Le nom de l'auteur est obligatoire");
        }

        // Vérifier que l'auteur existe
        Auteur auteur = obtenirAuteurParNom(livreDTO.getNomAuteur());

        // Vérifier qu'un autre livre avec le même titre et auteur n'existe pas
        if (!livre.getTitre().equalsIgnoreCase(livreDTO.getTitre().trim()) ||
                !livre.getAuteur().getId().equals(auteur.getId())) {

            List<Livre> livresExistants = livreRepository.findByAuteur(auteur);
            boolean livreExiste = livresExistants.stream()
                    .anyMatch(l -> l.getTitre().equalsIgnoreCase(livreDTO.getTitre().trim()) &&
                            !l.getId().equals(idLivre));

            if (livreExiste) {
                throw new BusinessException("Un autre livre avec ce titre et cet auteur existe déjà");
            }
        }

        // Mettre à jour les champs
        livre.setTitre(livreDTO.getTitre().trim());
        livre.setAuteur(auteur);
        livre.setGenre(livreDTO.getGenre() != null ? livreDTO.getGenre().trim() : null);
        livre.setDatePublication(livreDTO.getDatePublication());
        // Note: disponible n'est pas modifié ici, il est géré par les emprunts/retours

        return livreRepository.save(livre);
    }

    /**
     * Supprimer un livre par titre et auteur (ADMIN seulement)
     */
    public void supprimerLivreParTitreEtAuteur(String titre, String nomAuteur, Long idUtilisateurConnecte) {
        // Vérifier les droits d'administration
        verifierDroitsAdmin(idUtilisateurConnecte);

        // Validation des paramètres
        if (titre == null || titre.trim().isEmpty()) {
            throw new BusinessException("Le titre du livre est obligatoire pour la suppression");
        }

        if (nomAuteur == null || nomAuteur.trim().isEmpty()) {
            throw new BusinessException("Le nom de l'auteur est obligatoire pour la suppression");
        }

        // Rechercher l'auteur via le service
        List<Auteur> auteursRecherche = auteurService.rechercherAuteurs(nomAuteur.trim());
        Optional<Auteur> auteurOpt = auteursRecherche.stream()
                .filter(a -> a.getNom().equalsIgnoreCase(nomAuteur.trim()))
                .findFirst();

        if (!auteurOpt.isPresent()) {
            throw new EntityNotFoundException("Auteur non trouvé : " + nomAuteur);
        }

        Auteur auteur = auteurOpt.get();

        // Rechercher le livre par titre et auteur
        List<Livre> livres = livreRepository.findByAuteur(auteur);
        Optional<Livre> livreOpt = livres.stream()
                .filter(livre -> livre.getTitre().equalsIgnoreCase(titre.trim()))
                .findFirst();

        if (!livreOpt.isPresent()) {
            throw new EntityNotFoundException("Livre non trouvé avec le titre '" + titre + "' et l'auteur '" + nomAuteur + "'");
        }

        Livre livre = livreOpt.get();

        // Vérifier si le livre a des emprunts en cours
        if (!livre.getEmprunts().isEmpty()) {
            boolean hasActiveEmprunts = livre.getEmprunts().stream()
                    .anyMatch(emprunt -> emprunt.getDateRetour() == null);

            if (hasActiveEmprunts) {
                throw new BusinessException("Impossible de supprimer le livre '" + titre + "' car il a des emprunts en cours");
            }
        }

        livreRepository.delete(livre);
        System.out.println("Livre supprimé : " + titre + " par " + nomAuteur);
    }

    /**
     * Supprimer un livre par ID (ADMIN seulement)
     */
    public void supprimerLivre(Long idLivre, Long idUtilisateurConnecte) {
        // Vérifier les droits d'administration
        verifierDroitsAdmin(idUtilisateurConnecte);

        Livre livre = livreRepository.findById(idLivre)
                .orElseThrow(() -> new EntityNotFoundException("Livre non trouvé avec l'ID : " + idLivre));

        // Vérifier si le livre a des emprunts en cours
        if (!livre.getEmprunts().isEmpty()) {
            boolean hasActiveEmprunts = livre.getEmprunts().stream()
                    .anyMatch(emprunt -> emprunt.getDateRetour() == null);

            if (hasActiveEmprunts) {
                throw new BusinessException("Impossible de supprimer le livre car il a des emprunts en cours");
            }
        }

        livreRepository.delete(livre);
        System.out.println("Livre supprimé avec l'ID : " + idLivre);
    }

    // ==================== MÉTHODES PUBLIQUES (LECTURE) ====================

    /**
     * Obtenir tous les livres
     */
    public List<Livre> obtenirTousLesLivres() {
        return livreRepository.findAll();
    }

    /**
     * Obtenir un livre par ID
     */
    public Livre obtenirLivreParId(Long id) {
        return livreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Livre non trouvé avec l'ID : " + id));
    }

    /**
     * Rechercher des livres par titre
     */
    public List<Livre> rechercherLivresParTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            return livreRepository.findAll();
        }
        return livreRepository.findByTitreContainingIgnoreCase(titre.trim());
    }

    /**
     * Rechercher des livres par auteur
     */
    public List<Livre> rechercherLivresParAuteur(String nomAuteur) {
        if (nomAuteur == null || nomAuteur.trim().isEmpty()) {
            return livreRepository.findAll();
        }
        return livreRepository.findByAuteur_NomContainingIgnoreCase(nomAuteur.trim());
    }

    /**
     * Rechercher des livres par genre
     */
    public List<Livre> rechercherLivresParGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return livreRepository.findAll();
        }
        return livreRepository.findByGenreContainingIgnoreCase(genre.trim());
    }

    /**
     * Obtenir les livres disponibles
     */
    public List<Livre> obtenirLivresDisponibles() {
        return livreRepository.findByDisponible(true);
    }

    /**
     * Recherche multi-critères
     */
    public List<Livre> rechercherLivres(String titre, String auteur, String genre, Boolean disponible) {
        return livreRepository.rechercherLivres(titre, auteur, genre, disponible);
    }

    /**
     * Obtenir les genres distincts
     */
    public List<String> obtenirGenresDistincts() {
        return livreRepository.findDistinctGenres();
    }

    /**
     * Obtenir les livres les plus empruntés
     */
    public List<Object[]> obtenirLivresLesPlusEmpruntes() {
        return livreRepository.findLivresLesPlusEmpruntes();
    }

    /**
     * Obtenir les livres publiés dans une période
     */
    public List<Livre> obtenirLivresParPeriode(LocalDate dateDebut, LocalDate dateFin) {
        return livreRepository.findByDatePublicationBetween(dateDebut, dateFin);
    }

}
