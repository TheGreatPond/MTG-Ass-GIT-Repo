/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ColorPieChart;

/**
 *
 * @author lakes
 */
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.chart.*;
import javafx.scene.Group;
 
public class ChartPie extends Application {
 
    @Override public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Imported Fruits");
        stage.setWidth(500);
        stage.setHeight(500);
 
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Other", 13),
                new PieChart.Data("Red", 13),
                new PieChart.Data("Blue", 25),
                new PieChart.Data("Green", 10),
                new PieChart.Data("White", 22),
                new PieChart.Data("Black", 30));
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Color Distribution");
        scene.getStylesheets().add("/ColorPieChart/style.css");

        ((Group) scene.getRoot()).getChildren().add(chart);
        stage.setScene(scene);
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}
