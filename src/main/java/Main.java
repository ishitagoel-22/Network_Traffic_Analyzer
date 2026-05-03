import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class Main {

    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader("packets.csv"));
        String line;

        int totalPackets = 0;
        int tcpCount = 0;
        int udpCount = 0;

        Map<String, Integer> ipCount = new HashMap<>();

        br.readLine(); // skip header

        while ((line = br.readLine()) != null) {

            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");

            if (parts.length < 5) continue;

            totalPackets++;

            String srcIp = parts[2].replace("\"", "").trim();
            String protocol = parts[4].replace("\"", "").trim();

            ipCount.put(srcIp, ipCount.getOrDefault(srcIp, 0) + 1);

            if (protocol.equalsIgnoreCase("TCP")) {
                tcpCount++;
            } else if (protocol.equalsIgnoreCase("UDP")) {
                udpCount++;
            }
        }

        br.close();

        double tcpPercent = (tcpCount * 100.0) / totalPackets;
        double udpPercent = (udpCount * 100.0) / totalPackets;

        System.out.println("Total Packets: " + totalPackets);
        System.out.println("TCP: " + tcpCount);
        System.out.println("UDP: " + udpCount);

        System.out.println("\nTCP %: " + tcpPercent);
        System.out.println("UDP %: " + udpPercent);

        // Top IPs
        System.out.println("\nTop 5 IPs:");
        ipCount.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .forEach(System.out::println);


        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("TCP", tcpCount);
        dataset.setValue("UDP", udpCount);

        JFreeChart chart = ChartFactory.createPieChart(
                "Protocol Distribution",
                dataset,
                true,
                true,
                false
        );
        new DashBoardUI(totalPackets, tcpCount, udpCount, chart, ipCount);
        PiePlot plot = (PiePlot) chart.getPlot();

        plot.setLabelGenerator(
                new StandardPieSectionLabelGenerator(
                        "{0}: {2}",
                        new DecimalFormat("0"),
                        new DecimalFormat("0.00%")
                )
        );
    }
}