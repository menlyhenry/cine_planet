package pe.edu.upeu.sysventas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.components.ColumnInfo;
import pe.edu.upeu.sysventas.components.TableViewHelper;
import pe.edu.upeu.sysventas.model.Pelicula;
import pe.edu.upeu.sysventas.service.IPeliculaService;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Set;

@RequiredArgsConstructor
public class PeliculaController {
    private final IPeliculaService service;
    @FXML private TableView<Pelicula> tableView;
    @FXML private TextField txtBuscar;
    private ObservableList<Pelicula> items;
    private FilteredList<Pelicula> filtered;
    private Validator validator;

    @FXML
    public void initialize() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idPelicula", 70.0));
        columns.put("Título", new ColumnInfo("titulo", 240.0));
        columns.put("Género", new ColumnInfo("genero", 150.0));
        columns.put("Duración", new ColumnInfo("duracion", 110.0));
        new TableViewHelper<Pelicula>().addColumnsInOrderWithSize(tableView, columns, this::abrirFormulario, this::eliminar);
        tableView.setTableMenuButtonVisible(true);
        listar();
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> aplicarFiltro(newValue));
    }

    private void aplicarFiltro(String texto) {
        if (filtered == null) return;
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
        filtered.setPredicate(p -> filtro.isEmpty()
                || safe(p.getTitulo()).toLowerCase().contains(filtro)
                || safe(p.getGenero()).toLowerCase().contains(filtro)
                || String.valueOf(p.getDuracion()).contains(filtro));
    }

    @FXML public void nuevaPelicula() { abrirFormulario(null); }

    @FXML
    public void editarPelicula() {
        Pelicula seleccionado = tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alerta("Selecciona una película", "Debes seleccionar una película para editarla.");
            return;
        }
        abrirFormulario(seleccionado);
    }

    @FXML
    public void eliminarPelicula() {
        Pelicula seleccionado = tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alerta("Selecciona una película", "Debes seleccionar una película para eliminarla.");
            return;
        }
        eliminar(seleccionado);
    }

    private void eliminar(Pelicula pelicula) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas eliminar la película " + pelicula.getTitulo() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirmacion.setTitle("Eliminar película");
        confirmacion.setHeaderText(null);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                service.delete(pelicula.getIdPelicula());
                listar();
            } catch (RuntimeException e) {
                alerta("No se pudo eliminar", mensajeError(e));
            }
        }
    }

    private void abrirFormulario(Pelicula existente) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(existente == null ? "Nueva película" : "Editar película");
        ButtonType accion = new ButtonType(existente == null ? "Guardar" : "Actualizar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(accion, ButtonType.CANCEL);

        TextField titulo = new TextField(existente == null ? "" : safe(existente.getTitulo()));
        TextField genero = new TextField(existente == null ? "" : safe(existente.getGenero()));
        TextField duracion = new TextField(existente == null ? "" : String.valueOf(existente.getDuracion()));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new javafx.geometry.Insets(20));
        grid.addRow(0, new Label("Título:"), titulo);
        grid.addRow(1, new Label("Género:"), genero);
        grid.addRow(2, new Label("Duración (min):"), duracion);
        dialog.getDialogPane().setContent(grid);

        Button botonAccion = (Button) dialog.getDialogPane().lookupButton(accion);
        botonAccion.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                Pelicula pelicula = new Pelicula(existente == null ? null : existente.getIdPelicula(), titulo.getText().trim(), genero.getText().trim(), Integer.parseInt(duracion.getText().trim()));
                Set<ConstraintViolation<Pelicula>> errores = validator.validate(pelicula);
                if (!errores.isEmpty()) {
                    event.consume();
                    alerta("Datos incorrectos", errores.stream().sorted(Comparator.comparing(v -> v.getPropertyPath().toString())).map(ConstraintViolation::getMessage).distinct().reduce((a, b) -> a + "\n" + b).orElse("Datos incorrectos"));
                    return;
                }
                if (existente == null) service.save(pelicula);
                else service.update(existente.getIdPelicula(), pelicula);
                listar();
            } catch (NumberFormatException e) {
                event.consume();
                alerta("Datos incorrectos", "La duración debe ser un número entero.");
            } catch (RuntimeException e) {
                event.consume();
                alerta("No se pudo guardar", mensajeError(e));
            }
        });
        dialog.showAndWait();
    }

    private void listar() {
        items = FXCollections.observableArrayList(service.findAll());
        filtered = new FilteredList<>(items, p -> true);
        tableView.setItems(filtered);
        aplicarFiltro(txtBuscar == null ? "" : txtBuscar.getText());
    }

    private void alerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String safe(String value) { return value == null ? "" : value; }
    private String mensajeError(RuntimeException e) { return e.getMessage() == null || e.getMessage().isBlank() ? "Ocurrió un error al procesar la operación." : e.getMessage(); }
}
