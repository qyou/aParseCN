package io.github.qyou;

public class Terminal extends AST {
   public String spelling;

   public String toString() {
      return this.spelling;
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }
}
