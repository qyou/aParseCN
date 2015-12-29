package io.github.qyou;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Grammar extends AST {
   public final Rule primaryRule;
   public final ArrayList<Rule> rules;
   public final ArrayList<ExternalRule> externalRules;
   private final HashMap<String, Rule> ruleMap = new HashMap();

   public Grammar(ArrayList<Rule> rules) {
      this.rules = rules;
      this.externalRules = (ArrayList)this.accept(new Grammar.ExternalRuleVisitor(), (Object)null);
      Iterator i$ = rules.iterator();

      while(i$.hasNext()) {
         Rule rule = (Rule)i$.next();
         this.ruleMap.put(rule.rulename.spelling.toLowerCase(), rule);
      }

      this.primaryRule = (Rule)rules.get(0);
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }

   public Rule getRule(String rulename) {
      Rule rule = (Rule)this.ruleMap.get(rulename.toLowerCase());
      if(rule == null) {
         rule = new Rule(new Rulename("UNKNOWN", new ChineseSource(), 0, 0), new Alternation(new ArrayList()));
      }

      return rule;
   }

   // $FF: synthetic class
   static class SyntheticClass_1 {
   }

   private class ExternalRuleVisitor implements Visitor {
      HashMap<String, ExternalRule> externalRules;

      private ExternalRuleVisitor() {
         this.externalRules = new HashMap();
      }

      public Object visit(Grammar grammar, Object argument) {
         Iterator i$ = grammar.rules.iterator();

         while(i$.hasNext()) {
            Rule rule = (Rule)i$.next();
            rule.accept(this, argument);
         }

         return new ArrayList(this.externalRules.values());
      }

      public Object visit(Rule rule, Object argument) {
         rule.alternation.accept(this, argument);
         return null;
      }

      public Object visit(Alternation alternation, Object argument) {
         Iterator i$ = alternation.concatenations.iterator();

         while(i$.hasNext()) {
            Concatenation concatenation = (Concatenation)i$.next();
            concatenation.accept(this, argument);
         }

         return null;
      }

      public Object visit(Concatenation concatenation, Object argument) {
         Iterator i$ = concatenation.repetitions.iterator();

         while(i$.hasNext()) {
            Repetition repetition = (Repetition)i$.next();
            repetition.accept(this, argument);
         }

         return null;
      }

      public Object visit(Repetition repetition, Object argument) {
         repetition.element.accept(this, argument);
         return null;
      }

      public Object visit(Group group, Object argument) {
         group.alternation.accept(this, argument);
         return null;
      }

      public Object visit(ExternalRule rule, Object argument) {
         this.externalRules.put(rule.spelling, rule);
         return null;
      }

      public Object visit(Repeat repeat, Object argument) {
         return null;
      }

      public Object visit(Rulename rulename, Object argument) {
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

      // $FF: synthetic method
      ExternalRuleVisitor(Grammar.SyntheticClass_1 x1) {
         this();
      }
   }
}
