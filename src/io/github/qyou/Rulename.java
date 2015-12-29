package io.github.qyou;

public class Rulename extends Element {
   public String spelling;
   public ChineseSource source;
   public int line;
   public int column;

   public Rulename(String spelling, ChineseSource source, int line, int column) {
      this.spelling = spelling;
      this.source = source;
      this.line = line;
      this.column = column;
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }
}
