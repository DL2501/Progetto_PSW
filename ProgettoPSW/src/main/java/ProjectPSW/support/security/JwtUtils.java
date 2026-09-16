package ProjectPSW.support.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class JwtUtils {


    public static String getNomeUtente() {
        Authentication autenticazione = SecurityContextHolder.getContext().getAuthentication();
        if (!(autenticazione instanceof JwtAuthenticationToken tokenAutenticazione))
            return null;
        Jwt jwt = (Jwt) tokenAutenticazione.getCredentials();
        return (String) jwt.getClaims().get("preferred_username");
    }


    public static String getEmail() {
        Authentication autenticazione = SecurityContextHolder.getContext().getAuthentication();
        if (!(autenticazione instanceof JwtAuthenticationToken tokenAutenticazione))
            return null;
        Jwt jwt = (Jwt) tokenAutenticazione.getCredentials();
        return (String) jwt.getClaims().get("email");
    }


    public static String getNome() {
        Authentication autenticazione = SecurityContextHolder.getContext().getAuthentication();
        if (!(autenticazione instanceof JwtAuthenticationToken tokenAutenticazione))
            return null;
        Jwt jwt = (Jwt) tokenAutenticazione.getCredentials();
        return (String) jwt.getClaims().get("given_name");
    }



    public static String getCognome() {
        Authentication autenticazione = SecurityContextHolder.getContext().getAuthentication();
        if (!(autenticazione instanceof JwtAuthenticationToken tokenAutenticazione))
            return null;
        Jwt jwt = (Jwt) tokenAutenticazione.getCredentials();
        return (String) jwt.getClaims().get("family_name");
    }



























}
