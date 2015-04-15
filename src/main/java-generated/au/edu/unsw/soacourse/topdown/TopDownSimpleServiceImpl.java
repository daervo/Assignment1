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
	static String fileStorageDirectory = System.getProperty("java.io.tmpdir") + "9322-EM-JR/";
	
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
    	res.dataURL = fileStorageDirectory + parameters.eventSetID + ".csv";
    	return res; 	
    }

	@Override
	public VisualiseMarketDataResponse visualiseMarketData(VisualiseMarketDataRequest parameters)
	throws VisualiseMarketFaultMsg{
		// check if file exists, if it doesn't throw fault
    	File f = new File(fileStorageDirectory + parameters.eventSetID + ".csv");
    	if(!f.exists()) {
    		ServiceFaultType fault = factory.createServiceFaultType();
    		String msg = "File does not exist";
    		String code = "1";
    		fault.errcode = code;
    		fault.errtext = msg;
    		throw new VisualiseMarketFaultMsg(msg, fault);
    	}
		
		createHtmlFile(parameters.eventSetID);
		
		VisualiseMarketDataResponse res = factory.createVisualiseMarketDataResponse();
		// FIX: This URL needs to be on the web server
		res.dataURL = fileStorageDirectory + parameters.eventSetID + ".html";

    	return res;
	}

	@Override
	public SummariseMarketDataResponse summariseMarketData(SummariseMarketDataRequest parameters)
	throws SummariseMarketFaultMsg{
		// check if file exists, if it doesn't throw fault
    	File f = new File(fileStorageDirectory + parameters.eventSetID + ".csv");
    	if(!f.exists()) {
    		ServiceFaultType fault = factory.createServiceFaultType();
    		String msg = "File does not exist";
    		String code = "1";
    		fault.errcode = code;
    		fault.errtext = msg;
    		throw new SummariseMarketFaultMsg(msg, fault);
    	}
    	
		SummariseMarketDataResponse res = factory.createSummariseMarketDataResponse();
	    BufferedReader stream = null;
	    res.eventSetId = parameters.eventSetID;
	    
	    try {
	        stream = new BufferedReader(new FileReader(fileStorageDirectory + parameters.eventSetID + ".txt"));
	        res.sec = stream.readLine();
	        res.startDate = stream.readLine();
	        res.endDate = stream.readLine();
	        res.currencyCode = stream.readLine();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    
	    try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    File csvFile = new File(fileStorageDirectory + parameters.eventSetID + ".csv");
	    res.fileSize = readableSize(csvFile.length());

    	return res;
	}

	@Override
	public ConvertMarketDataResponse convertMarketData(ConvertMarketDataRequest parameters)
	throws ConvertMarketFaultMsg{
		String date = parameters.targetDate;
		String countryCodeFrom = "";
		String countryCodeTo = parameters.targetCurrency;
		double conversionRate = 0;
		// Get the country code the data is stored in
		BufferedReader stream = null;
		try {
	        stream = new BufferedReader(new FileReader(fileStorageDirectory + parameters.eventSetID + ".txt"));
	        stream.readLine();
	        stream.readLine();
	        stream.readLine();
	        countryCodeFrom = stream.readLine();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String downloadURL = "http://www.xe.com/currencytables/?from=" + countryCodeFrom + "&date=" + date;
		
		URL url = null;
		try {
			url = new URL(downloadURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URLConnection conn = null;
		try {
			conn = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn.setRequestProperty("User-Agent",
		        "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
		try {
			conn.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader serverResponse = null;
		try {
			serverResponse = new BufferedReader(
			        new InputStreamReader(conn.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while (!serverResponse.readLine().contains("<tbody>")){
				// do nothing, loop through and find necessary data
			}
			// now the next line contains all the data we need
			String dataString = serverResponse.readLine();
			
			String noHTML = dataString.replaceAll("<!--.*?-->", "").replaceAll("<[^>]+>", " "); // strip HTML
			noHTML = noHTML.replaceAll("\\-->", "");
			String[] array = noHTML.split("\\s+");;
			boolean done = false;
			for (int i = 0; !done && i < array.length - 1; i++){
				if (array[i].contentEquals(countryCodeTo)){
					while (!array[i].matches("-?\\d+(\\.\\d+)?")){
						i++;
					}
					conversionRate = Double.parseDouble(array[i]);
					done = true;
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			serverResponse.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Open original file, convert, and write to new file
		UUID fileID = UUID.randomUUID();
    	
    	// get info from original info file
    	BufferedReader stream2 = null;

	    String sec = null;
	    String startDate = null;
	    String endDate = null;
	    
	    try {
	        stream2 = new BufferedReader(new FileReader(fileStorageDirectory + parameters.eventSetID + ".txt"));
	        sec = stream2.readLine();
	        startDate = stream2.readLine();
	        endDate = stream2.readLine();
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    
	    try {
			stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	createInfoFile(fileStorageDirectory+fileID.toString(), sec, startDate, endDate, countryCodeTo);
    	createDataFile(fileStorageDirectory+fileID.toString(), fileStorageDirectory + parameters.eventSetID, conversionRate, countryCodeTo);
    	
    	ConvertMarketDataResponse res = factory.createConvertMarketDataResponse();
		res.eventSetId = fileID.toString();
    	return res;
	}
	
	public static void saveFile(String downloadURL, String newFile, String startDate, String endDate, String sec) throws IOException {
		// Check if our temporary directory exists, if not, create it
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
	
	private static void createDataFile(String newFile, String originalFile, double conversionRate, String countryCode){
		// create new data file
    	OutputStream os = null;
    	String line = null;
    	boolean first = true;
    	BufferedReader br = null;
    	
    	try {
			os = new FileOutputStream(newFile + ".csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	PrintStream ps = new PrintStream(os);
    	
    	try {
	        br = new BufferedReader(new FileReader(originalFile + ".csv"));
	        while ((line = br.readLine()) != null) {
	            if (first){
	            	// just append the first line as is
	            	ps.println(line);
	            	first = false;
	            } else {
	            	String[] split = line.split(",");
	            	
	            	// convert element 2,3,4,5, and 7 of split
	            	// The first 3 characters are the country code, so we can ignore them
	            	double open     = Double.parseDouble(split[2].substring(3))*conversionRate;
	            	double high     = Double.parseDouble(split[3].substring(3))*conversionRate;
	            	double low      = Double.parseDouble(split[4].substring(3))*conversionRate;
	            	double close    = Double.parseDouble(split[5].substring(3))*conversionRate;
	            	double adjClose = Double.parseDouble(split[7].substring(3))*conversionRate;
	            	
	            	// build up the new row of data
	            	line =  split[0] + ",";
	            	line += split[1] + ",";
	            	line += countryCode + String.format("%.2f", open)     + ",";
	            	line += countryCode + String.format("%.2f", high)     + ",";
	            	line += countryCode + String.format("%.2f", low)      + ",";
	            	line += countryCode + String.format("%.2f", close)    + ",";
	            	line += split[6] + ",";
	            	line += countryCode + String.format("%.2f", adjClose);
	            	
	            	ps.println(line);
	            }
	        }
	        
	    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
    	
    	ps.close();
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