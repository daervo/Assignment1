package au.edu.unsw.soacourse.topdown;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import javax.jws.WebService;

@WebService(endpointInterface = "au.edu.unsw.soacourse.topdown.TopDownSimpleService")
public class TopDownSimpleServiceImpl implements TopDownSimpleService {
	
	ObjectFactory factory = new ObjectFactory();
	static String fileStorageDirectory = System.getProperty("catalina.home") + File.separator + "webapps" + File.separator + "ROOT"
	+ File.separator + "MarketFiles" + File.separator;
	
    public ImportMarketDataResponse importMarketData(ImportMarketDataRequest parameters)
    throws ImportMarketFaultMsg {  
    	// Check the request is valid
    	
    	if (parameters.getSec().length() != 3) {
    		//assume that we only want a SEC value of length 3
    		String msg = "SEC code should be exactly 3 characters long";
    		String code= "2";
    		ServiceFaultType fault = factory.createServiceFaultType();
    		fault.errcode = code;
    		fault.errtext = msg;
    		
    		throw new ImportMarketFaultMsg(msg,fault);
    	}
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    	Date dateFrom = null;
    	Date dateTo = null;
		try {
			dateFrom = sdf.parse(parameters.startDate);
			dateTo   = sdf.parse(parameters.endDate);
		} catch (ParseException e1) {
			String msg = "Date not in correct format";
    		String code= "4";
    		ServiceFaultType fault = factory.createServiceFaultType();
    		fault.errcode = code;
    		fault.errtext = msg;
    		
    		throw new ImportMarketFaultMsg(msg,fault);
		}

    	if(dateFrom.compareTo(dateTo)>0){
    		String msg = "endDate must be after startDate";
    		String code= "3";
    		ServiceFaultType fault = factory.createServiceFaultType();
    		fault.errcode = code;
    		fault.errtext = msg;
    		
    		throw new ImportMarketFaultMsg(msg,fault);
    	}
    	
    	// Get parameters
    	String startDay   = parameters.startDate.substring(0,2);
    	String startMonth = parameters.startDate.substring(3,5);
    	String startYear  = parameters.startDate.substring(6,10);
    	String endDay     = parameters.endDate.substring(0,2);
    	String endMonth   = parameters.endDate.substring(3,5);
    	String endYear    = parameters.endDate.substring(6,10);
    	
    	// Generate URL
    	String downloadURL = "http://real-chart.finance.yahoo.com/table.csv?";
    	downloadURL += "s=" + parameters.sec;
    	downloadURL += "&a=" + startMonth;
    	downloadURL += "&b=" + startDay;
    	downloadURL += "&c=" + startYear;
    	downloadURL += "&d=" + endMonth;
    	downloadURL += "&e=" + endDay;
    	downloadURL += "&f=" + endYear;
    	downloadURL += "&g=d&ignore=.csv";
    	
    	// Generate unique file name
    	UUID fileID = UUID.randomUUID();
    	String tempFolderPath = fileStorageDirectory + fileID;
    	
    	try {
			saveFile(downloadURL, tempFolderPath, parameters.startDate, parameters.endDate, parameters.sec);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	ImportMarketDataResponse res = factory.createImportMarketDataResponse();
    	res.returnData = fileID.toString();
    	
    	return res;
  }

    public DownloadFileResponse downloadFile(DownloadFileRequest parameters)
    throws DownloadFileFaultMsg{
    	// check if file exists, if it doesn't throw fault
    	File f = new File(fileStorageDirectory + parameters.eventSetID + ".csv");
    	if(!f.exists()) {
    		ServiceFaultType fault = factory.createServiceFaultType();
    		String msg = "File does not exist";
    		String code = "1";
    		fault.errcode = code;
    		fault.errtext = msg;
    		throw new DownloadFileFaultMsg(msg, fault);
    	}
    	
    	DownloadFileResponse res = factory.createDownloadFileResponse();
    	res.dataURL = "http://localhost:8080/MarketFiles/" + parameters.eventSetID + ".csv";
    	return res; 	
    }

	public static void saveFile(String downloadURL, String newFile, String startDate, String endDate, String sec) throws IOException {
		// Check if our temporary directory exists, if not, create it
		
		System.out.println(fileStorageDirectory);
		File storageDirectory = new File(fileStorageDirectory);
		String line = "";
		// if the directory does not exist, create it
		if (!storageDirectory.exists()) {
		    try{
		    	storageDirectory.mkdir();
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		}
		
		URL url = new URL(downloadURL);
		InputStream is = url.openStream();
		OutputStream os = new FileOutputStream(newFile + ".csv");
		
		BufferedReader reader = new BufferedReader (new InputStreamReader(is));
		PrintStream ps = new PrintStream(os);
		
		boolean first = true;
		while ((line = reader.readLine()) != null){
			if (first){
				line = "Sec," + line;
				first = false;
				ps.println(line);
			} else {
				line = sec + "," + line;
				
				// split the line up and add in currency prefix
				String[] split = line.split(",");
				split[2] = "AUD" + split[2];    // open
				split[3] = "AUD" + split[3];   	// high  	
				split[4] = "AUD" + split[4];	// low
				split[5] = "AUD" + split[5];	// close
				split[7] = "AUD" + split[7];	// adj close
				
				// join string back together
				line = Arrays.asList(split).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
				ps.println(line);
			}
		}
		
		is.close();
		os.close();
		
		// Create info file
		createInfoFile(newFile, sec, startDate, endDate, "AUD");
	}
	
	// Taken from stackoverflow
	public static String readableSize(long bytes) {
	    int unit = 1000;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    char pre = ("KMGTPE").charAt(exp-1);
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public void createHtmlFile(String eventSetId){
		OutputStream os = null;
		String line = null;
	    BufferedReader stream = null;
	    boolean first = true;
	    boolean header = true;
	    
		try {
			os = new FileOutputStream(fileStorageDirectory + eventSetId + ".html");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		PrintStream printStream = new PrintStream(os);
		printStream.println("<table border=\"1\">");		
		
	    try {
	        stream = new BufferedReader(new FileReader(fileStorageDirectory + eventSetId + ".csv"));
	        while ((line = stream.readLine()) != null) {
	            String[] splitted = line.split(",");
	            
	            for (String data : splitted){
	            	if (header){
	            		printStream.println("<th>" + data + "</th>");
	            	} else if (first) {
	            		printStream.println("<tr><td>" + data + "</td>");
	            		first = false;
	            	} else{
	            		printStream.println("<td>" + data + "</td>");
	            	}
	            }
	            
	            header = false;
	            first = true;
	            printStream.println("</tr>");
	        }
	        
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    }
	    printStream.println("</table>");
	    printStream.close();
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createInfoFile(String fileName, String sec, String startDate, String endDate, String currency){
		OutputStream os = null;
		try {
			os = new FileOutputStream(fileName + ".txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintStream printStream = new PrintStream(os);
		printStream.println(sec);
		printStream.println(startDate);
		printStream.println(endDate);
		printStream.println(currency);		
		printStream.close();
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}