package com.backend.huertohogar.service;

import com.backend.huertohogar.dto.UserRequestDTO;
import com.backend.huertohogar.dto.UserResponseDTO;
import com.backend.huertohogar.exception.ResourceNotFoundException;
import com.backend.huertohogar.exception.ValidationException;
import com.backend.huertohogar.model.Rol;
import com.backend.huertohogar.model.User;
import com.backend.huertohogar.repository.RolRepository;
import com.backend.huertohogar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private static final Pattern RUN_PATTERN = Pattern.compile("^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$");

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RolRepository rolRepository) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserResponseDTO> findUserById(Integer id) {
        return userRepository.findById(id).map(UserResponseDTO::new);
    }

    @Override
    public UserResponseDTO saveUser(UserRequestDTO userDto) {
        Rol rol = rolRepository.findById(userDto.getRole_id())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + userDto.getRole_id()));

        User user = new User();

        String email = userDto.getEmail();
        String password = userDto.getPassword();

        if (userDto.getNombre() == null || userDto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El Nombre es obligatorio y no puede estar vacío.");
        }
        if (userDto.getApellido() == null || userDto.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El Apellido es obligatorio y no puede estar vacío.");
        }
        if (userDto.getRun() == null || userDto.getRun().trim().isEmpty()) {
            throw new IllegalArgumentException("El RUN es obligatorio y no puede estar vacío.");
        }
        if (!RUN_PATTERN.matcher(userDto.getRun()).matches()) {
            throw new IllegalArgumentException(
                    "El formato del RUN no es válido. Debe usar puntos y guión (ej: 12.345.678-K).");
        }
        if (userDto.getRegion() == null || userDto.getRegion().trim().isEmpty()) {
            throw new IllegalArgumentException("La Región es obligatoria y no puede estar vacía.");
        }
        if (userDto.getComuna() == null || userDto.getComuna().trim().isEmpty()) {
            throw new IllegalArgumentException("La Comuna es obligatoria y no puede estar vacía.");
        }
        if (userDto.getDireccion() == null || userDto.getDireccion().trim().isEmpty()
                || userDto.getDireccion().length() < 5) {
            throw new IllegalArgumentException(
                    "La Dirección es obligatoria, no puede estar vacía y debe tener al menos 5 caracteres.");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria y debe tener un largo mínimo de 4 caracteres.");
        }

        user.setEmail(email);
        user.setPassword(password);
        user.setNombre(userDto.getNombre());
        user.setApellido(userDto.getApellido());
        user.setRun(userDto.getRun());
        user.setTelefono(userDto.getTelefono());
        user.setRegion(userDto.getRegion());
        user.setComuna(userDto.getComuna());
        user.setDireccion(userDto.getDireccion());
        user.setComentario(userDto.getComentario());
        user.setRol(rol);
        user.setFechaRegistro(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return new UserResponseDTO(savedUser);
    }

    @Override
    public void deleteUser(Integer id) {
        if (userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("No se puede eliminar. Usuario no encontrado con ID: " + id);
        }
    }

    @Override
    public UserResponseDTO updateUser(Integer id, UserRequestDTO userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        Rol rol = rolRepository.findById(userDto.getRole_id())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + userDto.getRole_id()));

        String newEmail = userDto.getEmail();
        String newPassword = userDto.getPassword();

        if (userDto.getNombre() == null || userDto.getNombre().trim().isEmpty()) {
            throw new ValidationException("El Nombre es obligatorio y no puede estar vacío.");
        }
        if (userDto.getApellido() == null || userDto.getApellido().trim().isEmpty()) {
            throw new ValidationException("El Apellido es obligatorio y no puede estar vacío.");
        }
        if (userDto.getRun() == null || userDto.getRun().trim().isEmpty()) {
            throw new ValidationException("El RUN es obligatorio y no puede estar vacío.");
        }
        if (!RUN_PATTERN.matcher(userDto.getRun()).matches()) {
            throw new ValidationException(
                    "El formato del RUN no es válido. Debe usar puntos y guión (ej: 12.345.678-K).");
        }
        if (userDto.getRegion() == null || userDto.getRegion().trim().isEmpty()) {
            throw new ValidationException("La Región es obligatoria y no puede estar vacía.");
        }
        if (userDto.getComuna() == null || userDto.getComuna().trim().isEmpty()) {
            throw new ValidationException("La Comuna es obligatoria y no puede estar vacía.");
        }
        if (userDto.getDireccion() == null || userDto.getDireccion().trim().isEmpty()
                || userDto.getDireccion().length() < 5) {
            throw new ValidationException(
                    "La Dirección es obligatoria, no puede estar vacía y debe tener al menos 5 caracteres.");
        }
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new ValidationException("El Email es obligatorio.");
        }
        if (!(newEmail.endsWith("@duocuc.cl") || newEmail.endsWith("@profesor.duoc.cl"))) {
            throw new IllegalArgumentException(
                    "El formato de email no es válido. Debe terminar en @duocuc.cl o @profesor.duoc.cl.");
        }

        if (!newEmail.equals(existingUser.getEmail())) {
            if (userRepository.findByEmail(newEmail) != null) {
                throw new IllegalArgumentException(
                        "El email '" + newEmail + "' ya se encuentra registrado por otro usuario.");
            }
        }

        if (!userDto.getRun().equals(existingUser.getRun())) {
            if (userRepository.findByRun(userDto.getRun()) != null) {
                throw new IllegalArgumentException(
                        "El run '" + userDto.getRun() + "' ya se encuentra registrado por otro usuario.");
            }
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < 4) {
                throw new IllegalArgumentException("La nueva contraseña debe tener un largo mínimo de 4 caracteres.");
            }
            existingUser.setPassword(newPassword);
        }

        existingUser.setEmail(newEmail);
        existingUser.setNombre(userDto.getNombre());
        existingUser.setApellido(userDto.getApellido());
        existingUser.setRun(userDto.getRun());
        existingUser.setTelefono(userDto.getTelefono());
        existingUser.setRegion(userDto.getRegion());
        existingUser.setComuna(userDto.getComuna());
        existingUser.setDireccion(userDto.getDireccion());
        existingUser.setComentario(userDto.getComentario());
        existingUser.setRol(rol);

        User updatedUser = userRepository.save(existingUser);
        return new UserResponseDTO(updatedUser);
    }

}