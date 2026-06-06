package gestion.extrait.api

import grails.converters.JSON

class AuthController {

    AuthService authService
    static responseFormats = ['json']

    def register() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                nom:      request.JSON.nom,
                prenoms:  request.JSON.prenoms,
                email:    request.JSON.email,
                password: request.JSON.password
        )

        Map result = authService.register(dto)

        if (result.success) {
            response.status = 201
            render([message: result.message] as JSON)
        } else {
            response.status = 400
            render([message: result.message] as JSON)
        }
    }

    def login() {
        LoginRequestDTO dto = new LoginRequestDTO(
                email:    request.JSON.email,
                password: request.JSON.password
        )

        Map result = authService.login(dto)

        if (result.success) {
            response.status = 200
            render([
                    token: result.token,
                    user:  result.user
            ] as JSON)
        } else {
            response.status = 401
            render([message: result.message] as JSON)
        }
    }
}