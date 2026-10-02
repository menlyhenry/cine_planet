package pe.edu.upeu.sysventas.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysventas.enums.TipoDocumento;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    @NotBlank(message = "El documento es obligatorio")
    private String dniruc;

    @NotBlank(message = "Los nombres son obligatorios")
    private String nombres;

    private String repLegal;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento;
}
