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
import pe.edu.upeu.sysventas.model.Sala;
import pe.edu.upeu.sysventas.service.ISalaService;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Set;

@RequiredArgsConstructor
public class SalaController {
    private final ISalaService service;
    @FXML private TableView<Sala> tableView;
    @FXML private TextField txtBuscar;
    private ObservableList<Sala> items;
    private FilteredList<Sala> filtered;
    private Validator validator;

    @FXML
    public void initialize() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idSala", 70.0));
        columns.put("N° Sala", new ColumnInfo("numero", 100.0));
        columns.put("Capacidad", new ColumnInfo("capacidad", 120.0));
        columns.put("Tipo", new ColumnInfo("tipo", 150.0));
        new TableViewHelper<Sala>().addColumnsInOrderWithSize(tableView, columns, this::abrirFormulario, this::eliminar);
        tableView.setTableMenuButtonVisible(true);
        listar();
        txtBuscar.textProperty().addListener((obs, oldValue, newValue) -> aplicarFiltro(newValue));
    }

    private void aplicarFiltro(String texto) {
        if (filtered == null) return;
        String filtro = texto == null ? "" : texto.trim().toLowerCase();
        filtered.setPredicate(s -> filtro.isEmpty()
                || String.valueOf(s.getNumero()).contains(filtro)
                || String.valueOf(s.getCapacidad()).contains(filtro)
                || safe(s.getTipo()).toLowerCase().contains(filtro));
    }

    @FXML public void nuevaSala() { abrirFormulario(null); }

    @FXML
    public void editarSala() {
        Sala seleccionado = tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alerta("Selecciona una sala", "Debes seleccionar una sala para editarla.");
            return;
        }
        abrirFormulario(seleccionado);
    }

    @FXML
    public void eliminarSala() {
        Sala seleccionado = tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alerta("Selecciona una sala", "Debes seleccionar una sala para eliminarla.");
            return;
        }
        eliminar(seleccionado);
    }

    private void eliminar(Sala sala) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas eliminar la sala " + sala.getNumero() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirmacion.setTitle("Eliminar sala");
        confirmacion.setHeaderText(null);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                service.delete(sala.getIdSala());
                listar();
            } catch (RuntimeException e) {
                alerta("No se pudo eliminar", mensajeError(e));
            }
        }
    }

    private void abrirFormulario(Sala existente) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(existente == null ? "Nueva sala" : "Editar sala");
        ButtonType accion = new ButtonType(existente == null ? "Guardar" : "Actualizar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(accion, ButtonType.CANCEL);

        TextField numero = new TextField(existente == null ? "" : String.valueOf(existente.getNumero()));
        TextField capacidad = new TextField(existente == null ? "" : String.valueOf(existente.getCapacidad()));
        TextField tipo = new TextField(existente == null ? "" : safe(existente.getTipo()));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new javafx.geometry.Insets(20));
        grid.addRow(0, new Label("N° Sala:"), numero);
        grid.addRow(1, new Label("Capacidad:"), capacidad);
        grid.addRow(2, new Label("Tipo:"), tipo);
        dialog.getDialogPane().setContent(grid);

        Button botonAccion = (Button) dialog.getDialogPane().lookupButton(accion);
        botonAccion.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                Sala sala = new Sala(existente == null ? null : existente.getIdSala(), Integer.parseInt(numero.getText().trim()), Integer.parseInt(capacidad.getText().trim()), tipo.getText().trim());
                Set<ConstraintViolation<Sala>> errores = validator.validate(sala);
                if (!errores.isEmpty()) {
                    event.consume();
                    alerta("Datos incorrectos", errores.stream().sorted(Comparator.comparing(v -> v.getPropertyPath().toString())).map(ConstraintViolation::getMessage).distinct().reduce((a, b) -> a + "\n" + b).orElse("Datos incorrectos"));
                    return;
                }
                if (existente == null) {
                    service.save(sala);
                } else {
                    service.update(existente.getIdSala(), sala);
                }
                listar();
                tableView.refresh();
            } catch (NumberFormatException e) {
                event.consume();
                alerta("Datos incorrectos", "Número de sala y capacidad deben ser valores numéricos.");
            } catch (RuntimeException e) {
                event.consume();
                alerta("No se pudo guardar", mensajeError(e));
            }
        });
        dialog.showAndWait();
    }

    private void listar() {
        items = FXCollections.observableArrayList(service.findAll());
        filtered = new FilteredList<>(items, s -> true);
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
