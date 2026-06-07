package gestion.extrait.api

import grails.converters.JSON
import java.text.SimpleDateFormat

class AdminController {

    DemandeService demandeService
    PdfService pdfService  // ✅ Ajoute cette ligne en haut

    static responseFormats = ['json']

    private String formatDate(Date date) {
        if (!date) return null
        return new SimpleDateFormat("yyyy-MM-dd").format(date)
    }

    // Vérifier que c'est bien un ADMIN
    private User checkAdmin() {
        User user = SecurityHelper.utilisateurConnecte(request)
        if (!user || user.role != RoleUser.ADMIN) return null
        return user
    }

    // GET /api/admin/demandes - Toutes les demandes
    def index() {
        User admin = checkAdmin()
        if (!admin) {
            response.status = 403
            render([message: "Accès réservé aux administrateurs"] as JSON)
            return
        }

        List<Demande> demandes = Demande.list()
        response.status = 200
        render(demandes.collect {[
                id:            it.id,
                reference:     it.reference,
                statut:        it.statut?.toString(),
                nom:           it.nom,
                prenoms:       it.prenoms,
                typeExtrait:   it.typeExtrait?.toString(),
                dateNaissance: formatDate(it.dateNaissance),
                utilisateur: [
                        id:      it.utilisateur?.id,
                        nom:     it.utilisateur?.nom,
                        prenoms: it.utilisateur?.prenoms,
                        email:   it.utilisateur?.email
                ]
        ]} as JSON)
    }

    // GET /api/admin/demandes/{id} - Détail
    def show(Long id) {
        User admin = checkAdmin()
        if (!admin) {
            response.status = 403
            render([message: "Accès réservé aux administrateurs"] as JSON)
            return
        }

        Demande demande = Demande.get(id)
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
                lieuLivraison:  demande.lieuLivraison,
                dateCreated:    formatDate(demande.dateCreated),
                utilisateur: [
                        id:      demande.utilisateur?.id,
                        nom:     demande.utilisateur?.nom,
                        prenoms: demande.utilisateur?.prenoms,
                        email:   demande.utilisateur?.email
                ]
        ] as JSON)
    }

    // GET /api/admin/demandes/statut/{statut} - Filtrer par statut
    def parStatut(String statut) {
        User admin = checkAdmin()
        if (!admin) {
            response.status = 403
            render([message: "Accès réservé aux administrateurs"] as JSON)
            return
        }

        try {
            StatutDemande statutEnum = StatutDemande.valueOf(statut.toUpperCase())
            List<Demande> demandes = Demande.findAllByStatut(statutEnum)

            response.status = 200
            render(demandes.collect {[
                    id:          it.id,
                    reference:   it.reference,
                    statut:      it.statut?.toString(),
                    nom:         it.nom,
                    prenoms:     it.prenoms,
                    typeExtrait: it.typeExtrait?.toString(),
                    dateCreated: formatDate(it.dateCreated),
                    utilisateur: [
                            nom:     it.utilisateur?.nom,
                            prenoms: it.utilisateur?.prenoms,
                            email:   it.utilisateur?.email
                    ]
            ]} as JSON)
        } catch (Exception e) {
            response.status = 400
            render([message: "Statut invalide. Valeurs: BROUILLON, EN_TRAITEMENT, ACCEPTE, REFUSE"] as JSON)
        }
    }

    // POST /api/admin/demandes/{id}/accepter
    def accepter(Long id) {
        User admin = checkAdmin()
        if (!admin) {
            response.status = 403
            render([message: "Accès réservé aux administrateurs"] as JSON)
            return
        }

        Demande demande = demandeService.accepter(id)
        response.status = 200
        render([
                message:   "Demande acceptée avec succès",
                id:        demande.id,
                reference: demande.reference,
                statut:    demande.statut?.toString()
        ] as JSON)
    }

    // POST /api/admin/demandes/{id}/refuser
    def refuser(Long id) {
        User admin = checkAdmin()
        if (!admin) {
            response.status = 403
            render([message: "Accès réservé aux administrateurs"] as JSON)
            return
        }

        Demande demande = demandeService.refuser(id)
        response.status = 200
        render([
                message:   "Demande refusée",
                id:        demande.id,
                reference: demande.reference,
                statut:    demande.statut?.toString()
        ] as JSON)
    }


// POST /api/admin/demandes/{id}/generer-pdf
    def genererPdf(Long id) {
        User admin = checkAdmin()
        if (!admin) {
            response.status = 403
            render([message: "Accès réservé aux administrateurs"] as JSON)
            return
        }

        Demande demande = Demande.get(id)
        if (!demande) {
            response.status = 404
            render([message: "Demande introuvable"] as JSON)
            return
        }

        if (demande.statut != StatutDemande.ACCEPTE) {
            response.status = 400
            render([message: "La demande doit être ACCEPTÉE pour générer le PDF"] as JSON)
            return
        }

        String cheminFichier = pdfService.genererExtrait(demande)
        response.status = 200
        render([
                message:       "PDF généré avec succès",
                reference:     demande.reference,
                fichier:       cheminFichier
        ] as JSON)
    }

}