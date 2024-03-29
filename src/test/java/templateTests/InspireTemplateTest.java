package templateTests;

import static org.apache.commons.io.FileUtils.listFiles;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import filestructure.Templatedir;
import metadata.DatasetList;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import storage.DataStorage;

class TestAllTemplates {
    @TempDir
    static File defdir;


    @BeforeAll
    public static void oneTimeBeforeAll() throws IOException
    //throws IOException
     {



    }    

    @AfterAll
    public static void oneTimeTearDown() {
        generateReport("target/surefire-reports");

    }

    @Test
    @Disabled ("Run template tests only during test development") 
    @Order(1)
    void testTemplateParallel() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        DataStorage db = new DataStorage();

        DatasetList datasetListEnumSingleton1 = DatasetList.INSTANCE.getInstance();



        System.out.println(db.outputdir());
        db.ensureDirectory(db.outputdir());

        final Results results = Runner.parallel(getClass(), 1, "target/surefire-reports");
        String filename = db.outputdir() + File.separator + "TemplateInspireResults.csv";
        datasetListEnumSingleton1.writeResultsCSV(filename);

        assertEquals(0, results.getFailCount(), results.getErrorMessages());
    }

     @Test
     @Order(2)
    void CreateAndRunTests() throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        loadtestdata(defdir);
        Templatedir templatepath = new Templatedir();

        File temp = new File(new File(java.lang.System.getProperty("user.dir")) + File.separator + "src/test/java/templateTests");
        System.out.println("templatedir:" + temp.getAbsolutePath());
        assertTrue(temp.exists());
        templatepath.builder(temp, ".feature");
        File gentestdir = new File("T4") ;


        DatasetList.INSTANCE.getInstance().createdirstructure(gentestdir, templatepath, true);
        System.out.println("gentestdir:" + gentestdir.getAbsolutePath());
        assertTrue(gentestdir.exists());
        List<String> featurepaths = new ArrayList<>();
        Iterator<File> matchesIterator = FileUtils.iterateFiles(
                gentestdir, new WildcardFileFilter("*.feature"), TrueFileFilter.TRUE);

        while (matchesIterator.hasNext()) {
            File someFile = matchesIterator.next();
            String path = someFile.getAbsolutePath();
            System.out.println("test:" + path);
            featurepaths.add(path);
        }

        List<String> tags = List.of("~@ignore");
        final Results results = Runner.parallel(tags, featurepaths, 1, "target/surefire-reports");
      //  DataStorage db = new DataStorage();
      //  String filename = db.outputdir() + File.separator + "InspireResults.csv";
      //  DatasetList.INSTANCE.getInstance().writeResultsCSV(filename);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());

    }

    private void loadtestdata(File defdir) throws IOException {

        URL url = new URL("https://raw.githubusercontent.com/JLSchaap/nl-ngr-validation/gh-pages/T02_Datasets/datasets.csv");
        File file = new File(defdir + File.separator + "datasets.csv");
        FileUtils.copyURLToFile(url, file);
        System.out.println(file.getAbsolutePath());
        assertTrue(file.exists());
        DatasetList.INSTANCE.getInstance().loadDataset(file.getAbsolutePath(), true);
        URL url2 = new URL("https://raw.githubusercontent.com/JLSchaap/nl-ngr-validation/gh-pages/T02_Services/services-Beheer%20PDOK.csv");
        File servicefile = new File(defdir + File.separator + "services.csv");
        FileUtils.copyURLToFile(url2, servicefile);
        System.out.println(servicefile.getAbsolutePath());
        DatasetList.INSTANCE.getInstance().loadService(servicefile.getAbsolutePath());
    }

    private static void generateReport(final String karateOutputPath) {
        final Collection<File> jsonFiles = listFiles(new File(karateOutputPath), new String[]{"json"},
                true);
        final List<String> jsonPaths = new ArrayList<String>(jsonFiles.size());
        jsonFiles.forEach(file -> jsonPaths.add(file.getAbsolutePath()));
        final Configuration config = new Configuration(new File(karateOutputPath), "NLTEST");
        final ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);
        reportBuilder.generateReports();
    }

    

}
