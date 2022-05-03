package org.agmip.translators.salus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Objects;

public class SalusXmlWeatherHandler extends DefaultHandler {
    private StringBuilder _sb;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        _sb = new StringBuilder();
        System.out.println(qName);
        if (attributes.getLength() != 0) {
            for(int x=0; x < attributes.getLength(); x++) {
                System.out.println("\t"+attributes.getQName(x) + ": " + attributes.getValue(x));
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (Objects.isNull(_sb)) {
            _sb = new StringBuilder();
        }
        _sb.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName == "Weather") {
            System.out.println("In New Weather Station");
            String[] colNames = "Year,DOY,SRAD,Tmax,Tmin,Rain,DewP,Wind,PAR".split(",");

            //String json = "{ \"f1\" : \"v1\" } ";
            ObjectMapper objectMapper = new ObjectMapper();
            ArrayNode jsonNode = objectMapper.createArrayNode();

            int i, j;
            // Convert data blob to array of strings
            String[] days = _sb.toString().split(System.lineSeparator());
            // Loop through the days, only process non-zero length strings
            for (i = 0; i < days.length; i++) {
                if (days[i].length() > 6) {
                    String[] wthValues = days[i].split(",");
                    System.out.println(String.valueOf(colNames.length) + "  " + String.valueOf(wthValues.length));
                    ObjectNode wthColValue = objectMapper.createObjectNode();
                    // the split ignores trailing blank values so use the shortest of the header or data arrays
                    for (j = 0; j < Math.min(colNames.length, wthValues.length); j++) {
                        wthColValue.put(colNames[j], wthValues[j]);
                    }
                    jsonNode.add(wthColValue);
                }
            }
            String outFN = "/Users/baerb/OneDriveMSU/DataHarmonization/2022-05-02_Workshop/junk.json";
//            System.out.println(jsonNode.toPrettyString());
        }
    }
}

