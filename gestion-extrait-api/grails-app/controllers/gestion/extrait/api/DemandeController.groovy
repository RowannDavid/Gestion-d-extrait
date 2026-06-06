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
        if (!user) { response.status = 401; render([message: "Non autorisé"] as JSON); return }
        if (user.role != RoleUser.USER) { response.status = 403; render([message: "Réservé aux utilisateurs"] as JSON); return }

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
                statut:        demande.statut,
                nom:           demande.nom,
                prenoms:       demande.prenoms,
                typeExtrait:   demande.typeExtrait,
                dateNaissance: formatDate(demande.dateNaissance)
        ] as JSON)
    }

    def index() {
        User user = SecurityHelper.utilisateurConnecte(request)
        if (!user) { response.status = 401; render([message: "Non autorisé"] as JSON); return }

        List<Demande> demandes = (user.role == RoleUser.ADMIN) ? Demande.list() : demandeService.mesDemandes(user)

        response.status = 200
        render(demandes.collect {[
                id:            it.id,
                reference:     it.reference,
                statut:        it.statut,
                nom:           it.nom,
                prenoms:       it.prenoms,
                typeExtrait:   it.typeExtrait,
                dateNaissance: formatDate(it.dateNaissance)
        ]} as JSON)
    }

    def show(Long id) {
        User user = SecurityHelper.utilisateurConnecte(request)
        if (!user) { response.status = 401; render([message: "Non autorisé"] as JSON); return }

        Demande demande = (user.role == RoleUser.ADMIN) ? Demande.get(id) : demandeService.trouverParId(id, user)
        if (!demande) { response.status = 404; render([message: "Demande introuvable"] as JSON); return }

        response.status = 200
        render([
                id:             demande.id,
                reference:      demande.reference,
                statut:         demande.statut,
                nom:            demande.nom,
                prenoms:        demande.prenoms,
                genre:          demande.genre,
                dateNaissance:  formatDate(demande.dateNaissance),
                villeNaissance: demande.villeNaissance,
                telephone:      demande.telephone,
                nomParent:      demande.nomParent,
                typeExtrait:    demande.typeExtrait,
                lieuLivraison:  demande.lieuLivraison
        ] as JSON)
    }

    def update(Long id) {
        User user = SecurityHelper.utilisateurConnecte(request)
        if (!user) { response.status = 401; render([message: "Non autorisé"] as JSON); return }
        if (user.role != RoleUser.USER) { response.status = 403; render([message: "Réservé aux utilisateurs"] as JSON); return }

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
        render([id: demande.id, reference: demande.reference, statut: demande.statut] as JSON)
    }

    def soumettre(Long id) {
        User user = SecurityHelper.utilisateurConnecte(request)
        if (!user) { response.status = 401; render([message: "Non autorisé"] as JSON); return }
        if (user.role != RoleUser.USER) { response.status = 403; render([message: "Réservé aux utilisateurs"] as JSON); return }

        Demande demande = demandeService.soumettre(id, user)
        response.status = 200
        render([id: demande.id, reference: demande.reference, statut: demande.statut] as JSON)
    }

    // GET /api/admin/demandes/{id}/telecharger
    def telecharger(Long id) {
        User admin = checkAdmin()
        if (!admin) {
            response.status = 403
            render([message: "Accès réservé aux administrateurs"] as JSON)
            return
        }

        Extrait extrait = Extrait.findByDemande(Demande.get(id))
        if (!extrait) {
            response.status = 404
            render([message: "PDF non trouvé, générez-le d'abord"] as JSON)
            return
        }

        File fichier = new File(extrait.cheminFichier)
        if (!fichier.exists()) {
            response.status = 404
            render([message: "Fichier introuvable sur le serveur"] as JSON)
            return
        }

        response.contentType = "application/pdf"
        response.setHeader("Content-Disposition", "attachment; filename=${extrait.nomFichier}")
        response.outputStream << fichier.bytes
        response.outputStream.flush()
    }
}