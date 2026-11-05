package pe.edu.unsch.sigemu.presentation.controllers;

import jakarta.validation.Valid;
import java.net.URI;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.unsch.sigemu.application.usecases.GestionLotesUseCase;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;
import pe.edu.unsch.sigemu.domain.rules.ReglaNegocioException;
import pe.edu.unsch.sigemu.presentation.dto.CrearLoteRequestDTO;
import pe.edu.unsch.sigemu.presentation.dto.LoteResponseDTO;

@RestController
@Validated
@RequestMapping("/api/v1/lotes")
public class LoteController {

    private final GestionLotesUseCase gestionLotesUseCase;

    public LoteController(GestionLotesUseCase gestionLotesUseCase) {
        this.gestionLotesUseCase = Objects.requireNonNull(gestionLotesUseCase,
                "GestionLotesUseCase es obligatorio");
    }

    @PostMapping
    public ResponseEntity<LoteResponseDTO> crearLote(@Valid @RequestBody CrearLoteRequestDTO dto) {
        LoteMedicamento nuevoLote = dto.toDomain();
        LoteMedicamento loteCreado = gestionLotesUseCase.crearLote(nuevoLote);
        LoteResponseDTO respuesta = LoteResponseDTO.fromDomain(loteCreado);

        URI ubicacion = URI.create("/api/v1/lotes/" + respuesta.numeroLote());
        return ResponseEntity.created(ubicacion).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<List<LoteResponseDTO>> listarTodos() {
        List<LoteResponseDTO> lotes = gestionLotesUseCase.listarTodos()
                .stream()
                .map(LoteResponseDTO::fromDomain)
                .toList();
        return ResponseEntity.ok(lotes);
    }

    @GetMapping("/{numeroLote}")
    public ResponseEntity<LoteResponseDTO> buscarPorNumero(@PathVariable String numeroLote) {
        LoteMedicamento lote = gestionLotesUseCase.buscarPorNumero(numeroLote);
        if (lote == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(LoteResponseDTO.fromDomain(lote));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<LoteResponseDTO>> buscarPorMedicamento(
            @RequestParam("medicamento") String medicamento) {
        List<LoteResponseDTO> lotes = gestionLotesUseCase.buscarPorMedicamento(medicamento)
                .stream()
                .map(LoteResponseDTO::fromDomain)
                .toList();
        return ResponseEntity.ok(lotes);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocioException(ReglaNegocioException ex) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", HttpStatus.BAD_REQUEST.value());
        cuerpo.put("error", "Regla de Negocio");
        cuerpo.put("mensaje", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }
}
