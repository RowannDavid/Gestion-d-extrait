package gestion.extrait.api

import grails.gorm.transactions.Transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Transactional
class AuthService {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder()

    Map register(RegisterRequestDTO dto) {
        if (User.findByEmail(dto.email)) {
            return [success: false, message: "Email déjà utilisé"]
        }

        User user = new User(
                nom: dto.nom,
                prenoms: dto.prenoms,
                email: dto.email,
                password: passwordEncoder.encode(dto.password),
                role: RoleUser.USER
        )

        if (user.save(flush: true)) {
            return [success: true, message: "Inscription réussie"]
        } else {
            return [success: false, message: "Erreur", errors: user.errors]
        }
    }

    Map login(LoginRequestDTO dto) {
        User user = User.findByEmail(dto.email)
        if (!user) {
            return [success: false, message: "Email ou mot de passe incorrect"]
        }

        if (!passwordEncoder.matches(dto.password, user.password)) {
            return [success: false, message: "Email ou mot de passe incorrect"]
        }

        String token = JwtTokenUtil.generateToken(user)

        return [
                success: true,
                token: token,
                user: [
                        id: user.id,
                        nom: user.nom,
                        prenoms: user.prenoms,
                        email: user.email,
                        role: user.role
                ]
        ]
    }
}