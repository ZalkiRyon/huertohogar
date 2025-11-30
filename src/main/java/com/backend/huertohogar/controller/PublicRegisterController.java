package com.backend.huertohogar.controller;

import com.backend.huertohogar.dto.EmailValidationRequestDTO;
import com.backend.huertohogar.dto.UserRequestDTO;
import com.backend.huertohogar.dto.UserResponseDTO;
import com.backend.huertohogar.model.User;
import com.backend.huertohogar.repository.UserRepository;
import com.backend.huertohogar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/register")
@CrossOrigin(origins = "*")
public class PublicRegisterController {

    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public PublicRegisterController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Validar si un email es válido y está disponible
     * POST /api/public/register/validate-email
     * Body: { "email": "usuario@duocuc.cl" }
     * Response: { "valid": true/false, "available": true/false, "message": "..." }
     */
    @PostMapping("/validate-email")
    public ResponseEntity<Map<String, Object>> validateEmail(@RequestBody EmailValidationRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        
        String email = request.getEmail();
        
        // Validar que el email no esté vacío
        if (email == null || email.trim().isEmpty()) {
            response.put("valid", false);
            response.put("available", false);
            response.put("message", "Email no proporcionado");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        // Normalizar email
        final String normalizedEmail = email.trim().toLowerCase();
        
        // Validar dominio del email
        boolean isValidDomain = normalizedEmail.endsWith("@duocuc.cl") || 
                               normalizedEmail.endsWith("@profesor.duoc.cl");
        
        if (!isValidDomain) {
            response.put("valid", false);
            response.put("available", false);
            response.put("message", "El email debe terminar en @duocuc.cl o @profesor.duoc.cl");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        try {
            // Verificar si el email ya existe usando el repository
            User existingUser = userRepository.findByEmail(normalizedEmail);
            
            response.put("valid", true);
            response.put("available", existingUser == null);
            
            if (existingUser != null) {
                response.put("message", "El email ya está registrado");
            } else {
                response.put("message", "El email es válido y está disponible");
            }
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("valid", false);
            response.put("available", false);
            response.put("message", "Error al validar email: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Registrar un nuevo usuario desde el formulario público
     * POST /api/public/register/create-user
     * Body: UserRequestDTO (con todos los datos del usuario)
     * Response: UserResponseDTO (usuario creado sin contraseña)
     */
    @PostMapping("/create-user")
    public ResponseEntity<?> createUserRegister(@RequestBody UserRequestDTO userDto) {
        Map<String, String> errorResponse = new HashMap<>();
        
        try {
            // Validar que el roleId sea 2 (cliente) para registro público
            if (userDto.getRole_id() == null) {
                userDto.setRole_id(2); // Por defecto cliente
            } else if (userDto.getRole_id() != 2) {
                errorResponse.put("message", "El registro público solo permite crear usuarios con rol Cliente");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Validar formato del email
            String email = userDto.getEmail();
            if (email == null || email.trim().isEmpty()) {
                errorResponse.put("message", "El email es obligatorio");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            final String normalizedEmail = email.trim().toLowerCase();
            boolean isValidDomain = normalizedEmail.endsWith("@duocuc.cl") || normalizedEmail.endsWith("@profesor.duoc.cl");
            
            if (!isValidDomain) {
                errorResponse.put("message", "El email debe terminar en @duocuc.cl o @profesor.duoc.cl");
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }

            // Actualizar el email normalizado en el DTO
            userDto.setEmail(normalizedEmail);

            // Validar que el email no exista usando el repository
            User existingUserByEmail = userRepository.findByEmail(normalizedEmail);
            if (existingUserByEmail != null) {
                errorResponse.put("message", "El email ya está registrado");
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }

            // Validar que el RUN no exista usando el repository
            if (userDto.getRun() != null && !userDto.getRun().trim().isEmpty()) {
                User existingUserByRun = userRepository.findByRun(userDto.getRun());
                if (existingUserByRun != null) {
                    errorResponse.put("message", "El RUN ya está registrado");
                    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
                }
            }

            // Crear el usuario (el servicio ya tiene todas las validaciones)
            UserResponseDTO newUser = userService.saveUser(userDto);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            errorResponse.put("message", "Error al crear usuario: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
