package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AnalyticsDashboardFrame extends JFrame {

    public AnalyticsDashboardFrame(
            int totalQuestions,
            int correctAnswers,
            Map<String, Double> topicAccuracyMap
    ) {

        setTitle("Performance Analytics Dashboard");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1, 2));

        // ---------------- BAR CHART (Topic Accuracy) ----------------
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();

        for (String topic : topicAccuracyMap.keySet()) {
            barDataset.addValue(topicAccuracyMap.get(topic), "Accuracy", topic);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Topic-wise Accuracy",
                "Topic",
                "Accuracy (%)",
                barDataset
        );

        ChartPanel barPanel = new ChartPanel(barChart);

        // ---------------- PIE CHART (Correct vs Incorrect) ----------------
        DefaultPieDataset pieDataset = new DefaultPieDataset();

        pieDataset.setValue("Correct", correctAnswers);
        pieDataset.setValue("Incorrect", totalQuestions - correctAnswers);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Correct vs Incorrect",
                pieDataset,
                true,
                true,
                false
        );

        ChartPanel piePanel = new ChartPanel(pieChart);

        add(barPanel);
        add(piePanel);

        setVisible(true);
    }
}