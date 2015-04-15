
package au.edu.unsw.soacourse.topdown;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for serviceFaultType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="serviceFaultType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errcode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="errtext" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceFaultType", propOrder = {
    "errcode",
    "errtext"
})
public class ServiceFaultType {

    @XmlElement(required = true)
    protected String errcode;
    @XmlElement(required = true)
    protected String errtext;

    /**
     * Gets the value of the errcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrcode() {
        return errcode;
    }

    /**
     * Sets the value of the errcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrcode(String value) {
        this.errcode = value;
    }

    /**
     * Gets the value of the errtext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrtext() {
        return errtext;
    }

    /**
     * Sets the value of the errtext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrtext(String value) {
        this.errtext = value;
    }

}
