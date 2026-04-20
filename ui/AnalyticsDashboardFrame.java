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

    public AnalyticsDashboardFrame(int totalQuestions, int correctAnswers, Map<String, Double> topicAccuracyMap) {
        setTitle("Performance Analytics Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ModernTheme.prepareFrame(this, 1040, 620);

        JPanel page = ModernTheme.createPagePanel(new BorderLayout(20, 20));
        page.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        page.add(ModernTheme.createHeaderPanel("Performance Analytics", "Visual review of topic accuracy and your correct versus incorrect split."), BorderLayout.NORTH);

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

        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("Correct", correctAnswers);
        pieDataset.setValue("Incorrect", Math.max(totalQuestions - correctAnswers, 0));

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Correct vs Incorrect",
                pieDataset,
                true,
                true,
                false
        );

        JPanel charts = new JPanel(new GridLayout(1, 2, 18, 0));
        charts.setOpaque(false);
        charts.add(wrapChart("Topic Accuracy", new ChartPanel(barChart)));
        charts.add(wrapChart("Answer Split", new ChartPanel(pieChart)));

        page.add(charts, BorderLayout.CENTER);
        add(page);
        setVisible(true);
    }

    private JPanel wrapChart(String title, ChartPanel chartPanel) {
        chartPanel.setMouseWheelEnabled(true);
        JPanel card = ModernTheme.createCardPanel(new BorderLayout(0, 14));
        card.add(ModernTheme.createSectionTitle(title), BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }
}
