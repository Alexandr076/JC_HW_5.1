import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static String listToJSON(List<Employee> list) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list);
    }

    private static void writeString(String json, String fileName) {
        try(FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        } catch (IOException e) {
            throw new RuntimeException("Something was wrong... " + fileName + " " + e);
        }
    }
    public static List<Employee> parseCSV(String[] col, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setColumnMapping(col);
            strategy.setType(Employee.class);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            return (List<Employee>) csvToBean.parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found " + e);
        } catch (IOException e) {
            throw new RuntimeException("Exception " + e);
        }
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document doc = documentBuilder.parse(new File(fileName));
        Node root = doc.getDocumentElement();

        NodeList nodeList = root.getChildNodes();
        for (int i = 1; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                list.add(new Employee(
                        Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                        element.getElementsByTagName("firstName").item(0).getTextContent(),
                        element.getElementsByTagName("lastName").item(0).getTextContent(),
                        element.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                ));
            }
        }
        return list;
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        // first part
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        List<Employee> listFromCSV = parseCSV(columnMapping, csvFileName);
        String jsonFromCSV = listToJSON(listFromCSV);
        writeString(jsonFromCSV, "data.json");
        // second part
        String xmlFileName = "data.xml";
        List<Employee> listFromXML = parseXML("data.xml");
        String jsonFromXML = listToJSON(listFromXML);
        writeString(jsonFromXML, "data2.json");
    }
}
