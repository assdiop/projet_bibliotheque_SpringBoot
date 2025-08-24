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
@CrossOrigin(origins = "*")

public class LivreController {
    @Autowired
    private LivreService livreService;

    // ==================== ENDPOINTS DE LECTURE ====================

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

    // ==================== ENDPOINTS CRUD ====================

    /**
     * Ajouter un nouveau livre
     * POST /api/livres
     */
    @PostMapping
    public ResponseEntity<?> ajouterLivre(@Valid @RequestBody LivreDTO livreDTO) {
        try {
            Livre nouveauLivre = livreService.ajouterLivre(livreDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouveauLivre);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'ajout du livre : " + e.getMessage());
        }
    }

    /**
     * Modifier un livre
     * PUT /api/livres/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierLivre(
            @PathVariable Long id,
            @Valid @RequestBody LivreDTO livreDTO) {
        try {
            Livre livreModifie = livreService.modifierLivre(id, livreDTO);
            return ResponseEntity.ok(livreModifie);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Livre non trouvé ou erreur lors de la modification : " + e.getMessage());
        }
    }

    /**
     * Supprimer un livre par ID
     * DELETE /api/livres/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerLivre(@PathVariable Long id) {
        try {
            livreService.supprimerLivre(id);
            return ResponseEntity.ok("Livre supprimé avec succès");
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Livre non trouvé ou erreur lors de la suppression : " + e.getMessage());
        }
    }

    /**
     * Supprimer un livre par titre et auteur
     * DELETE /api/livres/par-titre-auteur?titre=...&auteur=...
     */
    @DeleteMapping("/par-titre-auteur")
    public ResponseEntity<?> supprimerLivreParTitreEtAuteur(
            @RequestParam String titre,
            @RequestParam String auteur) {
        try {
            livreService.supprimerLivreParTitreEtAuteur(titre, auteur);
            return ResponseEntity.ok("Livre '" + titre + "' par " + auteur + " supprimé avec succès");
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Livre non trouvé ou erreur lors de la suppression : " + e.getMessage());
        }
    }

}
