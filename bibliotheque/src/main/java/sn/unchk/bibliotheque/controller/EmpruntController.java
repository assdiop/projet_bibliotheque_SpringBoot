package sn.unchk.bibliotheque.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.unchk.bibliotheque.dto.EmpruntDTO;
import sn.unchk.bibliotheque.entity.Livre;
import sn.unchk.bibliotheque.service.EmpruntService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emprunts")
@CrossOrigin(origins = "*")
public class EmpruntController {

    @Autowired
    private EmpruntService empruntService;

    /**
     * Emprunter un livre
     * POST /api/emprunts
     */
    @PostMapping
    public ResponseEntity<?> emprunterLivre(@Valid @RequestBody EmpruntDTO empruntDTO) {
        try {
            EmpruntDTO resultat;
            if (empruntDTO.getDateEmprunt() != null) {
                resultat = empruntService.emprunterLivre(
                        empruntDTO.getUtilisateurId(),
                        empruntDTO.getLivreId(),
                        empruntDTO.getDateEmprunt()
                );
            } else {
                resultat = empruntService.emprunterLivre(
                        empruntDTO.getUtilisateurId(),
                        empruntDTO.getLivreId()
                );
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(resultat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Erreur lors de l'emprunt", e.getMessage()));
        }
    }

    /**
     * Retourner un livre emprunté
     * PUT /api/emprunts/{id}/retour
     */
    @PutMapping("/{id}/retour")
    public ResponseEntity<?> retournerLivre(@PathVariable Long id,
                                            @RequestBody(required = false) DateRetourRequest dateRetour) {
        try {
            EmpruntDTO resultat;
            if (dateRetour != null && dateRetour.getDateRetour() != null) {
                resultat = empruntService.retournerLivre(id, dateRetour.getDateRetour());
            } else {
                resultat = empruntService.retournerLivre(id);
            }
            return ResponseEntity.ok(resultat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Erreur lors du retour", e.getMessage()));
        }
    }

    /**
     * Obtenir tous les emprunts
     * GET /api/emprunts
     */
    @GetMapping
    public ResponseEntity<List<EmpruntDTO>> getAllEmprunts() {
        List<EmpruntDTO> emprunts = empruntService.getAllEmprunts();
        return ResponseEntity.ok(emprunts);
    }

    /**
     * Obtenir un emprunt par son ID
     * GET /api/emprunts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmpruntById(@PathVariable Long id) {
        EmpruntDTO emprunt = empruntService.getEmpruntById(id);
        if (emprunt != null) {
            return ResponseEntity.ok(emprunt);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Emprunt non trouvé", "Aucun emprunt avec l'ID " + id));
    }

    /**
     * Obtenir les emprunts d'un utilisateur
     * GET /api/emprunts/utilisateur/{utilisateurId}
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<EmpruntDTO>> getEmpruntsParUtilisateur(@PathVariable Long utilisateurId) {
        List<EmpruntDTO> emprunts = empruntService.getEmpruntsParUtilisateur(utilisateurId);
        return ResponseEntity.ok(emprunts);
    }

    /**
     * Obtenir tous les livres empruntés (en cours)
     * GET /api/emprunts/en-cours
     */
    @GetMapping("/en-cours")
    public ResponseEntity<List<EmpruntDTO>> getLivresEmpruntes() {
        List<EmpruntDTO> emprunts = empruntService.getLivresEmpruntes();
        return ResponseEntity.ok(emprunts);
    }

    /**
     * Obtenir tous les livres disponibles
     * GET /api/emprunts/livres-disponibles
     */
    @GetMapping("/livres-disponibles")
    public ResponseEntity<List<Livre>> getLivresDisponibles() {
        List<Livre> livres = empruntService.getLivresDisponibles();
        return ResponseEntity.ok(livres);
    }

    /**
     * Obtenir les emprunts en retard
     * GET /api/emprunts/en-retard
     */
    @GetMapping("/en-retard")
    public ResponseEntity<List<EmpruntDTO>> getEmpruntsEnRetard() {
        List<EmpruntDTO> emprunts = empruntService.getEmpruntsEnRetard();
        return ResponseEntity.ok(emprunts);
    }

    /**
     * Obtenir les emprunts à rendre bientôt
     * GET /api/emprunts/a-rendre-bientot?jours={nombreJours}
     */
    @GetMapping("/a-rendre-bientot")
    public ResponseEntity<List<EmpruntDTO>> getEmpruntsARendreBientot(
            @RequestParam(defaultValue = "3") int jours) {
        List<EmpruntDTO> emprunts = empruntService.getEmpruntsARendreBientot(jours);
        return ResponseEntity.ok(emprunts);
    }

    /**
     * Obtenir les statistiques d'emprunts par mois
     * GET /api/emprunts/statistiques/par-mois
     */
    @GetMapping("/statistiques/par-mois")
    public ResponseEntity<List<Object[]>> getStatistiquesParMois() {
        List<Object[]> statistiques = empruntService.getStatistiquesEmpruntsParMois();
        return ResponseEntity.ok(statistiques);
    }

    /**
     * Obtenir le nombre d'emprunts actifs par utilisateur
     * GET /api/emprunts/statistiques/par-utilisateur
     */
    @GetMapping("/statistiques/par-utilisateur")
    public ResponseEntity<List<Object[]>> getStatistiquesParUtilisateur() {
        List<Object[]> statistiques = empruntService.getNombreEmpruntsActifsParUtilisateur();
        return ResponseEntity.ok(statistiques);
    }

    // Classes internes pour les réponses
    public static class ErrorResponse {
        private String error;
        private String message;
        private LocalDate timestamp;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
            this.timestamp = LocalDate.now();
        }

        // Getters
        public String getError() { return error; }
        public String getMessage() { return message; }
        public LocalDate getTimestamp() { return timestamp; }
    }

    public static class DateRetourRequest {
        private LocalDate dateRetour;

        public DateRetourRequest() {}

        public LocalDate getDateRetour() { return dateRetour; }
        public void setDateRetour(LocalDate dateRetour) { this.dateRetour = dateRetour; }
    }
}
