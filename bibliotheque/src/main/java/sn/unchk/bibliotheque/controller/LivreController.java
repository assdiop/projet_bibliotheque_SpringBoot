package sn.unchk.bibliotheque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.unchk.bibliotheque.dto.LivreDTO;
import sn.unchk.bibliotheque.entity.Livre;
import sn.unchk.bibliotheque.entity.Utilisateur;
import sn.unchk.bibliotheque.exception.BusinessException;
import sn.unchk.bibliotheque.service.LivreService;
import sn.unchk.bibliotheque.service.UtilisateurService;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/livres")
@CrossOrigin(origins = "http://localhost:8080")

public class LivreController {
    @Autowired
    private LivreService livreService;

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Méthode utilitaire pour récupérer l'ID de l'utilisateur connecté
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("Utilisateur non authentifié");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Rechercher l'utilisateur par nom d'utilisateur
        List<Utilisateur> utilisateurs = utilisateurService.rechercherUtilisateurs(userDetails.getUsername());
        if (utilisateurs.isEmpty()) {
            throw new BusinessException("Utilisateur non trouvé");
        }
        return utilisateurs.get(0).getId();
    }

    // ==================== ENDPOINTS PUBLICS (LECTURE) ====================

    /**
     * Obtenir tous les livres
     * GET /api/livres
     */
    @GetMapping
    public ResponseEntity<List<Livre>> obtenirTousLesLivres() {
        try {
            List<Livre> livres = livreService.obtenirTousLesLivres();
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir un livre par ID
     * GET /api/livres/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Livre> obtenirLivreParId(@PathVariable Long id) {
        try {
            Livre livre = livreService.obtenirLivreParId(id);
            return ResponseEntity.ok(livre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Rechercher des livres par titre
     * GET /api/livres/recherche/titre?titre=...
     */
    @GetMapping("/recherche/titre")
    public ResponseEntity<List<Livre>> rechercherParTitre(
            @RequestParam(required = false) String titre) {
        try {
            List<Livre> livres = livreService.rechercherLivresParTitre(titre);
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Rechercher des livres par auteur
     * GET /api/livres/recherche/auteur?auteur=...
     */
    @GetMapping("/recherche/auteur")
    public ResponseEntity<List<Livre>> rechercherParAuteur(
            @RequestParam(required = false) String auteur) {
        try {
            List<Livre> livres = livreService.rechercherLivresParAuteur(auteur);
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Rechercher des livres par genre
     * GET /api/livres/recherche/genre?genre=...
     */
    @GetMapping("/recherche/genre")
    public ResponseEntity<List<Livre>> rechercherParGenre(
            @RequestParam(required = false) String genre) {
        try {
            List<Livre> livres = livreService.rechercherLivresParGenre(genre);
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir les livres disponibles
     * GET /api/livres/disponibles
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Livre>> obtenirLivresDisponibles() {
        try {
            List<Livre> livres = livreService.obtenirLivresDisponibles();
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Recherche multi-critères
     * GET /api/livres/recherche?titre=...&auteur=...&genre=...&disponible=true
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<Livre>> rechercherLivres(
            @RequestParam(required = false) String titre,
            @RequestParam(required = false) String auteur,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Boolean disponible) {
        try {
            List<Livre> livres = livreService.rechercherLivres(titre, auteur, genre, disponible);
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir les genres distincts
     * GET /api/livres/genres
     */
    @GetMapping("/genres")
    public ResponseEntity<List<String>> obtenirGenresDistincts() {
        try {
            List<String> genres = livreService.obtenirGenresDistincts();
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir les livres les plus empruntés
     * GET /api/livres/populaires
     */
    @GetMapping("/populaires")
    public ResponseEntity<List<Object[]>> obtenirLivresPopulaires() {
        try {
            List<Object[]> livres = livreService.obtenirLivresLesPlusEmpruntes();
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir les livres publiés dans une période
     * GET /api/livres/periode?debut=2020-01-01&fin=2023-12-31
     */
    @GetMapping("/periode")
    public ResponseEntity<List<Livre>> obtenirLivresParPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        try {
            List<Livre> livres = livreService.obtenirLivresParPeriode(debut, fin);
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== ENDPOINTS ADMIN SEULEMENT ====================

    /**
     * Ajouter un nouveau livre (ADMIN seulement)
     * POST /api/livres/admin
     */
    @PostMapping("/admin")
    public ResponseEntity<?> ajouterLivre(
            @Valid @RequestBody LivreDTO livreDTO,
            Authentication authentication) {
        try {
            Long idAdmin = getCurrentUserId(authentication);
            Livre nouveauLivre = livreService.ajouterLivre(livreDTO, idAdmin);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauLivre);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'ajout du livre : " + e.getMessage());
        }
    }

    /**
     * Modifier un livre (ADMIN seulement)
     * PUT /api/livres/admin/{id}
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> modifierLivre(
            @PathVariable Long id,
            @Valid @RequestBody LivreDTO livreDTO,
            Authentication authentication) {
        try {
            Long idAdmin = getCurrentUserId(authentication);
            Livre livreModifie = livreService.modifierLivre(id, livreDTO, idAdmin);
            return ResponseEntity.ok(livreModifie);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Livre non trouvé ou erreur lors de la modification : " + e.getMessage());
        }
    }

    /**
     * Supprimer un livre par ID (ADMIN seulement)
     * DELETE /api/livres/admin/{id}
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> supprimerLivre(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long idAdmin = getCurrentUserId(authentication);
            livreService.supprimerLivre(id, idAdmin);
            return ResponseEntity.ok("Livre supprimé avec succès");
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Livre non trouvé ou erreur lors de la suppression : " + e.getMessage());
        }
    }

    /**
     * Supprimer un livre par titre et auteur (ADMIN seulement)
     * DELETE /api/livres/admin/par-titre-auteur?titre=...&auteur=...
     */
    @DeleteMapping("/admin/par-titre-auteur")
    public ResponseEntity<?> supprimerLivreParTitreEtAuteur(
            @RequestParam String titre,
            @RequestParam String auteur,
            Authentication authentication) {
        try {
            Long idAdmin = getCurrentUserId(authentication);
            livreService.supprimerLivreParTitreEtAuteur(titre, auteur, idAdmin);
            return ResponseEntity.ok("Livre '" + titre + "' par " + auteur + " supprimé avec succès");
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Livre non trouvé ou erreur lors de la suppression : " + e.getMessage());
        }
    }

    // ==================== ENDPOINTS DE TEST (À SUPPRIMER EN PRODUCTION) ====================

    /**
     * Endpoint de test pour ajouter un livre sans authentification
     * À SUPPRIMER EN PRODUCTION
     */
    @PostMapping("/test")
    public ResponseEntity<?> ajouterLivreTest(@Valid @RequestBody LivreDTO livreDTO) {
        try {
            // Utiliser un ID admin fictif pour les tests (assurez-vous qu'un admin avec ID=1 existe)
            Long idAdminTest = 1L;
            Livre nouveauLivre = livreService.ajouterLivre(livreDTO, idAdminTest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauLivre);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'ajout du livre : " + e.getMessage());
        }
    }

    /**
     * Endpoint de test pour modifier un livre sans authentification
     * À SUPPRIMER EN PRODUCTION
     */
    @PutMapping("/test/{id}")
    public ResponseEntity<?> modifierLivreTest(
            @PathVariable Long id,
            @Valid @RequestBody LivreDTO livreDTO) {
        try {
            Long idAdminTest = 1L;
            Livre livreModifie = livreService.modifierLivre(id, livreDTO, idAdminTest);
            return ResponseEntity.ok(livreModifie);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Livre non trouvé ou erreur lors de la modification : " + e.getMessage());
        }
    }

    /**
     * Endpoint de test pour supprimer un livre sans authentification
     * À SUPPRIMER EN PRODUCTION
     */
    @DeleteMapping("/test/{id}")
    public ResponseEntity<?> supprimerLivreTest(@PathVariable Long id) {
        try {
            Long idAdminTest = 1L;
            livreService.supprimerLivre(id, idAdminTest);
            return ResponseEntity.ok("Livre supprimé avec succès");
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Livre non trouvé ou erreur lors de la suppression : " + e.getMessage());
        }
    }

}
