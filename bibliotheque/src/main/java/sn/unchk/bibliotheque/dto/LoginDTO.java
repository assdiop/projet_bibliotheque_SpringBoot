package sn.unchk.bibliotheque.dto;
import jakarta.validation.constraints.*;

// ================= LOGIN DTO =================
public class LoginDTO {

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;


    // Constructeurs et getters/setters
    public LoginDTO() {}

    public LoginDTO(String email, String motDePasse) {
        this.email = email;
        this.motDePasse = motDePasse;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

}
