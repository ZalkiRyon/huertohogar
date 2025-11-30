package com.backend.huertohogar.controller;

import com.backend.huertohogar.dto.AuthResponseDTO;
import com.backend.huertohogar.dto.LoginRequestDTO;
import com.backend.huertohogar.dto.UserDataDTO;
import com.backend.huertohogar.model.User;
import com.backend.huertohogar.security.CustomUserDetailsService;
import com.backend.huertohogar.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            // Autenticar usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Cargar detalles del usuario
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            final User user = userDetailsService.getUserByEmail(loginRequest.getEmail());

            // Generar token JWT
            final String jwt = jwtUtil.generateToken(
                    userDetails,
                    user.getId()
            );

            // Crear objeto UserDataDTO con todos los datos del usuario
            UserDataDTO userData = new UserDataDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getNombre(),
                    user.getApellido(),
                    user.getRun(),
                    user.getTelefono(),
                    user.getRegion(),
                    user.getComuna(),
                    user.getDireccion(),
                    user.getComentario(),
                    user.getFechaRegistro(),
                    user.getRol().getNombre(),
                    user.getRol().getId()
            );

            // Crear respuesta con estructura anidada
            AuthResponseDTO response = new AuthResponseDTO(jwt, userData);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error en el servidor: " + e.getMessage());
        }
    }
}
