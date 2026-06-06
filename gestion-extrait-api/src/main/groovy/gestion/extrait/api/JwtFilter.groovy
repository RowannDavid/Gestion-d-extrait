package gestion.extrait.api

import jakarta.servlet.*
import jakarta.servlet.http.*
import org.springframework.stereotype.Component

@Component
class JwtFilter implements Filter {

    // Routes publiques - pas besoin de token
    static final List<String> PUBLIC_ROUTES = [
            "/api/auth/register",
            "/api/auth/login"
    ]

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request
        HttpServletResponse httpResponse = (HttpServletResponse) response

        String path = httpRequest.requestURI

        // ✅ Autoriser les routes publiques
        if (PUBLIC_ROUTES.any { path.startsWith(it) }) {
            chain.doFilter(request, response)
            return
        }

        // ✅ Vérifier le token pour les routes protégées
        String authHeader = httpRequest.getHeader("Authorization")

        if (!authHeader || !authHeader.startsWith("Bearer ")) {
            httpResponse.status = 401
            httpResponse.contentType = "application/json"
            httpResponse.writer.write('{"message": "Token manquant"}')
            return
        }

        String token = authHeader.substring(7)

        if (!JwtTokenUtil.validateToken(token)) {
            httpResponse.status = 401
            httpResponse.contentType = "application/json"
            httpResponse.writer.write('{"message": "Token invalide"}')
            return
        }

        // ✅ Token valide, on continue
        chain.doFilter(request, response)
    }
}