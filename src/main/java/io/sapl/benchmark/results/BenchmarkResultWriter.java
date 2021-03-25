package io.sapl.benchmark.results;

import io.sapl.benchmark.PolicyCharacteristics;
import io.sapl.benchmark.index.IndexType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jxls.template.SimpleExporter;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.XYChart;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BenchmarkResultWriter {

    private static final int DEFAULT_HEIGHT = 1080;
    private static final int DEFAULT_WIDTH = 1920;

    private static final String ERROR_READING_TEST_CONFIGURATION = "Error reading test configuration";
    private static final String ERROR_WRITING_BITMAP = "Error writing bitmap";
    private static final String EXPORT_PROPERTIES = "number, name, timePreparation, timeDuration, request, response";
    private static final String EXPORT_PROPERTIES_AGGREGATES = "name, min, max, avg, mdn, seed, policyCount, variableCount, runs, iterations";

    private final String resultPath;
    private final IndexType indexType;

    public void writeFinalResults(BenchmarkResultContainer resultContainer, XYChart overviewChart) {
        log.info("writing charts and results to {}", resultPath);

        writeOverviewChart(overviewChart);
        writeOverviewExcel(resultContainer.getData());


        buildAggregateData(resultContainer);
        writeHistogramChart(resultContainer);
        writeHistogramExcel(resultContainer.getAggregateData());
        appendHistogramToCSVFile(resultContainer.getAggregateData(), resultPath);
    }

    public void writeDetailsChart(List<BenchmarkRecord> results, double[] times, String configName) {
        int i = 0;
        for (BenchmarkRecord item : results) {
            times[i] = item.getTimeDuration();
            i++;
        }

        XYChart details = new XYChart(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        details.setTitle("Evaluation Time");
        details.setXAxisTitle("Run");
        details.setYAxisTitle("ms");
        details.addSeries(configName, times);

        try {
            BitmapEncoder.saveBitmap(details, resultPath + configName
                    .replaceAll("[^a-zA-Z0-9]", ""), BitmapFormat.PNG);
        } catch (IOException e) {
            log.error(ERROR_WRITING_BITMAP, e);
            System.exit(1);
        }
    }


    // HOSPITAL(15932) p20115 v821735	25,62	63,89	28,86	27,17
    private void appendHistogramToCSVFile(List<BenchmarkAggregate> aggregateRecords, String path) {
        try (FileWriter fw = new FileWriter(path + "benchmark_summary.csv", Charset.defaultCharset(), true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            for (BenchmarkAggregate dat : aggregateRecords) {
                // indextype, name, seed, policies, variables, min, max, avg, mdn
                out.printf("%s,\t %s;\t %s;\t %d;\t %d;\t %.2f;\t %.2f;\t %.2f;\t %.2f;\t %s",
                        indexType, dat.getName(),
                        dat.getSeed(), dat.getPolicyCount(), dat.getVariableCount(), dat.getMin(), dat.getMax(),
                        dat.getAvg(), dat.getMdn(), System.lineSeparator());
            }
        } catch (IOException e) {
            log.error("Error appending to  CSV", e);
            System.exit(1);
        }
    }

    private void writeOverviewChart(XYChart chart) {
        chart.setTitle("Evaluation Time");
        chart.setXAxisTitle("Run");
        chart.setYAxisTitle("ms");
        try {
            BitmapEncoder.saveBitmap(chart, resultPath + "overview-" + indexType, BitmapFormat.PNG);
        } catch (IOException e) {
            log.error(ERROR_WRITING_BITMAP, e);
            System.exit(1);
        }
    }

    private void writeHistogramChart(BenchmarkResultContainer resultContainer) {

        CategoryChart histogram = new CategoryChart(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        histogram.setTitle("Aggregates");
        histogram.setXAxisTitle("Run");
        histogram.setYAxisTitle("ms");
        histogram.addSeries("min", resultContainer.getIdentifier(), resultContainer.getMinValues());
        histogram.addSeries("max", resultContainer.getIdentifier(), resultContainer.getMaxValues());
        histogram.addSeries("avg", resultContainer.getIdentifier(), resultContainer.getAvgValues());
        histogram.addSeries("mdn", resultContainer.getIdentifier(), resultContainer.getMdnValues());

        try {
            BitmapEncoder.saveBitmap(histogram, resultPath + "histogram-" + indexType, BitmapFormat.PNG);
        } catch (IOException e) {
            log.error(ERROR_WRITING_BITMAP, e);
            System.exit(1);
        }
    }

    private void writeOverviewExcel(List<BenchmarkRecord> data) {
        try (OutputStream os = Files.newOutputStream(Paths.get(resultPath, "overview-" + indexType + ".xls"))) {
            SimpleExporter exp = new SimpleExporter();
            exp.gridExport(getExportHeader(), data, EXPORT_PROPERTIES, os);
        } catch (IOException e) {
            log.error("Error writing XLS", e);
            System.exit(1);
        }
    }

    private void writeHistogramExcel(List<BenchmarkAggregate> data) {
        try (OutputStream os = Files.newOutputStream(Paths.get(resultPath, "histogram-" + indexType + ".xls"))) {
            SimpleExporter exp = new SimpleExporter();
            exp.gridExport(getExportHeaderAggregates(), data, EXPORT_PROPERTIES_AGGREGATES, os);
        } catch (IOException e) {
            log.error("Error writing XLS", e);
            System.exit(1);
        }
    }

    private void buildAggregateData(BenchmarkResultContainer resultContainer) {
        for (int i = 0; i < resultContainer.getIdentifier().size(); i++) {
            PolicyCharacteristics characteristics = resultContainer.getCharacteristics().get(i);
            resultContainer.getAggregateData()
                    .add(new BenchmarkAggregate(
                            resultContainer.getIdentifier().get(i), //name
                            resultContainer.getMinValues().get(i),  //min
                            resultContainer.getMaxValues().get(i),  //max
                            resultContainer.getAvgValues().get(i),  //avg
                            resultContainer.getMdnValues().get(i),  //mdn
                            characteristics.getSeed(),
                            characteristics.getPolicyCount(),
                            characteristics.getVariablePoolCount(),
                            resultContainer.getRuns(),
                            resultContainer.getIterations()
                    ));
        }
    }

    private List<String> getExportHeader() {
        return Arrays.asList("Iteration", "Test Case", "Preparation Time (ms)", "Execution Time (ms)", "Request String",
                "Response String (ms)");
    }

    private List<String> getExportHeaderAggregates() {
        return Arrays.asList("Test Case", "Minimum Time (ms)", "Maximum Time (ms)", "Average Time (ms)",
                "Median Time (ms)", "Seed", "Policy Count", "Variable Count", "Runs", "Iterations");
    }
}
