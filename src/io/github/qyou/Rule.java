package io.github.qyou;

public class Rule extends AST {
   public Rulename rulename;
   public Alternation alternation;

   public Rule(Rulename rulename, Alternation alternation) {
      this.rulename = rulename;
      this.alternation = alternation;
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }

   public void append(Rule rule) {
      this.alternation.append(rule.alternation);
   }
}
