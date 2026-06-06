package gestion.extrait.api

import grails.gorm.transactions.Transactional

@Transactional
class DemandeService {

    // Générer référence unique
    String genererReference() {
        return "EXT-" + UUID.randomUUID().toString().toUpperCase().substring(0, 8)
    }

    // Créer une demande
    Demande creerDemande(User user, DemandeRequestDTO dto) {
        Demande demande = new Demande(
                utilisateur:   user,
                nom:           dto.nom,
                prenoms:       dto.prenoms,
                genre:         dto.genre,
                dateNaissance: dto.dateNaissance,
                villeNaissance: dto.villeNaissance,
                telephone:     dto.telephone,
                nomParent:     dto.nomParent,
                typeExtrait:   dto.typeExtrait,
                lieuLivraison: dto.lieuLivraison,
                reference:     genererReference(),
                statut:        StatutDemande.BROUILLON
        )

        if (!demande.save(flush: true)) {
            throw new RuntimeException("Erreur création : ${demande.errors}")
        }
        return demande
    }

    // Mes demandes
    List<Demande> mesDemandes(User user) {
        return Demande.findAllByUtilisateur(user)
    }

    // Détail d'une demande
    Demande trouverParId(Long id, User user) {
        Demande demande = Demande.get(id)
        if (!demande || demande.utilisateur.id != user.id) {
            throw new RuntimeException("Demande introuvable")
        }
        return demande
    }

    // Modifier une demande
    Demande modifierDemande(Long id, User user, DemandeRequestDTO dto) {
        Demande demande = trouverParId(id, user)

        if (demande.statut != StatutDemande.BROUILLON) {
            throw new RuntimeException("Impossible de modifier une demande déjà soumise")
        }

        demande.nom           = dto.nom
        demande.prenoms       = dto.prenoms
        demande.genre         = dto.genre
        demande.dateNaissance = dto.dateNaissance
        demande.villeNaissance = dto.villeNaissance
        demande.telephone     = dto.telephone
        demande.nomParent     = dto.nomParent
        demande.typeExtrait   = dto.typeExtrait
        demande.lieuLivraison = dto.lieuLivraison

        demande.save(flush: true)
        return demande
    }

    // Soumettre une demande
    Demande soumettre(Long id, User user) {
        Demande demande = trouverParId(id, user)

        if (demande.statut != StatutDemande.BROUILLON) {
            throw new RuntimeException("Demande déjà soumise")
        }

        demande.statut = StatutDemande.EN_TRAITEMENT
        demande.save(flush: true)
        return demande
    }

    // Accepter une demande - ADMIN
    Demande accepter(Long id) {
        Demande demande = Demande.get(id)
        if (!demande) {
            throw new RuntimeException("Demande introuvable")
        }
        if (demande.statut != StatutDemande.EN_TRAITEMENT) {
            throw new RuntimeException("La demande doit être EN_TRAITEMENT pour être acceptée")
        }
        demande.statut = StatutDemande.ACCEPTE
        demande.save(flush: true)
        return demande
    }

// Refuser une demande - ADMIN
    Demande refuser(Long id) {
        Demande demande = Demande.get(id)
        if (!demande) {
            throw new RuntimeException("Demande introuvable")
        }
        if (demande.statut != StatutDemande.EN_TRAITEMENT) {
            throw new RuntimeException("La demande doit être EN_TRAITEMENT pour être refusée")
        }
        demande.statut = StatutDemande.REFUSE
        demande.save(flush: true)
        return demande
    }
}