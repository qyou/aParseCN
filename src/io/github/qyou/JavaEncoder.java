package io.github.qyou;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

final class JavaEncoder implements Encoder {
   private Grammar grammar;
   private String producedBy;
   private Date producedAt;
   private String javaPackage;
   private boolean annotate;
   private int alternationLevel = 0;
   private static final String newline = System.getProperty("line.separator", "\n");
   private static final char hyphenSubstitute = '_';
   private static final boolean javadocs = true;
   private static final String rulePrefix = "Rule_";
   private static final String terminalPrefix = "Terminal_";
   private int tabLevel = 0;

   public void encode(Grammar grammar, String producedBy, Properties arguments) throws IOException {
      this.grammar = grammar;
      this.producedBy = producedBy;
      this.producedAt = new Date();
      this.javaPackage = arguments.getProperty("Package");
      this.annotate = arguments.getProperty("Annotate").equalsIgnoreCase("On");
      String destDir = arguments.getProperty("DestDir");
      if(!destDir.endsWith(System.getProperty("file.separator"))) {
         destDir = destDir.concat(System.getProperty("file.separator"));
      }

      this.createParserClass(destDir + "Parser.java");
      this.createParserContextClass(destDir + "ParserContext.java");
      this.createParserAlternativeClass(destDir + "ParserAlternative.java");
      this.createVisitorClass(destDir + "Visitor.java");
      this.createRuleClass(destDir + "Rule.java");
      this.createRuleClasses(destDir);
      this.createParserExceptionClass(destDir + "ParserException.java");
      this.createDisplayerClass(destDir + "Displayer.java");
      this.createXmlDisplayerClass(destDir + "XmlDisplayer.java");
   }

   private void createParserClass(String filename) throws IOException {
      boolean javadocs = false;
      StringBuffer text = new StringBuffer();
      String primaryRulename = this.grammar.primaryRule.rulename.spelling;
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Parser.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline);
         text.append(this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      text.append(newline);
      text.append(this.tab() + "import java.util.Stack;" + newline);
      text.append(this.tab() + "import java.util.Properties;" + newline);
      text.append(this.tab() + "import java.io.File;" + newline);
      text.append(this.tab() + "import java.io.FileReader;" + newline);
      text.append(this.tab() + "import java.io.BufferedReader;" + newline);
      text.append(this.tab() + "import java.io.InputStream;" + newline);
      text.append(this.tab() + "import java.io.IOException;" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * <p>A parser of character streams that represent a <code>" + primaryRulename + "</code>" + newline + this.tab() + " * whose structure has been defined using the ABNF metalanguage.</p>" + newline + this.tab() + " * " + newline + this.tab() + " * <p>Producer : " + this.producedBy + "<br/>" + newline + this.tab() + " * Produced : " + this.producedAt.toString() + "</p>" + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public class Parser" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "private Parser() {}" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The main() method by which the parser can be run as a standalone " + newline + this.tab() + " * program to parse a string or the contents of a file." + newline + this.tab() + " * <p>" + newline + this.tab() + " * <table border=\"1\" cellpadding=\"3\" cellspacing=\"0\">" + newline + this.tab() + " * <tr bgcolor=\"#CCCCFF\">" + newline + this.tab() + " * <th align=\"left\" colspan=\"2\">" + newline + this.tab() + " * <b>Arguments</b>" + newline + this.tab() + " * </th>" + newline + this.tab() + " * </tr>" + newline + this.tab() + " * <tr>" + newline + this.tab() + " * <td width=\"150\" valign=\"top\">" + newline + this.tab() + " * [-rule rulename]" + newline + this.tab() + " * </td>" + newline + this.tab() + " * <td>" + newline + this.tab() + " * The name of the ABNF rule to use when parsing the specified string " + newline + this.tab() + " * or file contents. If not specified then the rule used is " + newline + this.tab() + " * <code>" + primaryRulename + "</code>." + newline + this.tab() + " * </td>" + newline + this.tab() + " * </tr>" + newline + this.tab() + " * <tr>" + newline + this.tab() + " * <td valign=\"top\">" + newline + this.tab() + " * [-trace]" + newline + this.tab() + " * </td>" + newline + this.tab() + " * <td>" + newline + this.tab() + " * To output a trace of the steps performed by the parser to " + newline + this.tab() + " * <code>System.out</code>." + newline + this.tab() + " * </td>" + newline + this.tab() + " * </tr>" + newline + this.tab() + " * <tr>" + newline + this.tab() + " * <td valign=\"top\">" + newline + this.tab() + " * -string string | -file file" + newline + this.tab() + " * </td>" + newline + this.tab() + " * <td>" + newline + this.tab() + " * The string of characters to be parsed or the name of the file that" + newline + this.tab() + " * contains the stream of characters to be parsed." + newline + this.tab() + " * </td>" + newline + this.tab() + " * </tr>" + newline + this.tab() + " * <tr>" + newline + this.tab() + " * <td valign=\"top\">" + newline + this.tab() + " * [-visitor visitor]" + newline + this.tab() + " * </td>" + newline + this.tab() + " * <td>" + newline + this.tab() + " * The name of a class, that implements the {@link Visitor} interface," + newline + this.tab() + " * that is invoked to scan the tree of ABNF rules produced." + newline + this.tab() + " * </td>" + newline + this.tab() + " * </tr>" + newline + this.tab() + " * </table>" + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "static public void main(String[] args)" + newline + this.inc() + "{" + newline + this.tab() + "Properties arguments = new Properties();" + newline + this.tab() + "String error = \"\";" + newline + this.tab() + "boolean ok = args.length > 0;" + newline + newline + this.tab() + "if (ok)" + newline + this.inc() + "{" + newline + this.tab() + "arguments.setProperty(\"Trace\", \"Off\");" + newline + this.tab() + "arguments.setProperty(\"Rule\", \"" + primaryRulename + "\");" + newline + newline + this.tab() + "for (int i = 0; i < args.length; i++)" + newline + this.inc() + "{" + newline + this.tab() + "if (args[i].equals(\"-trace\"))" + newline + this.add() + "arguments.setProperty(\"Trace\", \"On\");" + newline + this.tab() + "else if (args[i].equals(\"-visitor\"))" + newline + this.add() + "arguments.setProperty(\"Visitor\", args[++i]);" + newline + this.tab() + "else if (args[i].equals(\"-file\"))" + newline + this.add() + "arguments.setProperty(\"File\", args[++i]);" + newline + this.tab() + "else if (args[i].equals(\"-string\"))" + newline + this.add() + "arguments.setProperty(\"String\", args[++i]);" + newline + this.tab() + "else if (args[i].equals(\"-rule\"))" + newline + this.add() + "arguments.setProperty(\"Rule\", args[++i]);" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "error = \"unknown argument: \" + args[i];" + newline + this.tab() + "ok = false;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (ok)" + newline + this.inc() + "{" + newline + this.tab() + "if (arguments.getProperty(\"File\") == null &&" + newline + this.tab() + "    arguments.getProperty(\"String\") == null)" + newline + this.inc() + "{" + newline + this.tab() + "error = \"insufficient arguments: -file or -string required\";" + newline + this.tab() + "ok = false;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (!ok)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.println(\"error: \" + error);" + newline + this.tab() + "System.out.println(\"usage: Parser [-rule rulename] [-trace] <-file file | -string string> [-visitor visitor]\");" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "try" + newline + this.inc() + "{" + newline + this.tab() + "Rule rule = null;" + newline + newline + this.tab() + "if (arguments.getProperty(\"File\") != null)" + newline + this.inc() + "{" + newline + this.tab() + "rule = " + newline + this.add() + "parse(" + newline + this.add() + "  arguments.getProperty(\"Rule\"), " + newline + this.add() + "  new File(arguments.getProperty(\"File\")), " + newline + this.add() + "  arguments.getProperty(\"Trace\").equals(\"On\"));" + newline + this.dec() + "}" + newline + this.tab() + "else if (arguments.getProperty(\"String\") != null)" + newline + this.inc() + "{" + newline + this.tab() + "rule = " + newline + this.add() + "parse(" + newline + this.add() + "  arguments.getProperty(\"Rule\"), " + newline + this.add() + "  arguments.getProperty(\"String\"), " + newline + this.add() + "  arguments.getProperty(\"Trace\").equals(\"On\"));" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (arguments.getProperty(\"Visitor\") != null)" + newline + this.inc() + "{" + newline + this.tab() + "Visitor visitor = " + newline + this.add() + "(Visitor)Class.forName(arguments.getProperty(\"Visitor\")).newInstance();" + newline + this.tab() + "rule.accept(visitor);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "catch (IllegalArgumentException e)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.println(\"argument error: \" + e.getMessage());" + newline + this.dec() + "}" + newline + this.tab() + "catch (IOException e)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.println(\"io error: \" + e.getMessage());" + newline + this.dec() + "}" + newline + this.tab() + "catch (ParserException e)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.println(\"parser error: \" + e.getMessage());" + newline + this.dec() + "}" + newline + this.tab() + "catch (ClassNotFoundException e)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.println(\"visitor error: class not found - \" + e.getMessage());" + newline + this.dec() + "}" + newline + this.tab() + "catch (IllegalAccessException e)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.println(\"visitor error: illegal access - \" + e.getMessage());" + newline + this.dec() + "}" + newline + this.tab() + "catch (InstantiationException e)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.println(\"visitor error: instantiation failure - \" + e.getMessage());" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * Builds an instance of the specified ABNF rule by parsing the given string." + newline + this.tab() + " * A {@link ParserException} is raised if the given string does not conform" + newline + this.tab() + " * to the rule\'s ABNF grammar." + newline + this.tab() + " *" + newline + this.tab() + " * @param rulename The ABNF rule to use to parse the given string." + newline + this.tab() + " * @param string The string to parse." + newline + this.tab() + " * @return An instance of the specified rule that encapsulates the " + newline + this.tab() + " * composition of the given string in accordance with the rules defined in " + newline + this.tab() + " * its ABNF grammar." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "static public Rule parse(String rulename, String string)" + newline + this.tab() + "throws IllegalArgumentException," + newline + this.tab() + "       ParserException" + newline + this.inc() + "{" + newline + this.tab() + "return parse(rulename, string, false);" + newline + this.dec() + "}" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * Builds an instance of the specified ABNF rule by parsing the contents of " + newline + this.tab() + " * the given stream. A {@link ParserException} is raised if the contents of " + newline + this.tab() + " * the stream do not conform to the rule\'s ABNF grammar." + newline + this.tab() + " *" + newline + this.tab() + " * @param rulename The ABNF rule to use to parse the contents of the given " + newline + this.tab() + " * stream." + newline + this.tab() + " * @param in The input stream whose contents are to be parsed." + newline + this.tab() + " * @return An instance of the specified rule that encapsulates the " + newline + this.tab() + " * composition of the given stream in accordance with the rules defined in " + newline + this.tab() + " * its ABNF grammar." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "static public Rule parse(String rulename, InputStream in)" + newline + this.tab() + "throws IllegalArgumentException," + newline + this.tab() + "       IOException," + newline + this.tab() + "       ParserException" + newline + this.inc() + "{" + newline + this.tab() + "return parse(rulename, in, false);" + newline + this.dec() + "}" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * Builds an instance of the specified ABNF rule by parsing the contents of " + newline + this.tab() + " * the specified file. A {@link ParserException} is raised if the contents " + newline + this.tab() + " * of the file do not conform to the rule\'s ABNF grammar." + newline + this.tab() + " *" + newline + this.tab() + " * @param rulename The ABNF rule to use to parse the contents of the " + newline + this.tab() + " * specified file." + newline + this.tab() + " * @param file The file whose contents are to be parsed." + newline + this.tab() + " * @return An instance of the specified rule that encapsulates the " + newline + this.tab() + " * composition of the contents of the given file in accordance with the " + newline + this.tab() + " * rules defined in its ABNF grammar." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "static public Rule parse(String rulename, File file)" + newline + this.tab() + "throws IllegalArgumentException," + newline + this.tab() + "       IOException," + newline + this.tab() + "       ParserException" + newline + this.inc() + "{" + newline + this.tab() + "return parse(rulename, file, false);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static private Rule parse(String rulename, String string, boolean trace)" + newline + this.tab() + "throws IllegalArgumentException," + newline + this.tab() + "       ParserException" + newline + this.inc() + "{" + newline + this.tab() + "if (rulename == null)" + newline + this.add() + "throw new IllegalArgumentException(\"null rulename\");" + newline + this.tab() + "if (string == null)" + newline + this.add() + "throw new IllegalArgumentException(\"null string\");" + newline);
      text.append(newline + this.tab() + "ParserContext context = new ParserContext(string, trace);" + newline);
      text.append(newline + this.tab() + "Rule rule = null;" + newline);
      boolean first = true;

      for(Iterator out = this.grammar.rules.iterator(); out.hasNext(); first = false) {
         Rule rule = (Rule)out.next();
         text.append(this.tab());
         if(!first) {
            text.append("else ");
         }

         text.append("if (rulename.equalsIgnoreCase(\"" + rule.rulename.spelling + "\")) rule = " + "Rule_" + rule.rulename.spelling.replace('-', '_') + ".parse(context);" + newline);
      }

      text.append(this.tab() + "else throw new IllegalArgumentException(\"unknown rule\");" + newline);
      text.append(newline + this.tab() + "if (rule == null)" + newline + this.inc() + "{" + newline + this.tab() + "throw new ParserException(" + newline + this.add() + "\"rule \\\"\" + (String)context.getErrorStack().peek() + \"\\\" failed\"," + newline + this.add() + "context.text," + newline + this.add() + "context.getErrorIndex()," + newline + this.add() + "context.getErrorStack());" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (context.text.length() > context.index)" + newline + this.inc() + "{" + newline + this.tab() + "ParserException primaryError = " + newline + this.add() + "new ParserException(" + newline + this.add() + "  \"extra data found\"," + newline + this.add() + "  context.text," + newline + this.add() + "  context.index," + newline + this.add() + "  new Stack<String>());" + newline + newline + this.tab() + "if (context.getErrorIndex() > context.index)" + newline + this.inc() + "{" + newline + this.tab() + "ParserException secondaryError = " + newline + this.add() + "new ParserException(" + newline + this.add() + "  \"rule \\\"\" + (String)context.getErrorStack().peek() + \"\\\" failed\"," + newline + this.add() + "  context.text," + newline + this.add() + "  context.getErrorIndex()," + newline + this.add() + "  context.getErrorStack());" + newline + newline + this.tab() + "primaryError.initCause(secondaryError);" + newline + this.dec() + "}" + newline + newline + this.tab() + "throw primaryError;" + newline + this.dec() + "}" + newline + newline + this.tab() + "return rule;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static private Rule parse(String rulename, InputStream in, boolean trace)" + newline + this.tab() + "throws IllegalArgumentException," + newline + this.tab() + "       IOException," + newline + this.tab() + "       ParserException" + newline + this.inc() + "{" + newline + this.tab() + "if (rulename == null)" + newline + this.add() + "throw new IllegalArgumentException(\"null rulename\");" + newline + this.tab() + "if (in == null)" + newline + this.add() + "throw new IllegalArgumentException(\"null input stream\");" + newline + newline + this.tab() + "int ch = 0;" + newline + this.tab() + "StringBuffer out = new StringBuffer();" + newline + this.tab() + "while ((ch = in.read()) != -1)" + newline + this.add() + "out.append((char)ch);" + newline + newline + this.tab() + "return parse(rulename, out.toString(), trace);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static private Rule parse(String rulename, File file, boolean trace)" + newline + this.tab() + "throws IllegalArgumentException," + newline + this.tab() + "       IOException," + newline + this.tab() + "       ParserException" + newline + this.inc() + "{" + newline + this.tab() + "if (rulename == null)" + newline + this.add() + "throw new IllegalArgumentException(\"null rulename\");" + newline + this.tab() + "if (file == null)" + newline + this.add() + "throw new IllegalArgumentException(\"null file\");" + newline + newline + this.tab() + "BufferedReader in = new BufferedReader(new FileReader(file));" + newline + this.tab() + "int ch = 0;" + newline + this.tab() + "StringBuffer out = new StringBuffer();" + newline + this.tab() + "while ((ch = in.read()) != -1)" + newline + this.add() + "out.append((char)ch);" + newline + newline + this.tab() + "in.close();" + newline + newline + this.tab() + "return parse(rulename, out.toString(), trace);" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
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
      boolean javadocs = false;
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserContext.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline);
         text.append(this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      text.append(newline + this.tab() + "import java.util.Stack;" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * <p>A context class that encapsulates the information required by the ABNF " + newline + this.tab() + " * rule parsers.</p>" + newline + newline + this.tab() + " * The {@link text} is the entire string of characters being parsed and " + newline + this.tab() + " * {@link index} points to the" + newline + this.tab() + " * start of the characters, within the text string, that the invoked parser has been called on to" + newline + this.tab() + " * decode." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public class ParserContext" + newline + this.inc() + "{" + newline);
      if(javadocs) {
         text.append(this.tab() + "/**" + newline + this.tab() + " * The string being parsed." + newline + this.tab() + " */" + newline);
      }

      text.append(this.tab() + "public final String text;" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The index to the character, within in the string being parsed," + newline + this.tab() + " * that is next to be consumed by a rule parser." + newline + this.tab() + " */" + newline);
      }

      text.append(this.tab() + "public int index;" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/*" + newline + this.tab() + " * Private data." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "private Stack<Integer> startStack = new Stack<Integer>();" + newline + this.tab() + "private Stack<String> callStack = new Stack<String>();" + newline + this.tab() + "private Stack<String> errorStack = new Stack<String>();" + newline + this.tab() + "private int level = 0;" + newline + this.tab() + "private int errorIndex = 0;" + newline + newline + this.tab() + "private final boolean traceOn;" + newline);
      text.append(newline + this.tab() + "public ParserContext(String text, boolean traceOn)" + newline + this.inc() + "{" + newline + this.tab() + "this.text = text;" + newline + this.tab() + "this.traceOn = traceOn;" + newline + this.tab() + "index = 0;" + newline + this.dec() + "}" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * A rule\'s parser must call this method before it tries to determine " + newline + this.tab() + " * whether the next sequence of characters within the string being parsed " + newline + this.tab() + " * are a match for that rule." + newline + newline + this.tab() + " * @param rulename The name of the rule being parsed." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public void push(String rulename)" + newline + this.inc() + "{" + newline + this.tab() + "push(rulename, \"\");" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void push(String rulename, String trace)" + newline + this.inc() + "{" + newline + this.tab() + "callStack.push(rulename);" + newline + this.tab() + "startStack.push(new Integer(index));" + newline + newline + this.tab() + "if (traceOn)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.println(\"-> \" + ++level + \": \" + rulename + \"(\" + (trace != null ? trace : \"\") + \")\");" + newline + this.tab() + "System.out.println(index + \": \" + text.substring(index, index + 10 > text.length() ? text.length() : index + 10).replaceAll(\"[\\\\x00-\\\\x1F]\", \" \"));" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void pop(String function, boolean result)" + newline + this.inc() + "{" + newline + this.tab() + "Integer start = startStack.pop();" + newline + this.tab() + "callStack.pop();" + newline + newline + this.tab() + "if (traceOn)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.println(" + newline + this.add() + "\"<- \" + level-- + " + newline + this.add() + "\": \" + function + " + newline + this.add() + "\"(\" + (result ? \"true\" : \"false\") + " + newline + this.add() + "\",s=\" + start + " + newline + this.add() + "\",l=\" + (index - start) + " + newline + this.add() + "\",e=\" + errorIndex + \")\");" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (!result)" + newline + this.inc() + "{" + newline + this.tab() + "if (index > errorIndex)" + newline + this.inc() + "{" + newline + this.tab() + "errorIndex = index;" + newline + this.tab() + "errorStack = new Stack<String>();" + newline + this.tab() + "errorStack.addAll(callStack);" + newline + this.dec() + "}" + newline + this.tab() + "else if (index == errorIndex && errorStack.isEmpty())" + newline + this.inc() + "{" + newline + this.tab() + "errorStack = new Stack<String>();" + newline + this.tab() + "errorStack.addAll(callStack);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "if (index > errorIndex) errorIndex = 0;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public Stack<String> getErrorStack()" + newline + this.inc() + "{" + newline + this.tab() + "return errorStack;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public int getErrorIndex()" + newline + this.inc() + "{" + newline + this.tab() + "return errorIndex;" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
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
      text.append(" * ParserAlternative.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline);
         text.append(this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      text.append(newline + this.tab() + "import java.util.ArrayList;" + newline);
      text.append(newline + this.tab() + "import java.util.List;" + newline);
      text.append(newline + this.tab() + "public class ParserAlternative" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "public ArrayList<Rule> rules;" + newline + this.tab() + "public int start;" + newline + this.tab() + "public int end;" + newline);
      text.append(newline + this.tab() + "public ParserAlternative(int start)" + newline + this.inc() + "{" + newline + this.tab() + "this.rules = new ArrayList<Rule>();" + newline + this.tab() + "this.start = start;" + newline + this.tab() + "this.end = start;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void add(Rule rule, int end)" + newline + this.inc() + "{" + newline + this.tab() + "this.rules.add(rule);" + newline + this.tab() + "this.end = end;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public void add(ArrayList<Rule> rules, int end)" + newline + this.inc() + "{" + newline + this.tab() + "this.rules.addAll(rules);" + newline + this.tab() + "this.end = end;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static public ParserAlternative getBest(List<ParserAlternative> alternatives)" + newline + this.inc() + "{" + newline + this.tab() + "ParserAlternative best = null;" + newline + newline + this.tab() + "for (ParserAlternative alternative : alternatives)" + newline + this.inc() + "{" + newline + this.tab() + "if (best == null || alternative.end > best.end)" + newline + this.add() + "best = alternative;" + newline + this.dec() + "}" + newline + newline + this.tab() + "return best;" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
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
      boolean javadocs = false;
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Visitor.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline + this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The visitor interface for the <code>" + this.grammar.primaryRule.rulename.spelling + "</code> ABNF rule" + newline + this.tab() + " * and its component rules. A visitor method is defined for every" + newline + this.tab() + " * rule in the <code>" + this.grammar.primaryRule.rulename.spelling + "</code> ABNF grammar." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public interface Visitor" + newline + this.inc() + "{" + newline);

      Iterator out;
      String rulename;
      for(out = this.grammar.rules.iterator(); out.hasNext(); text.append(this.tab() + "public Object visit(" + "Rule_" + rulename + " rule);" + newline)) {
         Rule rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         if(javadocs) {
            text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The <code>" + rulename + "</code> ABNF rule visitor method." + newline + this.tab() + " * " + newline + this.tab() + " * @param rule The <code>" + rulename + "</code> rule " + newline + this.tab() + " * to be passed to the invoked instance of this method." + newline + this.tab() + " * @return Any object that might be returned by the invoked instance of" + newline + this.tab() + " * this method." + newline + this.tab() + " */" + newline + newline);
         }
      }

      for(out = this.grammar.externalRules.iterator(); out.hasNext(); text.append(this.tab() + "public Object visit(" + rulename + " rule);" + newline)) {
         ExternalRule rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         if(javadocs) {
            text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The <code>" + rulename + "</code> rule visitor method." + newline + this.tab() + " * " + newline + this.tab() + " * @param rule The <code>" + rulename + "</code> rule " + newline + this.tab() + " * to be passed to the invoked instance of this method." + newline + this.tab() + " * @return Any object that might be returned by the invoked instance of" + newline + this.tab() + " * this method." + newline + this.tab() + " */" + newline + newline);
         }
      }

      text.append(newline);
      if(javadocs) {
         text.append(this.tab() + "/**" + newline + this.tab() + " * The terminal <code>StringValue</code> visitor method." + newline + this.tab() + " * " + newline + this.tab() + " * @param value The terminal <code>StringValue</code> " + newline + this.tab() + " * to be passed to the invoked instance of this method." + newline + this.tab() + " * @return Any object that might be returned by the invoked instance" + newline + this.tab() + " * of this method." + newline + this.tab() + " */" + newline + newline);
      }

      text.append(this.tab() + "public Object visit(" + "Terminal_" + "StringValue value);" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The terminal <code>NumericValue</code> visitor method." + newline + this.tab() + " * " + newline + this.tab() + " * @param value The terminal <code>NumericValue</code> " + newline + this.tab() + " * to be passed to the invoked instance of this method." + newline + this.tab() + " * @return Any object that might be returned by the invoked instance" + newline + this.tab() + " * of this method." + newline + this.tab() + " */" + newline + newline);
      }

      text.append(this.tab() + "public Object visit(" + "Terminal_" + "NumericValue value);" + newline);
      text.append(this.dec() + "}" + newline);
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
      boolean javadocs = false;
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Rule.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline + this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      text.append(newline + this.tab() + "import java.util.ArrayList;" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The base class for all ABNF rules defined in an ABNF grammar." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public abstract class Rule" + newline + this.inc() + "{" + newline);
      if(javadocs) {
         text.append(this.tab() + "/**" + newline + this.tab() + " * The string of characters that comprise this rule." + newline + this.tab() + " */" + newline);
      }

      text.append(this.tab() + "public final String spelling;" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The rules that comprise this rule." + newline + this.tab() + " */" + newline);
      }

      text.append(this.tab() + "public final ArrayList<Rule> rules;" + newline + newline);
      if(javadocs) {
         text.append(this.tab() + "/**" + newline + this.tab() + " * Creates a rule from a list of rules and their associated" + newline + this.tab() + " * string of characters." + newline + this.tab() + " *" + newline + this.tab() + " * @param spelling A string of characters associated with the" + newline + this.tab() + " * component rules." + newline + this.tab() + " * @param rules The component rules." + newline + this.tab() + " */" + newline + newline);
      }

      text.append(this.tab() + "protected Rule(String spelling, ArrayList<Rule> rules)" + newline + this.inc() + "{" + newline + this.tab() + "this.spelling = spelling;" + newline + this.tab() + "this.rules = rules;" + newline + this.dec() + "}" + newline + newline);
      if(javadocs) {
         text.append(this.tab() + "/**" + newline + this.tab() + " * Returns a <code>String</code> object representing the rule. This is simply" + newline + this.tab() + " * the rule\'s {@link #spelling}." + newline + newline + this.tab() + " * @return A <code>String</code> representation of the rule (i.e. the rule\'s" + newline + this.tab() + " * {@link #spelling})." + newline + this.tab() + " */" + newline + newline);
      }

      text.append(this.tab() + "public String toString()" + newline + this.inc() + "{" + newline + this.tab() + "return spelling;" + newline + this.dec() + "}" + newline + newline);
      if(javadocs) {
         text.append(this.tab() + "/**" + newline + this.tab() + " * Compares this rule and the specifed object for equality." + newline + this.tab() + " * This rule and the object are deemed to be equal if the object is" + newline + this.tab() + " * a <code>Rule</code> and the {@link #spelling} of both rules" + newline + this.tab() + " * are identical." + newline + newline + this.tab() + " * @param object The object to compare this rule against." + newline + this.tab() + " * @return <code>true</code> if equal, otherwise <code>false</code>." + newline + this.tab() + " */" + newline + newline);
      }

      text.append(this.tab() + "public boolean equals(Object object)" + newline + this.inc() + "{" + newline + this.tab() + "return object instanceof Rule && spelling.equals(((Rule)object).spelling);" + newline + this.dec() + "}" + newline + newline);
      if(javadocs) {
         text.append(this.tab() + "/**" + newline + this.tab() + " * Returns the hash code for this rule. The hash code returned is the" + newline + this.tab() + " * hash code of the rule\'s {@link #spelling} produced by the" + newline + this.tab() + " * <code>String.hashCode</code> method." + newline + newline + this.tab() + " * @return The hash code for this rule." + newline + this.tab() + " */" + newline + newline);
      }

      text.append(this.tab() + "public int hashCode()" + newline + this.inc() + "{" + newline + this.tab() + "return spelling.hashCode();" + newline + this.dec() + "}" + newline + newline);
      if(javadocs) {
         text.append(this.tab() + "/**" + newline + this.tab() + " * Compares this rule and the specified rule. The {@link #spelling}" + newline + this.tab() + " * of the rules are compared lexicographically using the" + newline + this.tab() + " * <code>String.compareTo</code> method." + newline + newline + this.tab() + " * @return The value <code>0</code> if the rules are equal;" + newline + this.tab() + " * a value less than <code>0</code> if this rule is lexicographically less" + newline + this.tab() + " * than the specified rule; a value greater than <code>0</code> if this" + newline + this.tab() + " * rule is lexicographically greater than the specified rule." + newline + this.tab() + " */" + newline + newline);
      }

      text.append(this.tab() + "public int compareTo(Rule rule)" + newline + this.inc() + "{" + newline + this.tab() + "return spelling.compareTo(rule.spelling);" + newline + this.dec() + "}" + newline + newline);
      if(javadocs) {
         text.append(this.tab() + "/**" + newline + this.tab() + " * The visitor method that passes this rule to the specified visitor" + newline + this.tab() + " * to be processed as required by that visitor." + newline + newline + this.tab() + " * @param visitor A class that has implemented the {@link Visitor} interface." + newline + this.tab() + " * @return Any object that might be returned by the <code>visitor</code>." + newline + this.tab() + " */" + newline + newline);
      }

      text.append(this.tab() + "public abstract Object accept(Visitor visitor);" + newline + this.dec() + "}" + newline);
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
               boolean javadocs = false;
               StringBuffer text = (StringBuffer)argument;
               String rulename = rule.rulename.spelling.replace('-', '_');
               text.append("/* -----------------------------------------------------------------------------" + JavaEncoder.newline);
               text.append(" * Rule_" + rulename + ".java" + JavaEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + JavaEncoder.newline);
               text.append(" *" + JavaEncoder.newline);
               text.append(" * Producer : " + JavaEncoder.this.producedBy + JavaEncoder.newline);
               text.append(" * Produced : " + JavaEncoder.this.producedAt.toString() + JavaEncoder.newline);
               text.append(" *" + JavaEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + JavaEncoder.newline);
               text.append(" */" + JavaEncoder.newline);
               if(JavaEncoder.this.javaPackage != null) {
                  text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "package " + JavaEncoder.this.javaPackage + ";" + JavaEncoder.newline);
               }

               text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "import java.util.ArrayList;" + JavaEncoder.newline);
               if(javadocs) {
                  text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "/**" + JavaEncoder.newline + JavaEncoder.this.tab() + " * The <code>" + rulename + "</code> rule." + JavaEncoder.newline + JavaEncoder.this.tab() + " */" + JavaEncoder.newline);
               }

               text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "final public class " + "Rule_" + rulename + " extends Rule" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
               text.append(JavaEncoder.this.tab() + "public " + "Rule_" + rulename + "(String spelling, ArrayList<Rule> rules)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.tab() + "super(spelling, rules);" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               if(javadocs) {
                  text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "/**" + JavaEncoder.newline + JavaEncoder.this.tab() + " * The visit method that passes this <code>" + rulename + "</code> rule" + JavaEncoder.newline + JavaEncoder.this.tab() + " * to the specified visitor." + JavaEncoder.newline + JavaEncoder.this.tab() + " *" + JavaEncoder.newline + JavaEncoder.this.tab() + " * @param visitor The visitor to which this <code>" + rulename + "</code> rule" + JavaEncoder.newline + JavaEncoder.this.tab() + " * is passed." + JavaEncoder.newline + JavaEncoder.this.tab() + " */" + JavaEncoder.newline);
               }

               text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "public Object accept(Visitor visitor)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.tab() + "return visitor.visit(this);" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               if(javadocs) {
                  text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "/**" + JavaEncoder.newline + JavaEncoder.this.tab() + " * The <code>" + rulename + "</code> rule parser." + JavaEncoder.newline + JavaEncoder.this.tab() + " *" + JavaEncoder.newline + JavaEncoder.this.tab() + " * @param context The parser context." + JavaEncoder.newline + JavaEncoder.this.tab() + " * @return An instance of the <code>" + rulename + "</code> rule or" + JavaEncoder.newline + JavaEncoder.this.tab() + " * null if the parse failed." + JavaEncoder.newline + JavaEncoder.this.tab() + " */" + JavaEncoder.newline);
               }

               text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "public static " + "Rule_" + rulename + " parse(ParserContext context)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
               text.append(JavaEncoder.this.tab() + "context.push(\"" + rule.rulename.spelling + "\");" + JavaEncoder.newline);
               text.append(JavaEncoder.newline);
               text.append(JavaEncoder.this.tab() + "boolean parsed = true;" + JavaEncoder.newline);
               text.append(JavaEncoder.this.tab() + "int s0 = context.index;" + JavaEncoder.newline);
               text.append(JavaEncoder.this.tab() + "ParserAlternative a0 = new ParserAlternative(s0);" + JavaEncoder.newline);
               text.append(JavaEncoder.newline);
               rule.alternation.accept(this, argument);
               text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "Rule rule = null;" + JavaEncoder.newline + JavaEncoder.this.tab() + "if (parsed)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.add() + "rule = new " + "Rule_" + rulename + "(context.text.substring(a0.start, a0.end), a0.rules);" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline + JavaEncoder.this.tab() + "else" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.add() + "context.index = s0;" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "context.pop(\"" + rule.rulename.spelling + "\", parsed);" + JavaEncoder.newline);
               text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "return (" + "Rule_" + rulename + ")rule;" + JavaEncoder.newline);
               text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               text.append(JavaEncoder.newline);
               text.append("/* -----------------------------------------------------------------------------" + JavaEncoder.newline);
               text.append(" * eof" + JavaEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + JavaEncoder.newline);
               text.append(" */" + JavaEncoder.newline);
               return null;
            }

            public Object visit(Alternation alternation, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               JavaEncoder.access$808(JavaEncoder.this);
               if(JavaEncoder.this.annotate) {
                  text.append(JavaEncoder.this.tab() + "/* alternation[" + JavaEncoder.this.alternationLevel + "] - start */" + JavaEncoder.newline);
               }

               text.append(JavaEncoder.this.tab() + "ArrayList<ParserAlternative> as" + JavaEncoder.this.alternationLevel + " = new ArrayList<ParserAlternative>();" + JavaEncoder.newline + JavaEncoder.this.tab() + "parsed = false;" + JavaEncoder.newline);

               for(int i = 0; i < alternation.concatenations.size(); ++i) {
                  ((Concatenation)alternation.concatenations.get(i)).accept(this, argument);
               }

               text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "ParserAlternative b = ParserAlternative.getBest(as" + JavaEncoder.this.alternationLevel + ");" + JavaEncoder.newline + JavaEncoder.newline + JavaEncoder.this.tab() + "parsed = b != null;" + JavaEncoder.newline + JavaEncoder.newline + JavaEncoder.this.tab() + "if (parsed)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.tab() + "a" + (JavaEncoder.this.alternationLevel - 1) + ".add(b.rules, b.end);" + JavaEncoder.newline + JavaEncoder.this.tab() + "context.index = b.end;" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               if(JavaEncoder.this.annotate) {
                  text.append(JavaEncoder.this.tab() + "/* alternation[" + JavaEncoder.this.alternationLevel + "] - end */" + JavaEncoder.newline);
               }

               JavaEncoder.access$810(JavaEncoder.this);
               return null;
            }

            public Object visit(Concatenation concatenation, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               if(JavaEncoder.this.annotate) {
                  text.append(JavaEncoder.this.tab() + "/* concatenation - start */" + JavaEncoder.newline);
               }

               text.append(JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.tab() + "int s" + JavaEncoder.this.alternationLevel + " = context.index;" + JavaEncoder.newline + JavaEncoder.this.tab() + "ParserAlternative a" + JavaEncoder.this.alternationLevel + " = new ParserAlternative(s" + JavaEncoder.this.alternationLevel + ");" + JavaEncoder.newline + JavaEncoder.this.tab() + "parsed = true;" + JavaEncoder.newline);

               for(int i = 0; i < concatenation.repetitions.size(); ++i) {
                  text.append(JavaEncoder.this.tab() + "if (parsed)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
                  ((Repetition)concatenation.repetitions.get(i)).accept(this, argument);
                  text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               }

               text.append(JavaEncoder.this.tab() + "if (parsed)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.tab() + "as" + JavaEncoder.this.alternationLevel + ".add(a" + JavaEncoder.this.alternationLevel + ");" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline + JavaEncoder.this.tab() + "context.index = s" + JavaEncoder.this.alternationLevel + ";" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               if(JavaEncoder.this.annotate) {
                  text.append(JavaEncoder.this.tab() + "/* concatenation - end */" + JavaEncoder.newline);
               }

               return null;
            }

            public Object visit(Repetition repetition, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               String index = "i" + JavaEncoder.this.alternationLevel;
               String found = "f" + JavaEncoder.this.alternationLevel;
               String count = "c" + JavaEncoder.this.alternationLevel;
               int atLeast = repetition.repeat.atLeast;
               int atMost = repetition.repeat.atMost;
               if(JavaEncoder.this.annotate) {
                  text.append(JavaEncoder.this.tab() + "/* repetition(" + atLeast + "*" + atMost + ") - start */" + JavaEncoder.newline);
               }

               text.append(JavaEncoder.this.tab() + "boolean " + found + " = true;" + JavaEncoder.newline);
               if(atLeast > 0 && atLeast == atMost) {
                  text.append(JavaEncoder.this.tab() + "int " + count + " = 0;" + JavaEncoder.newline);
                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* required */" + JavaEncoder.newline);
                  }

                  text.append(JavaEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atLeast + " && " + found + "; " + index + "++)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* optional - none */" + JavaEncoder.newline);
                  }

                  text.append(JavaEncoder.this.tab() + "parsed = " + count + " == " + atLeast + ";" + JavaEncoder.newline);
               } else if(atLeast == 0 && atMost == 0) {
                  text.append(JavaEncoder.this.tab() + "@SuppressWarnings(\"unused\")" + JavaEncoder.newline + JavaEncoder.this.tab() + "int " + count + " = 0;" + JavaEncoder.newline);
                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* required - none */" + JavaEncoder.newline);
                  }

                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* optional */" + JavaEncoder.newline);
                  }

                  text.append(JavaEncoder.this.tab() + "while (" + found + ")" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
                  text.append(JavaEncoder.this.tab() + "parsed = true;" + JavaEncoder.newline);
               } else if(atLeast == 0 && atMost > 0) {
                  text.append(JavaEncoder.this.tab() + "@SuppressWarnings(\"unused\")" + JavaEncoder.newline + JavaEncoder.this.tab() + "int " + count + " = 0;" + JavaEncoder.newline);
                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* required - none */" + JavaEncoder.newline);
                  }

                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* optional */" + JavaEncoder.newline);
                  }

                  text.append(JavaEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atMost + " && " + found + "; " + index + "++)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
                  text.append(JavaEncoder.this.tab() + "parsed = true;" + JavaEncoder.newline);
               } else if(atLeast > 0 && atLeast < atMost) {
                  text.append(JavaEncoder.this.tab() + "int " + count + " = 0;" + JavaEncoder.newline);
                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* required */" + JavaEncoder.newline);
                  }

                  text.append(JavaEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atLeast + " && " + found + "; " + index + "++)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* optional */" + JavaEncoder.newline);
                  }

                  text.append(JavaEncoder.this.tab() + "for (int " + index + " = " + atLeast + "; " + index + " < " + atMost + " && " + found + "; " + index + "++)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
                  text.append(JavaEncoder.this.tab() + "parsed = " + count + " >= " + atLeast + ";" + JavaEncoder.newline);
               } else if(atLeast > 0 && atMost == 0) {
                  text.append(JavaEncoder.this.tab() + "int " + count + " = 0;" + JavaEncoder.newline);
                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* required */" + JavaEncoder.newline);
                  }

                  text.append(JavaEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atLeast + " && " + found + "; " + index + "++)" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
                  if(JavaEncoder.this.annotate) {
                     text.append(JavaEncoder.this.tab() + "/* optional */" + JavaEncoder.newline);
                  }

                  text.append(JavaEncoder.this.tab() + "while (" + found + ")" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
                  text.append(JavaEncoder.this.tab() + "parsed = " + count + " >= " + atLeast + ";" + JavaEncoder.newline);
               }

               if(JavaEncoder.this.annotate) {
                  text.append(JavaEncoder.this.tab() + "/* repetition - end */" + JavaEncoder.newline);
               }

               return null;
            }

            public Object visit(Repeat repeat, Object argument) {
               return null;
            }

            public Object visit(Rulename rulename, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(JavaEncoder.this.tab() + "Rule rule = " + "Rule_" + JavaEncoder.this.grammar.getRule(rulename.spelling).rulename.spelling.replace('-', '_') + ".parse(context);" + JavaEncoder.newline + JavaEncoder.this.tab() + "if ((f" + JavaEncoder.this.alternationLevel + " = rule != null))" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.tab() + "a" + JavaEncoder.this.alternationLevel + ".add(rule, context.index);" + JavaEncoder.newline + JavaEncoder.this.tab() + "c" + JavaEncoder.this.alternationLevel + "++;" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               return null;
            }

            public Object visit(Group group, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               if(JavaEncoder.this.annotate) {
                  text.append(JavaEncoder.this.tab() + "/* group - start */" + JavaEncoder.newline);
               }

               text.append(JavaEncoder.this.tab() + "int g" + JavaEncoder.this.alternationLevel + " = context.index;" + JavaEncoder.newline);
               group.alternation.accept(this, argument);
               text.append(JavaEncoder.newline + JavaEncoder.this.tab() + "f" + JavaEncoder.this.alternationLevel + " = context.index > g" + JavaEncoder.this.alternationLevel + ";" + JavaEncoder.newline + JavaEncoder.this.tab() + "if (parsed) c" + JavaEncoder.this.alternationLevel + "++;" + JavaEncoder.newline);
               if(JavaEncoder.this.annotate) {
                  text.append(JavaEncoder.this.tab() + "/* group - end */" + JavaEncoder.newline);
               }

               return null;
            }

            public Object visit(StringValue stringValue, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(JavaEncoder.this.tab() + "Rule rule = " + "Terminal_" + "StringValue.parse(context, \"" + stringValue.regex.replace("\\", "\\\\") + "\");" + JavaEncoder.newline + JavaEncoder.this.tab() + "if ((f" + JavaEncoder.this.alternationLevel + " = rule != null))" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.tab() + "a" + JavaEncoder.this.alternationLevel + ".add(rule, context.index);" + JavaEncoder.newline + JavaEncoder.this.tab() + "c" + JavaEncoder.this.alternationLevel + "++;" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               return null;
            }

            public Object visit(NumericValue numericValue, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(JavaEncoder.this.tab() + "Rule rule = " + "Terminal_" + "NumericValue.parse(context, \"" + numericValue.spelling + "\", \"" + numericValue.regex + "\", " + numericValue.length + ");" + JavaEncoder.newline + JavaEncoder.this.tab() + "if ((f" + JavaEncoder.this.alternationLevel + " = rule != null))" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.tab() + "a" + JavaEncoder.this.alternationLevel + ".add(rule, context.index);" + JavaEncoder.newline + JavaEncoder.this.tab() + "c" + JavaEncoder.this.alternationLevel + "++;" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               return null;
            }

            public Object visit(Terminal terminal, Object argument) {
               return null;
            }

            public Object visit(ExternalRule rule, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(JavaEncoder.this.tab() + "Rule rule = " + rule.spelling + ".parse(context);" + JavaEncoder.newline + JavaEncoder.this.tab() + "if ((f" + JavaEncoder.this.alternationLevel + " = rule != null))" + JavaEncoder.newline + JavaEncoder.this.inc() + "{" + JavaEncoder.newline + JavaEncoder.this.tab() + "a" + JavaEncoder.this.alternationLevel + ".add(rule, context.index);" + JavaEncoder.newline + JavaEncoder.this.tab() + "c" + JavaEncoder.this.alternationLevel + "++;" + JavaEncoder.newline + JavaEncoder.this.dec() + "}" + JavaEncoder.newline);
               return null;
            }
         }

         rule.accept(new RuleVisitor(), text);
         rulename = rule.rulename.spelling.replace('-', '_');
         out = new FileOutputStream(destDir + "Rule_" + rulename + ".java");
         out.write(text.toString().getBytes());
         out.close();
      }

      this.createStringValueClass(destDir);
      this.createNumericValueClass(destDir);
   }

   private void createStringValueClass(String destDir) throws IOException {
      boolean javadocs = false;
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Terminal_StringValue.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline + this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      text.append(newline + this.tab() + "import java.util.ArrayList;" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The terminal <code>StringValue</code> rule." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public class " + "Terminal_" + "StringValue extends Rule" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "private " + "Terminal_" + "StringValue(String spelling, ArrayList<Rule> rules)" + newline + this.inc() + "{" + newline + this.tab() + "super(spelling, rules);" + newline + this.dec() + "}" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The <code>StringValue</code> parser." + newline + this.tab() + " *" + newline + this.tab() + " * @param context The parser context." + newline + this.tab() + " * @param regex The regular expression that will be used to decode" + newline + this.tab() + " * the string value." + newline + this.tab() + " * @return An instance of the <code>StringValue</code> rule or " + this.tab() + " * <code>null</code> if the parse failed." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public static " + "Terminal_" + "StringValue parse(" + newline + this.add() + "ParserContext context, " + newline + this.add() + "String regex)" + newline + this.inc() + "{" + newline + this.tab() + "context.push(\"StringValue\", regex);" + newline + newline + this.tab() + "boolean parsed = true;" + newline + newline + this.tab() + "Terminal_" + "StringValue stringValue = null;" + newline + this.tab() + "try" + newline + this.inc() + "{" + newline + this.tab() + "String value = " + newline + this.add() + "context.text.substring(" + newline + this.add() + "  context.index, " + newline + this.add() + "  context.index + regex.length());" + newline + newline + this.tab() + "if ((parsed = value.equalsIgnoreCase(regex)))" + newline + this.inc() + "{" + newline + this.tab() + "context.index += regex.length();" + newline + this.tab() + "stringValue = new " + "Terminal_" + "StringValue(value, null);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "catch (IndexOutOfBoundsException e) {parsed = false;}" + newline + newline + this.tab() + "context.pop(\"StringValue\", parsed);" + newline + newline + this.tab() + "return stringValue;" + newline + this.dec() + "}" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The visit method that passes this <code>StringValue</code> to the " + newline + this.tab() + " * specified visitor." + newline + this.tab() + " *" + newline + this.tab() + " * @param visitor The visitor to which this <code>StringValue</code> " + newline + this.tab() + " * rule is passed." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public Object accept(Visitor visitor)" + newline + this.inc() + "{" + newline + this.tab() + "return visitor.visit(this);" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}");
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(destDir + "Terminal_" + "StringValue.java");
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createNumericValueClass(String destDir) throws IOException {
      boolean javadocs = false;
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Terminal_NumericValue.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline + this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      text.append(newline + this.tab() + "import java.util.ArrayList;" + newline + this.tab() + "import java.util.regex.Pattern;" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The terminal <code>NumericValue</code> rule." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public class " + "Terminal_" + "NumericValue extends Rule" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "private " + "Terminal_" + "NumericValue(String spelling, ArrayList<Rule> rules)" + newline + this.inc() + "{" + newline + this.tab() + "super(spelling, rules);" + newline + this.dec() + "}" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The <code>NumericValue</code> parser." + newline + this.tab() + " *" + newline + this.tab() + " * @param context The parser context." + newline + this.tab() + " * @param spelling The original ABNF definition for this numeric value." + newline + this.tab() + " * @param regex The regular expression that will be used to decode" + newline + this.tab() + " * the numeric value." + newline + this.tab() + " * @param length The number of characters in the source string to be " + newline + this.tab() + " * checked against the specified regular expression." + newline + this.tab() + " * @return An instance of the <code>NumericValue</code> rule or " + newline + this.tab() + " * <code>null</code> if the parse failed." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public static " + "Terminal_" + "NumericValue parse(" + newline + this.add() + "ParserContext context, " + newline + this.add() + "String spelling, " + newline + this.add() + "String regex, " + newline + this.add() + "int length)" + newline + this.inc() + "{" + newline + this.tab() + "context.push(\"NumericValue\", spelling + \",\" + regex);" + newline + newline + this.tab() + "boolean parsed = true;" + newline + newline + this.tab() + "Terminal_" + "NumericValue numericValue = null;" + newline + this.tab() + "try" + newline + this.inc() + "{" + newline + this.tab() + "String value = " + newline + this.add() + "context.text.substring(" + newline + this.add() + "  context.index, " + newline + this.add() + "  context.index + length);" + newline + newline + this.tab() + "if ((parsed = Pattern.matches(regex, value)))" + newline + this.inc() + "{" + newline + this.tab() + "context.index += length;" + newline + this.tab() + "numericValue = new " + "Terminal_" + "NumericValue(value, null);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "catch (IndexOutOfBoundsException e) {parsed = false;}" + newline + newline + this.tab() + "context.pop(\"NumericValue\", parsed);" + newline + newline + this.tab() + "return numericValue;" + newline + this.dec() + "}" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The visit method that passes this <code>NumericValue</code> to the " + newline + this.tab() + " * specified visitor." + newline + this.tab() + " *" + newline + this.tab() + " * @param visitor The visitor to which this <code>NumericValue</code>" + newline + this.tab() + " * rule is passed." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public Object accept(Visitor visitor)" + newline + this.inc() + "{" + newline + this.tab() + "return visitor.visit(this);" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}");
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(destDir + "Terminal_" + "NumericValue.java");
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createParserExceptionClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserException.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline + this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      text.append(newline + this.tab() + "import java.util.Stack;" + newline);
      text.append(newline + this.tab() + "/**" + newline + this.tab() + " * <p>Signals that a parse failure has occurred.</p>" + newline + this.tab() + " * " + newline + this.tab() + " * <p>Producer : " + this.producedBy + "<br/>" + newline + this.tab() + " * Produced : " + this.producedAt.toString() + "</p>" + newline + this.tab() + " */" + newline);
      text.append(newline + this.tab() + "public class ParserException extends Exception" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "private String reason;" + newline + this.tab() + "private String text60;" + newline + this.tab() + "private int index60;" + newline + this.tab() + "private Stack<String> ruleStack;" + newline);
      text.append(newline + this.tab() + "static final private String newline = System.getProperty(\"line.separator\", \"\\n\");" + newline);
      text.append(newline + this.tab() + "/**" + newline + this.tab() + " * Creates a parser exception from the specified parse failure information." + newline + this.tab() + " *" + newline + this.tab() + " * @param reason A description of the parse failure." + newline + this.tab() + " * @param text The string of characters being parsed." + newline + this.tab() + " * @param index The index to the character at which the parse failure occurred." + newline + this.tab() + " * @param ruleStack The ABNF rule stack at the point the parse failure occurred." + newline + this.tab() + " */" + newline);
      text.append(newline + this.tab() + "public ParserException(" + newline + this.tab() + this.tab() + "String reason," + newline + this.tab() + this.tab() + "String text," + newline + this.tab() + this.tab() + "int index," + newline + this.tab() + this.tab() + "Stack<String> ruleStack)" + newline + this.inc() + "{" + newline + this.tab() + "this.reason = reason;" + newline + this.tab() + "this.ruleStack = ruleStack;" + newline + newline + this.tab() + "int start = (index < 30) ? 0: index - 30;" + newline + this.tab() + "int end = (text.length() < index + 30) ? text.length(): index + 30;" + newline + this.tab() + "text60 = text.substring(start, end).replaceAll(\"[\\\\x00-\\\\x1F]\", \" \");" + newline + this.tab() + "index60 = (index < 30) ? index : 30;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "/**" + newline + this.tab() + " * Returns the description of the parse failure." + newline + this.tab() + " *" + newline + this.tab() + " * @return The description of the parse failure." + newline + this.tab() + " */" + newline);
      text.append(newline + this.tab() + "public String getReason()" + newline + this.inc() + "{" + newline + this.tab() + "return reason;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "/**" + newline + this.tab() + " * Returns a substring of the parsed string that encompasses the point " + newline + this.tab() + " * at which the parse failure occurred. The substring will be up to 60 " + newline + this.tab() + " * characters in length unless the point of failure occurred within " + newline + this.tab() + " * 30 characters of the start or end of the parsed string. " + newline + this.tab() + " * {@link #getSubstringIndex} returns an index to the character within " + newline + this.tab() + " * this substring at which the parse failure occurred. This substring " + newline + this.tab() + " * may contain non-printable characters." + newline + this.tab() + " *" + newline + this.tab() + " * @return The substring that encompasses the point of failure." + newline + this.tab() + " */" + newline);
      text.append(newline + this.tab() + "public String getSubstring()" + newline + this.inc() + "{" + newline + this.tab() + "return text60;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "/**" + newline + this.tab() + " * Returns an index to the character within the substring returned by " + newline + this.tab() + " * {@link #getSubstring} at which the parse failure occurred. " + newline + this.tab() + " *" + newline + this.tab() + " * @return The index to the character within the substring returned " + newline + this.tab() + " * {@link #getSubstring} at which the parse failure occurred. " + newline + this.tab() + " */" + newline);
      text.append(newline + this.tab() + "public int getSubstringIndex()" + newline + this.inc() + "{" + newline + this.tab() + "return index60;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "/**" + newline + this.tab() + " * Returns the ABNF rule stack at the point the parse failure occurred." + newline + this.tab() + " *" + newline + this.tab() + " * @return The ABNF rule stack." + newline + this.tab() + " */" + newline);
      text.append(newline + this.tab() + "public Stack<String> getRuleStack()" + newline + this.inc() + "{" + newline + this.tab() + "return ruleStack;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "/**" + newline + this.tab() + " * Returns a message detailing the parse failure. The message detail" + newline + this.tab() + " * the reason for the failure and where the failure occurred." + newline + this.tab() + " *" + newline + this.tab() + " * <br><br>For example ...<br><br><code>" + newline + this.tab() + " * rule \"Minutes\" failed<br>" + newline + this.tab() + " * 15:75:47<br>" + newline + this.tab() + " * &nbsp;&nbsp;&nbsp;^<br>" + newline + this.tab() + " * rule stack:<br>" + newline + this.tab() + " * &nbsp;&nbsp;Clock<br>" + newline + this.tab() + " * &nbsp;&nbsp;Minutes</code><br>" + newline + this.tab() + " *" + newline + this.tab() + " * @return Details of the parse failure." + newline + this.tab() + " */" + newline);
      text.append(newline + this.tab() + "public String getMessage()" + newline + this.inc() + "{" + newline + this.tab() + "String marker = \"                              \";" + newline + newline + this.tab() + "StringBuffer buffer = new StringBuffer();" + newline + this.tab() + "buffer.append(reason + newline);" + newline + this.tab() + "buffer.append(text60 + newline);" + newline + this.tab() + "buffer.append(marker.substring(0, index60) + \"^\" + newline);" + newline);
      text.append(newline + this.tab() + "if (!ruleStack.empty())" + newline + this.inc() + "{" + newline + this.tab() + "buffer.append(\"rule stack:\");" + newline + newline + this.tab() + "for (String rule : ruleStack)" + newline + this.add() + "buffer.append(newline + \"  \" + rule);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "ParserException secondaryError = (ParserException)getCause();" + newline + this.tab() + "if (secondaryError != null)" + newline + this.inc() + "{" + newline + this.tab() + "buffer.append(\"possible cause: \" + secondaryError.reason + newline);" + newline + this.tab() + "buffer.append(secondaryError.text60 + newline);" + newline + this.tab() + "buffer.append(marker.substring(0, secondaryError.index60) + \"^\" + newline);" + newline);
      text.append(newline + this.tab() + "if (!secondaryError.ruleStack.empty())" + newline + this.inc() + "{" + newline + this.tab() + "buffer.append(\"rule stack:\");" + newline + newline + this.tab() + "for (String rule : secondaryError.ruleStack)" + newline + this.add() + "buffer.append(newline + \"  \" + rule);" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
      text.append(newline + this.tab() + "return buffer.toString();" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
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
      boolean javadocs = false;
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Displayer.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline + this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      text.append(newline + this.tab() + "import java.util.ArrayList;" + newline);
      if(javadocs) {
         text.append(newline + this.tab() + "/**" + newline + this.tab() + " * A visitor that displays the terminal values of a <code>" + this.grammar.primaryRule.rulename.spelling + "</code> " + newline + this.tab() + " * rule tree." + newline + this.tab() + " */" + newline);
      }

      text.append(newline + this.tab() + "public class Displayer implements Visitor" + newline + this.inc() + "{" + newline);

      Iterator out;
      String rulename;
      for(out = this.grammar.rules.iterator(); out.hasNext(); text.append(newline + this.tab() + "public Object visit(" + "Rule_" + rulename + " rule)" + newline + this.inc() + "{" + newline + this.tab() + "return visitRules(rule.rules);" + newline + this.dec() + "}" + newline)) {
         Rule rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         if(javadocs) {
            text.append(newline + this.tab() + "/**" + newline + this.tab() + " * The <code>" + rulename + "</code> visitor." + newline + newline + this.tab() + " * @param rule The <code>" + rulename + "<code> rule to process." + newline + this.tab() + " */" + newline);
         }
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         ExternalRule rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(newline + this.tab() + "public Object visit(" + rulename + " rule)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.print(rule.spelling);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      }

      text.append(newline + this.tab() + "public Object visit(" + "Terminal_" + "StringValue value)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.print(value.spelling);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public Object visit(" + "Terminal_" + "NumericValue value)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.print(value.spelling);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "private Object visitRules(ArrayList<Rule> rules)" + newline + this.inc() + "{" + newline + this.tab() + "for (Rule rule : rules)" + newline + this.add() + "rule.accept(this);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
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
      text.append(" * XmlDisplayer.java" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      if(this.javaPackage != null) {
         text.append(newline + this.tab() + "package " + this.javaPackage + ";" + newline);
      }

      text.append(newline + this.tab() + "import java.util.ArrayList;" + newline);
      text.append(newline + this.tab() + "public class XmlDisplayer implements Visitor" + newline + this.inc() + "{" + newline + this.tab() + "private boolean terminal = true;" + newline);
      Iterator out = this.grammar.rules.iterator();

      String rulename;
      while(out.hasNext()) {
         Rule rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(newline + this.tab() + "public Object visit(" + "Rule_" + rulename + " rule)" + newline + this.inc() + "{" + newline + this.tab() + "if (!terminal) System.out.println();" + newline + this.tab() + "System.out.print(\"<" + rule.rulename.spelling + ">\");" + newline + this.tab() + "terminal = false;" + newline + this.tab() + "visitRules(rule.rules);" + newline + this.tab() + "if (!terminal) System.out.println();" + newline + this.tab() + "System.out.print(\"</" + rule.rulename.spelling + ">\");" + newline + this.tab() + "terminal = false;" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         ExternalRule rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(newline + this.tab() + "public Object visit(" + rulename + " rule)" + newline + this.inc() + "{" + newline + this.tab() + "if (!terminal) System.out.println();" + newline + this.tab() + "System.out.print(\"<" + rulename + ">\");" + newline + this.tab() + "System.out.print(rule.spelling);" + newline + this.tab() + "System.out.print(\"</" + rulename + ">\");" + newline + this.tab() + "terminal = false;" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      }

      text.append(newline + this.tab() + "public Object visit(" + "Terminal_" + "StringValue value)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.print(value.spelling);" + newline + this.tab() + "terminal = true;" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "public Object visit(" + "Terminal_" + "NumericValue value)" + newline + this.inc() + "{" + newline + this.tab() + "System.out.print(value.spelling);" + newline + this.tab() + "terminal = true;" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "private Boolean visitRules(ArrayList<Rule> rules)" + newline + this.inc() + "{" + newline + this.tab() + "for (Rule rule : rules)" + newline + this.add() + "rule.accept(this);" + newline + this.tab() + "return null;" + newline + this.dec() + "}" + newline);
      text.append(this.dec() + "}" + newline);
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
   static int access$808(JavaEncoder x0) {
      return x0.alternationLevel++;
   }

   // $FF: synthetic method
   static int access$810(JavaEncoder x0) {
      return x0.alternationLevel--;
   }
}
