package io.github.qyou;

public class ChineseToken {
	public int kind;
	   public String spelling;
	   public ChineseSource source;
	   public int line;
	   public int column;
	   public static final int UNKNOWN = 0;
	   public static final int EOT = 1;
	   public static final int IDENTIFIER = 2;
	   public static final int INT_VALUE = 3;
	   public static final int EOL = 4;
	   public static final int PROSE_VALUE = 5;
	   public static final int CHAR_VALUE = 6;
	   public static final int BIN_VALUE = 7;
	   public static final int DEC_VALUE = 8;
	   public static final int HEX_VALUE = 9;
	   public static final int REPETITION = 10;
	   public static final int EQUALS = 11;
	   public static final int INCREMENTAL_EQUALS = 12;
	   public static final int ALTERNATIVE = 13;
	   public static final int GROUP_START = 14;
	   public static final int GROUP_END = 15;
	   public static final int OPTION_START = 16;
	   public static final int OPTION_END = 17;
	   public static final int DIRECTIVE_START = 18;
	   public static final int ANNOTATION_START = 19;
	   public static final int COMMA = 20;
	   static final String[] descriptions = new String[]{"UNKNOWN", "EOT", "IDENTIFIER", "INT VALUE", "EOL", "PROSE VALUE", "CHAR VALUE", "BIN VALUE", "DEC VALUE", "HEX VALUE", "REPETITION", "EQUALS", "INCREMENTAL EQUALS", "ALTERNATIVE", "GROUP START", "GROUP END", "OPTION START", "OPTION END", "DIRECTIVE START", "ANNOTATION START", "COMMA"};

	   public ChineseToken(int kind, String spelling, ChineseSource source, int line, int column) {
	      this.kind = kind;
	      this.spelling = spelling;
	      this.source = source;
	      this.line = line;
	      this.column = column;
	   }

	   public String toString() {
	      return descriptions[this.kind] + "|" + this.spelling + "|" + ((ChineseSource.Line)this.source.lines.get(this.line - 1)).filename + "|" + this.line + "|" + this.column;
	   }

	   public static String getDescripton(int kind) {
	      if(kind >= descriptions.length) {
	         kind = 0;
	      }

	      return descriptions[kind];
	   }
}
