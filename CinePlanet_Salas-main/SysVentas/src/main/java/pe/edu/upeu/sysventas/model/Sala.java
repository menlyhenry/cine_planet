package pe.edu.upeu.sysventas.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sala {

    private Long idSala;

    @NotNull(message = "El número de sala es obligatorio")
    @Positive(message = "El número de sala debe ser positivo")
    private Integer numero;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser positiva")
    private Integer capacidad;

    @NotBlank(message = "El tipo de sala es obligatorio")
    private String tipo;
}
