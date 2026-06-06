// Historique.groovy
package gestion.extrait.api

class Historique {
    String action
    Date dateAction = new Date()

    static belongsTo = [
        utilisateur: User,
        demande: Demande
    ]
}