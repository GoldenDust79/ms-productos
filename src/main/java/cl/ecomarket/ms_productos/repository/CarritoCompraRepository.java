package cl.ecomarket.ms_productos.repository;

import cl.ecomarket.ms_productos.model.CarritoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoCompraRepository extends JpaRepository<CarritoCompra, Long> {
}
