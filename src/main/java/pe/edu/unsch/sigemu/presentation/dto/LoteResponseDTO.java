package pe.edu.unsch.sigemu.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import pe.edu.unsch.sigemu.domain.entities.EstadoLote;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;

public record LoteResponseDTO(
        Long id,
        String numeroLote,
        String medicamento,
        LocalDate fechaFabricacion,
        LocalDate fechaCaducidad,
        int stock,
        int stockMinimo,
        EstadoLote estado
) {
    @JsonProperty("nombreMedicamento")
    public String nombreMedicamento() {
        return medicamento;
    }

    @JsonProperty("stockActual")
    public int stockActual() {
        return stock;
    }

    @JsonProperty("stockInicial")
    public int stockInicial() {
        return stock;
    }

    public static LoteResponseDTO fromDomain(LoteMedicamento domain) {
        if (domain == null) {
            return null;
        }

        return new LoteResponseDTO(
                domain.getId(),
                domain.getNumeroLote(),
                domain.getMedicamento(),
                domain.getFechaFabricacion(),
                domain.getFechaCaducidad(),
                domain.getStock(),
                domain.getStockMinimo(),
                domain.getEstado()
        );
    }
}
