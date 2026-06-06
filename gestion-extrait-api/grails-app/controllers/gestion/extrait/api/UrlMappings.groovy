package gestion.extrait.api

class UrlMappings {
    static mappings = {

        // Auth - public
        "/api/auth/register"(controller: "auth", action: "register", method: "POST")
        "/api/auth/login"(controller: "auth", action: "login", method: "POST")

        // Demandes - USER
        "/api/demandes"(controller: "demande", action: "save",       method: "POST")
        "/api/demandes"(controller: "demande", action: "index",      method: "GET")
        "/api/demandes/$id"(controller: "demande", action: "show",   method: "GET")
        "/api/demandes/$id"(controller: "demande", action: "update", method: "PUT")
        "/api/demandes/$id/soumettre"(controller: "demande",  action: "soumettre",  method: "POST")
        "/api/demandes/$id/telecharger"(controller: "demande", action: "telecharger", method: "GET")

        // Admin
        "/api/admin/demandes"(controller: "admin", action: "index",      method: "GET")
        "/api/admin/demandes/$id"(controller: "admin", action: "show",   method: "GET")
        "/api/admin/demandes/statut/$statut"(controller: "admin", action: "parStatut",  method: "GET")
        "/api/admin/demandes/$id/accepter"(controller: "admin",   action: "accepter",   method: "POST")
        "/api/admin/demandes/$id/refuser"(controller: "admin",    action: "refuser",    method: "POST")
        "/api/admin/demandes/$id/generer-pdf"(controller: "admin", action: "genererPdf", method: "POST")

        "/"(view: "/index")
        "500"(view: "/error")
        "404"(view: "/notFound")
    }
}