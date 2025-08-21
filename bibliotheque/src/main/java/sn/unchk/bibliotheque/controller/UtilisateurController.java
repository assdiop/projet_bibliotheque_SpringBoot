package sn.unchk.bibliotheque.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import sn.unchk.bibliotheque.dto.UtilisateurDTO;
import sn.unchk.bibliotheque.entity.Utilisateur;
import sn.unchk.bibliotheque.exception.BusinessException;
import sn.unchk.bibliotheque.service.UtilisateurService;

import jakarta.validation.Valid;
import java.util.List;

@RestController      // ✅ API REST avec retour auto en JSON
@RequestMapping("/api/utilisateurs")    // ✅ Préfixe d'URL pour toutes les routes
@CrossOrigin(origins = "*") // Autorise un domaine spécifique du frontend
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Méthode utilitaire pour récupérer l'ID de l'utilisateur connecté
     * À adapter selon votre système d'authentification
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("Utilisateur non authentifié");
        }

        // Adaptez cette logique selon votre implémentation d'authentification
        // Exemple si vous stockez l'ID dans le principal
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Supposons que le username soit l'ID ou que vous ayez une méthode pour récupérer l'ID
        // return Long.valueOf(userDetails.getUsername()); // Si username = ID

        // Ou si vous avez un service pour récupérer l'utilisateur par username
        Utilisateur utilisateur = utilisateurService.rechercherUtilisateurs(userDetails.getUsername()).get(0);
        return utilisateur.getId();
    }

    // ==================== ENDPOINTS PUBLICS (LECTURE) ====================

    /**
     * Obtenir tous les utilisateurs
     * GET /api/utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<Utilisateur>> obtenirTousLesUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.obtenirTousLesUtilisateurs();
            return ResponseEntity.ok(utilisateurs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtenir un utilisateur par ID
     * GET /api/utilisateurs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> obtenirUtilisateurParId(@PathVariable Long id) {
        try {
            Utilisateur utilisateur = utilisateurService.obtenirUtilisateurParId(id);
            return ResponseEntity.ok(utilisateur);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Rechercher des utilisateurs par nom
     * GET /api/utilisateurs/recherche?nom=...
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<Utilisateur>> rechercherUtilisateurs(
            @RequestParam(required = false) String nom) {
        try {
            List<Utilisateur> utilisateurs = utilisateurService.rechercherUtilisateurs(nom);
            return ResponseEntity.ok(utilisateurs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== ENDPOINTS ADMIN SEULEMENT ====================

    /**
     * Créer un nouvel utilisateur (ADMIN seulement)
     * POST /api/utilisateurs/admin
     */
    @PostMapping("/admin")
    public ResponseEntity<?> creerUtilisateur(
            @Valid @RequestBody UtilisateurDTO utilisateurDTO,
            Authentication authentication) {
        try {
            Long idAdmin = getCurrentUserId(authentication);
            Utilisateur nouvelUtilisateur = utilisateurService.creerUtilisateurAvecValidation(utilisateurDTO, idAdmin);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de l'utilisateur");
        }
    }

    /**
     * Mettre à jour un utilisateur (ADMIN seulement)
     * PUT /api/utilisateurs/admin/{id}
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<?> mettreAJourUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurDTO utilisateurDTO,
            Authentication authentication) {
        try {
            Long idAdmin = getCurrentUserId(authentication);
            Utilisateur utilisateurModifie = utilisateurService.mettreAJourUtilisateur(id, utilisateurDTO, idAdmin);
            return ResponseEntity.ok(utilisateurModifie);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé ou erreur lors de la modification");
        }
    }

    /**
     * Supprimer un utilisateur (ADMIN seulement)
     * DELETE /api/utilisateurs/admin/{id}
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> supprimerUtilisateur(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            Long idAdmin = getCurrentUserId(authentication);
            utilisateurService.supprimerUtilisateur(id, idAdmin);
            return ResponseEntity.ok("Utilisateur supprimé avec succès");
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé ou erreur lors de la suppression");
        }
    }

    // ==================== ENDPOINT UTILISATEUR (AUTO-MODIFICATION) ====================

    /**
     * Modifier ses propres informations
     * PUT /api/utilisateurs/mon-profil
     */
    @PutMapping("/mon-profil")
    public ResponseEntity<?> modifierMonProfil(
            @Valid @RequestBody UtilisateurDTO utilisateurDTO,
            Authentication authentication) {
        try {
            Long idUtilisateur = getCurrentUserId(authentication);
            Utilisateur utilisateurModifie = utilisateurService.modifierSesPropresInformations(
                    idUtilisateur, utilisateurDTO, idUtilisateur);
            return ResponseEntity.ok(utilisateurModifie);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la modification du profil");
        }
    }

    /**
     * Obtenir ses propres informations
     * GET /api/utilisateurs/mon-profil
     */
    @GetMapping("/mon-profil")
    public ResponseEntity<?> obtenirMonProfil(Authentication authentication) {
        try {
            Long idUtilisateur = getCurrentUserId(authentication);
            Utilisateur utilisateur = utilisateurService.obtenirUtilisateurParId(idUtilisateur);
            return ResponseEntity.ok(utilisateur);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Profil utilisateur non trouvé");
        }
    }

    // ==================== ENDPOINTS DE TEST (À SUPPRIMER EN PRODUCTION) ====================

    /**
     * Endpoint de test pour créer un utilisateur sans authentification
     * À SUPPRIMER EN PRODUCTION
     */
    @PostMapping("/test")
    public ResponseEntity<?> creerUtilisateurTest(@Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            // Utiliser un ID admin fictif pour les tests
            Long idAdminTest = 1L; // Assurez-vous qu'un utilisateur avec ID=1 et role=ADMIN existe
            Utilisateur nouvelUtilisateur = utilisateurService.creerUtilisateurAvecValidation(utilisateurDTO, idAdminTest);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de l'utilisateur: " + e.getMessage());
        }
    }
}
