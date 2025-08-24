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

    // ==================== ENDPOINTS CRUD ====================

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

    /**
     * Créer un nouvel utilisateur
     * POST /api/utilisateurs
     */
    @PostMapping
    public ResponseEntity<?> creerUtilisateur(@Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            Utilisateur nouvelUtilisateur = utilisateurService.creerUtilisateurAvecValidation(utilisateurDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création de l'utilisateur");
        }
    }

    /**
     * Mettre à jour un utilisateur
     * PUT /api/utilisateurs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> mettreAJourUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            Utilisateur utilisateurModifie = utilisateurService.mettreAJourUtilisateur(id, utilisateurDTO);
            return ResponseEntity.ok(utilisateurModifie);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé ou erreur lors de la modification");
        }
    }

    /**
     * Supprimer un utilisateur
     * DELETE /api/utilisateurs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerUtilisateur(@PathVariable Long id) {
        try {
            utilisateurService.supprimerUtilisateur(id);
            return ResponseEntity.ok("Utilisateur supprimé avec succès");
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur non trouvé ou erreur lors de la suppression");
        }
    }

    /**
     * Modifier les informations d'un utilisateur
     * PUT /api/utilisateurs/modifier/{id}
     */
    @PutMapping("/modifier/{id}")
    public ResponseEntity<?> modifierInformationsUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            Utilisateur utilisateurModifie = utilisateurService.modifierInformationsUtilisateur(id, utilisateurDTO);
            return ResponseEntity.ok(utilisateurModifie);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la modification des informations");
        }
    }
}
