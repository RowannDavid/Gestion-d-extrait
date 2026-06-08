package gestion.extrait.api

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class BootStrap {

    def init = { servletContext ->

        User.withTransaction {
            if (!User.findByEmail("admin@mairie.ci")) {
                User admin = new User(
                        nom: "Admin",
                        prenoms: "System",
                        email: "admin@mairie.ci",
                        password: new BCryptPasswordEncoder().encode("Admin123"),
                        role: RoleUser.ADMIN
                )
                if (admin.save(flush: true)) {
                    println "Administrateur créé avec succès"
                } else {
                    println "Erreur : ${admin.errors}"
                }
            } else {
                println "ℹ️ Administrateur déjà existant"
            }
        }
    }

    def destroy = {}
}