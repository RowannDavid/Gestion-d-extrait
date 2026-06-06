// Demande.groovy
package gestion.extrait.api

class Demande {

    String        numeroDemande
    String        nom
    String        prenoms
    Genre         genre
    Date          dateNaissance
    String        villeNaissance
    String        telephone
    String        nomParent
    TypeExtrait   typeExtrait
    String        lieuLivraison
    StatutDemande statut = StatutDemande.BROUILLON
    String        reference
    Date          dateCreated
    Date          lastUpdated

    static belongsTo = [utilisateur: User]

    static constraints = {
        numeroDemande nullable: true
        reference     nullable: true
        telephone     nullable: true
        lieuLivraison nullable: true
        nom           blank: false
        prenoms       blank: false
        villeNaissance blank: false
        nomParent     blank: false
    }
}