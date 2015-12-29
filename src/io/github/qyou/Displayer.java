package io.github.qyou;

import java.util.Iterator;

final class Displayer implements Visitor {
   private int level = 0;

   public void display(Grammar grammar) {
      grammar.accept(this, (Object)null);
   }

   public Object visit(Grammar grammar, Object argument) {
      Iterator i$ = grammar.rules.iterator();

      while(i$.hasNext()) {
         Rule rule = (Rule)i$.next();
         rule.accept(this, (Object)null);
      }

      return null;
   }

   public Object visit(Rule rule, Object argument) {
      System.out.println(this.indent() + "rule:");
      ++this.level;
      rule.rulename.accept(this, (Object)null);
      rule.alternation.accept(this, (Object)null);
      --this.level;
      return null;
   }

   public Object visit(Rulename rulename, Object argument) {
      System.out.println(this.indent() + "rulename: " + rulename.spelling);
      return null;
   }

   public Object visit(Alternation alternation, Object argument) {
      System.out.println(this.indent() + "alternation:");
      ++this.level;
      Iterator i$ = alternation.concatenations.iterator();

      while(i$.hasNext()) {
         Concatenation i = (Concatenation)i$.next();
         i.accept(this, (Object)null);
      }

      --this.level;
      return null;
   }

   public Object visit(Concatenation concatenation, Object argument) {
      System.out.println(this.indent() + "concatenation:");
      ++this.level;
      Iterator i$ = concatenation.repetitions.iterator();

      while(i$.hasNext()) {
         Repetition i = (Repetition)i$.next();
         i.accept(this, (Object)null);
      }

      --this.level;
      return null;
   }

   public Object visit(Repetition repetition, Object argument) {
      System.out.println(this.indent() + "repetition:");
      ++this.level;
      repetition.repeat.accept(this, (Object)null);
      repetition.element.accept(this, (Object)null);
      --this.level;
      return null;
   }

   public Object visit(Repeat repeat, Object argument) {
      System.out.println(this.indent() + "repeat: " + repeat.atLeast + "*" + repeat.atMost);
      return null;
   }

   public Object visit(Group group, Object argument) {
      System.out.println(this.indent() + "group:");
      ++this.level;
      group.alternation.accept(this, (Object)null);
      --this.level;
      return null;
   }

   public Object visit(StringValue stringValue, Object argument) {
      System.out.println(this.indent() + "string value: " + stringValue.spelling);
      return null;
   }

   public Object visit(NumericValue numericValue, Object argument) {
      System.out.println(this.indent() + "numeric value: " + numericValue.spelling);
      return null;
   }

   public Object visit(Terminal terminal, Object argument) {
      System.out.println(this.indent() + "terminal: " + terminal.spelling);
      return null;
   }

   public Object visit(ExternalRule rule, Object argument) {
      System.out.println(this.indent() + "external rule: " + rule.spelling);
      return null;
   }

   protected String indent() {
      StringBuffer buffer = new StringBuffer();

      for(int i = 0; i < this.level; ++i) {
         buffer.append("  ");
      }

      return buffer.toString();
   }
}
