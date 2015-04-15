
package au.edu.unsw.soacourse.topdown;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 3.0.4
 * 2015-04-15T20:10:17.159+10:00
 * Generated source version: 3.0.4
 */

@WebFault(name = "downloadFileFault", targetNamespace = "http://topdown.soacourse.unsw.edu.au")
public class DownloadFileFaultMsg extends Exception {
    
    private au.edu.unsw.soacourse.topdown.ServiceFaultType downloadFileFault;

    public DownloadFileFaultMsg() {
        super();
    }
    
    public DownloadFileFaultMsg(String message) {
        super(message);
    }
    
    public DownloadFileFaultMsg(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadFileFaultMsg(String message, au.edu.unsw.soacourse.topdown.ServiceFaultType downloadFileFault) {
        super(message);
        this.downloadFileFault = downloadFileFault;
    }

    public DownloadFileFaultMsg(String message, au.edu.unsw.soacourse.topdown.ServiceFaultType downloadFileFault, Throwable cause) {
        super(message, cause);
        this.downloadFileFault = downloadFileFault;
    }

    public au.edu.unsw.soacourse.topdown.ServiceFaultType getFaultInfo() {
        return this.downloadFileFault;
    }
}
