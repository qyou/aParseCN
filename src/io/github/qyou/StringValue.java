package io.github.qyou;


public class StringValue extends Element {
   public String spelling;
   public String regex;

   public StringValue(String spelling) {
      this.spelling = spelling;
      this.regex = spelling.substring(1, spelling.length() - 1);
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }
}
