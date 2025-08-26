package sn.unchk.bibliotheque.controller;


import jakarta.persistence.EntityNotFoundException;
import sn.unchk.bibliotheque.dto.AuteurDTO;
import sn.unchk.bibliotheque.entity.Auteur;
import sn.unchk.bibliotheque.exception.BusinessException;
import sn.unchk.bibliotheque.service.AuteurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auteurs")
@CrossOrigin(origins = "*")

public class AuteurController {


    @Autowired
    private AuteurService auteurService;

    /**
     * Créer un nouvel auteur
     * POST /api/auteurs
     */
    @PostMapping
    public ResponseEntity<Auteur> creerAuteur(@Valid @RequestBody AuteurDTO auteurDTO) {
        try {
            Auteur auteur = auteurService.creerAuteurAvecValidation(auteurDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(auteur);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtenir tous les auteurs
     * GET /api/auteurs
     */
    @GetMapping
    public ResponseEntity<List<Auteur>> obtenirTousLesAuteurs() {
        List<Auteur> auteurs = auteurService.obtenirTousLesAuteurs();
        return ResponseEntity.ok(auteurs);
    }

    /**
     * Obtenir un auteur par ID
     * GET /api/auteurs/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<Auteur> obtenirAuteurParId(@PathVariable Long id) {
        try {
            Auteur auteur = auteurService.obtenirAuteurParId(id);
            return ResponseEntity.ok(auteur);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Rechercher des auteurs
     * GET /api/auteurs/recherche?nom=Hugo
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<Auteur>> rechercherAuteurs(@RequestParam(required = false) String nom) {
        List<Auteur> auteurs = auteurService.rechercherAuteurs(nom);
        return ResponseEntity.ok(auteurs);
    }

    /**
     * Mettre à jour un auteur existant
     * PUT /api/auteurs/{id}
     *
     * @param id L'identifiant de l'auteur à modifier
     * @param auteurDTO Les nouvelles données de l'auteur
     * @return L'auteur modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> mettreAJourAuteur(
            @PathVariable Long id,
            @Valid @RequestBody AuteurDTO auteurDTO) {
        try {
            Auteur auteurModifie = auteurService.mettreAJourAuteur(id, auteurDTO);
            return ResponseEntity.ok(auteurModifie);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Auteur avec l'ID " + id + " introuvable");
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erreur de validation : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Paramètres invalides : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la modification de l'auteur");
        }
    }

    /**
     * Supprimer un auteur
     * DELETE /api/auteurs/1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerAuteur(@PathVariable Long id) {
        try {
            auteurService.supprimerAuteur(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
