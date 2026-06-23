package com.biometricAccess.repository;

import com.biometricAccess.projection.ReporteAccesoProjection;
import com.biometricAccess.model.RegistroAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReporteRepository extends JpaRepository<RegistroAcceso, Long> {

    @Query(value = """
            SELECT
                DATE(ra.fecha_hora) AS fecha,
                TIME(ra.fecha_hora) AS horaIngreso,
                NULL AS horaSalida,
                u.identificacion AS identificacion,
                u.nombre AS nombre,
                u.apellido AS apellido,
                u.rol AS rol,
                u.numero_ficha AS numeroFicha,
                u.tipo_documento AS tipoDocumento,
                e.nombre_equipo AS equipo,
                e.tipo_equipo AS tipoEquipo,
                ra.resultado AS resultado,
                ra.observacion AS observacion
            FROM registros_acceso ra
            INNER JOIN usuarios u ON ra.usuario_id = u.id
            LEFT JOIN equipos e ON ra.equipo_id = e.id
            WHERE
                (:fechaInicio IS NULL OR DATE(ra.fecha_hora) >= :fechaInicio)
                AND (:fechaFin IS NULL OR DATE(ra.fecha_hora) <= :fechaFin)
                AND (
                    :criterio IS NULL
                    OR :valor IS NULL
                    OR :valor = ''
                    OR (
                        :criterio = 'identificacion'
                        AND u.identificacion LIKE CONCAT('%', :valor, '%')
                    )
                    OR (
                        :criterio = 'nombre'
                        AND (
                            u.nombre LIKE CONCAT('%', :valor, '%')
                            OR u.apellido LIKE CONCAT('%', :valor, '%')
                            OR CONCAT(u.nombre, ' ', u.apellido) LIKE CONCAT('%', :valor, '%')
                        )
                    )
                    OR (
                        :criterio = 'rol'
                        AND u.rol LIKE CONCAT('%', :valor, '%')
                    )
                    OR (
                        :criterio = 'ficha'
                        AND u.numero_ficha LIKE CONCAT('%', :valor, '%')
                    )
                )
            ORDER BY ra.fecha_hora DESC
            """, nativeQuery = true)
    List<ReporteAccesoProjection> buscarReportes(
            @Param("criterio") String criterio,
            @Param("valor") String valor,
            @Param("fechaInicio") String fechaInicio,
            @Param("fechaFin") String fechaFin
    );
}