import javax.swing.*;
import java.awt.*;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;

public class DashBoardUI {

    public DashBoardUI(int total, int tcp, int udp, JFreeChart chart, Map<String, Integer> ipCount) {

        JFrame frame = new JFrame("Network Dashboard");
        frame.setSize(900, 650);
        frame.setLayout(new BorderLayout());


        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Total: " + total));
        topPanel.add(new JLabel("TCP: " + tcp));
        topPanel.add(new JLabel("UDP: " + udp));


        ChartPanel chartPanel = new ChartPanel(chart);

        DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
        new Thread(() -> {
            try {
                int time = 1;

                while (time <= 10) {
                    Thread.sleep(1000);

                    lineDataset.addValue(tcp + (int)(Math.random()*5000), "TCP", "T" + time);
                    lineDataset.addValue(udp + (int)(Math.random()*1000), "UDP", "T" + time);

                    time++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        lineDataset.addValue(tcp, "TCP", "Now");
        lineDataset.addValue(udp, "UDP", "Now");


        JFreeChart lineChart = ChartFactory.createLineChart(
                "Traffic Trend",
                "Time",
                "Packets",
                lineDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        ChartPanel linePanel = new ChartPanel(lineChart);


        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(chartPanel);
        centerPanel.add(linePanel);

        String[] columns = {"IP Address", "Packets"};

        Object[][] data = ipCount.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .map(e -> new Object[]{e.getKey(), e.getValue()})
                .toArray(Object[][]::new);

        JTable table = new JTable(data, columns);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(800, 150));
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));


        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(tableScroll, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}