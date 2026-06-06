package gestion.extrait.api

class Extrait {
    String nomFichier
    String cheminFichier
    Date dateGeneration = new Date()

    static belongsTo = [demande: Demande]

    static constraints = {
        nomFichier  blank: false
        cheminFichier blank: false
    }
}