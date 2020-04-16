package de.terrestris.shogun.interceptor.util;

import de.terrestris.shogun.interceptor.exception.InterceptorException;
import de.terrestris.shogun.interceptor.servlet.MutableHttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.Charset;

import static org.apache.logging.log4j.LogManager.getLogger;

public class OgcXmlUtil {

    /**
     * The Logger.
     */
    private static final Logger LOG = getLogger(OgcXmlUtil.class);

    /**
     * The default charset.
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * @param request
     * @return
     */
    public static String getRequestBody(HttpServletRequest request) {
        try (
            ServletInputStream in = request.getInputStream();
        ) {
            String encoding = request.getCharacterEncoding();
            Charset charset;
            if (!StringUtils.isEmpty(encoding)) {
                charset = Charset.forName(encoding);
            } else {
                charset = Charset.forName(DEFAULT_CHARSET);
            }
            return StreamUtils.copyToString(in, charset);
        } catch (IOException e) {
            LOG.error("Could not read the InputStream as String: " +
                e.getMessage());
        }
        return null;
    }

    /**
     * @param xml
     * @return
     * @throws IOException
     */
    public static Document getDocumentFromString(String xml) throws IOException {
        Document document;
        try {
            InputSource source = new InputSource(new StringReader(xml));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(source);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IOException("Could not parse input body " +
                "as XML: " + e.getMessage());
        }
        return document;
    }

    /**
     * @param document
     * @param path
     * @return
     * @throws InterceptorException
     */
    public static String getPathInDocument(Document document, String path)
        throws InterceptorException {

        if (document == null) {
            throw new InterceptorException("Document may not be null");
        }

        if (StringUtils.isEmpty(path)) {
            throw new InterceptorException("Missing parameter path");
        }

        String result;

        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(path);
            result = expr.evaluate(document, XPathConstants.STRING).toString();
        } catch (XPathExpressionException e) {
            throw new InterceptorException("Error while selecting document " +
                "element with XPath: " + e.getMessage());
        }

        return result;
    }

    /**
     * @param document
     * @param path
     * @return
     * @throws InterceptorException
     */
    public static NodeList getPathInDocumentAsNodeList(Document document, String path) throws InterceptorException {
        if (document == null) {
            throw new InterceptorException("Document may not be null");
        }

        if (StringUtils.isEmpty(path)) {
            throw new InterceptorException("Missing parameter path");
        }

        NodeList result;

        try {
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile(path);
            result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new InterceptorException("Error while selecting document " +
                "element with XPath: " + e.getMessage());
        }

        return result;
    }

    /**
     * Return the document {@link Element} of a {@link Document}
     *
     * @param doc The {@link Document} to obtain the document element from
     * @return
     */
    public static Element getDocumentElement(Document doc) {
        Element docElement = null;
        if (doc != null) {
            docElement = doc.getDocumentElement();
            if (docElement != null) {
                // optional, but recommended
                // see here: http://bit.ly/1h2Ybzb
                docElement.normalize();
            }
        }
        return docElement;
    }

    /**
     * Method overrides the {@link InputStream} of an request with the given doc
     *
     * @param doc The document to copy from
     *
     * @return MutableHttpServletRequest
     */
    public static MutableHttpServletRequest setRequestInputStreamWithDoc(Document doc, MutableHttpServletRequest request) {
        Source xmlSource = new DOMSource(doc);
        try (
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ) {
            Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);

            try (InputStream is = new ByteArrayInputStream(outputStream.toByteArray())) {
                request.setInputStream(is);
            }
            return request;
        } catch (TransformerException | IOException e) {
            LOG.error("Error on trying to parse an xml body.");
            LOG.trace("Stack trace:", e);
        }
        return null;
    }

}
