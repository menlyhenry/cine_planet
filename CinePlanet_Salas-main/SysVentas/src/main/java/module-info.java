module pe.edu.upeu.sysventas {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires static lombok;
    requires jakarta.validation;
    requires org.hibernate.validator;

    opens pe.edu.upeu.sysventas.controller to javafx.fxml;
    opens pe.edu.upeu.sysventas to javafx.fxml;
    opens pe.edu.upeu.sysventas.model to javafx.fxml, javafx.base, org.hibernate.validator;

    exports pe.edu.upeu.sysventas;
    exports pe.edu.upeu.sysventas.model;
}
