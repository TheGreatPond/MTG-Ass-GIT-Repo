
package ManaCurve;

/**
 *
 * @author lakes
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
 
public class ChartManaCurve extends Application {
    final static String zero = "0";
    final static String one = "1";
    final static String two = "2";
    final static String three = "3";
    final static String four = "4";
    final static String five = "5";
    final static String six = "6";
    final static String other = "Other";
 
    @Override public void start(Stage stage) {
        stage.setTitle("Chart Mana Curve");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = 
            new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle("Mana Curve");
        xAxis.setLabel("Cost");       
        yAxis.setLabel("Quantity");
 
        XYChart.Series series1 = new XYChart.Series();
        
        //series1.getData().add("cs321.magic.deck.builder/ChartManaCurveStyleSheet.css");
        series1.setName("2003");       
        series1.getData().add(new XYChart.Data(zero, 32));
        series1.getData().add(new XYChart.Data(one, 10));
        series1.getData().add(new XYChart.Data(two, 5));
        series1.getData().add(new XYChart.Data(three, 20));
        series1.getData().add(new XYChart.Data(four, 10));
        series1.getData().add(new XYChart.Data(five, 5));
        series1.getData().add(new XYChart.Data(six, 20));
        series1.getData().add(new XYChart.Data(other, 12));      
            
        Scene scene  = new Scene(bc,800,600);
        bc.getData().addAll(series1);
        scene.getStylesheets().add("/styles/mana_style.css");
        stage.setScene(scene);
        stage.show();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}