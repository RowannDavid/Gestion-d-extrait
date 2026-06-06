package gestion.extrait.api


class SecurityHelper {

    // Récupérer l'utilisateur connecté depuis le token
    static User utilisateurConnecte(def request) {
        String authHeader = request.getHeader("Authorization")
        if (!authHeader || !authHeader.startsWith("Bearer ")) {
            return null
        }
        String token = authHeader.substring(7)
        if (!JwtTokenUtil.validateToken(token)) {
            return null
        }
        String email = JwtTokenUtil.extractEmail(token)
        return User.findByEmail(email)
    }

    // Vérifier si l'utilisateur est ADMIN
    static boolean isAdmin(def request) {
        User user = utilisateurConnecte(request)
        return user?.role == RoleUser.ADMIN
    }

    // Vérifier si l'utilisateur est USER
    static boolean isUser(def request) {
        User user = utilisateurConnecte(request)
        return user?.role == RoleUser.USER
    }

}