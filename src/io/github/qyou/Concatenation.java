package io.github.qyou;


import java.util.ArrayList;

public class Concatenation extends AST {
   public ArrayList<Repetition> repetitions;

   public Concatenation(ArrayList<Repetition> repetitions) {
      this.repetitions = repetitions;
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }
}
