package com.backend.huertohogar.service;

import com.backend.huertohogar.model.Categoria;
import com.backend.huertohogar.model.Producto;
import com.backend.huertohogar.repository.CategoriaRepository;
import com.backend.huertohogar.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> getProductoById(Integer id) {
        return productoRepository.findById(id);
    }

    public Producto saveProducto(Producto producto, String nombreCategoria) {
        if (nombreCategoria != null && !nombreCategoria.isEmpty()) {
            Optional<Categoria> categoriaOpt = categoriaRepository.findByNombre(nombreCategoria);
            if (categoriaOpt.isPresent()) {
                producto.setCategoria(categoriaOpt.get());
            } else {
                throw new RuntimeException("Categoria no encontrada: " + nombreCategoria);
            }
        }
        return productoRepository.save(producto);
    }

    public void deleteProducto(Integer id) {
        productoRepository.deleteById(id);
    }

    public Producto updateProducto(Integer id, Producto productoDetails, String nombreCategoria) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        producto.setNombre(productoDetails.getNombre());
        producto.setPrecio(productoDetails.getPrecio());
        producto.setStock(productoDetails.getStock());
        producto.setDescripcion(productoDetails.getDescripcion());
        producto.setImagen(productoDetails.getImagen());

        if (nombreCategoria != null && !nombreCategoria.isEmpty()) {
            Optional<Categoria> categoriaOpt = categoriaRepository.findByNombre(nombreCategoria);
            if (categoriaOpt.isPresent()) {
                producto.setCategoria(categoriaOpt.get());
            } else {
                throw new RuntimeException("Categoria no encontrada: " + nombreCategoria);
            }
        }

        return productoRepository.save(producto);
    }
}
