package com.davidnhn.book.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service // Indique que cette classe est un service Spring
public class JwtService {

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration; // Durée de validité du JWT en millisecondes

    @Value("${application.security.jwt.secret-key}")
    private String secretKey; // Clé secrète utilisée pour signer les JWT

    // Génère un token JWT pour un utilisateur sans claims supplémentaires
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Génère un token JWT pour un utilisateur avec des claims supplémentaires
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, jwtExpiration);
    }

    // Construit le token JWT
    private String buildToken(
            Map<String,Object> extraClaims,
            UserDetails userDetails,
            long jwtExpiration
    ) {
        // Extrait les autorités de l'utilisateur
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Construit le JWT
        return Jwts
                .builder()
                .setClaims(extraClaims) // ça ajoute les claims supplémentaires
                .setSubject(userDetails.getUsername()) // Définit le sujet du token (username)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date d'émission
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Date d'expiration
                .claim("authorities", authorities) // Ajoute les autorités aux claims
                .signWith(getSignInKey()) // Signe le token avec la clé secrète
                .compact(); // Génère le token sous forme de chaîne
    }

    /*
    System.currentTimeMillis() renvoie le nombre de millisecondes écoulées depuis le 1er janvier 1970 (époque UNIX).
     */

    // Vérifie si un token est valide pour un utilisateur donné
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Vérifie si le token a expiré
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extrait la date d'expiration du token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extrait le nom d'utilisateur du token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extrait une claim spécifique du token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrait toutes les claims du token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // Définit la clé de signature pour vérifier le token
                .build()
                .parseClaimsJws(token) // Parse le token
                .getBody(); // Récupère le corps du token (les claims)
    }

    /*
    Si la signature ne correspond pas (parce que le token a été modifié), la méthode parseClaimsJws lèvera une exception, ce qui invalidera le token.
     */

    // Récupère la clé de signature à partir de la clé secrète
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Décode la clé secrète en Base64
        return Keys.hmacShaKeyFor(keyBytes); // Crée une clé HMAC SHA
    }
}

/*
Explication détaillée de getSignInKey() :

1. Décodage de la clé secrète :
   - La variable 'secretKey' est supposée contenir une chaîne encodée en Base64.
   - Decoders.BASE64.decode(secretKey) convertit cette chaîne en un tableau d'octets (byte[]).
   - Cette étape est nécessaire car la clé est généralement stockée en Base64 dans les fichiers
     de configuration pour une meilleure gestion des caractères spéciaux et de la sécurité.

2. Création de l'objet Key :
   - Keys.hmacShaKeyFor(keyBytes) prend le tableau d'octets décodé et crée une instance de Key.
   - Cette méthode ne modifie pas les bytes de la clé et ne les ré-encode pas.
   - Elle crée simplement une implémentation de javax.crypto.SecretKey adaptée aux algorithmes HMAC-SHA.

Pourquoi cette approche ?
- Sécurité : Stocker la clé en Base64 dans la configuration est plus sûr que la clé brute.
- Flexibilité : Permet d'utiliser des clés de différentes longueurs, encodées en Base64.
- Compatibilité : Fournit un objet Key directement utilisable par la bibliothèque JWT.
- Performance : L'objet Key créé est optimisé pour une utilisation répétée dans les opérations JWT.

Note : Cette méthode ne modifie pas la clé elle-même, elle la prépare simplement pour son utilisation
dans les opérations de signature et de vérification des JWT.
*/

/*
 * Fonctionnement de .apply() dans l'extraction des claims :
 *
 * 1. Interface Function<T,R> :
 *    - Interface fonctionnelle représentant une fonction qui prend un argument de type T et retourne un résultat de type R.
 *    - Contient la méthode abstraite R apply(T t).
 *
 * 2. Usage dans extractClaim :
 *    - claimsResolver est une Function<Claims, T> qui prend un objet Claims et retourne un objet de type T.
 *    - claimsResolver.apply(claims) appelle la fonction passée en paramètre sur l'objet claims.
 *
 * 3. Exemples :
 *    a) extractExpiration :
 *       extractClaim(token, Claims::getExpiration)
 *       Claims::getExpiration est une référence de méthode qui retourne la date d'expiration.
 *
 *    b) extractUsername :
 *       extractClaim(token, Claims::getSubject)
 *       Claims::getSubject retourne le sujet du token (ici, le nom d'utilisateur).
 *
 * 4. Avantages :
 *    - Flexibilité : Extraction de différentes informations avec une seule méthode.
 *    - Réutilisabilité : Pas de duplication de code pour extraire différentes claims.
 *    - Lisibilité : Code plus concis et expressif.
 *
 * Cette approche permet d'extraire diverses informations du token JWT de manière générique et flexible.
 */


/*
 * Structure d'un JWT et rôle des claims :
 *
 * 1. Structure d'un JWT :
 *    Un JWT est composé de trois parties séparées par des points : Header.Payload.Signature
 *
 * 2. Payload et Claims :
 *    - Le payload est la deuxième partie du JWT.
 *    - Il contient les "claims", qui sont des déclarations sur l'entité (généralement l'utilisateur) et des métadonnées.
 *
 * 3. Types de claims :
 *    a) Claims réservées : Prédéfinies, comme "iss" (émetteur), "exp" (expiration), "sub" (sujet), etc.
 *    b) Claims publiques : Définies par les utilisateurs de JWT, mais enregistrées dans le registre IANA.
 *    c) Claims privées : Personnalisées, utilisées pour partager des informations spécifiques à l'application.
 *
 * 4. Exemples de claims courantes :
 *    - "sub" : Sujet du token, souvent l'identifiant de l'utilisateur.
 *    - "iat" : Timestamp de l'émission du token.
 *    - "exp" : Timestamp d'expiration du token.
 *    - "authorities" ou "roles" : Souvent utilisées pour stocker les rôles/permissions de l'utilisateur.
 *
 * 5. Sécurité :
 *    - Les claims dans le payload sont lisibles par quiconque possède le token.
 *    - Ne pas stocker d'informations sensibles (comme des mots de passe) dans les claims.
 *    - L'intégrité des claims est protégée par la signature, mais leur contenu n'est pas chiffré.
 *
 * Dans notre code, les méthodes extractClaim, extractUsername, etc., servent à lire
 * ces différentes claims du payload du JWT.
 */