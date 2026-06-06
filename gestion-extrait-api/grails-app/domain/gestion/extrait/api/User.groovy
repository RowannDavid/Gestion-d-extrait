// User.groovy
package gestion.extrait.api

class User {
    String nom
    String prenoms
    String email
    String password
    RoleUser role = RoleUser.USER
    Date dateCreated
    Date lastUpdated

    static constraints = {
        nom blank: false
        prenoms blank: false
        email blank: false, unique: true
        password blank: false
    }

    static mapping = {
        table 'app_user'
    }
}