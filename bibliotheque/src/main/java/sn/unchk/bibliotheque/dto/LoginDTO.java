package sn.unchk.bibliotheque.dto;
import jakarta.validation.constraints.*;

// ================= LOGIN DTO =================
public class LoginDTO {

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;
}
