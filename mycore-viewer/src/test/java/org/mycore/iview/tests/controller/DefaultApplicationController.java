package org.mycore.iview.tests.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.mycore.common.selenium.MCRSeleniumTestBase;
import org.mycore.iview.tests.TestProperties;
import org.mycore.iview.tests.model.TestDerivate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DefaultApplicationController extends ApplicationController {

    private static final String webpath = "target/test-classes/testFiles";

    @Override
    public void init() {
        DefaultApplicationController.derivateHTMLMapping = new HashMap<TestDerivate, String>();
    }

    private static Map<TestDerivate, String> derivateHTMLMapping;

    private static final Logger LOGGER = Logger.getLogger(DefaultApplicationController.class);

    private static Properties PROPERTIES = TestProperties.getInstance();

    private static Boolean SKIP_DOWNLOAD = Boolean
        .parseBoolean(PROPERTIES.getProperty("test.viewer.skipDownload", "False"));

    @Override
    public void setUpDerivate(WebDriver webdriver, TestDerivate testDerivate) {
        if (!derivateHTMLMapping.containsKey(testDerivate)) {
            try {
                URL zipLocation = testDerivate.getZipLocation();
                String name = testDerivate.getName();
                download(zipLocation, webpath);

                if (zipLocation.toString().endsWith(".pdf")) {
                    buildHTMLFile(name, testDerivate.getStartFile(), "MyCoRePDFViewer");
                } else {
                    buildHTMLFile(name, testDerivate.getStartFile(), "MyCoReImageViewer");
                }

                DefaultApplicationController.derivateHTMLMapping.put(testDerivate, buildFileName(name));
            } catch (IOException e) {
                LOGGER.error("Error while open connection to File Location!", e);
            }
        }
    }

    protected String buildHTMLFile(String name, String startFile, String page) throws IOException {
        InputStream viewerHTMLFileStream = DefaultApplicationController.class.getClassLoader()
            .getResourceAsStream("testStub/" + page + ".html");
        String content = IOUtils.toString(viewerHTMLFileStream, "UTF-8");
        String result = content.replace("{$name}", name).replace("{$startFile}", startFile).replace("{$baseUrl}",
            MCRSeleniumTestBase.getBaseUrl(System.getProperty("BaseUrlPort")) + "/test-classes/testFiles/");
        String fileName = buildFileName(name);
        String resultLocation = webpath + "/" + fileName;
        File resultFile = new File(resultLocation);
        resultFile.getParentFile().mkdirs();
        FileOutputStream resultStream = new FileOutputStream(resultFile);
        IOUtils.write(result, resultStream, Charset.defaultCharset());
        IOUtils.closeQuietly(viewerHTMLFileStream);
        IOUtils.closeQuietly(resultStream);
        return resultLocation;
    }

    private String buildFileName(String name) {
        return name + ".html";
    }

    protected void download(URL fileLocation, String dest) throws IOException {
        if (!SKIP_DOWNLOAD.booleanValue()) {
            createTestFolder(webpath);
            InputStream openStream = new BufferedInputStream(fileLocation.openStream(), 1024 * 1024 * 16);

            String pathToFile = fileLocation.getFile();
            if (pathToFile.endsWith(".pdf")) {
                String[] token = pathToFile.split("/");
                String fileName = token[token.length - 1];
                String destination = dest + "/" + fileName;

                LOGGER.info("Downloading pdf file to " + destination);
                IOUtils.copy(openStream, new FileOutputStream(destination));
            } else {
                LOGGER.info("Downloading test files to : " + dest);
                byte[] bytes = IOUtils.toByteArray(openStream);
                extractZip(dest, new ArrayList<String>(), new ZipInputStream(new ByteArrayInputStream(bytes)));
            }
        }
    }

    private void extractZip(String dest, List<String> relativePaths, ZipInputStream zipInputStream)
        throws IOException, FileNotFoundException {
        ZipEntry nextEntry;
        zipInputStream.available();
        while ((nextEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = nextEntry.getName();
            String fileName = dest + "/" + entryName;
            File localFile = new File(fileName);

            if (nextEntry.isDirectory()) {
                localFile.mkdir();
            } else {
                relativePaths.add(entryName);
                localFile.createNewFile();
                FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
                IOUtils.copyLarge(zipInputStream, localFileOutputStream, 0, nextEntry.getSize());

                localFileOutputStream.flush();
                localFileOutputStream.close();
            }
            zipInputStream.closeEntry();
        }
        zipInputStream.close();
        LOGGER.info("File download complete!");
    }

    private void createTestFolder(String dest) {
        File destFolder = new File(dest);
        if (!destFolder.exists()) {
            destFolder.mkdirs();
        }
    }

    @Override
    public void shutDownDerivate(WebDriver webdriver, TestDerivate testDerivate) {

    }

    @Override
    public void openViewer(WebDriver webdriver, TestDerivate testDerivate) {
        String path = null;
        path = MCRSeleniumTestBase.getBaseUrl(System.getProperty("BaseUrlPort")) + "/test-classes/testFiles/"
            + DefaultApplicationController.derivateHTMLMapping.get(testDerivate);
        LOGGER.info("Open Viewer with path : " + path);
        webdriver.navigate().to(path);

        WebDriverWait wait = new WebDriverWait(webdriver, 5000);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("/.//ol[contains(@class, 'chapterTreeDesktop')]")));

    }

}
