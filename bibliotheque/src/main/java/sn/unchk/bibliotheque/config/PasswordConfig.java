package sn.unchk.bibliotheque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration

public class PasswordConfig {


    /**
     * Bean pour l'encodage des mots de passe avec BCrypt
     * BCrypt est recommandé pour le hashage des mots de passe car il est :
     * - Sécurisé contre les attaques par force brute
     * - Adaptatif (le coût peut être augmenté avec le temps)
     * - Génère automatiquement un salt unique pour chaque mot de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength de 12 pour un bon équilibre sécurité/performance
    }
}
