package pe.edu.unsch.sigemu.presentation.controllers;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.unsch.sigemu.application.usecases.ConsultarKardexUseCase;
import pe.edu.unsch.sigemu.application.usecases.RegistrarMovimientoUseCase;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.models.MovimientoInventario;
import pe.edu.unsch.sigemu.domain.rules.LoteNoEncontradoException;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;
import pe.edu.unsch.sigemu.presentation.dto.MovimientoHistorialResponseDTO;
import pe.edu.unsch.sigemu.presentation.dto.MovimientoResponseDTO;
import pe.edu.unsch.sigemu.presentation.dto.RegistrarMovimientoRequestDTO;

@RestController
@Validated
@RequestMapping("/api/v1/inventario/movimientos")
public class MovimientoController {

    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;
    private final ConsultarKardexUseCase consultarKardexUseCase;

    public MovimientoController(RegistrarMovimientoUseCase registrarMovimientoUseCase,
                                ConsultarKardexUseCase consultarKardexUseCase) {
        this.registrarMovimientoUseCase = Objects.requireNonNull(registrarMovimientoUseCase,
                "RegistrarMovimientoUseCase es obligatorio");
        this.consultarKardexUseCase = Objects.requireNonNull(consultarKardexUseCase,
                "ConsultarKardexUseCase es obligatorio");
    }

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> registrarMovimiento(
            @Valid @RequestBody RegistrarMovimientoRequestDTO request) {
        String tipoNormalizado = request.tipo().trim().toUpperCase();
        LoteMedicamento loteActualizado = registrarMovimientoUseCase.registrarMovimiento(
                request.numeroLote(),
                tipoNormalizado,
                request.cantidad(),
                request.motivo()
        );

        MovimientoResponseDTO respuesta = new MovimientoResponseDTO(
                "Movimiento registrado exitosamente",
                loteActualizado.getNumeroLote(),
                tipoNormalizado,
                request.cantidad(),
                loteActualizado.getStock(),
                loteActualizado.getEstado()
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/lote/{numeroLote}")
    public ResponseEntity<List<MovimientoHistorialResponseDTO>> consultarKardexPorLote(
            @PathVariable String numeroLote) {
        List<MovimientoInventario> movimientos = consultarKardexUseCase.consultarPorLote(numeroLote);
        List<MovimientoHistorialResponseDTO> respuesta = movimientos.stream()
                .map(MovimientoHistorialResponseDTO::fromDomain)
                .toList();
        return ResponseEntity.ok(respuesta);
    }

    @ExceptionHandler(LoteNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarLoteNoEncontrado(LoteNoEncontradoException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", HttpStatus.NOT_FOUND.value());
        cuerpo.put("error", "Lote No Encontrado");
        cuerpo.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocio(ReglaNegocioException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", HttpStatus.BAD_REQUEST.value());
        cuerpo.put("error", "Regla de Negocio");
        cuerpo.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }
}
