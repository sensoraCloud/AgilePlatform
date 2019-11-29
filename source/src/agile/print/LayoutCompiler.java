/*
 * LayoutCompiler.java
 *
 * Created on 29 ottobre 2007, 1.01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 * Author: Julien Buratto
 */

package agile.print;

import java.io.*;
import javax.microedition.io.*;
/**
 *
 * @author Julien Buratto
 *
 * Create a text file with %VARIABLES% in the file, then:
 * - Create the layout: LayoutCompiler LC=new LayoutCompiler("filename.txt");
 * - Add the vars one by one: LC.add("VARIABLES","Hello World");
 * - Print the result: System.out.println(LC.parse());
 */
public class LayoutCompiler {
    
    private InputConnection IC=null;
    private String layoutFilename;
    private static int CAPACITY=50;
    private String [] variablesNames=new String[CAPACITY];
    private String [] variablesValues=new String[CAPACITY];
    private int variablesCounter=0;
    
    private StringBuffer layout=null;
    private String compiledLayout=null;
    
    /**
     * Creates a new instance of LayoutCompiler
     */
    public LayoutCompiler(String layoutFilename) {
        this.layoutFilename=layoutFilename;
    }
    
    public void add(String variableName, String value,int maxStringLenght){
        // Cuts the string if too long
        if (value.length()>maxStringLenght) this.add(variableName,value.substring(0,maxStringLenght));
        // Enhance the string if too short
        else if(value.length()<maxStringLenght){
            StringBuffer tmp=new StringBuffer(value);
            // Enhance the string adding missing spaces
            for (int k=0;k<(maxStringLenght-value.length());k++){
                tmp.append(" ");
            }
            this.add(variableName,tmp.toString());
        }
        else this.add(variableName,value);
    }
    
    public void add(String variableName, String value){
        if (variablesCounter==CAPACITY) System.out.println("You have a hard limit of "+CAPACITY+" variables");
        variablesNames[variablesCounter]=variableName;
        variablesValues[variablesCounter]=value;
        this.variablesCounter++;
    }
    
    /** Compile the text file with all the variables */
    public String parse() throws Exception {
        readFile();
        this.compiledLayout=this.layout.toString();
        for(int i=0;i<this.variablesCounter;i++){
            compiledLayout=replace(compiledLayout,"%"+this.variablesNames[i]+"%",this.variablesValues[i]);
        }
        System.out.print(compiledLayout);
        return compiledLayout;
    }
    
    /** Reset the compiled template */
    public void reset(){
        variablesNames=new String[CAPACITY];
        variablesValues=new String[CAPACITY];
        this.variablesCounter=0;
        this.compiledLayout=null;
    }
    
    private void readFile() throws Exception {
        if (this.layout!=null) return;
        try {
        InputStream is = getClass().getResourceAsStream(this.layoutFilename);
        
            if (is==null){ throw new Exception("File not found: "+this.layoutFilename);}
            InputStreamReader isr = new InputStreamReader(is,"ISO-8859-1");
            if (isr==null){ throw new Exception("File not readable: "+this.layoutFilename);}
            StringBuffer sb = new StringBuffer();
            int chr, i = 0;
            // Read until the end of the stream
            while ((chr = isr.read()) != -1) sb.append((char) chr);
            try { is.close();} catch (Exception e){}
            this.layout=sb;
            // System.out.println("Testo letto:"+sb.toString());
        } catch (Exception e) {
            System.out.println("Unable to create stream ("+e.getMessage()+"): "+this.layoutFilename);
            throw e;
        }
    }
    
    private static String replace(String _text, String _searchStr, String _replacementStr) {
        // String buffer to store str
        StringBuffer sb = new StringBuffer();
 
        // Search for search
        int searchStringPos = _text.indexOf(_searchStr);
        int startPos = 0;
        int searchStringLength = _searchStr.length();
 
        // Iterate to add string
        while (searchStringPos != -1) {
            sb.append(_text.substring(startPos, searchStringPos)).append(_replacementStr);
            startPos = searchStringPos + searchStringLength;
            searchStringPos = _text.indexOf(_searchStr, startPos);
        }
 
        // Create string
        sb.append(_text.substring(startPos,_text.length()));
 
        return sb.toString();
    } 
}
