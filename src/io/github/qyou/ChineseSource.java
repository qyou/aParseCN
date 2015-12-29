package io.github.qyou;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class ChineseSource {
	public File file;
	   public StringBuffer text = new StringBuffer();
	   public ArrayList<ChineseSource.Line> lines = new ArrayList();

	   public void load(File file) throws IOException {
	      this.file = file;
	      this.lines = new ArrayList();
	      this.text = new StringBuffer();
	      //BufferedReader in = new BufferedReader(new FileReader(file));
	      String tempEncoding = FileDetectorEncoder.guessChineseCharset(file);
	      String encoding = (tempEncoding == null) ? System.getProperty("file.encoding") : tempEncoding;
	      BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
	      String line = null;
	      int lineNumber = 0;

	      while((line = in.readLine()) != null) {
	         this.lines.add(new ChineseSource.Line(file.toString(), lineNumber++, line));
	         this.text.append(line);
	         this.text.append(System.getProperty("line.separator"));
	      }

	      this.text.deleteCharAt(this.text.length() - 1);
	      in.close();
	   }

	   public ChineseSource append(ChineseSource source) {
	      this.text.append(source.text);
	      this.lines.addAll(source.lines);
	      return this;
	   }

	   public void dump() {
	      System.out.println("-- dump | start --");
	      System.out.println(this.text);
	      int i = 0;

	      while(i < this.text.length()) {
	         int j;
	         for(j = 0; j < 16 && i < this.text.length(); ++j) {
	            System.out.printf("%02X ", new Object[]{Integer.valueOf(this.text.charAt(i))});
	            ++i;
	         }

	         i = i;

	         for(j = 0; j < 16 && i < this.text.length(); ++j) {
	            if(!Character.isISOControl(this.text.charAt(i))) {
	               System.out.print(this.text.charAt(i));
	            } else {
	               System.out.print(" ");
	            }

	            ++i;
	         }

	         System.out.println("");
	      }

	      System.out.println("-- dump | end --");
	   }

	   public class Line {
	      String filename;
	      int index;
	      String text;

	      Line(String filename, int index, String text) {
	         this.filename = filename;
	         this.index = index;
	         this.text = text;
	      }
	   }
}
