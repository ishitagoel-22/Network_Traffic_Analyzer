import java.io.*;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import java.text.DecimalFormat;
import org.jfree.chart.plot.PiePlot;
public class Main {

    public static void main(String[] args) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader("packets.csv"));

        String line;

        int totalPackets = 0;
        int tcpCount = 0;
        int udpCount = 0;

        Map<String, Integer> ipCount = new HashMap<>();

        br.readLine();

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

        Map.Entry<String, Integer> top =
                ipCount.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .get();

        System.out.println("Total Packets: " + totalPackets);
        System.out.println("TCP Packets: " + tcpCount);
        System.out.println("UDP Packets: " + udpCount);

        System.out.println("\nTCP %: " + tcpPercent);
        System.out.println("UDP %: " + udpPercent);

        System.out.println("\nTop IPs:");
        ipCount.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(5)
                .forEach(System.out::println);

        System.out.println("\nMost Active IP: " + top);

        System.out.println("\nInsight: Most traffic is TCP, which indicates web browsing activity.");

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("TCP", tcpCount);
        dataset.setValue("UDP", udpCount);


        JFreeChart chart = ChartFactory.createPieChart(
                "Protocol Distribution",
                dataset,
                true, true, false);

        PiePlot plot = (PiePlot) chart.getPlot();

        plot.setLabelGenerator(
                new StandardPieSectionLabelGenerator(
                        "{0} ({2})",
                        new DecimalFormat("0"),
                        new DecimalFormat("0%")
                )
        );


        ChartFrame frame = new ChartFrame("Network Analysis", chart);
        frame.setSize(500, 400);
        frame.setVisible(true);
    }
}