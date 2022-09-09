package org.varmentano.nocode_plugin.jdbc.mapping;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.varmentano.nocode_plugin.jdbc.domain.definition.FieldDefinition;
import org.varmentano.nocode_plugin.jdbc.domain.definition.ObjectDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

public class HibernateMapper {

    public SessionFactoryBuilder mapToSessionFactoryBuilder(ObjectDefinition udoDef, DataSource dataSource, Map<String, Object> settings) {
        Document xmlDoc = mapUdoToHibernateMapping(udoDef);
        InputStream xmlInputStream = docToInputStream(xmlDoc);

        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .applySetting(AvailableSettings.DATASOURCE, dataSource)
                .build();
        Metadata metadata = new MetadataSources(standardRegistry)
                .addInputStream(xmlInputStream)
                .getMetadataBuilder()
                .build();
        return metadata.getSessionFactoryBuilder();
    }
    private Document mapUdoToHibernateMapping(ObjectDefinition udoDef) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("hibernate-mapping");
            Element classElement = doc.createElement("class");
            classElement.setAttribute("entity-name", udoDef.getName());
            udoDef.getFieldDefinitions().stream()
                    .map(fieldDef -> this.mapFieldToElement(fieldDef, doc))
                    .forEach(classElement::appendChild);
            rootElement.appendChild(classElement);
            doc.appendChild(rootElement);
            return doc;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Element mapFieldToElement(FieldDefinition fieldDef, Document doc) {
        Element elem;
        if (fieldDef.isId()) {
            elem = doc.createElement("id");
        } else {
            elem = doc.createElement("property");
        }
        elem.setAttribute("name", fieldDef.getName());
        elem.setAttribute("length", "255");
        elem.setAttribute("type", fieldDef.getType());
        return elem;
    }

    private InputStream docToInputStream(Document doc) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(outputStream);
            transformer.transform(source, result);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
