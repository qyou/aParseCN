package io.github.qyou;


public class ExternalRule extends Element {
   public String spelling;

   public ExternalRule(String spelling) {
      this.spelling = spelling;
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }
}
