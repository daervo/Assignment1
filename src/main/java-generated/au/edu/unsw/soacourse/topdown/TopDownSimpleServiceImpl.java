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
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jws.WebService;

import org.apache.commons.io.FileUtils;

@WebService(endpointInterface = "au.edu.unsw.soacourse.topdown.TopDownSimpleService")
public class TopDownSimpleServiceImpl implements TopDownSimpleService {
	
	ObjectFactory factory = new ObjectFactory();
	static String fileStorageDirectory = System.getProperty("catalina.home") + File.separator + "webapps" + File.separator + "Assignment1" + File.separator;//System.getProperty("java.io.tmpdir") + "9322-EM-JR/";
	
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
    	// FIX: This URL needs to be on the web server
    	/*try {
			res.dataURL = f.toURI().toURL().toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	//res.dataURL = fileStorageDirectory + parameters.eventSetID + ".csv";
    	res.dataURL = "http://localhost:8080/Assignment1/" + parameters.eventSetID + ".csv";
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