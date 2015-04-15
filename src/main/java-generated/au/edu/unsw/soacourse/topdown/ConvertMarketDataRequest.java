
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
 *         &lt;element name="eventSetID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="targetCurrency" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="targetDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "eventSetID",
    "targetCurrency",
    "targetDate"
})
@XmlRootElement(name = "convertMarketDataRequest")
public class ConvertMarketDataRequest {

    @XmlElement(required = true)
    protected String eventSetID;
    @XmlElement(required = true)
    protected String targetCurrency;
    @XmlElement(required = true)
    protected String targetDate;

    /**
     * Gets the value of the eventSetID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventSetID() {
        return eventSetID;
    }

    /**
     * Sets the value of the eventSetID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventSetID(String value) {
        this.eventSetID = value;
    }

    /**
     * Gets the value of the targetCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetCurrency() {
        return targetCurrency;
    }

    /**
     * Sets the value of the targetCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetCurrency(String value) {
        this.targetCurrency = value;
    }

    /**
     * Gets the value of the targetDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetDate() {
        return targetDate;
    }

    /**
     * Sets the value of the targetDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetDate(String value) {
        this.targetDate = value;
    }

}
