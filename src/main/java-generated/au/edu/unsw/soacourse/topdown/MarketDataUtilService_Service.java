package au.edu.unsw.soacourse.topdown;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 3.0.4
 * 2015-04-15T21:12:12.616+10:00
 * Generated source version: 3.0.4
 * 
 */
@WebServiceClient(name = "MarketDataUtilService", 
                  wsdlLocation = "file:/C:/Users/Ervin/workspace2015/Assignment1/src/main/resources/wsdl/MarketDataUtilService.wsdl",
                  targetNamespace = "http://topdown.soacourse.unsw.edu.au") 
public class MarketDataUtilService_Service extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://topdown.soacourse.unsw.edu.au", "MarketDataUtilService");
    public final static QName MarketDataUtilServiceSOAP = new QName("http://topdown.soacourse.unsw.edu.au", "MarketDataUtilServiceSOAP");
    static {
        URL url = null;
        try {
            url = new URL("file:/C:/Users/Ervin/workspace2015/Assignment1/src/main/resources/wsdl/MarketDataUtilService.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(MarketDataUtilService_Service.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:/C:/Users/Ervin/workspace2015/Assignment1/src/main/resources/wsdl/MarketDataUtilService.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public MarketDataUtilService_Service(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public MarketDataUtilService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MarketDataUtilService_Service() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public MarketDataUtilService_Service(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public MarketDataUtilService_Service(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    //This constructor requires JAX-WS API 2.2. You will need to endorse the 2.2
    //API jar or re-run wsdl2java with "-frontend jaxws21" to generate JAX-WS 2.1
    //compliant code instead.
    public MarketDataUtilService_Service(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }    

    /**
     *
     * @return
     *     returns MarketDataUtilService
     */
    @WebEndpoint(name = "MarketDataUtilServiceSOAP")
    public MarketDataUtilService getMarketDataUtilServiceSOAP() {
        return super.getPort(MarketDataUtilServiceSOAP, MarketDataUtilService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns MarketDataUtilService
     */
    @WebEndpoint(name = "MarketDataUtilServiceSOAP")
    public MarketDataUtilService getMarketDataUtilServiceSOAP(WebServiceFeature... features) {
        return super.getPort(MarketDataUtilServiceSOAP, MarketDataUtilService.class, features);
    }

}
