
package au.edu.unsw.soacourse.topdown;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataURL" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "dataURL"
})
@XmlRootElement(name = "downloadFileResponse")
public class DownloadFileResponse {

    @XmlElement(required = true)
    protected String dataURL;

    /**
     * Gets the value of the dataURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataURL() {
        return dataURL;
    }

    /**
     * Sets the value of the dataURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataURL(String value) {
        this.dataURL = value;
    }

}
