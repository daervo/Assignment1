package au.edu.unsw.soacourse.topdown;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import javax.jws.WebService;

@WebService(endpointInterface = "au.edu.unsw.soacourse.topdown.MarketDataUtilService")
public class MarketDataUtilServiceImpl implements MarketDataUtilService{
	ObjectFactory factory = new ObjectFactory();
	static String fileStorageDirectory = System.getProperty("catalina.home") + File.separator + "webapps" + File.separator + "ROOT"
	+ File.separator + "MarketFiles" + File.separator;

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