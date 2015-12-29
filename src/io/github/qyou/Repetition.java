package io.github.qyou;


public class Repetition extends AST {
   public Repeat repeat;
   public Element element;

   public Repetition(Repeat repeat, Element element) {
      this.repeat = repeat;
      this.element = element;
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }
}
