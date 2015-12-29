package io.github.qyou;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

final class CsEncoder implements Encoder {
   private Grammar grammar;
   private String producedBy;
   private Date producedAt;
   private String namespace;
   private boolean annotate;
   private int alternationLevel = 0;
   private static final String newline = System.getProperty("line.separator", "\n");
   private static final char hyphenSubstitute = '_';
   private static final String rulePrefix = "Rule_";
   private static final String terminalPrefix = "Terminal_";
   private int tabLevel = 0;

   public void encode(Grammar grammar, String producedBy, Properties arguments) throws IOException {
      this.grammar = grammar;
      this.producedBy = producedBy;
      this.producedAt = new Date();
      this.namespace = arguments.getProperty("Namespace");
      this.annotate = arguments.getProperty("Annotate").equalsIgnoreCase("On");
      String destDir = arguments.getProperty("DestDir");
      if(!destDir.endsWith(System.getProperty("file.separator"))) {
         destDir = destDir.concat(System.getProperty("file.separator"));
      }

      this.createParserClass(destDir + "Parser.cs");
      this.createParserContextClass(destDir + "ParserContext.cs");
      this.createParserAlternativeClass(destDir + "ParserAlternative.cs");
      this.createVisitorClass(destDir + "Visitor.cs");
      this.createRuleClass(destDir + "Rule.cs");
      this.createRuleClasses(destDir);
      this.createParserExceptionClass(destDir + "ParserException.cs");
      this.createDisplayerClass(destDir + "Displayer.cs");
      this.createXmlDisplayerClass(destDir + "XmlDisplayer.cs");
   }

   private void createParserClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      String primaryRulename = this.grammar.primaryRule.rulename.spelling;
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Parser.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(newline);
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline + this.tab() + "using System.Text;" + newline + this.tab() + "using System.IO;" + newline + this.tab() + "using System.Collections.Generic;" + newline);
      text.append(newline + this.tab() + "public class Parser" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "private Parser() {}" + newline);
      text.append(newline + this.tab() + "static public void Main(String[] args)" + newline + this.inc() + "{" + newline + this.tab() + "Dictionary<String, String> arguments = new Dictionary<String, String>();" + newline + this.tab() + "String error = \"\";" + newline + this.tab() + "bool ok = args.Length > 0;" + newline + newline + this.tab() + "if (ok)" + newline + this.inc() + "{" + newline + this.tab() + "arguments[\"Trace\"] = \"Off\";" + newline + this.tab() + "arguments[\"Rule\"] = \"" + primaryRulename + "\";" + newline + newline + this.tab() + "for (int i = 0; i < args.Length; i++)" + newline + this.inc() + "{" + newline + this.tab() + "if (args[i].Equals(\"-trace\"))" + newline + this.add() + "arguments[\"Trace\"] = \"On\";" + newline + this.tab() + "else if (args[i].Equals(\"-visitor\"))" + newline + this.add() + "arguments[\"Visitor\"] = args[++i];" + newline + this.tab() + "else if (args[i].Equals(\"-file\"))" + newline + this.add() + "arguments[\"File\"] = args[++i];" + newline + this.tab() + "else if (args[i].Equals(\"-string\"))" + newline + this.add() + "arguments[\"String\"] = args[++i];" + newline + this.tab() + "else if (args[i].Equals(\"-rule\"))" + newline + this.add() + "arguments[\"Rule\"] = args[++i];" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "error = \"unknown argument: \" + args[i];" + newline + this.tab() + "ok = false;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (ok)" + newline + this.inc() + "{" + newline + this.tab() + "if (!arguments.ContainsKey(\"File\") &&" + newline + this.tab() + "    !arguments.ContainsKey(\"String\"))" + newline + this.inc() + "{" + newline + this.tab() + "error = \"insufficient arguments: -file or -string required\";" + newline + this.tab() + "ok = false;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (!ok)" + newline + this.inc() + "{" + newline + this.tab() + "System.Console.WriteLine(\"error: \" + error);" + newline + this.tab() + "System.Console.WriteLine(\"usage: Parser [-rule rulename] [-trace] <-file file | -string string> [-visitor visitor]\");" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "try" + newline + this.inc() + "{" + newline + this.tab() + "Rule rule = null;" + newline + newline + this.tab() + "if (arguments.ContainsKey(\"File\"))" + newline + this.inc() + "{" + newline + this.tab() + "rule = " + newline + this.add() + "Parse(" + newline + this.add() + "  arguments[\"Rule\"], " + newline + this.add() + "  new FileStream(arguments[\"File\"], FileMode.Open), " + newline + this.add() + "  arguments[\"Trace\"].Equals(\"On\"));" + newline + this.dec() + "}" + newline + this.tab() + "else if (arguments.ContainsKey(\"String\"))" + newline + this.inc() + "{" + newline + this.tab() + "rule = " + newline + this.add() + "Parse(" + newline + this.add() + "  arguments[\"Rule\"], " + newline + this.add() + "  arguments[\"String\"], " + newline + this.add() + "  arguments[\"Trace\"].Equals(\"On\"));" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (arguments.ContainsKey(\"Visitor\"))" + newline + this.inc() + "{" + newline + this.tab() + "Type type = Type.GetType(arguments[\"Visitor\"]);" + newline + newline + this.tab() + "if (type == null)" + newline + this.inc() + "{" + newline + this.tab() + "System.Console.WriteLine(" + newline + this.add() + "\"visitor error: class not found - \" + " + newline + this.add() + "arguments[\"Visitor\"]);" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.inc() + "Visitor visitor = (Visitor)System.Activator.CreateInstance(type);" + newline + newline + this.dec() + "rule.Accept(visitor);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "catch (ArgumentException e)" + newline + this.inc() + "{" + newline + this.tab() + "System.Console.WriteLine(\"argument error: \" + e.Message);" + newline + this.dec() + "}" + newline + this.tab() + "catch (IOException e)" + newline + this.inc() + "{" + newline + this.tab() + "System.Console.WriteLine(\"io error: \" + e.Message);" + newline + this.dec() + "}" + newline + this.tab() + "catch (ParserException e)" + newline + this.inc() + "{" + newline + this.tab() + "System.Console.WriteLine(\"parser error: \" + e.Message);" + newline + this.dec() + "}" + newline + this.tab() + "catch (Exception e)" + newline + this.inc() + "{" + newline + this.tab() + "System.Console.WriteLine(\"error: \" + e.Message);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static public Rule Parse(String rulename, String text)" + newline + this.inc() + "{" + newline + this.tab() + "return Parse(rulename, text, false);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static public Rule Parse(String rulename, StreamReader input)" + newline + this.inc() + "{" + newline + this.tab() + "return Parse(rulename, input, false);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static public Rule Parse(String rulename, FileStream file)" + newline + this.inc() + "{" + newline + this.tab() + "return Parse(rulename, file, false);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static private Rule Parse(String rulename, String text, bool trace)" + newline + this.inc() + "{" + newline + this.tab() + "if (rulename == null)" + newline + this.add() + "throw new ArgumentNullException(\"null rulename\");" + newline + this.tab() + "if (text == null)" + newline + this.add() + "throw new ArgumentException(\"null string\");" + newline);
      text.append(newline + this.tab() + "ParserContext context = new ParserContext(text, trace);" + newline);
      text.append(newline + this.tab() + "Rule rule = null;" + newline);
      boolean first = true;

      for(Iterator out = this.grammar.rules.iterator(); out.hasNext(); first = false) {
         Rule rule = (Rule)out.next();
         text.append(this.tab());
         if(!first) {
            text.append("else ");
         }

         text.append("if (rulename.ToLower().Equals(\"" + rule.rulename.spelling + "\".ToLower())) rule = " + "Rule_" + rule.rulename.spelling.replace('-', '_') + ".Parse(context);" + newline);
      }

      text.append(this.tab() + "else throw new ArgumentException(\"unknown rule\");" + newline);
      text.append(newline + this.tab() + "if (rule == null)" + newline + this.inc() + "{" + newline + this.tab() + "throw new ParserException(" + newline + this.add() + "\"rule \\\"\" + (String)context.GetErrorStack().Peek() + \"\\\" failed\"," + newline + this.add() + "context.text," + newline + this.add() + "context.GetErrorIndex()," + newline + this.add() + "context.GetErrorStack());" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (context.text.Length > context.index)" + newline + this.inc() + "{" + newline + this.tab() + "ParserException primaryError = " + newline + this.add() + "new ParserException(" + newline + this.add() + "  \"extra data found\"," + newline + this.add() + "  context.text," + newline + this.add() + "  context.index," + newline + this.add() + "  new Stack<String>());" + newline + newline + this.tab() + "if (context.GetErrorIndex() > context.index)" + newline + this.inc() + "{" + newline + this.tab() + "ParserException secondaryError = " + newline + this.add() + "new ParserException(" + newline + this.add() + "  \"rule \\\"\" + (String)context.GetErrorStack().Peek() + \"\\\" failed\"," + newline + this.add() + "  context.text," + newline + this.add() + "  context.GetErrorIndex()," + newline + this.add() + "  context.GetErrorStack());" + newline + newline + this.tab() + "primaryError.SetCause(secondaryError);" + newline + this.dec() + "}" + newline + newline + this.tab() + "throw primaryError;" + newline + this.dec() + "}" + newline + newline + this.tab() + "return rule;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static private Rule Parse(String rulename, StreamReader input, bool trace)" + newline + this.inc() + "{" + newline + this.tab() + "if (rulename == null)" + newline + this.add() + "throw new ArgumentNullException(\"null rulename\");" + newline + this.tab() + "if (input == null)" + newline + this.add() + "throw new ArgumentNullException(\"null input stream\");" + newline + newline + this.tab() + "int ch = 0;" + newline + this.tab() + "StringBuilder output = new StringBuilder();" + newline + this.tab() + "while ((ch = input.Read()) != -1)" + newline + this.add() + "output.Append((char)ch);" + newline + newline + this.tab() + "return Parse(rulename, output.ToString(), trace);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static private Rule Parse(String rulename, FileStream file, bool trace)" + newline + this.inc() + "{" + newline + this.tab() + "if (rulename == null)" + newline + this.add() + "throw new ArgumentNullException(\"null rulename\");" + newline + this.tab() + "if (file == null)" + newline + this.add() + "throw new ArgumentNullException(\"null file\");" + newline + newline + this.tab() + "StreamReader input = new StreamReader(file);" + newline + this.tab() + "int ch = 0;" + newline + this.tab() + "StringBuilder output = new StringBuilder();" + newline + this.tab() + "while ((ch = input.Read()) != -1)" + newline + this.add() + "output.Append((char)ch);" + newline + newline + this.tab() + "input.Close();" + newline + newline + this.tab() + "return Parse(rulename, output.ToString(), trace);" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out1 = new FileOutputStream(filename);
      out1.write(text.toString().getBytes());
      out1.close();
   }

   private void createParserContextClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserContext.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(newline);
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline + this.tab() + "using System.Collections.Generic;" + newline + this.tab() + "using System.Text.RegularExpressions;" + newline);
      text.append(newline + this.tab() + "public class ParserContext" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "public readonly String text;" + newline);
      text.append(this.tab() + "public int index;" + newline);
      text.append(newline + this.tab() + "private Stack<int> startStack = new Stack<int>();" + newline + this.tab() + "private Stack<String> callStack = new Stack<String>();" + newline + this.tab() + "private Stack<String> errorStack = new Stack<String>();" + newline + this.tab() + "private int level = 0;" + newline + this.tab() + "private int errorIndex = 0;" + newline + newline + this.tab() + "private readonly bool traceOn;" + newline);
      text.append(newline + this.tab() + "public ParserContext(String text, bool traceOn)" + newline + this.inc() + "{" + newline + this.tab() + "this.text = text;" + newline + this.tab() + "this.traceOn = traceOn;" + newline + this.tab() + "index = 0;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void Push(String rulename)" + newline + this.inc() + "{" + newline + this.tab() + "Push(rulename, \"\");" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void Push(String rulename, String trace)" + newline + this.inc() + "{" + newline + this.tab() + "callStack.Push(rulename);" + newline + this.tab() + "startStack.Push(index);" + newline + newline + this.tab() + "if (traceOn)" + newline + this.inc() + "{" + newline + this.tab() + "String sample = text.Substring(index, index + 10 > text.Length ? text.Length - index : 10);" + newline + newline + this.tab() + "Regex regex = new Regex(\"[\\\\x00-\\\\x1F]\");" + newline + this.tab() + "sample = regex.Replace(sample, \" \");" + newline + newline + this.tab() + "System.Console.WriteLine(\"-> \" + ++level + \": \" + rulename + \"(\" + (trace != null ? trace : \"\") + \")\");" + newline + this.tab() + "System.Console.WriteLine(index + \": \" + sample);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void Pop(String function, bool result)" + newline + this.inc() + "{" + newline + this.tab() + "int start = startStack.Pop();" + newline + this.tab() + "callStack.Pop();" + newline + newline + this.tab() + "if (traceOn)" + newline + this.inc() + "{" + newline + this.tab() + "System.Console.WriteLine(" + newline + this.add() + "\"<- \" + level-- + " + newline + this.add() + "\": \" + function + " + newline + this.add() + "\"(\" + (result ? \"true\" : \"false\") + " + newline + this.add() + "\",s=\" + start + " + newline + this.add() + "\",l=\" + (index - start) + " + newline + this.add() + "\",e=\" + errorIndex + \")\");" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (!result)" + newline + this.inc() + "{" + newline + this.tab() + "if (index > errorIndex)" + newline + this.inc() + "{" + newline + this.tab() + "errorIndex = index;" + newline + this.tab() + "errorStack = new Stack<String>(callStack);" + newline + this.dec() + "}" + newline + this.tab() + "else if (index == errorIndex && errorStack.Count == 0)" + newline + this.inc() + "{" + newline + this.tab() + "errorStack = new Stack<String>(callStack);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "if (index > errorIndex) errorIndex = 0;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public Stack<String> GetErrorStack()" + newline + this.inc() + "{" + newline + this.tab() + "return errorStack;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public int GetErrorIndex()" + newline + this.inc() + "{" + newline + this.tab() + "return errorIndex;" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createParserAlternativeClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserAlternative.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(newline);
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline + this.tab() + "using System.Collections.Generic;" + newline);
      text.append(newline + this.tab() + "public class ParserAlternative" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "public List<Rule> rules;" + newline + this.tab() + "public int start;" + newline + this.tab() + "public int end;" + newline);
      text.append(newline + this.tab() + "public ParserAlternative(int start)" + newline + this.inc() + "{" + newline + this.tab() + "this.rules = new List<Rule>();" + newline + this.tab() + "this.start = start;" + newline + this.tab() + "this.end = start;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void Add(Rule rule, int end)" + newline + this.inc() + "{" + newline + this.tab() + "this.rules.Add(rule);" + newline + this.tab() + "this.end = end;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void Add(List<Rule> rules, int end)" + newline + this.inc() + "{" + newline + this.tab() + "this.rules.AddRange(rules);" + newline + this.tab() + "this.end = end;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static public ParserAlternative GetBest(List<ParserAlternative> alternatives)" + newline + this.inc() + "{" + newline + this.tab() + "ParserAlternative best = null;" + newline + newline + this.tab() + "foreach (ParserAlternative alternative in alternatives)" + newline + this.inc() + "{" + newline + this.tab() + "if (best == null || alternative.end > best.end)" + newline + this.add() + "best = alternative;" + newline + this.dec() + "}" + newline + newline + this.tab() + "return best;" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createVisitorClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Visitor.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline);
      text.append(newline + this.tab() + "public interface Visitor" + newline + this.inc() + "{" + newline);
      Iterator out = this.grammar.rules.iterator();

      String rulename;
      while(out.hasNext()) {
         Rule rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(this.tab() + "Object Visit(" + "Rule_" + rulename + " rule);" + newline);
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         ExternalRule rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(this.tab() + "Object Visit(" + rulename + " rule);" + newline);
      }

      text.append(newline);
      text.append(this.tab() + "Object Visit(" + "Terminal_" + "StringValue value);" + newline);
      text.append(this.tab() + "Object Visit(" + "Terminal_" + "NumericValue value);" + newline);
      text.append(this.dec() + "}" + newline);
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out1 = new FileOutputStream(filename);
      out1.write(text.toString().getBytes());
      out1.close();
   }

   private void createRuleClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Rule.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline + this.tab() + "using System.Collections.Generic;" + newline);
      text.append(newline + this.tab() + "public abstract class Rule" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "public readonly String spelling;" + newline);
      text.append(this.tab() + "public readonly List<Rule> rules;" + newline + newline);
      text.append(this.tab() + "protected Rule(String spelling, List<Rule> rules)" + newline + this.inc() + "{" + newline + this.tab() + "this.spelling = spelling;" + newline + this.tab() + "this.rules = rules;" + newline + this.dec() + "}" + newline + newline);
      text.append(this.tab() + "public override String ToString()" + newline + this.inc() + "{" + newline + this.tab() + "return spelling;" + newline + this.dec() + "}" + newline + newline);
      text.append(this.tab() + "public override Boolean Equals(Object rule)" + newline + this.inc() + "{" + newline + this.tab() + "return rule is Rule && spelling.Equals(((Rule)rule).spelling);" + newline + this.dec() + "}" + newline + newline);
      text.append(this.tab() + "public override int GetHashCode()" + newline + this.inc() + "{" + newline + this.tab() + "return spelling.GetHashCode();" + newline + this.dec() + "}" + newline + newline);
      text.append(this.tab() + "public int CompareTo(Rule rule)" + newline + this.inc() + "{" + newline + this.tab() + "return spelling.CompareTo(rule.spelling);" + newline + this.dec() + "}" + newline + newline);
      text.append(this.tab() + "public abstract Object Accept(Visitor visitor);" + newline + this.dec() + "}" + newline);
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createRuleClasses(String destDir) throws IOException {
      String rulename = null;
      FileOutputStream out = null;
      Iterator i$ = this.grammar.rules.iterator();

      while(i$.hasNext()) {
         Rule rule = (Rule)i$.next();
         StringBuffer text = new StringBuffer();
         class RuleVisitor implements Visitor {
            public Object visit(Grammar grammar, Object argument) {
               return null;
            }

            public Object visit(Rule rule, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               String rulename = rule.rulename.spelling.replace('-', '_');
               text.append("/* -----------------------------------------------------------------------------" + CsEncoder.newline);
               text.append(" * Rule_" + rulename + ".cs" + CsEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + CsEncoder.newline);
               text.append(" *" + CsEncoder.newline);
               text.append(" * Producer : " + CsEncoder.this.producedBy + CsEncoder.newline);
               text.append(" * Produced : " + CsEncoder.this.producedAt.toString() + CsEncoder.newline);
               text.append(" *" + CsEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + CsEncoder.newline);
               text.append(" */" + CsEncoder.newline);
               if(CsEncoder.this.namespace != null) {
                  text.append(CsEncoder.this.inc() + "namespace " + CsEncoder.this.namespace + " {" + CsEncoder.newline);
               }

               text.append(CsEncoder.newline + CsEncoder.this.tab() + "using System;" + CsEncoder.newline + CsEncoder.this.tab() + "using System.Collections.Generic;" + CsEncoder.newline);
               text.append(CsEncoder.newline + CsEncoder.this.tab() + "sealed public class " + "Rule_" + rulename + ":Rule" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.tab() + "private " + "Rule_" + rulename + "(String spelling, List<Rule> rules) :" + CsEncoder.newline + CsEncoder.this.tab() + "base(spelling, rules)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline);
               text.append(CsEncoder.newline + CsEncoder.this.tab() + "public override Object Accept(Visitor visitor)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.tab() + "return visitor.Visit(this);" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline);
               text.append(CsEncoder.newline + CsEncoder.this.tab() + "public static " + "Rule_" + rulename + " Parse(ParserContext context)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline);
               text.append(CsEncoder.this.tab() + "context.Push(\"" + rule.rulename.spelling + "\");" + CsEncoder.newline);
               text.append(CsEncoder.newline);
               text.append(CsEncoder.this.tab() + "Rule rule;" + CsEncoder.newline);
               text.append(CsEncoder.this.tab() + "bool parsed = true;" + CsEncoder.newline);
               text.append(CsEncoder.this.tab() + "ParserAlternative b;" + CsEncoder.newline);
               text.append(CsEncoder.this.tab() + "int s0 = context.index;" + CsEncoder.newline);
               text.append(CsEncoder.this.tab() + "ParserAlternative a0 = new ParserAlternative(s0);" + CsEncoder.newline);
               text.append(CsEncoder.newline);
               rule.alternation.accept(this, argument);
               text.append(CsEncoder.newline + CsEncoder.this.tab() + "rule = null;" + CsEncoder.newline + CsEncoder.this.tab() + "if (parsed)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.add() + "rule = new " + "Rule_" + rulename + "(context.text.Substring(a0.start, a0.end - a0.start), a0.rules);" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline + CsEncoder.this.tab() + "else" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.add() + "context.index = s0;" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline);
               text.append(CsEncoder.newline + CsEncoder.this.tab() + "context.Pop(\"" + rule.rulename.spelling + "\", parsed);" + CsEncoder.newline);
               text.append(CsEncoder.newline + CsEncoder.this.tab() + "return (" + "Rule_" + rulename + ")rule;" + CsEncoder.newline);
               text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
               text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
               if(CsEncoder.this.namespace != null) {
                  text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
               }

               text.append(CsEncoder.newline);
               text.append("/* -----------------------------------------------------------------------------" + CsEncoder.newline);
               text.append(" * eof" + CsEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + CsEncoder.newline);
               text.append(" */" + CsEncoder.newline);
               return null;
            }

            public Object visit(Alternation alternation, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               CsEncoder.access$808(CsEncoder.this);
               if(CsEncoder.this.annotate) {
                  text.append(CsEncoder.this.tab() + "/* alternation[" + CsEncoder.this.alternationLevel + "] - start */" + CsEncoder.newline);
               }

               text.append(CsEncoder.this.tab() + "List<ParserAlternative> as" + CsEncoder.this.alternationLevel + " = new List<ParserAlternative>();" + CsEncoder.newline + CsEncoder.this.tab() + "parsed = false;" + CsEncoder.newline);

               for(int i = 0; i < alternation.concatenations.size(); ++i) {
                  ((Concatenation)alternation.concatenations.get(i)).accept(this, argument);
               }

               text.append(CsEncoder.newline + CsEncoder.this.tab() + "b = ParserAlternative.GetBest(as" + CsEncoder.this.alternationLevel + ");" + CsEncoder.newline + CsEncoder.newline + CsEncoder.this.tab() + "parsed = b != null;" + CsEncoder.newline + CsEncoder.newline + CsEncoder.this.tab() + "if (parsed)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.tab() + "a" + (CsEncoder.this.alternationLevel - 1) + ".Add(b.rules, b.end);" + CsEncoder.newline + CsEncoder.this.tab() + "context.index = b.end;" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline);
               if(CsEncoder.this.annotate) {
                  text.append(CsEncoder.this.tab() + "/* alternation[" + CsEncoder.this.alternationLevel + "] - end */" + CsEncoder.newline);
               }

               CsEncoder.access$810(CsEncoder.this);
               return null;
            }

            public Object visit(Concatenation concatenation, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               if(CsEncoder.this.annotate) {
                  text.append(CsEncoder.this.tab() + "/* concatenation - start */" + CsEncoder.newline);
               }

               text.append(CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.tab() + "int s" + CsEncoder.this.alternationLevel + " = context.index;" + CsEncoder.newline + CsEncoder.this.tab() + "ParserAlternative a" + CsEncoder.this.alternationLevel + " = new ParserAlternative(s" + CsEncoder.this.alternationLevel + ");" + CsEncoder.newline + CsEncoder.this.tab() + "parsed = true;" + CsEncoder.newline);

               for(int i = 0; i < concatenation.repetitions.size(); ++i) {
                  text.append(CsEncoder.this.tab() + "if (parsed)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline);
                  ((Repetition)concatenation.repetitions.get(i)).accept(this, argument);
                  text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
               }

               text.append(CsEncoder.this.tab() + "if (parsed)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.tab() + "as" + CsEncoder.this.alternationLevel + ".Add(a" + CsEncoder.this.alternationLevel + ");" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline + CsEncoder.this.tab() + "context.index = s" + CsEncoder.this.alternationLevel + ";" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline);
               if(CsEncoder.this.annotate) {
                  text.append(CsEncoder.this.tab() + "/* concatenation - end */" + CsEncoder.newline);
               }

               return null;
            }

            public Object visit(Repetition repetition, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               String index = "i" + CsEncoder.this.alternationLevel;
               String found = "f" + CsEncoder.this.alternationLevel;
               String count = "c" + CsEncoder.this.alternationLevel;
               int atLeast = repetition.repeat.atLeast;
               int atMost = repetition.repeat.atMost;
               if(CsEncoder.this.annotate) {
                  text.append(CsEncoder.this.tab() + "/* repetition(" + atLeast + "*" + atMost + ") - start */" + CsEncoder.newline);
               }

               text.append(CsEncoder.this.tab() + "bool " + found + " = true;" + CsEncoder.newline);
               if(atLeast > 0 && atLeast == atMost) {
                  text.append(CsEncoder.this.tab() + "int " + count + " = 0;" + CsEncoder.newline);
                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* required */" + CsEncoder.newline);
                  }

                  text.append(CsEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atLeast + " && " + found + "; " + index + "++)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* optional - none */" + CsEncoder.newline);
                  }

                  text.append(CsEncoder.this.tab() + "parsed = " + count + " == " + atLeast + ";" + CsEncoder.newline);
               } else if(atLeast == 0 && atMost == 0) {
                  text.append(CsEncoder.this.tab() + "int " + count + " = 0;" + CsEncoder.newline);
                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* required - none */" + CsEncoder.newline);
                  }

                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* optional */" + CsEncoder.newline);
                  }

                  text.append(CsEncoder.this.tab() + "while (" + found + ")" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
                  text.append(CsEncoder.this.tab() + "parsed = true;" + CsEncoder.newline);
               } else if(atLeast == 0 && atMost > 0) {
                  text.append(CsEncoder.this.tab() + "int " + count + " = 0;" + CsEncoder.newline);
                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* required - none */" + CsEncoder.newline);
                  }

                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* optional */" + CsEncoder.newline);
                  }

                  text.append(CsEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atMost + " && " + found + "; " + index + "++)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
                  text.append(CsEncoder.this.tab() + "parsed = true;" + CsEncoder.newline);
               } else if(atLeast > 0 && atLeast < atMost) {
                  text.append(CsEncoder.this.tab() + "int " + count + " = 0;" + CsEncoder.newline);
                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* required */" + CsEncoder.newline);
                  }

                  text.append(CsEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atLeast + " && " + found + "; " + index + "++)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* optional */" + CsEncoder.newline);
                  }

                  text.append(CsEncoder.this.tab() + "for (int " + index + " = " + atLeast + "; " + index + " < " + atMost + " && " + found + "; " + index + "++)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
                  text.append(CsEncoder.this.tab() + "parsed = " + count + " >= " + atLeast + ";" + CsEncoder.newline);
               } else if(atLeast > 0 && atMost == 0) {
                  text.append(CsEncoder.this.tab() + "int " + count + " = 0;" + CsEncoder.newline);
                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* required */" + CsEncoder.newline);
                  }

                  text.append(CsEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atLeast + " && " + found + "; " + index + "++)" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
                  if(CsEncoder.this.annotate) {
                     text.append(CsEncoder.this.tab() + "/* optional */" + CsEncoder.newline);
                  }

                  text.append(CsEncoder.this.tab() + "while (" + found + ")" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CsEncoder.this.dec() + "}" + CsEncoder.newline);
                  text.append(CsEncoder.this.tab() + "parsed = " + count + " >= " + atLeast + ";" + CsEncoder.newline);
               }

               if(CsEncoder.this.annotate) {
                  text.append(CsEncoder.this.tab() + "/* repetition - end */" + CsEncoder.newline);
               }

               return null;
            }

            public Object visit(Repeat repeat, Object argument) {
               return null;
            }

            public Object visit(Rulename rulename, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(CsEncoder.this.tab() + "rule = " + "Rule_" + CsEncoder.this.grammar.getRule(rulename.spelling).rulename.spelling.replace('-', '_') + ".Parse(context);" + CsEncoder.newline + CsEncoder.this.tab() + "if ((f" + CsEncoder.this.alternationLevel + " = rule != null))" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.tab() + "a" + CsEncoder.this.alternationLevel + ".Add(rule, context.index);" + CsEncoder.newline + CsEncoder.this.tab() + "c" + CsEncoder.this.alternationLevel + "++;" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline);
               return null;
            }

            public Object visit(Group group, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               if(CsEncoder.this.annotate) {
                  text.append(CsEncoder.this.tab() + "/* group - start */" + CsEncoder.newline);
               }

               text.append(CsEncoder.this.tab() + "int g" + CsEncoder.this.alternationLevel + " = context.index;" + CsEncoder.newline);
               group.alternation.accept(this, argument);
               text.append(CsEncoder.this.tab() + "f" + CsEncoder.this.alternationLevel + " = context.index > g" + CsEncoder.this.alternationLevel + ";" + CsEncoder.newline + CsEncoder.this.tab() + "if (parsed) c" + CsEncoder.this.alternationLevel + "++;" + CsEncoder.newline);
               if(CsEncoder.this.annotate) {
                  text.append(CsEncoder.this.tab() + "/* group - end */" + CsEncoder.newline);
               }

               return null;
            }

            public Object visit(StringValue stringValue, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(CsEncoder.this.tab() + "rule = " + "Terminal_" + "StringValue.Parse(context, \"" + stringValue.regex.replace("\\", "\\\\") + "\");" + CsEncoder.newline + CsEncoder.this.tab() + "if ((f" + CsEncoder.this.alternationLevel + " = rule != null))" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.tab() + "a" + CsEncoder.this.alternationLevel + ".Add(rule, context.index);" + CsEncoder.newline + CsEncoder.this.tab() + "c" + CsEncoder.this.alternationLevel + "++;" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline);
               return null;
            }

            public Object visit(NumericValue numericValue, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(CsEncoder.this.tab() + "rule = " + "Terminal_" + "NumericValue.Parse(context, \"" + numericValue.spelling + "\", \"" + numericValue.regex + "\", " + numericValue.length + ");" + CsEncoder.newline + CsEncoder.this.tab() + "if ((f" + CsEncoder.this.alternationLevel + " = rule != null))" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.tab() + "a" + CsEncoder.this.alternationLevel + ".Add(rule, context.index);" + CsEncoder.newline + CsEncoder.this.tab() + "c" + CsEncoder.this.alternationLevel + "++;" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline);
               return null;
            }

            public Object visit(Terminal terminal, Object argument) {
               return null;
            }

            public Object visit(ExternalRule rule, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(CsEncoder.this.tab() + "rule = " + rule.spelling + ".Parse(context);" + CsEncoder.newline + CsEncoder.this.tab() + "if ((f" + CsEncoder.this.alternationLevel + " = rule != null))" + CsEncoder.newline + CsEncoder.this.inc() + "{" + CsEncoder.newline + CsEncoder.this.tab() + "a" + CsEncoder.this.alternationLevel + ".Add(rule, context.index);" + CsEncoder.newline + CsEncoder.this.tab() + "c" + CsEncoder.this.alternationLevel + "++;" + CsEncoder.newline + CsEncoder.this.dec() + "}" + CsEncoder.newline);
               return null;
            }
         }

         rule.accept(new RuleVisitor(), text);
         rulename = rule.rulename.spelling.replace('-', '_');
         out = new FileOutputStream(destDir + "Rule_" + rulename + ".cs");
         out.write(text.toString().getBytes());
         out.close();
      }

      this.createStringValueClass(destDir);
      this.createNumericValueClass(destDir);
   }

   private void createStringValueClass(String destDir) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Terminal_StringValue.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline + this.tab() + "using System.Collections.Generic;" + newline);
      text.append(newline + this.tab() + "public class " + "Terminal_" + "StringValue:Rule" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "private " + "Terminal_" + "StringValue(String spelling, List<Rule> rules) :" + newline + this.tab() + "base(spelling, rules)" + newline + this.inc() + "{" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public static " + "Terminal_" + "StringValue Parse(" + newline + this.add() + "ParserContext context, " + newline + this.add() + "String regex)" + newline + this.inc() + "{" + newline + this.tab() + "context.Push(\"StringValue\", regex);" + newline + newline + this.tab() + "bool parsed = true;" + newline + newline + this.tab() + "Terminal_" + "StringValue stringValue = null;" + newline + this.tab() + "try" + newline + this.inc() + "{" + newline + this.tab() + "String value = context.text.Substring(context.index, regex.Length);" + newline + newline + this.tab() + "if ((parsed = value.ToLower().Equals(regex.ToLower())))" + newline + this.inc() + "{" + newline + this.tab() + "context.index += regex.Length;" + newline + this.tab() + "stringValue = new " + "Terminal_" + "StringValue(value, null);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "catch (ArgumentOutOfRangeException) {parsed = false;}" + newline + newline + this.tab() + "context.Pop(\"StringValue\", parsed);" + newline + newline + this.tab() + "return stringValue;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public override Object Accept(Visitor visitor)" + newline + this.inc() + "{" + newline + this.tab() + "return visitor.Visit(this);" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}");
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(destDir + "Terminal_" + "StringValue.cs");
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createNumericValueClass(String destDir) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Terminal_NumericValue.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline + this.tab() + "using System.Collections.Generic;" + newline + this.tab() + "using System.Text.RegularExpressions;" + newline);
      text.append(newline + this.tab() + "public class " + "Terminal_" + "NumericValue:Rule" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "private " + "Terminal_" + "NumericValue(String spelling, List<Rule> rules) :" + newline + this.tab() + "base(spelling, rules)" + newline + this.inc() + "{" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public static " + "Terminal_" + "NumericValue Parse(" + newline + this.add() + "ParserContext context, " + newline + this.add() + "String spelling, " + newline + this.add() + "String regex, " + newline + this.add() + "int length)" + newline + this.inc() + "{" + newline + this.tab() + "context.Push(\"NumericValue\", spelling + \",\" + regex);" + newline + newline + this.tab() + "bool parsed = true;" + newline + newline + this.tab() + "Terminal_" + "NumericValue numericValue = null;" + newline + this.tab() + "try" + newline + this.inc() + "{" + newline + this.tab() + "String value = context.text.Substring(context.index, length);" + newline + newline + this.tab() + "if ((parsed = Regex.IsMatch(value, regex)))" + newline + this.inc() + "{" + newline + this.tab() + "context.index += length;" + newline + this.tab() + "numericValue = new " + "Terminal_" + "NumericValue(value, null);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "catch (ArgumentOutOfRangeException) {parsed = false;}" + newline + newline + this.tab() + "context.Pop(\"NumericValue\", parsed);" + newline + newline + this.tab() + "return numericValue;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public override Object Accept(Visitor visitor)" + newline + this.inc() + "{" + newline + this.tab() + "return visitor.Visit(this);" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}");
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(destDir + "Terminal_" + "NumericValue.cs");
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createParserExceptionClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserException.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline + this.tab() + "using System.Text;" + newline + this.tab() + "using System.Text.RegularExpressions;" + newline + this.tab() + "using System.Collections.Generic;" + newline);
      text.append(newline + this.tab() + "public class ParserException:Exception" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "private String reason;" + newline + this.tab() + "private String text60;" + newline + this.tab() + "private int index60;" + newline + this.tab() + "private Stack<String> ruleStack;" + newline + newline + this.tab() + "private ParserException cause = null;" + newline);
      text.append(newline + this.tab() + "static private readonly String newline = System.Environment.NewLine;" + newline);
      text.append(newline + this.tab() + "public ParserException(" + newline + this.tab() + this.tab() + "String reason," + newline + this.tab() + this.tab() + "String text," + newline + this.tab() + this.tab() + "int index," + newline + this.tab() + this.tab() + "Stack<String> ruleStack) : base(reason)" + newline + this.inc() + "{" + newline + this.tab() + "this.reason = reason;" + newline + this.tab() + "this.ruleStack = ruleStack;" + newline + newline + this.tab() + "int start = (index < 30) ? 0 : index - 30;" + newline + this.tab() + "int end = (text.Length < index + 30) ? text.Length : index + 30;" + newline + this.tab() + "text60 = text.Substring(start, end - start);" + newline + this.tab() + "index60 = (index < 30) ? index : 30;" + newline + newline + this.tab() + "Regex regex = new Regex(\"[\\\\x00-\\\\x1F]\");" + newline + this.tab() + "text60 = regex.Replace(text60, \" \");" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public String GetReason()" + newline + this.inc() + "{" + newline + this.tab() + "return reason;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public String GetSubstring()" + newline + this.inc() + "{" + newline + this.tab() + "return text60;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public int GetSubstringIndex()" + newline + this.inc() + "{" + newline + this.tab() + "return index60;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public Stack<String> GetRuleStack()" + newline + this.inc() + "{" + newline + this.tab() + "return ruleStack;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public override String Message" + newline + this.inc() + "{" + newline + this.tab() + "get" + newline + this.inc() + "{" + newline + this.tab() + "String marker = \"                              \";" + newline + newline + this.tab() + "StringBuilder buffer = new StringBuilder();" + newline + this.tab() + "buffer.Append(reason + newline);" + newline + this.tab() + "buffer.Append(text60 + newline);" + newline + this.tab() + "buffer.Append(marker.Substring(0, index60) + \"^\" + newline);" + newline + newline + this.tab() + "if (ruleStack.Count > 0)" + newline + this.inc() + "{" + newline + this.tab() + "buffer.Append(\"rule stack:\");" + newline + newline + this.tab() + "foreach (String rule in ruleStack)" + newline + this.add() + "buffer.Append(newline + \"  \" + rule);" + newline + this.dec() + "}" + newline + newline + this.tab() + "ParserException secondaryError = (ParserException)GetCause();" + newline + this.tab() + "if (secondaryError != null)" + newline + this.inc() + "{" + newline + this.tab() + "buffer.Append(\"possible cause: \" + secondaryError.reason + newline);" + newline + this.tab() + "buffer.Append(secondaryError.text60 + newline);" + newline + this.tab() + "buffer.Append(marker.Substring(0, secondaryError.index60) + \"^\" + newline);" + newline + newline + this.tab() + "if (secondaryError.ruleStack.Count > 0)" + newline + this.inc() + "{" + newline + this.tab() + "buffer.Append(\"rule stack:\");" + newline + newline + this.tab() + "foreach (String rule in secondaryError.ruleStack)" + newline + this.add() + "buffer.Append(newline + \"  \" + rule);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + newline + this.tab() + "return buffer.ToString();" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void SetCause(ParserException cause)" + newline + this.inc() + "{" + newline + this.tab() + "this.cause = cause;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "ParserException GetCause()" + newline + this.inc() + "{" + newline + this.tab() + "return cause;" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createDisplayerClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Displayer.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline + this.tab() + "using System.Collections.Generic;" + newline);
      text.append(newline + this.tab() + "public class Displayer:Visitor" + newline + this.inc() + "{" + newline);
      Iterator out = this.grammar.rules.iterator();

      String rulename;
      while(out.hasNext()) {
         Rule rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(newline + this.tab() + "public Object Visit(" + "Rule_" + rulename + " rule)" + newline + this.inc() + "{" + newline + this.tab() + "return VisitRules(rule.rules);" + newline + this.dec() + "}" + newline);
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         ExternalRule rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(newline + this.tab() + "public Object Visit(" + rulename + " rule)" + newline + this.inc() + "{" + newline + this.tab() + "Console.Write(rule.spelling);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      }

      text.append(newline + this.tab() + "public Object Visit(" + "Terminal_" + "StringValue value)" + newline + this.inc() + "{" + newline + this.tab() + "Console.Write(value.spelling);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public Object Visit(" + "Terminal_" + "NumericValue value)" + newline + this.inc() + "{" + newline + this.tab() + "Console.Write(value.spelling);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "private Object VisitRules(List<Rule> rules)" + newline + this.inc() + "{" + newline + this.tab() + "foreach (Rule rule in rules)" + newline + this.add() + "rule.Accept(this);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out1 = new FileOutputStream(filename);
      out1.write(text.toString().getBytes());
      out1.close();
   }

   private void createXmlDisplayerClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * XmlDisplayer.cs" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.namespace != null) {
         text.append(this.inc() + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "using System;" + newline + this.tab() + "using System.Collections.Generic;" + newline);
      text.append(newline + this.tab() + "public class XmlDisplayer:Visitor" + newline + this.inc() + "{" + newline + this.tab() + "private bool terminal = true;" + newline);
      Iterator out = this.grammar.rules.iterator();

      String rulename;
      while(out.hasNext()) {
         Rule rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(newline + this.tab() + "public Object Visit(" + "Rule_" + rulename + " rule)" + newline + this.inc() + "{" + newline + this.tab() + "if (!terminal) System.Console.WriteLine();" + newline + this.tab() + "Console.Write(\"<" + rule.rulename.spelling + ">\");" + newline + this.tab() + "terminal = false;" + newline + this.tab() + "VisitRules(rule.rules);" + newline + this.tab() + "if (!terminal) System.Console.WriteLine();" + newline + this.tab() + "Console.Write(\"</" + rule.rulename.spelling + ">\");" + newline + this.tab() + "terminal = false;" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         ExternalRule rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(newline + this.tab() + "public Object Visit(" + rulename + " rule)" + newline + this.inc() + "{" + newline + this.tab() + "if (!terminal) System.Console.WriteLine();" + newline + this.tab() + "Console.Write(\"<" + rulename + ">\");" + newline + this.tab() + "Console.Write(rule.spelling);" + newline + this.tab() + "Console.Write(\"</" + rulename + ">\");" + newline + this.tab() + "terminal = false;" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      }

      text.append(newline + this.tab() + "public Object Visit(" + "Terminal_" + "StringValue value)" + newline + this.inc() + "{" + newline + this.tab() + "Console.Write(value.spelling);" + newline + this.tab() + "terminal = true;" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public Object Visit(" + "Terminal_" + "NumericValue value)" + newline + this.inc() + "{" + newline + this.tab() + "Console.Write(value.spelling);" + newline + this.tab() + "terminal = true;" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "private Object VisitRules(List<Rule> rules)" + newline + this.inc() + "{" + newline + this.tab() + "foreach (Rule rule in rules)" + newline + this.add() + "rule.Accept(this);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
      if(this.namespace != null) {
         text.append(this.dec() + "}" + newline);
      }

      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out1 = new FileOutputStream(filename);
      out1.write(text.toString().getBytes());
      out1.close();
   }

   private String tab() {
      StringBuffer buffer = new StringBuffer();

      for(int i = 0; i < this.tabLevel; ++i) {
         buffer.append("  ");
      }

      return buffer.toString();
   }

   private String inc() {
      String tabs = this.tab();
      ++this.tabLevel;
      return tabs;
   }

   private String dec() {
      --this.tabLevel;
      return this.tab();
   }

   private String add() {
      ++this.tabLevel;
      String tabs = this.tab();
      --this.tabLevel;
      return tabs;
   }

   // $FF: synthetic method
   static int access$808(CsEncoder x0) {
      return x0.alternationLevel++;
   }

   // $FF: synthetic method
   static int access$810(CsEncoder x0) {
      return x0.alternationLevel--;
   }
}
