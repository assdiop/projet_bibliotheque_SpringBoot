package sn.unchk.bibliotheque.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import sn.unchk.bibliotheque.dto.LivreDTO;
import sn.unchk.bibliotheque.entity.Livre;
import sn.unchk.bibliotheque.exception.BusinessException;
import sn.unchk.bibliotheque.service.LivreService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/livres")
@CrossOrigin(origins = "*")

public class LivreController {
    @Autowired
    private LivreService livreService;

    // ==================== ENDPOINTS DE LECTURE ====================

    @GetMapping
    public ResponseEntity<List<Livre>> obtenirTousLesLivres() {
        try {
            List<Livre> livres = livreService.obtenirTousLesLivres();
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livre> obtenirLivreParId(@PathVariable Long id) {
        try {
            Livre livre = livreService.obtenirLivreParId(id);
            return ResponseEntity.ok(livre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

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

    @GetMapping("/disponibles")
    public ResponseEntity<List<Livre>> obtenirLivresDisponibles() {
        try {
            List<Livre> livres = livreService.obtenirLivresDisponibles();
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    @GetMapping("/genres")
    public ResponseEntity<List<String>> obtenirGenresDistincts() {
        try {
            List<String> genres = livreService.obtenirGenresDistincts();
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/populaires")
    public ResponseEntity<List<Object[]>> obtenirLivresPopulaires() {
        try {
            List<Object[]> livres = livreService.obtenirLivresLesPlusEmpruntes();
            return ResponseEntity.ok(livres);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    // ==================== ENDPOINTS CRUD AMÉLIORÉS ====================

    /**
     * Ajouter un nouveau livre - VERSION CORRIGÉE
     */
    @PostMapping
    public ResponseEntity<?> ajouterLivre(@Valid @RequestBody LivreDTO livreDTO) {
        try {
            System.out.println("Données reçues - Titre: " + livreDTO.getTitre() +
                    ", AuteurId: " + livreDTO.getAuteurId());

            // Appeler votre méthode existante du service
            Livre nouveauLivre = livreService.ajouterLivre(livreDTO);

            // Créer une réponse simple sans référence circulaire
            Map<String, Object> response = new HashMap<>();
            response.put("id", nouveauLivre.getId());
            response.put("titre", nouveauLivre.getTitre());
            response.put("genre", nouveauLivre.getGenre());
            response.put("datePublication", nouveauLivre.getDatePublication());
            response.put("disponible", nouveauLivre.getDisponible());
            response.put("auteurId", nouveauLivre.getAuteurId());
            response.put("nomAuteur", nouveauLivre.getNomAuteur());
            response.put("message", "Livre créé avec succès");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage(), "timestamp", LocalDateTime.now()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Auteur non trouvé avec l'ID: " + livreDTO.getAuteurId(),
                            "timestamp", LocalDateTime.now()));
        } catch (Exception e) {
            e.printStackTrace(); // Pour le debug
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur interne du serveur",
                            "details", e.getMessage(),
                            "timestamp", LocalDateTime.now()));
        }
    }

    /**
     * Modifier un nouveau livre
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierLivre(
            @PathVariable Long id,
            @Valid @RequestBody LivreDTO livreDTO) {
        try {
            Livre livreModifie = livreService.modifierLivre(id, livreDTO);

            // Réponse simple sans référence circulaire
            Map<String, Object> response = new HashMap<>();
            response.put("id", livreModifie.getId());
            response.put("titre", livreModifie.getTitre());
            response.put("genre", livreModifie.getGenre());
            response.put("datePublication", livreModifie.getDatePublication());
            response.put("disponible", livreModifie.getDisponible());
            response.put("auteurId", livreModifie.getAuteurId());
            response.put("nomAuteur", livreModifie.getNomAuteur());
            response.put("message", "Livre modifié avec succès");

            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Livre non trouvé ou erreur lors de la modification"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerLivre(@PathVariable Long id) {
        try {
            livreService.supprimerLivre(id);
            return ResponseEntity.ok(Map.of("message", "Livre supprimé avec succès"));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Livre non trouvé"));
        }
    }

    @DeleteMapping("/par-titre-auteur")
    public ResponseEntity<?> supprimerLivreParTitreEtAuteur(
            @RequestParam String titre,
            @RequestParam String auteur) {
        try {
            livreService.supprimerLivreParTitreEtAuteur(titre, auteur);
            return ResponseEntity.ok(Map.of("message",
                    "Livre '" + titre + "' par " + auteur + " supprimé avec succès"));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Livre non trouvé"));
        }
    }

    // ==================== GESTION DES ERREURS ====================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(Map.of(
                "error", "Erreurs de validation",
                "details", errors,
                "timestamp", LocalDateTime.now()
        ));
    }
}
