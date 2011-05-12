/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.geosolutions.geoserver.rest.encoder;

import java.util.HashMap;
import java.util.Map;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Creates an XML document by mapping properties to XML nodes.<br/>
 * You can set the root element in the constructor.
 * Any key/value pair will be encoded as <key>value</key> node.
 * Any key containing one or more slash ("/") will be encoded as nested nodes;
 * <P>e.g.:</P>
 * <PRE> {@code key = "k1/k2/k3", value = "value" }</pre>
 * will be encoded as
 * <PRE> {@code <k1><k2><k3>value</k3></k2></k1> }</pre>
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class PropertyXMLEncoder {

    private final static XMLOutputter OUTPUTTER = new XMLOutputter(Format.getCompactFormat());
    private final Map<String, String> configElements = new HashMap<String, String>();
    private final String rootName;

    public PropertyXMLEncoder(String rootName) {
        this.rootName = rootName;
    }
        
    protected void setOrRemove(String key, String value) {
        if (value != null) {
            configElements.put(key, value);
        } else {
            configElements.remove(key);
        }
    }

    protected void set(String key, String value) {
        setOrRemove(key, value);
    }

    public boolean isEmpty() {
        return configElements.isEmpty();
    }

    public String encodeXml() {

        Element layer = new Element(rootName);
        for (String key : configElements.keySet()) {
            final String value = configElements.get(key);
            add(layer, key, value);
        }

        addNodesBeforeOutput(layer);
        return OUTPUTTER.outputString(layer);
    }

    protected void addNodesBeforeOutput(Element e) {
    
    }
    
    private void add(Element e, String key, String value) {
        if( ! key.contains("/") ) {
            e.addContent(new Element(key).setText(value));
        } else {
            int i = key.indexOf("/");
            String childName = key.substring(0,i);
            String newkey = key.substring(i+1);
            
            Element child = e.getChild(childName);
            if(child == null) {
                child = new Element(childName);
                e.addContent(child);
            }
            
            add(child, newkey, value);        
        }

    }
}
