package com.backend.huertohogar.controller;

import com.backend.huertohogar.dto.AuthResponseDTO;
import com.backend.huertohogar.dto.LoginRequestDTO;
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
                    user.getRol().getNombre(), 
                    user.getId()
            );

            // Crear respuesta con el token y datos del usuario
            AuthResponseDTO response = new AuthResponseDTO(
                    jwt,
                    user.getId(),
                    user.getEmail(),
                    user.getNombre(),
                    user.getApellido(),
                    user.getRol().getNombre(),
                    user.getRol().getId()
            );

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
