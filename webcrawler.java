package healthcoco;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class drugsupdate 
{
	static String URL;
	static int break_point=3;
	static int prevrows_m=0;
	static TreeMap < String, Object[] > druginfo = new TreeMap < String, Object[] >();
	static XSSFRow row;
	public void crawldrugs(String selected_link,Inclass in) throws Exception
	{	
		Document doc = Jsoup.connect(selected_link)
	         	  .data("query", "Java")
	       		  .cookie("auth", "token")
	       		  .timeout(15000)
	       		  .get();
		Elements drug_name=doc.select("div.brand_row:last-of-type>table>tbody>tr:eq(1)");
		
		Elements combination_one_strength=doc.select("div.brand_row:last-of-type>table>tbody>tr:eq(4)>td:eq(0)");
		Elements combination_one_volume=doc.select("div.brand_row:last-of-type>table>tbody>tr:eq(4)>td:eq(1)");
		Elements combination_one_presentation=doc.select("div.brand_row:last-of-type>table>tbody>tr:eq(4)>td:eq(2)");
		Elements combination_one_price=doc.select("div.brand_row:last-of-type>table>tbody>tr:eq(4)>td:eq(3)");

		int length=drug_name.size();
		System.out.println(drug_name.size());
		
		int size=combination_one_strength.size();
		if(size!=20)
		{
			int remaining= 20-size;
			for(int i=0;i<remaining;i++)
			{
				Element e = null;
				combination_one_strength.add(e);
				combination_one_presentation.add(e);
				combination_one_volume.add(e);
				combination_one_price.add(e);
			}
		}
		
		for(int i=0;i<length;i++)
	    {			
			String r=Integer.toString(in.getPrevrows());
			String d_name=drug_name.get(i).text();
			System.out.println(d_name);
			String c_o_strength=combination_one_strength.get(i).text();
			System.out.println(c_o_strength);
			String c_o_volume=combination_one_volume.get(i).text();
			System.out.println(c_o_volume);
			String c_o_presentation=combination_one_presentation.get(i).text();
			System.out.println(c_o_presentation);
			String c_o_price=combination_one_price.get(i).text();
			System.out.println(c_o_price);
			druginfo.put(r, new Object[] {d_name,c_o_strength,c_o_volume,c_o_presentation,c_o_price});
			in.incrementprevrows();
		}	
	}
	public void crawl(String url,Inclass in) throws Exception
	{
		crawldrugs(url,in);
		System.out.println("hey");
		
		/*if(break_point==61)
			break_point++;*/
		String url_pagination="URL"+break_point;
        System.out.println(url_pagination);
        break_point++;
        if(break_point==79)
        	return ;
        crawl(url_pagination,in);        
	}
	public static Inclass input() throws IOException 
	{
		 FileInputStream in = new FileInputStream(new File("createworkbook.xlsx"));
	      XSSFWorkbook workbook = new XSSFWorkbook(in);
	      XSSFSheet spreadsheet = workbook.getSheetAt(0);
	      Iterator < Row > rowIterator = spreadsheet.iterator();
	      int prevrows=0;
	      while (rowIterator.hasNext()){
	    	  prevrows++;
	    	  prevrows_m++;
	    	  rowIterator.next();
	      }
		return new Inclass(in,workbook,spreadsheet,prevrows);
	}
	
	public static void main(String[] args) throws Exception 
	{
		Inclass in=input();
		System.out.println(in.getPrevrows());
		
		drugsupdate crawler =new drugsupdate();
		System.out.println("Please enter URL");
		Scanner sc= new Scanner(System.in);
		String url= sc.nextLine();	
		URL=url;
		crawler.crawl(url,in);
				
		  Set < String > keyid = druginfo.keySet();
		  System.out.println(druginfo.size());
	      
		  int rowid = prevrows_m;
	      System.out.println("ROWID"+rowid+"*******");
	      
	      XSSFWorkbook workbookout=in.getWorkbook(); 
	      XSSFSheet spreadsheetout=in.getSheet();
	      for (String key : keyid)
	      {
	         row = spreadsheetout.createRow(rowid++);
	         Object [] objectArr = druginfo.get(key);
	         int cellid = 0;
	         for (Object obj : objectArr)
	         {
	            Cell cell = row.createCell(cellid++);
	            cell.setCellValue((String)obj);
	         }
	      }
	      FileOutputStream out = new FileOutputStream(new File("createworkbook.xlsx"));
	      workbookout.write(out); 
	      workbookout.close();
	      System.out.println("completed");
	      FileInputStream fis=in.getfileinputstream();
	      fis.close();
	}
}
