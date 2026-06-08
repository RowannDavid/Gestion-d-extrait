package gestion.extrait.api

import grails.converters.JSON
import java.text.SimpleDateFormat

class DemandeController {

    DemandeService demandeService
    static responseFormats = ['json']

    private String formatDate(Date date) {
        if (!date) return null
        return new SimpleDateFormat("yyyy-MM-dd").format(date)
    }

    private Date parseDate(String dateStr) {
        if (!dateStr) return null
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr)
        } catch (Exception e) {
            return null
        }
    }

    def save() {
        User user = SecurityHelper.utilisateurConnecte(request)
        if (!user) {
            response.status = 401
            render([message: "Non autorisé"] as JSON)
            return
        }

        if (user.role != RoleUser.USER) {
            response.status = 403
            render([message: "Réservé aux utilisateurs"] as JSON)
            return
        }

        DemandeRequestDTO dto = new DemandeRequestDTO(
                nom:            request.JSON.nom,
                prenoms:        request.JSON.prenoms,
                genre:          request.JSON.genre as Genre,
                dateNaissance:  parseDate(request.JSON.dateNaissance),
                villeNaissance: request.JSON.villeNaissance,
                telephone:      request.JSON.telephone,
                nomParent:      request.JSON.nomParent,
                typeExtrait:    request.JSON.typeExtrait as TypeExtrait,
                lieuLivraison:  request.JSON.lieuLivraison
        )

        Demande demande = demandeService.creerDemande(user, dto)

        response.status = 201
        render([
                id:            demande.id,
                reference:     demande.reference,
                statut:        demande.statut?.toString(),
                nom:           demande.nom,
                prenoms:       demande.prenoms,
                typeExtrait:   demande.typeExtrait?.toString(),
                dateNaissance: formatDate(demande.dateNaissance)
        ] as JSON)
    }

    def index() {
        User user = SecurityHelper.utilisateurConnecte(request)

        if (!user) {
            response.status = 401
            render([message: "Non autorisé"] as JSON)
            return
        }

        List<Demande> demandes = (user.role == RoleUser.ADMIN)
                ? Demande.list()
                : demandeService.mesDemandes(user)

        response.status = 200

        render(demandes.collect { [
                id:            it.id,
                reference:     it.reference,
                statut:        it.statut?.toString(),
                nom:           it.nom,
                prenoms:       it.prenoms,
                typeExtrait:   it.typeExtrait?.toString(),
                dateNaissance: formatDate(it.dateNaissance)
        ] } as JSON)
    }

    def show(Long id) {
        User user = SecurityHelper.utilisateurConnecte(request)

        if (!user) {
            response.status = 401
            render([message: "Non autorisé"] as JSON)
            return
        }

        Demande demande = (user.role == RoleUser.ADMIN)
                ? Demande.get(id)
                : demandeService.trouverParId(id, user)

        if (!demande) {
            response.status = 404
            render([message: "Demande introuvable"] as JSON)
            return
        }

        response.status = 200

        render([
                id:             demande.id,
                reference:      demande.reference,
                statut:         demande.statut?.toString(),
                nom:            demande.nom,
                prenoms:        demande.prenoms,
                genre:          demande.genre?.toString(),
                dateNaissance:  formatDate(demande.dateNaissance),
                villeNaissance: demande.villeNaissance,
                telephone:      demande.telephone,
                nomParent:      demande.nomParent,
                typeExtrait:    demande.typeExtrait?.toString(),
                lieuLivraison:  demande.lieuLivraison
        ] as JSON)
    }

    def update(Long id) {
        User user = SecurityHelper.utilisateurConnecte(request)

        if (!user) {
            response.status = 401
            render([message: "Non autorisé"] as JSON)
            return
        }

        if (user.role != RoleUser.USER) {
            response.status = 403
            render([message: "Réservé aux utilisateurs"] as JSON)
            return
        }

        DemandeRequestDTO dto = new DemandeRequestDTO(
                nom:            request.JSON.nom,
                prenoms:        request.JSON.prenoms,
                genre:          request.JSON.genre as Genre,
                dateNaissance:  parseDate(request.JSON.dateNaissance),
                villeNaissance: request.JSON.villeNaissance,
                telephone:      request.JSON.telephone,
                nomParent:      request.JSON.nomParent,
                typeExtrait:    request.JSON.typeExtrait as TypeExtrait,
                lieuLivraison:  request.JSON.lieuLivraison
        )

        Demande demande = demandeService.modifierDemande(id, user, dto)

        response.status = 200
        render([
                id: demande.id,
                reference: demande.reference,
                statut: demande.statut?.toString()
        ] as JSON)
    }

    def soumettre(Long id) {
        User user = SecurityHelper.utilisateurConnecte(request)

        if (!user) {
            response.status = 401
            render([message: "Non autorisé"] as JSON)
            return
        }

        if (user.role != RoleUser.USER) {
            response.status = 403
            render([message: "Réservé aux utilisateurs"] as JSON)
            return
        }

        Demande demande = demandeService.soumettre(id, user)

        response.status = 200
        render([
                id: demande.id,
                reference: demande.reference,
                statut: demande.statut?.toString()
        ] as JSON)
    }

    // GET /api/demandes/{id}/telecharger - USER télécharge son extrait
    def telecharger(Long id) {
        // ✅ Utilise utilisateurConnecte() au lieu de checkAdmin()
        User user = SecurityHelper.utilisateurConnecte(request)

        if (!user) {
            response.status = 401
            render([message: "Non autorisé"] as JSON)
            return
        }

        // Vérifier que la demande appartient à l'utilisateur
        Demande demande = demandeService.trouverParId(id, user)
        if (!demande) {
            response.status = 404
            render([message: "Demande introuvable"] as JSON)
            return
        }

        // Vérifier que la demande est ACCEPTEE
        if (demande.statut != StatutDemande.ACCEPTE) {
            response.status = 400
            render([message: "Votre demande n'est pas encore acceptée. Statut actuel : ${demande.statut}"] as JSON)
            return
        }

        // Chercher l'extrait PDF
        Extrait extrait = Extrait.findByDemande(demande)
        if (!extrait) {
            response.status = 404
            render([message: "Extrait non disponible, contactez l'administration"] as JSON)
            return
        }

        File fichier = new File(extrait.cheminFichier)
        if (!fichier.exists()) {
            response.status = 404
            render([message: "Fichier introuvable sur le serveur"] as JSON)
            return
        }

        // ✅ Téléchargement
        response.contentType = "application/pdf"
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200")
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition")
        response.setHeader("Content-Disposition", "attachment; filename=${extrait.nomFichier}")
        response.outputStream << fichier.bytes
        response.outputStream.flush()
    }
}