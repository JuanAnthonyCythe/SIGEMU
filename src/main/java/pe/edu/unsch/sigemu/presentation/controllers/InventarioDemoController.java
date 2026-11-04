package pe.edu.unsch.sigemu.presentation.controllers;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@RestController
@Validated
@RequestMapping("/api/v1/inventario")
public class InventarioDemoController {

    @GetMapping("/estado")
    public Map<String, Object> estado() {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("sistema", "SIGEMU - Farmacia Universitaria UNSCH");
        respuesta.put("sprint", 1);
        respuesta.put("arquitectura", "Hexagonal / Clean Architecture");
        respuesta.put("estadoServicio", "OPERATIVO");
        return respuesta;
    }

    @PostMapping("/movimientos/simular")
    public ResponseEntity<Map<String, Object>> simularMovimiento(
            @Valid @RequestBody MovimientoSimulado movimiento) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Movimiento simulado correctamente");
        respuesta.put("numeroLote", movimiento.numeroLote());
        respuesta.put("tipo", movimiento.tipo());
        respuesta.put("cantidad", movimiento.cantidad());
        respuesta.put("stockActualizado", true);
        respuesta.put("estado", "PROCESADO");
        return ResponseEntity.ok(respuesta);
    }

    public record MovimientoSimulado(
            @NotBlank String numeroLote,
            @NotBlank String tipo,
            @Positive int cantidad) {
    }
}
