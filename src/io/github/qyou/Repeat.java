package io.github.qyou;

public class Repeat extends AST {
   public int atLeast;
   public int atMost;

   public Repeat(int atLeast, int atMost) {
      this.atLeast = atLeast;
      this.atMost = atMost;
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }
}
