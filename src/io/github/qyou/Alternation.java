package io.github.qyou;


import java.util.ArrayList;

public class Alternation extends AST {
   public ArrayList<Concatenation> concatenations;

   public Alternation(ArrayList<Concatenation> concatenations) {
      this.concatenations = concatenations;
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }

   public void append(Alternation alternation) {
      this.concatenations.addAll(alternation.concatenations);
   }
}
