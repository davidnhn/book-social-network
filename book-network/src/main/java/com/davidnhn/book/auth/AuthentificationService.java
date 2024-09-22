package com.davidnhn.book.auth;

import com.davidnhn.book.email.EmailService;
import com.davidnhn.book.email.EmailTemplate;
import com.davidnhn.book.role.RoleRepository;
import com.davidnhn.book.security.JwtService;
import com.davidnhn.book.user.Token;
import com.davidnhn.book.user.TokenRepository;
import com.davidnhn.book.user.User;
import com.davidnhn.book.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthentificationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    // Injection de la valeur de l'URL d'activation depuis les propriétés de l'application
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    // Méthode pour enregistrer un nouvel utilisateur
    public void register(RegistrationRequest request) throws MessagingException {
        // Recherche du rôle "USER"
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));
        // todo - better execption handling

        // Construction de l'objet User
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build()
                ;
        userRepository.save(user);
        sendValidationEmail(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        // authentication a fonctionné
        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.fullName());
        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                // exception has to be defined
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token expired. A new token has been sent to the same address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private String generateAndSaveActivationToken(User user) {
        log.info("Generating new activation token for user: {}", user.getEmail());
        user = userRepository.findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        // generate token
        String generatedToken = generateActivationCode(6);
        log.info("Generated token: {}", generatedToken);
        // Construction de l'objet Token
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        // Sauvegarde du token dans la base de données
        Token savedToken = tokenRepository.save(token);
        log.info("Token saved with ID: {}", savedToken.getId());
        if (savedToken.getId() == null) {
            throw new RuntimeException("Failed to save activation token");
        }
        return generatedToken;

    }

    // Méthode pour envoyer l'email de validation
    private void sendValidationEmail(User user) throws MessagingException {
        // Génération et sauvegarde du token d'activation
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplate.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );

    }



    // Méthode pour générer un code d'activation aléatoire
    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); // 0..9
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }





}

/*
 *   Génère un code d'activation aléatoire sécurisé.
 * Utilisation de SecureRandom :
 * - SecureRandom est une classe Java pour la génération de nombres aléatoires cryptographiquement sécurisés.
 * - Avantages par rapport à Random :
 *   1. Utilise des sources d'entropie du système pour une meilleure imprévisibilité.
 *   2. Résiste aux attaques statistiques et cryptographiques.
 *   3. Idéal pour les applications de sécurité comme la génération de tokens.
 *
 * Dans ce contexte :
 * - Assure que les codes d'activation sont véritablement aléatoires et difficiles à prédire.
 * - Renforce la sécurité du processus d'activation du compte.
 * - Réduit le risque de génération ou de devinette de codes valides.
 *
 * Note : SecureRandom est généralement plus lent que Random, mais la sécurité prime ici sur la performance.
 */

/*
 * Cette méthode authentifie un utilisateur en utilisant l'AuthenticationManager de Spring Security.

 * Processus d'authentification :
 * 1. Création d'un token d'authentification :
 *    - La méthode reçoit une requête d'authentification contenant l'email et le mot de passe.
 *    - Un `UsernamePasswordAuthenticationToken` est créé à partir de ces informations.

 * 2. Authentification par l'AuthenticationManager :
 *    - Le `AuthenticationManager` tente d'authentifier le token d'authentification.
 *    - Cela implique la vérification des informations d'identification (email et mot de passe) en utilisant les détails de l'utilisateur chargés par `UserDetailsServiceImpl` et la comparaison des mots de passe encodés via le `PasswordEncoder`.

 * 3. Authentification réussie :
 *    - Si les informations d'identification sont correctes, l'authentification réussit et un objet `Authentication` est retourné.
 *    - L'objet `Authentication` contient des informations sur l'utilisateur authentifié, y compris ses rôles et privilèges.

 * 4. Génération du token JWT :
 *    - Si l'authentification réussit, un token JWT est généré pour l'utilisateur authentifié.
 *    - Des claims supplémentaires peuvent être ajoutés au token JWT, comme le nom complet de l'utilisateur.

 * 5. Retour de la réponse d'authentification :
 *    - La réponse contient le token JWT généré, qui peut être utilisé pour des requêtes futures nécessitant une authentification.

 */