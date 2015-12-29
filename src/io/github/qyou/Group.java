package io.github.qyou;


public class Group extends Element {
   public Alternation alternation;

   public Group(Alternation alternation) {
      this.alternation = alternation;
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }
}
