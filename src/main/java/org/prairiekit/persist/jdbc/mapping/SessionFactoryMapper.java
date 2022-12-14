package org.prairiekit.persist.jdbc.mapping;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.prairiekit.domain.definition.FieldDefinition;
import org.prairiekit.domain.definition.FieldType;
import org.prairiekit.domain.definition.UdoDefinition;
import org.prairiekit.persist.jdbc.UdoDefinitionEntity;
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
import java.util.Collections;
import java.util.Map;

public class SessionFactoryMapper {

    public SessionFactoryBuilder mapToSessionFactoryBuilder(DataSource dataSource) {
        return mapToSessionFactoryBuilder(dataSource, Collections.emptyMap(), null);
    }

    public SessionFactoryBuilder mapToSessionFactoryBuilder(DataSource dataSource, Map<String, Object> settings, UdoDefinition udoDef) {
        InputStream xmlInputStream = null;
        if (udoDef != null) {
            Document xmlDoc = mapUdoToHibernateMapping(udoDef);
            xmlInputStream = docToInputStream(xmlDoc);
        }

        StandardServiceRegistry standardRegistry = buildStandardRegistry(dataSource, settings);
        MetadataSources metadataSources = new MetadataSources(standardRegistry)
                .addAnnotatedClass(UdoDefinitionEntity.class);
        if (udoDef != null) {
            metadataSources.addInputStream(xmlInputStream);
        }
        Metadata metadata = metadataSources.getMetadataBuilder().build();
        return metadata.getSessionFactoryBuilder();
    }

    private StandardServiceRegistry buildStandardRegistry(DataSource dataSource, Map<String, Object> settings) {
        return new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .applySetting(AvailableSettings.DATASOURCE, dataSource)
                .build();
    }

    private Document mapUdoToHibernateMapping(UdoDefinition udoDef) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("hibernate-mapping");
            Element classElement = doc.createElement("class");
            classElement.setAttribute("entity-name", udoDef.name());
            classElement.appendChild(mapIdToElement(doc));
            udoDef.fieldDefinitions().stream()
                    .map(fieldDef -> this.mapFieldToElement(fieldDef, doc))
                    .forEach(classElement::appendChild);
            rootElement.appendChild(classElement);
            doc.appendChild(rootElement);
            return doc;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Element mapIdToElement(Document doc) {
        Element elem = doc.createElement("id");
        elem.setAttribute("name", DynamicEntityMapper.ID_COL_NAME);
        elem.setAttribute("length", "255");
        elem.setAttribute("type", "integer");
        Element generator = doc.createElement("generator");
        generator.setAttribute("class", "native");
        elem.appendChild(generator);
        return elem;
    }

    private Element mapFieldToElement(FieldDefinition fieldDef, Document doc) {
        Element elem = doc.createElement("property");
        elem.setAttribute("name", fieldDef.name());
        elem.setAttribute("length", "255");
        elem.setAttribute("type", mapFieldType(fieldDef.type()));
        return elem;
    }

    private String mapFieldType(FieldType fieldType) {
        return switch (fieldType) {
            case TEXT -> "string";
            case INTEGER -> "integer";
            case DATE -> "LocalDate";
        };
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
