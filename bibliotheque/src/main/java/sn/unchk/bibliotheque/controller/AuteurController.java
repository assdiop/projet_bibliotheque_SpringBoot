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

@RestController
@RequestMapping("/api/auteurs")
@CrossOrigin(origins = "http://localhost:8080")

public class AuteurController {

    @Autowired
    private AuteurService auteurService;

    /**
     * Créer un nouvel auteur
     * POST /api/auteurs
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Seuls les admins peuvent créer des auteurs
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
     * Mettre à jour un auteur
     * PUT /api/auteurs/1
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Auteur> mettreAJourAuteur(@PathVariable Long id,
                                                    @Valid @RequestBody AuteurDTO auteurDTO) {
        try {
            Auteur auteur = auteurService.mettreAJourAuteur(id, auteurDTO);
            return ResponseEntity.ok(auteur);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprimer un auteur
     * DELETE /api/auteurs/1
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
