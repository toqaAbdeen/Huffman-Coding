module AlgoPrj2 {
	requires javafx.controls;
	
	opens application to javafx.graphics, javafx.fxml;
	exports application;
}
