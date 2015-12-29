package io.github.qyou;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

final class Checker {
   private Grammar grammar;
   private ArrayList<Error> errors;
   static final String newline = System.getProperty("line.separator", "\n");
   private Checker.DeclarationChecker dChecker = new Checker.DeclarationChecker();
   private Checker.RecursionChecker rChecker = new Checker.RecursionChecker();
   private boolean checkDeclarations = true;
   private boolean checkRecursion = false;

   public Checker(ArrayList<Error> errors) {
      this.errors = errors;
   }

   public void check(Grammar grammar) {
      this.grammar = grammar;
      if(this.checkDeclarations) {
         this.dChecker.check();
      }

      if(this.checkRecursion) {
         this.rChecker.check();
      }

   }

   class RecursionChecker implements Visitor {
      private TreeSet<String> set;

      public void check() {
         Checker.this.grammar.accept(this, (Object)null);
      }

      public Object visit(Grammar grammar, Object argument) {
         Iterator i$ = grammar.rules.iterator();

         while(i$.hasNext()) {
            Rule i = (Rule)i$.next();
            this.set = new TreeSet();
            i.accept(this, i);
         }

         return null;
      }

      public Object visit(Rule rule, Object argument) {
         if(!this.set.contains(rule.rulename.spelling)) {
            this.set.add(rule.rulename.spelling);
            rule.alternation.accept(this, argument);
         }

         return null;
      }

      public Object visit(Rulename rulename, Object argument) {
         Rule parentRule = (Rule)argument;
         if(rulename.spelling.equalsIgnoreCase(parentRule.rulename.spelling)) {
            Checker.this.errors.add(new Error(2, "recursive rule", rulename.source, rulename.line, rulename.column));
         } else {
            Iterator i$ = Checker.this.grammar.rules.iterator();

            while(i$.hasNext()) {
               Rule i = (Rule)i$.next();
               if(rulename.spelling.equalsIgnoreCase(i.rulename.spelling)) {
                  i.accept(this, parentRule);
               }
            }
         }

         return null;
      }

      public Object visit(Alternation alternation, Object argument) {
         Iterator i$ = alternation.concatenations.iterator();

         while(i$.hasNext()) {
            Concatenation i = (Concatenation)i$.next();
            i.accept(this, argument);
         }

         return null;
      }

      public Object visit(Concatenation concatenation, Object argument) {
         Iterator i$ = concatenation.repetitions.iterator();

         while(i$.hasNext()) {
            Repetition i = (Repetition)i$.next();
            i.accept(this, argument);
         }

         return null;
      }

      public Object visit(Repetition repetition, Object argument) {
         repetition.element.accept(this, argument);
         return null;
      }

      public Object visit(Repeat repeat, Object argument) {
         return null;
      }

      public Object visit(Group group, Object argument) {
         group.alternation.accept(this, argument);
         return null;
      }

      public Object visit(StringValue stringValue, Object argument) {
         return null;
      }

      public Object visit(NumericValue numericValue, Object argument) {
         return null;
      }

      public Object visit(Terminal terminal, Object argument) {
         return null;
      }

      public Object visit(ExternalRule rule, Object argument) {
         return null;
      }
   }

   class DeclarationChecker implements Visitor {
      public void check() {
         Checker.this.grammar.accept(this, (Object)null);
      }

      public Object visit(Grammar grammar, Object argument) {
         Iterator i$ = grammar.rules.iterator();

         while(i$.hasNext()) {
            Rule i = (Rule)i$.next();
            i.accept(this, (Object)null);
         }

         return null;
      }

      public Object visit(Rule rule, Object argument) {
         int count = 0;
         Iterator i$ = Checker.this.grammar.rules.iterator();

         while(i$.hasNext()) {
            Rule i = (Rule)i$.next();
            if(i.rulename.spelling.equals(rule.rulename.spelling)) {
               ++count;
            }
         }

         if(count > 1) {
            Checker.this.errors.add(new Error(2, "multiply declared rule", rule.rulename.source, rule.rulename.line, rule.rulename.column));
         }

         rule.alternation.accept(this, rule);
         return null;
      }

      public Object visit(Rulename rulename, Object argument) {
         boolean found = false;

         for(Iterator i = Checker.this.grammar.rules.iterator(); i.hasNext() && !found; found = ((Rule)i.next()).rulename.spelling.equalsIgnoreCase(rulename.spelling)) {
            ;
         }

         if(!found) {
            Checker.this.errors.add(new Error(2, "undeclared rule", rulename.source, rulename.line, rulename.column));
         }

         return null;
      }

      public Object visit(Alternation alternation, Object argument) {
         Iterator i$ = alternation.concatenations.iterator();

         while(i$.hasNext()) {
            Concatenation i = (Concatenation)i$.next();
            i.accept(this, argument);
         }

         return null;
      }

      public Object visit(Concatenation concatenation, Object argument) {
         Iterator i$ = concatenation.repetitions.iterator();

         while(i$.hasNext()) {
            Repetition i = (Repetition)i$.next();
            i.accept(this, argument);
         }

         return null;
      }

      public Object visit(Repetition repetition, Object argument) {
         repetition.element.accept(this, argument);
         return null;
      }

      public Object visit(Repeat repeat, Object argument) {
         return null;
      }

      public Object visit(Group group, Object argument) {
         group.alternation.accept(this, argument);
         return null;
      }

      public Object visit(StringValue stringValue, Object argument) {
         return null;
      }

      public Object visit(NumericValue numericValue, Object argument) {
         return null;
      }

      public Object visit(Terminal terminal, Object argument) {
         return null;
      }

      public Object visit(ExternalRule rule, Object argument) {
         return null;
      }
   }
}
