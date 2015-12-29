package io.github.qyou;

public interface Visitor {
   Object visit(Grammar var1, Object var2);

   Object visit(Rule var1, Object var2);

   Object visit(Rulename var1, Object var2);

   Object visit(Alternation var1, Object var2);

   Object visit(Concatenation var1, Object var2);

   Object visit(Repetition var1, Object var2);

   Object visit(Repeat var1, Object var2);

   Object visit(Group var1, Object var2);

   Object visit(ExternalRule var1, Object var2);

   Object visit(StringValue var1, Object var2);

   Object visit(NumericValue var1, Object var2);

   Object visit(Terminal var1, Object var2);
}
