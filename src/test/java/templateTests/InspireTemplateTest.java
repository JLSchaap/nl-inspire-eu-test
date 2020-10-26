package templateTests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import filestructure.Templatedir;
import metadata.DatasetList;
import org.apache.commons.io.FileUtils;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.junit.jupiter.api.io.TempDir;

class TestAllTemplates {
    @TempDir static  File defdir;
    @TempDir static File gentestdir;

    @Test
    @Order(1)
    void testTemplateParallel() {
        final Results results = Runner.parallel(getClass(), 1, "target/surefire-reports");
        generateReport(results.getReportDir());
        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

    @Test
    @Order(2)
    void Createtests() throws IOException {
        loadtestdata(defdir);
        Templatedir templatepath = new Templatedir();
        File temp = new File (".");
        System.out.println("templatedir:"+ temp.getAbsolutePath());
        assertTrue(temp.exists());
        templatepath.builder(temp, ".feature");
        DatasetList.INSTANCE.getInstance().createdirstructure(gentestdir, templatepath);
        System.out.println("templatedir:"+ gentestdir.getAbsolutePath());
        assertTrue(gentestdir.exists());

    }

    private void loadtestdata(File defdir) throws IOException {
        String root = java.lang.System.getenv("MAVEN_PROJECTBASEDIR");
        URL url = new URL("https://raw.githubusercontent.com/JLSchaap/nl-ngr-validation/gh-pages/T02_Datasets/datasets.csv");
        File file = new File(defdir+  File.separator + "datasets.csv");
        FileUtils.copyURLToFile(url, file);
        System.out.println(file.getAbsolutePath());
         assertTrue(file.exists());
        DatasetList.INSTANCE.getInstance().loadDataset(file.getAbsolutePath(), true);
        URL url2 = new URL("https://raw.githubusercontent.com/JLSchaap/nl-ngr-validation/gh-pages/T02_Services/services.csv");
        File servicefile= new File(defdir + File.separator + "services.csv");
        FileUtils.copyURLToFile(url2, servicefile);
        System.out.println(servicefile.getAbsolutePath());
        DatasetList.INSTANCE.getInstance().loadService(servicefile.getAbsolutePath());
    }


    private static void generateReport(final String karateOutputPath) {
        final Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] { "json" },
                true);
        final List<String> jsonPaths = new ArrayList<String>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        final Configuration config = new Configuration(new File(karateOutputPath), "NLTEST");
        final ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

}
