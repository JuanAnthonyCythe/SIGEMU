package pe.edu.unsch.sigemu.presentation.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import pe.edu.unsch.sigemu.domain.entities.LoteMedicamento;

public record CrearLoteRequestDTO(
        @NotBlank(message = "El número de lote es obligatorio")
        String numeroLote,

        @NotBlank(message = "El medicamento es obligatorio")
        @JsonAlias({"nombreMedicamento", "medicamento"})
        String medicamento,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaFabricacion,

        @NotNull(message = "La fecha de caducidad es obligatoria")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate fechaCaducidad,

        @JsonAlias({"stock", "stockInicial"})
        Integer stockInicial,

        Integer stockMinimo
) {
    public CrearLoteRequestDTO(
            String numeroLote,
            String medicamento,
            LocalDate fechaFabricacion,
            LocalDate fechaCaducidad,
            int stock,
            int stockMinimo
    ) {
        this(numeroLote, medicamento, fechaFabricacion, fechaCaducidad, Integer.valueOf(stock), Integer.valueOf(stockMinimo));
    }

    public int stock() {
        return stockInicial != null ? stockInicial : 0;
    }

    public LoteMedicamento toDomain() {
        LocalDate fab = (fechaFabricacion != null) ? fechaFabricacion : LocalDate.now().minusDays(1);
        int st = (stockInicial != null) ? stockInicial : 0;
        int min = (stockMinimo != null) ? stockMinimo : Math.max(5, st / 10);
        return new LoteMedicamento(
                numeroLote,
                medicamento,
                fab,
                fechaCaducidad,
                st,
                min
        );
    }
}
