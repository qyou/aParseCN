package io.github.qyou;


public class Error {
   public int error;
   public String reason;
   public String filename;
   public String text;
   public int line;
   public int column;
   public static final int SCAN_ERROR = 0;
   public static final int PARSE_ERROR = 1;
   public static final int CONTEXT_ERROR = 2;
   public static final int PREPROCESSOR_ERROR = 3;
   private static final String newline = System.getProperty("line.separator", "\n");
   final String[] description = new String[]{"scan error", "parse error", "context error", "preprocessor error"};

   public Error(int error, String reason, ChineseSource source, int line, int column) {
      this.error = error;
      this.reason = reason;
      if(line > 0 && line - 1 < source.lines.size()) {
         this.filename = ((ChineseSource.Line)source.lines.get(line - 1)).filename;
         this.text = ((ChineseSource.Line)source.lines.get(line - 1)).text;
      } else {
         this.filename = source.file.getPath();
         this.text = "";
      }

      this.line = line;
      this.column = column;
   }

   public String toMessage() {
      StringBuffer message = new StringBuffer();
      message.append(this.filename + ":" + this.line + " - " + this.description[this.error] + " : " + this.reason + newline);
      if(this.line > 0) {
         message.append(this.text + newline);
         String marker = this.text.replaceAll("\\p{Print}", " ");
         //System.out.println(marker); -> ÖĞÎÄ×Ö·û²âÊÔ
         message.append(marker.substring(0, this.column - 1) + "^");
      }

      return message.toString();
   }

   public String toString() {
      return this.description[this.error] + " : " + this.reason + "|" + this.filename + "|" + this.line + "|" + this.column;
   }
}
