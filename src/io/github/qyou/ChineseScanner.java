package io.github.qyou;

import java.util.ArrayList;

public class ChineseScanner {
	private ChineseSource source;
	   private ArrayList<Error> errors;
	   private int index = 0;
	   private int line = 1;
	   private int column = 1;
	   private char ch;
	   private int kind;
	   private StringBuffer spelling;

	   public ChineseScanner(ChineseSource source, ArrayList<Error> errors) {
	      this.source = source;
	      this.errors = errors;
	      this.ch = source.text.length() == 0?0:source.text.charAt(0);
	   }

	   public ChineseToken scan() {
	      this.spelling = new StringBuffer("");
	      this.scanSeparator();
	      this.spelling = new StringBuffer("");
	      int startColumn = this.column;
	      this.kind = this.scanToken();
	      return new ChineseToken(this.kind, this.spelling.toString(), this.source, this.line, startColumn);
	   }

	   private void takeIt() {
	      this.spelling.append(this.ch);
	      this.ch = ++this.index < this.source.text.length()?this.source.text.charAt(this.index):0;
	      ++this.column;
	   }

	   private void take(char ch, String errorText) {
	      if(this.ch == ch) {
	         this.takeIt();
	      } else {
	         this.errors.add(new Error(0, errorText, this.source, this.line, this.column));
	      }

	   }

	   private void scanSeparator() {
	      while(this.ch == 35 || this.ch == 32 || this.ch == 10 || this.ch == 13 || this.ch == 9) {
	         switch(this.ch) {
	         case '\t':
	         case '\r':
	         case ' ':
	            this.takeIt();
	            break;
	         case '\n':
	            this.takeIt();
	            ++this.line;
	            this.column = 1;
	            break;
	         case '#':
	            this.takeIt();

	            while(this.ch != 10 && this.ch != 0) {
	               this.takeIt();
	            }
	         }
	      }

	   }

	   private int scanToken() {
	      if(this.ch == 0) {
	         return 1;
	      } else if(this.ch == 59) {
	         this.takeIt();
	         return 4;
	      } else if(Character.isLetter(this.ch)) {
	         this.takeIt();

	         while(Character.isLetter(this.ch) || Character.isDigit(this.ch) || this.ch == 45 || this.ch == 95) {
	            this.takeIt();
	         }

	         return 2;
	      } else if(Character.isDigit(this.ch)) {
	         this.takeIt();

	         while(Character.isDigit(this.ch)) {
	            this.takeIt();
	         }

	         return 3;
	      } else if(this.ch == 61) {
	         this.takeIt();
	         byte token = 11;
	         if(this.ch == 47) {
	            this.takeIt();
	            token = 12;
	         }

	         return token;
	      } else if(this.ch == 42) {
	         this.takeIt();
	         return 10;
	      } else if(this.ch == 47) {
	         this.takeIt();
	         return 13;
	      } else if(this.ch == 40) {
	         this.takeIt();
	         return 14;
	      } else if(this.ch == 41) {
	         this.takeIt();
	         return 15;
	      } else if(this.ch == 91) {
	         this.takeIt();
	         return 16;
	      } else if(this.ch == 93) {
	         this.takeIt();
	         return 17;
	      } else if(this.ch == 60) {
	         this.takeIt();

	         while(this.ch >= 32 && this.ch <= 126 && this.ch != 62) {
	            this.takeIt();
	         }

	         this.take('>', "prose-value-end > expected");
	         return 5;
	      } else if(this.ch == 34) {
	         this.takeIt();

//	         while(this.ch >= 32 && this.ch <= 126 && this.ch != 34) {
//	            this.takeIt();
//	         }
	         while (this.ch >= 32 && this.ch != 34) {
	        	 this.takeIt();
	         }

	         this.take('\"', "char-value-end \" expected");
	         return 6;
	      } else {
	         if(this.ch == 37) {
	            this.takeIt();
	            switch(this.ch) {
	            case 'b':
	               this.takeIt();

	               while(this.ch == 48 || this.ch == 49 || this.ch == 46 || this.ch == 45) {
	                  this.takeIt();
	               }

	               return 7;
	            case 'd':
	               this.takeIt();

	               while(this.ch >= 48 && this.ch <= 57 || this.ch == 46 || this.ch == 45) {
	                  this.takeIt();
	               }

	               return 8;
	            case 'x':
	               this.takeIt();

	               while(this.ch >= 48 && this.ch <= 57 || this.ch >= 65 && this.ch <= 70 || this.ch >= 97 && this.ch <= 102 || this.ch == 46 || this.ch == 45) {
	                  this.takeIt();
	               }

	               return 9;
	            }
	         }

	         if(this.ch == 36) {
	            this.takeIt();
	            return 18;
	         } else if(this.ch == 64) {
	            this.takeIt();
	            return 19;
	         } else if(this.ch == 44) {
	            this.takeIt();
	            return 20;
	         } else {
	            this.errors.add(new Error(0, "unexpected character", this.source, this.line, this.column));
	            this.takeIt();
	            return 0;
	         }
	      }
	   }
}
