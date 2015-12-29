package io.github.qyou;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

final class CppEncoder implements Encoder {
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

      if(arguments.getProperty("Main").equals("Yes")) {
         this.createParserMain(destDir + "ParserMain.cpp", arguments.getProperty("Visitors"));
      }

      this.createParserHeader(destDir + "Parser.hpp");
      this.createParserClass(destDir + "Parser.cpp");
      this.createParserContextHeader(destDir + "ParserContext.hpp");
      this.createParserContextClass(destDir + "ParserContext.cpp");
      this.createParserAlternativeHeader(destDir + "ParserAlternative.hpp");
      this.createParserAlternativeClass(destDir + "ParserAlternative.cpp");
      this.createVisitorHeader(destDir + "Visitor.hpp");
      this.createRuleHeader(destDir + "Rule.hpp");
      this.createRuleClass(destDir + "Rule.cpp");
      this.createRuleHeaders(destDir);
      this.createRuleClasses(destDir);
      this.createParserExceptionHeader(destDir + "ParserException.hpp");
      this.createParserExceptionClass(destDir + "ParserException.cpp");
      this.createDisplayerHeader(destDir + "Displayer.hpp");
      this.createDisplayerClass(destDir + "Displayer.cpp");
      this.createXmlDisplayerHeader(destDir + "XmlDisplayer.hpp");
      this.createXmlDisplayerClass(destDir + "XmlDisplayer.cpp");
   }

   private void createParserMain(String filename, String visitors) throws IOException {
      String primaryRulename = this.grammar.primaryRule.rulename.spelling;
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserMain.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "using std::string;" + newline + newline + this.tab() + "#include <iostream>" + newline + this.tab() + "using std::cout;" + newline + this.tab() + "using std::endl;" + newline + this.tab() + "#include <fstream>" + newline + this.tab() + "using std::ifstream;" + newline + newline + this.tab() + "#include <map>" + newline + this.tab() + "using std::map;" + newline + newline + this.tab() + "#include <exception>" + newline + this.tab() + "using std::exception;" + newline + newline + this.tab() + "#include <algorithm>" + newline + this.tab() + "using std::transform;" + newline);
      text.append(newline + this.tab() + "#include \"Parser.hpp\"" + newline + this.tab() + "#include \"ParserContext.hpp\"" + newline + this.tab() + "#include \"ParserException.hpp\"" + newline + this.tab() + "#include \"Rule.hpp\"" + newline);
      if(visitors != null) {
         text.append(newline);
         StringTokenizer out = new StringTokenizer(visitors, ";,");

         while(out.hasMoreElements()) {
            String tokenizer = out.nextToken();
            if(tokenizer.length() > 0) {
               text.append(this.tab() + "#include \"" + tokenizer + ".hpp\"" + newline);
            }
         }
      }

      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      text.append(newline + this.tab() + "int main(int argc, char* argv[])" + newline + this.inc() + "{" + newline + this.tab() + "string* lc_argv = new string[argc];" + newline + this.tab() + "for (int i = 0; i < argc; i++)" + newline + this.inc() + "{" + newline + this.tab() + "lc_argv[i] = argv[i];" + newline + this.tab() + "transform(lc_argv[i].begin(), lc_argv[i].end(), lc_argv[i].begin(), tolower);" + newline + this.dec() + "}" + newline + newline + this.tab() + "map<string, string> arguments;" + newline + this.tab() + "string error(\"\");" + newline + this.tab() + "bool ok = argc > 1;" + newline + newline + this.tab() + "if (ok)" + newline + this.inc() + "{" + newline + this.tab() + "arguments[\"Trace\"] = \"Off\";" + newline + this.tab() + "arguments[\"Rule\"] = \"" + primaryRulename + "\";" + newline + newline + this.tab() + "for (int i = 1; i < argc; i++)" + newline + this.inc() + "{" + newline + this.tab() + "if (lc_argv[i].compare(\"-trace\") == 0)" + newline + this.inc() + "{" + newline + this.tab() + "arguments[\"Trace\"] = \"On\";" + newline + this.dec() + "}" + newline + this.tab() + "else if (lc_argv[i].compare(\"-visitor\") == 0)" + newline + this.inc() + "{" + newline + this.tab() + "if (i < argc - 1) arguments[\"Visitor\"] = argv[++i];" + newline + this.dec() + "}" + newline + this.tab() + "else if (lc_argv[i].compare(\"-file\") == 0)" + newline + this.inc() + "{" + newline + this.tab() + "if (i < argc - 1) arguments[\"File\"] = argv[++i];" + newline + this.dec() + "}" + newline + this.tab() + "else if (lc_argv[i].compare(\"-string\") == 0)" + newline + this.inc() + "{" + newline + this.tab() + "if (i < argc - 1) arguments[\"String\"] = argv[++i];" + newline + this.dec() + "}" + newline + this.tab() + "else if (lc_argv[i].compare(\"-rule\") == 0)" + newline + this.inc() + "{" + newline + this.tab() + "if (i < argc - 1) arguments[\"Rule\"] = argv[++i];" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "error = string(\"unknown argument: \") + argv[i];" + newline + this.tab() + "ok = false;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + newline + this.tab() + "delete[] lc_argv;" + newline + newline + this.tab() + "if (ok)" + newline + this.inc() + "{" + newline + this.tab() + "if (arguments.find(\"File\") == arguments.end() &&" + newline + this.tab() + "    arguments.find(\"String\") == arguments.end())" + newline + this.inc() + "{" + newline + this.tab() + "error = \"insufficient arguments: -file or -string required\";" + newline + this.tab() + "ok = false;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + newline + this.tab() + "const Rule* rule = NULL;" + newline + newline + this.tab() + "if (!ok)" + newline + this.inc() + "{" + newline + this.tab() + "cout << \"error: \" << error << endl;" + newline + this.tab() + "cout << \"usage: Parser [-rule rulename] [-trace] <-file file | -string string> [-visitor visitor]\" << endl;" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "try" + newline + this.inc() + "{" + newline + this.tab() + "if (arguments.find(\"File\") != arguments.end())" + newline + this.inc() + "{" + newline + this.tab() + "ifstream file(arguments[\"File\"].c_str());" + newline + this.tab() + "if (!file.is_open())" + newline + this.inc() + "{" + newline + this.tab() + "cout << \"io error: unable to open \" << arguments[\"File\"] << endl;" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "rule = " + newline + this.add() + "Parser::parse(" + newline + this.add() + "  arguments[\"Rule\"], " + newline + this.add() + "  file, " + newline + this.add() + "  arguments[\"Trace\"].compare(\"On\") == 0);" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "else if (arguments.find(\"String\") != arguments.end())" + newline + this.inc() + "{" + newline + this.tab() + "rule = " + newline + this.add() + "Parser::parse(" + newline + this.add() + "  arguments[\"Rule\"], " + newline + this.add() + "  arguments[\"String\"], " + newline + this.add() + "  arguments[\"Trace\"].compare(\"On\") == 0);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "if (arguments.find(\"Visitor\") != arguments.end())" + newline + this.inc() + "{" + newline + this.tab() + "string argument = arguments[\"Visitor\"];" + newline + newline);
      if(visitors != null) {
         boolean out1 = true;
         StringTokenizer tokenizer1 = new StringTokenizer(visitors, ";,");

         while(tokenizer1.hasMoreElements()) {
            String visitor = tokenizer1.nextToken();
            if(visitor.length() > 0) {
               text.append(this.tab());
               if(!out1) {
                  text.append("else ");
               }

               text.append("if (argument.compare(\"" + visitor + "\") == 0)" + newline + this.inc() + "{" + newline + this.tab() + visitor + " visitor;" + newline + this.tab() + "rule->accept(visitor);" + newline + this.tab() + "cout << endl;" + newline + this.dec() + "}" + newline);
               out1 = false;
            }
         }
      }

      text.append(this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "catch (ParserException& e)" + newline + this.inc() + "{" + newline + this.tab() + "cout << \"parser error: \" << e.what();" + newline + this.dec() + "}" + newline + this.tab() + "catch (exception& e)" + newline + this.inc() + "{" + newline + this.tab() + "cout << \"error: \" << e.what() << endl;" + newline + this.dec() + "}" + newline + this.tab() + "catch (...)" + newline + this.inc() + "{" + newline + this.tab() + "cout << \"unknown error\" << endl;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (rule != NULL) delete rule;" + newline + newline + this.tab() + "return rule != NULL ? 0 : 1;" + newline + this.dec() + "}" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out2 = new FileOutputStream(filename);
      out2.write(text.toString().getBytes());
      out2.close();
   }

   private void createParserHeader(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Parser.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef Parser_hpp" + newline + "#define Parser_hpp" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "#include <iostream>" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "class Rule;" + newline);
      text.append(newline + this.tab() + "class Parser" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      text.append(this.tab() + "static const Rule* parse(const std::string& rulename, const std::string& text);" + newline + this.tab() + "static const Rule* parse(const std::string& rulename, std::istream& in);" + newline + newline + this.tab() + "static const Rule* parse(const std::string& rulename, const std::string& text, bool trace);" + newline + this.tab() + "static const Rule* parse(const std::string& rulename, std::istream& in, bool trace);" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createParserClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Parser.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "using std::string;" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline + this.tab() + "using std::vector;" + newline);
      text.append(newline + this.tab() + "#include <map>" + newline + this.tab() + "using std::map;" + newline);
      text.append(newline + this.tab() + "#include <iostream>" + newline + this.tab() + "using std::istream;" + newline);
      text.append(newline + this.tab() + "#include <algorithm>" + newline + this.tab() + "using std::transform;" + newline);
      text.append(newline + this.tab() + "#include \"Parser.hpp\"" + newline + newline + this.tab() + "#include \"ParserContext.hpp\"" + newline + this.tab() + "#include \"ParserException.hpp\"" + newline);
      text.append(newline + this.tab() + "#include \"Rule.hpp\"" + newline);
      Iterator out = this.grammar.rules.iterator();

      Rule rule;
      while(out.hasNext()) {
         rule = (Rule)out.next();
         text.append(this.tab() + "#include \"" + "Rule_" + rule.rulename.spelling.replace('-', '_') + ".hpp\"" + newline);
      }

      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      text.append(newline + this.tab() + "typedef const Rule* (*pParser)(ParserContext&);" + newline);
      text.append(newline + this.tab() + "static map<string, pParser> buildParserMap(void)" + newline + this.inc() + "{" + newline + this.tab() + "map<string, pParser> parsers;" + newline + newline);
      out = this.grammar.rules.iterator();

      while(out.hasNext()) {
         rule = (Rule)out.next();
         text.append(this.tab() + "parsers[\"" + rule.rulename.spelling.replace('-', '_').toLowerCase() + "\"] = (pParser)" + "Rule_" + rule.rulename.spelling.replace('-', '_') + "::parse;" + newline);
      }

      text.append(newline + this.tab() + "return parsers;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "static map<string, pParser> parsers = buildParserMap();" + newline);
      text.append(newline + this.tab() + "const Rule* Parser::parse(const string& rulename, const string& text)" + newline + this.inc() + "{" + newline + this.tab() + "return parse(rulename, text, false);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const Rule* Parser::parse(const string& rulename, istream& in)" + newline + this.inc() + "{" + newline + this.tab() + "return parse(rulename, in, false);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const Rule* Parser::parse(const string& rulename, const string& text, bool trace)" + newline + this.inc() + "{" + newline);
      text.append(this.tab() + "string lcRulename(rulename);" + newline + this.tab() + "transform(lcRulename.begin(), lcRulename.end(), lcRulename.begin(), tolower);" + newline);
      text.append(newline + this.tab() + "ParserContext context(text, trace);" + newline);
      text.append(newline + this.tab() + "const Rule* rule = NULL;" + newline);
      text.append(newline + this.tab() + "pParser parser = parsers[lcRulename];" + newline);
      text.append(newline + this.tab() + "if (parser != NULL)" + newline + this.inc() + "{" + newline + this.tab() + "rule = parser(context);" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "throw ParserException(" + newline + this.add() + "string(\"unknown rule \\\"\") + rulename + \"\\\"\"," + newline + this.add() + "context.text," + newline + this.add() + "context.getErrorIndex()," + newline + this.add() + "context.getErrorStack());" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "if (rule == NULL)" + newline + this.inc() + "{" + newline + this.tab() + "throw ParserException(" + newline + this.add() + "string(\"rule \\\"\") + context.getErrorStack().back() + \"\\\" failed\"," + newline + this.add() + "context.text," + newline + this.add() + "context.getErrorIndex()," + newline + this.add() + "context.getErrorStack());" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (context.text.length() > context.index)" + newline + this.inc() + "{" + newline + this.tab() + "ParserException primaryError(" + newline + this.add() + "\"extra data found\"," + newline + this.add() + "context.text," + newline + this.add() + "context.index," + newline + this.add() + "vector<string>());" + newline + newline + this.tab() + "if (context.getErrorIndex() > context.index)" + newline + this.inc() + "{" + newline + this.tab() + "ParserException secondaryError(" + newline + this.add() + "\"rule \\\"\" + context.getErrorStack().back() + \"\\\" failed\"," + newline + this.add() + "context.text," + newline + this.add() + "context.getErrorIndex()," + newline + this.add() + "context.getErrorStack());" + newline + newline + this.tab() + "primaryError.setCause(secondaryError);" + newline + this.dec() + "}" + newline + newline + this.tab() + "throw primaryError;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "return rule;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const Rule* Parser::parse(const string& rulename, istream& in, bool trace)" + newline + this.inc() + "{" + newline + this.tab() + "string out;" + newline + this.tab() + "int ch;" + newline + newline + this.tab() + "while ((ch = in.get()) != EOF)" + newline + this.add() + "out += ch;" + newline + newline + this.tab() + "return parse(rulename, out, trace);" + newline + this.dec() + "}" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out1 = new FileOutputStream(filename);
      out1.write(text.toString().getBytes());
      out1.close();
   }

   private void createParserContextHeader(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserContext.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef ParserContext_hpp" + newline + "#define ParserContext_hpp" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "#include <vector>" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "class ParserContext" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      text.append(this.tab() + "const std::string text;" + newline + this.tab() + "unsigned int index;" + newline);
      text.append(newline + this.tab() + "ParserContext(const std::string& text, bool traceOn);" + newline + this.tab() + "~ParserContext();" + newline);
      text.append(newline + this.tab() + "void push(const std::string& rulename);" + newline + this.tab() + "void push(const std::string& rulename, const std::string& trace);" + newline + this.tab() + "void pop(const std::string& function, bool result);" + newline + this.tab() + "const std::vector<std::string>& getErrorStack(void) const;" + newline + this.tab() + "unsigned int getErrorIndex(void) const;" + newline);
      text.append(this.dec() + newline + this.inc() + "private:" + newline + this.tab() + "std::vector<unsigned int> startStack;" + newline + this.tab() + "std::vector<std::string> callStack;" + newline + this.tab() + "std::vector<std::string> errorStack;" + newline + this.tab() + "unsigned int errorIndex;" + newline + this.tab() + "unsigned int level;" + newline + newline + this.tab() + "bool traceOn;" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createParserContextClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserContext.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "using std::string;" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline + this.tab() + "using std::vector;" + newline);
      text.append(newline + this.tab() + "#include <iostream>" + newline + this.tab() + "using std::cout;" + newline + this.tab() + "using std::endl;" + newline);
      text.append(newline + this.tab() + "#include <regex>" + newline + this.tab() + "using std::regex;" + newline);
      text.append(newline + this.tab() + "#include \"ParserContext.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      text.append(newline + this.tab() + "ParserContext::ParserContext(const string& text, bool traceOn) :" + newline + this.tab() + "text(text), index(0), errorIndex(0), level(0), traceOn(traceOn)" + newline + this.inc() + "{" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "ParserContext::~ParserContext()" + newline + this.inc() + "{" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void ParserContext::push(const string& rulename)" + newline + this.inc() + "{" + newline + this.tab() + "push(rulename, \"\");" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void ParserContext::push(const string& rulename, const string& trace)" + newline + this.inc() + "{" + newline + this.tab() + "callStack.push_back(rulename);" + newline + this.tab() + "startStack.push_back(index);" + newline + newline + this.tab() + "if (traceOn)" + newline + this.inc() + "{" + newline + this.tab() + "string sample = text.substr(index, (index + 10 > text.length() ? text.length() - index : 10));" + newline + newline + this.tab() + "regex rx(\"[\\\\x00-\\\\x1F]\");" + newline + this.tab() + "sample = regex_replace(sample, rx, string(\" \"));" + newline + newline + this.tab() + "cout << \"-> \" << ++level << \": \" << rulename << \"(\" << trace << \")\" << endl;" + newline + this.tab() + "cout << index << \": \" << sample << endl;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void ParserContext::pop(const string& function, bool result)" + newline + this.inc() + "{" + newline + this.tab() + "unsigned int start = startStack.back();" + newline + this.tab() + "startStack.pop_back();" + newline + this.tab() + "callStack.pop_back();" + newline + newline + this.tab() + "if (traceOn)" + newline + this.inc() + "{" + newline + this.tab() + "cout << \"<- \" << level--" + newline + this.tab() + "     << \": \" << function" + newline + this.tab() + "     << \"(\" << (result ? \"true\" : \"false\")" + newline + this.tab() + "     << \",s=\" << start" + newline + this.tab() + "     << \",l=\" << (index - start)" + newline + this.tab() + "     << \",e=\" << errorIndex" + newline + this.tab() + "     << \")\" << endl;" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (!result)" + newline + this.inc() + "{" + newline + this.tab() + "if (index > errorIndex)" + newline + this.inc() + "{" + newline + this.tab() + "errorIndex = index;" + newline + this.tab() + "errorStack.clear();" + newline + this.tab() + "errorStack = callStack;" + newline + this.dec() + "}" + newline + this.tab() + "else if (index == errorIndex && errorStack.empty())" + newline + this.inc() + "{" + newline + this.tab() + "errorStack = callStack;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "else" + newline + this.inc() + "{" + newline + this.tab() + "if (index > errorIndex) errorIndex = 0;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const vector<string>& ParserContext::getErrorStack() const" + newline + this.inc() + "{" + newline + this.tab() + "return errorStack;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "unsigned int ParserContext::getErrorIndex() const" + newline + this.inc() + "{" + newline + this.tab() + "return errorIndex;" + newline + this.dec() + "}" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createParserAlternativeHeader(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserAlternative.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef ParserAlternative_hpp" + newline + "#define ParserAlternative_hpp" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "#include <vector>" + newline);
      text.append(newline + this.tab() + "#include \"Rule.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "class ParserAlternative" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      text.append(this.tab() + "std::vector<const Rule*> rules;" + newline + this.tab() + "unsigned int start;" + newline + this.tab() + "unsigned int end;" + newline);
      text.append(newline + this.tab() + "ParserAlternative(unsigned int start);" + newline + this.tab() + "ParserAlternative(const ParserAlternative& alternative);" + newline + newline + this.tab() + "ParserAlternative& operator=(const ParserAlternative& alternative);" + newline + newline + this.tab() + "~ParserAlternative();" + newline + newline + this.tab() + "void add(const Rule& rule, unsigned int end);" + newline + this.tab() + "void add(const std::vector<const Rule*>& rules, unsigned int end);" + newline + newline + this.tab() + "static const ParserAlternative* getBest(vector<const ParserAlternative*> alternatives);" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
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
      text.append(" * ParserAlternative.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "using std::string;" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline + this.tab() + "using std::vector;" + newline);
      text.append(newline + this.tab() + "#include \"ParserAlternative.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      text.append(newline + this.tab() + "ParserAlternative::ParserAlternative(unsigned int start) :" + newline + this.tab() + "start(start), end(start)" + newline + this.inc() + "{" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "ParserAlternative::ParserAlternative(const ParserAlternative& alternative) :" + newline + this.tab() + "start(alternative.start), end(alternative.end)" + newline + this.inc() + "{" + newline + this.tab() + "vector<const Rule*>::const_iterator r;" + newline + newline + this.tab() + "for (r = alternative.rules.begin(); r != alternative.rules.end(); r++)" + newline + this.add() + "this->rules.push_back((*r)->clone());" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "ParserAlternative& ParserAlternative::operator=(const ParserAlternative& alternative)" + newline + this.inc() + "{" + newline + this.tab() + "if (&alternative != this)" + newline + this.inc() + "{" + newline + this.tab() + "start = alternative.start;" + newline + this.tab() + "end = alternative.end;" + newline + newline + this.tab() + "vector<const Rule*>::const_iterator r;" + newline + newline + this.tab() + "for (r = rules.begin(); r != rules.end(); r++)" + newline + this.add() + "delete *r;" + newline + newline + this.tab() + "rules.empty();" + newline + newline + this.tab() + "for (r = alternative.rules.begin(); r != alternative.rules.end(); r++)" + newline + this.add() + "rules.push_back((*r)->clone());" + newline + this.dec() + "}" + newline + this.tab() + "return *this;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "ParserAlternative::~ParserAlternative()" + newline + this.inc() + "{" + newline + this.tab() + "vector<const Rule*>::const_iterator r;" + newline + newline + this.tab() + "for (r = rules.begin(); r != rules.end(); r++)" + newline + this.add() + "delete *r;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void ParserAlternative::add(const Rule& rule, unsigned int end)" + newline + this.inc() + "{" + newline + this.tab() + "rules.push_back(rule.clone());" + newline + this.tab() + "this->end = end;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void ParserAlternative::add(const std::vector<const Rule*>& rules, unsigned int end)" + newline + this.inc() + "{" + newline + this.tab() + "vector<const Rule*>::const_iterator r;" + newline + newline + this.tab() + "for (r = rules.begin(); r != rules.end(); r++)" + newline + this.add() + "this->rules.push_back((*r)->clone());" + newline + newline + this.tab() + "this->end = end;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const ParserAlternative* ParserAlternative::getBest(vector<const ParserAlternative*> alternatives)" + newline + this.inc() + "{" + newline + this.tab() + "vector<const ParserAlternative*>::const_iterator a;" + newline + newline + this.tab() + "const ParserAlternative* best = NULL;" + newline + newline + this.tab() + "for (a = alternatives.begin(); a != alternatives.end(); a++)" + newline + this.inc() + "{" + newline + this.tab() + "if (best == NULL || (*a)->end > best->end)" + newline + this.inc() + "{" + newline + this.tab() + "best = *a;" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + newline + this.tab() + "return best;" + newline + this.dec() + "}" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createVisitorHeader(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Visitor.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef visitor_hpp" + newline + "#define visitor_hpp" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline);
      Iterator out = this.grammar.rules.iterator();

      Rule rule;
      String rulename;
      while(out.hasNext()) {
         rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(this.tab() + "class " + "Rule_" + rulename + ";" + newline);
      }

      out = this.grammar.externalRules.iterator();

      ExternalRule rule1;
      while(out.hasNext()) {
         rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(this.tab() + "class " + rulename + ";" + newline);
      }

      text.append(this.tab() + "class " + "Terminal_" + "StringValue;" + newline);
      text.append(this.tab() + "class " + "Terminal_" + "NumericValue;" + newline);
      text.append(newline + this.tab() + "class Visitor" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      out = this.grammar.rules.iterator();

      while(out.hasNext()) {
         rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(this.tab() + "virtual void* visit(const " + "Rule_" + rulename + "* rule) = 0;" + newline);
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(this.tab() + "virtual void* visit(const " + rulename + "* rule) = 0;" + newline);
      }

      text.append(newline);
      text.append(this.tab() + "virtual void* visit(const " + "Terminal_" + "StringValue* value) = 0;" + newline);
      text.append(this.tab() + "virtual void* visit(const " + "Terminal_" + "NumericValue* value) = 0;" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out1 = new FileOutputStream(filename);
      out1.write(text.toString().getBytes());
      out1.close();
   }

   private void createRuleHeader(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Rule.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef Rule_hpp" + newline + "#define Rule_hpp" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "#include <vector>" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "class Visitor;" + newline);
      text.append(newline + this.tab() + "class Rule" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      text.append(this.tab() + "std::string spelling;" + newline + this.tab() + "std::vector<const Rule*> rules;" + newline);
      text.append(newline + this.tab() + "Rule(const std::string& spelling, const std::vector<const Rule*>& rules);" + newline + this.tab() + "Rule(const Rule& rule);" + newline);
      text.append(newline + this.tab() + "Rule& operator=(const Rule& rule);" + newline);
      text.append(newline + this.tab() + "virtual ~Rule();" + newline);
      text.append(newline + this.tab() + "virtual const Rule* clone(void) const = 0;" + newline);
      text.append(newline + this.tab() + "virtual void* accept(Visitor& visitor) const = 0;" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createRuleClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Rule.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "using std::string;" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline + this.tab() + "using std::vector;" + newline);
      text.append(newline + this.tab() + "#include \"Rule.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      text.append(newline + this.tab() + "Rule::Rule(const string& spelling, const vector<const Rule*>& rules) :" + newline + this.tab() + "spelling(spelling)" + newline + this.inc() + "{" + newline + this.tab() + "vector<const Rule*>::const_iterator r;" + newline + newline + this.tab() + "for (r = rules.begin(); r != rules.end(); r++)" + newline + this.add() + "this->rules.push_back((*r)->clone());" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "Rule::Rule(const Rule& rule) :" + newline + this.tab() + "spelling(rule.spelling)" + newline + this.inc() + "{" + newline + this.tab() + "vector<const Rule*>::const_iterator r;" + newline + newline + this.tab() + "for (r = rule.rules.begin(); r != rule.rules.end(); r++)" + newline + this.add() + "this->rules.push_back((*r)->clone());" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "Rule& Rule::operator=(const Rule& rule)" + newline + this.inc() + "{" + newline + this.tab() + "if (&rule != this)" + newline + this.inc() + "{" + newline + this.tab() + "spelling = rule.spelling;" + newline + newline + this.tab() + "vector<const Rule*>::const_iterator r;" + newline + newline + this.tab() + "for (r = rules.begin(); r != rules.end(); r++)" + newline + this.add() + "delete *r;" + newline + newline + this.tab() + "rules.empty();" + newline + newline + this.tab() + "for (r = rule.rules.begin(); r != rule.rules.end(); r++)" + newline + this.add() + "rules.push_back((*r)->clone());" + newline + this.dec() + "}" + newline + this.tab() + "return *this;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "Rule::~Rule()" + newline + this.inc() + "{" + newline + this.tab() + "vector<const Rule*>::const_iterator r;" + newline + newline + this.tab() + "for (r = rules.begin(); r != rules.end(); r++)" + newline + this.add() + "delete *r;" + newline + this.dec() + "}" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createRuleHeaders(String destDir) throws IOException {
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
               text.append("/* -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" * Rule_" + rulename + ".hpp" + CppEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" *" + CppEncoder.newline);
               text.append(" * Producer : " + CppEncoder.this.producedBy + CppEncoder.newline);
               text.append(" * Produced : " + CppEncoder.this.producedAt.toString() + CppEncoder.newline);
               text.append(" *" + CppEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" */" + CppEncoder.newline);
               text.append(CppEncoder.newline + "#ifndef Rule_" + rulename + "_hpp" + CppEncoder.newline + "#define Rule_" + rulename + "_hpp" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "#include <string>" + CppEncoder.newline + CppEncoder.this.tab() + "#include <vector>" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "#include \"Rule.hpp\"" + CppEncoder.newline);
               if(CppEncoder.this.namespace != null) {
                  text.append(CppEncoder.newline + "namespace " + CppEncoder.this.namespace + " {" + CppEncoder.newline);
               }

               text.append(CppEncoder.newline + CppEncoder.this.tab() + "class Visitor;" + CppEncoder.newline + CppEncoder.this.tab() + "class ParserContext;" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "class " + "Rule_" + rulename + " : public Rule" + CppEncoder.newline + CppEncoder.this.tab() + "{" + CppEncoder.newline + CppEncoder.this.inc() + "public:" + CppEncoder.newline);
               text.append(CppEncoder.this.tab() + "Rule_" + rulename + "(const std::string& spelling, const std::vector<const Rule*>& rules);" + CppEncoder.newline + CppEncoder.this.tab() + "Rule_" + rulename + "(const " + "Rule_" + rulename + "& rule);" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "Rule_" + rulename + "& operator=(const " + "Rule_" + rulename + "& rule);" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "const " + "Rule_" + rulename + "* clone(void) const;" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "static const " + "Rule_" + rulename + "* parse(ParserContext& context);" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "void* accept(Visitor& visitor) const;" + CppEncoder.newline);
               text.append(CppEncoder.this.dec() + "};" + CppEncoder.newline);
               if(CppEncoder.this.namespace != null) {
                  text.append(CppEncoder.newline + "}" + CppEncoder.newline);
               }

               text.append(CppEncoder.newline + "#endif" + CppEncoder.newline);
               text.append("/* -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" * eof" + CppEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" */" + CppEncoder.newline);
               return null;
            }

            public Object visit(Alternation alternation, Object argument) {
               return null;
            }

            public Object visit(Concatenation concatenation, Object argument) {
               return null;
            }

            public Object visit(Repetition repetition, Object argument) {
               return null;
            }

            public Object visit(Repeat repeat, Object argument) {
               return null;
            }

            public Object visit(Rulename rulename, Object argument) {
               return null;
            }

            public Object visit(Group group, Object argument) {
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

         rule.accept(new RuleVisitor(), text);
         rulename = rule.rulename.spelling.replace('-', '_');
         out = new FileOutputStream(destDir + "Rule_" + rulename + ".hpp");
         out.write(text.toString().getBytes());
         out.close();
      }

      this.createStringValueHeader(destDir);
      this.createNumericValueHeader(destDir);
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
               text.append("/* -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" * Rule_" + rulename + ".cpp" + CppEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" *" + CppEncoder.newline);
               text.append(" * Producer : " + CppEncoder.this.producedBy + CppEncoder.newline);
               text.append(" * Produced : " + CppEncoder.this.producedAt.toString() + CppEncoder.newline);
               text.append(" *" + CppEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" */" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "#include <string>" + CppEncoder.newline + CppEncoder.this.tab() + "using std::string;" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "#include <vector>" + CppEncoder.newline + CppEncoder.this.tab() + "using std::vector;" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "#include \"" + "Rule_" + rulename + ".hpp\"" + CppEncoder.newline + CppEncoder.this.tab() + "#include \"Visitor.hpp\"" + CppEncoder.newline + CppEncoder.this.tab() + "#include \"ParserAlternative.hpp\"" + CppEncoder.newline + CppEncoder.this.tab() + "#include \"ParserContext.hpp\"" + CppEncoder.newline);
               ArrayList includedRules = (ArrayList)rule.accept(new RuleVisitor.IncludeVisitor(), (Object)null);
               if(includedRules.size() > 0) {
                  text.append(CppEncoder.newline);
                  Iterator i$ = includedRules.iterator();

                  while(i$.hasNext()) {
                     String includedRule = (String)i$.next();
                     includedRule = includedRule.replace('-', '_');
                     text.append(CppEncoder.this.tab() + "#include \"" + includedRule + ".hpp\"" + CppEncoder.newline);
                  }
               }

               if(CppEncoder.this.namespace != null) {
                  text.append(CppEncoder.newline + "using namespace " + CppEncoder.this.namespace + ";" + CppEncoder.newline);
               }

               text.append(CppEncoder.newline + CppEncoder.this.tab() + "Rule_" + rulename + "::" + "Rule_" + rulename + "(" + CppEncoder.newline + CppEncoder.this.add() + "const string& spelling, " + CppEncoder.newline + CppEncoder.this.add() + "const vector<const Rule*>& rules) : Rule(spelling, rules)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "Rule_" + rulename + "::" + "Rule_" + rulename + "(const " + "Rule_" + rulename + "& rule) : Rule(rule)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "Rule_" + rulename + "& " + "Rule_" + rulename + "::operator=(const " + "Rule_" + rulename + "& rule)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "Rule::operator=(rule);" + CppEncoder.newline + CppEncoder.this.tab() + "return *this;" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "const " + "Rule_" + rulename + "* " + "Rule_" + rulename + "::clone() const" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "return new " + "Rule_" + rulename + "(this->spelling, this->rules);" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "void* " + "Rule_" + rulename + "::accept(Visitor& visitor) const" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "return visitor.visit(this);" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "const " + "Rule_" + rulename + "* " + "Rule_" + rulename + "::parse(ParserContext& context)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline);
               text.append(CppEncoder.this.tab() + "context.push(\"" + rule.rulename.spelling + "\");" + CppEncoder.newline);
               text.append(CppEncoder.newline);
               text.append(CppEncoder.this.tab() + "bool parsed = true;" + CppEncoder.newline);
               text.append(CppEncoder.this.tab() + "int s0 = context.index;" + CppEncoder.newline);
               text.append(CppEncoder.this.tab() + "ParserAlternative a0(s0);" + CppEncoder.newline);
               text.append(CppEncoder.newline);
               rule.alternation.accept(this, argument);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "const Rule* rule = NULL;" + CppEncoder.newline + CppEncoder.this.tab() + "if (parsed)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "rule = new " + "Rule_" + rulename + "(context.text.substr(a0.start, a0.end - a0.start), a0.rules);" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline + CppEncoder.this.tab() + "else" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "context.index = s0;" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "context.pop(\"" + rule.rulename.spelling + "\", parsed);" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "return (" + "Rule_" + rulename + "*)rule;" + CppEncoder.newline);
               text.append(CppEncoder.this.dec() + "}" + CppEncoder.newline);
               text.append(CppEncoder.newline);
               text.append("/* -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" * eof" + CppEncoder.newline);
               text.append(" * -----------------------------------------------------------------------------" + CppEncoder.newline);
               text.append(" */" + CppEncoder.newline);
               return null;
            }

            public Object visit(Alternation alternation, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               CppEncoder.access$808(CppEncoder.this);
               if(CppEncoder.this.annotate) {
                  text.append(CppEncoder.this.tab() + "/* alternation[" + CppEncoder.this.alternationLevel + "] - start */" + CppEncoder.newline);
               }

               text.append(CppEncoder.this.tab() + "vector<const ParserAlternative*> as" + CppEncoder.this.alternationLevel + ";" + CppEncoder.newline + CppEncoder.this.tab() + "parsed = false;" + CppEncoder.newline);

               for(int i = 0; i < alternation.concatenations.size(); ++i) {
                  ((Concatenation)alternation.concatenations.get(i)).accept(this, argument);
               }

               text.append(CppEncoder.newline + CppEncoder.this.tab() + "const ParserAlternative* b = ParserAlternative::getBest(as" + CppEncoder.this.alternationLevel + ");" + CppEncoder.newline + CppEncoder.newline + CppEncoder.this.tab() + "if ((parsed = b != NULL))" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "a" + (CppEncoder.this.alternationLevel - 1) + ".add(b->rules, b->end);" + CppEncoder.newline + CppEncoder.this.tab() + "context.index = b->end;" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "for (vector<const ParserAlternative*>::const_iterator a = as" + CppEncoder.this.alternationLevel + ".begin(); a != as" + CppEncoder.this.alternationLevel + ".end(); a++)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "delete *a;" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               if(CppEncoder.this.annotate) {
                  text.append(CppEncoder.this.tab() + "/* alternation[" + CppEncoder.this.alternationLevel + "] - end */" + CppEncoder.newline);
               }

               CppEncoder.access$810(CppEncoder.this);
               return null;
            }

            public Object visit(Concatenation concatenation, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               if(CppEncoder.this.annotate) {
                  text.append(CppEncoder.this.tab() + "/* concatenation - start */" + CppEncoder.newline);
               }

               text.append(CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "int s" + CppEncoder.this.alternationLevel + " = context.index;" + CppEncoder.newline + CppEncoder.this.tab() + "ParserAlternative a" + CppEncoder.this.alternationLevel + "(s" + CppEncoder.this.alternationLevel + ");" + CppEncoder.newline + CppEncoder.this.tab() + "parsed = true;" + CppEncoder.newline);

               for(int i = 0; i < concatenation.repetitions.size(); ++i) {
                  text.append(CppEncoder.this.tab() + "if (parsed)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline);
                  ((Repetition)concatenation.repetitions.get(i)).accept(this, argument);
                  text.append(CppEncoder.this.dec() + "}" + CppEncoder.newline);
               }

               text.append(CppEncoder.this.tab() + "if (parsed)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "as" + CppEncoder.this.alternationLevel + ".push_back(new ParserAlternative(a" + CppEncoder.this.alternationLevel + "));" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline + CppEncoder.this.tab() + "context.index = s" + CppEncoder.this.alternationLevel + ";" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               if(CppEncoder.this.annotate) {
                  text.append(CppEncoder.this.tab() + "/* concatenation - end */" + CppEncoder.newline);
               }

               return null;
            }

            public Object visit(Repetition repetition, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               String index = "i" + CppEncoder.this.alternationLevel;
               String found = "f" + CppEncoder.this.alternationLevel;
               String count = "c" + CppEncoder.this.alternationLevel;
               int atLeast = repetition.repeat.atLeast;
               int atMost = repetition.repeat.atMost;
               if(CppEncoder.this.annotate) {
                  text.append(CppEncoder.this.tab() + "/* repetition(" + atLeast + "*" + atMost + ") - start */" + CppEncoder.newline);
               }

               text.append(CppEncoder.this.tab() + "bool " + found + " = true;" + CppEncoder.newline);
               if(atLeast > 0 && atLeast == atMost) {
                  text.append(CppEncoder.this.tab() + "int " + count + " = 0;" + CppEncoder.newline);
                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* required */" + CppEncoder.newline);
                  }

                  text.append(CppEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atLeast + " && " + found + "; " + index + "++)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CppEncoder.this.dec() + "}" + CppEncoder.newline);
                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* optional - none */" + CppEncoder.newline);
                  }

                  text.append(CppEncoder.this.tab() + "parsed = " + count + " == " + atLeast + ";" + CppEncoder.newline);
               } else if(atLeast == 0 && atMost == 0) {
                  text.append(CppEncoder.this.tab() + "int " + count + " = 0;" + CppEncoder.newline);
                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* required - none */" + CppEncoder.newline);
                  }

                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* optional */" + CppEncoder.newline);
                  }

                  text.append(CppEncoder.this.tab() + "while (" + found + ")" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CppEncoder.this.dec() + "}" + CppEncoder.newline);
                  text.append(CppEncoder.this.tab() + "parsed = true;" + CppEncoder.newline);
               } else if(atLeast == 0 && atMost > 0) {
                  text.append(CppEncoder.this.tab() + "int " + count + " = 0;" + CppEncoder.newline);
                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* required - none */" + CppEncoder.newline);
                  }

                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* optional */" + CppEncoder.newline);
                  }

                  text.append(CppEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atMost + " && " + found + "; " + index + "++)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CppEncoder.this.dec() + "}" + CppEncoder.newline);
                  text.append(CppEncoder.this.tab() + "parsed = true;" + CppEncoder.newline);
               } else if(atLeast > 0 && atLeast < atMost) {
                  text.append(CppEncoder.this.tab() + "int " + count + " = 0;" + CppEncoder.newline);
                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* required */" + CppEncoder.newline);
                  }

                  text.append(CppEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atLeast + " && " + found + "; " + index + "++)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CppEncoder.this.dec() + "}" + CppEncoder.newline);
                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* optional */" + CppEncoder.newline);
                  }

                  text.append(CppEncoder.this.tab() + "for (int " + index + " = " + atLeast + "; " + index + " < " + atMost + " && " + found + "; " + index + "++)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CppEncoder.this.dec() + "}" + CppEncoder.newline);
                  text.append(CppEncoder.this.tab() + "parsed = " + count + " >= " + atLeast + ";" + CppEncoder.newline);
               } else if(atLeast > 0 && atMost == 0) {
                  text.append(CppEncoder.this.tab() + "int " + count + " = 0;" + CppEncoder.newline);
                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* required */" + CppEncoder.newline);
                  }

                  text.append(CppEncoder.this.tab() + "for (int " + index + " = 0; " + index + " < " + atLeast + " && " + found + "; " + index + "++)" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CppEncoder.this.dec() + "}" + CppEncoder.newline);
                  if(CppEncoder.this.annotate) {
                     text.append(CppEncoder.this.tab() + "/* optional */" + CppEncoder.newline);
                  }

                  text.append(CppEncoder.this.tab() + "while (" + found + ")" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline);
                  repetition.element.accept(this, argument);
                  text.append(CppEncoder.this.dec() + "}" + CppEncoder.newline);
                  text.append(CppEncoder.this.tab() + "parsed = " + count + " >= " + atLeast + ";" + CppEncoder.newline);
               }

               if(CppEncoder.this.annotate) {
                  text.append(CppEncoder.this.tab() + "/* repetition - end */" + CppEncoder.newline);
               }

               return null;
            }

            public Object visit(Repeat repeat, Object argument) {
               return null;
            }

            public Object visit(Rulename rulename, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(CppEncoder.this.tab() + "const Rule* rule = " + "Rule_" + CppEncoder.this.grammar.getRule(rulename.spelling).rulename.spelling.replace('-', '_') + "::parse(context);" + CppEncoder.newline + CppEncoder.this.tab() + "if ((f" + CppEncoder.this.alternationLevel + " = rule != NULL))" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "a" + CppEncoder.this.alternationLevel + ".add(*rule, context.index);" + CppEncoder.newline + CppEncoder.this.tab() + "c" + CppEncoder.this.alternationLevel + "++;" + CppEncoder.newline + CppEncoder.this.tab() + "delete rule;" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               return null;
            }

            public Object visit(Group group, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               if(CppEncoder.this.annotate) {
                  text.append(CppEncoder.this.tab() + "/* group - start */" + CppEncoder.newline);
               }

               text.append(CppEncoder.this.tab() + "unsigned int g" + CppEncoder.this.alternationLevel + " = context.index;" + CppEncoder.newline);
               group.alternation.accept(this, argument);
               text.append(CppEncoder.newline + CppEncoder.this.tab() + "f" + CppEncoder.this.alternationLevel + " = context.index > g" + CppEncoder.this.alternationLevel + ";" + CppEncoder.newline + CppEncoder.this.tab() + "if (parsed) c" + CppEncoder.this.alternationLevel + "++;" + CppEncoder.newline);
               if(CppEncoder.this.annotate) {
                  text.append(CppEncoder.this.tab() + "/* group - end */" + CppEncoder.newline);
               }

               return null;
            }

            public Object visit(StringValue stringValue, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(CppEncoder.this.tab() + "const Rule* rule = " + "Terminal_" + "StringValue::parse(context, \"" + stringValue.regex.replace("\\", "\\\\") + "\");" + CppEncoder.newline + CppEncoder.this.tab() + "if ((f" + CppEncoder.this.alternationLevel + " = rule != NULL))" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "a" + CppEncoder.this.alternationLevel + ".add(*rule, context.index);" + CppEncoder.newline + CppEncoder.this.tab() + "c" + CppEncoder.this.alternationLevel + "++;" + CppEncoder.newline + CppEncoder.this.tab() + "delete rule;" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               return null;
            }

            public Object visit(NumericValue numericValue, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(CppEncoder.this.tab() + "const Rule* rule = " + "Terminal_" + "NumericValue::parse(context, \"" + numericValue.spelling + "\", \"" + numericValue.regex + "\", " + numericValue.length + ");" + CppEncoder.newline + CppEncoder.this.tab() + "if ((f" + CppEncoder.this.alternationLevel + " = rule != NULL))" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "a" + CppEncoder.this.alternationLevel + ".add(*rule, context.index);" + CppEncoder.newline + CppEncoder.this.tab() + "c" + CppEncoder.this.alternationLevel + "++;" + CppEncoder.newline + CppEncoder.this.tab() + "delete rule;" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               return null;
            }

            public Object visit(Terminal terminal, Object argument) {
               return null;
            }

            public Object visit(ExternalRule rule, Object argument) {
               StringBuffer text = (StringBuffer)argument;
               text.append(CppEncoder.this.tab() + "const Rule* rule = " + rule.spelling + "::parse(context);" + CppEncoder.newline + CppEncoder.this.tab() + "if ((f" + CppEncoder.this.alternationLevel + " = rule != NULL))" + CppEncoder.newline + CppEncoder.this.inc() + "{" + CppEncoder.newline + CppEncoder.this.tab() + "a" + CppEncoder.this.alternationLevel + ".add(*rule, context.index);" + CppEncoder.newline + CppEncoder.this.tab() + "c" + CppEncoder.this.alternationLevel + "++;" + CppEncoder.newline + CppEncoder.this.tab() + "delete rule;" + CppEncoder.newline + CppEncoder.this.dec() + "}" + CppEncoder.newline);
               return null;
            }

            class IncludeVisitor implements Visitor {
               HashMap<String, String> includes = new HashMap();

               public Object visit(Grammar grammar, Object argument) {
                  return null;
               }

               public Object visit(Rule rule, Object argument) {
                  rule.alternation.accept(this, argument);
                  return new ArrayList(this.includes.values());
               }

               public Object visit(Rulename rulename, Object argument) {
                  this.includes.put("Rule_" + rulename.spelling, "Rule_" + rulename.spelling);
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

               public Object visit(Repeat repeat, Object argument) {
                  return null;
               }

               public Object visit(Group group, Object argument) {
                  group.alternation.accept(this, argument);
                  return null;
               }

               public Object visit(ExternalRule rule, Object argument) {
                  this.includes.put(rule.spelling, rule.spelling);
                  return null;
               }

               public Object visit(StringValue stringValue, Object argument) {
                  this.includes.put("Terminal_StringValue", "Terminal_StringValue");
                  return null;
               }

               public Object visit(NumericValue numericValue, Object argument) {
                  this.includes.put("Terminal_NumericValue", "Terminal_NumericValue");
                  return null;
               }

               public Object visit(Terminal terminal, Object argument) {
                  return null;
               }
            }
         }

         rule.accept(new RuleVisitor(), text);
         rulename = rule.rulename.spelling.replace('-', '_');
         out = new FileOutputStream(destDir + "Rule_" + rulename + ".cpp");
         out.write(text.toString().getBytes());
         out.close();
      }

      this.createStringValueClass(destDir);
      this.createNumericValueClass(destDir);
   }

   private void createStringValueHeader(String destDir) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Terminal_StringValue.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef Terminal_StringValue_hpp" + newline + "#define Terminal_StringValue_hpp" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "#include <vector>" + newline);
      text.append(newline + this.tab() + "#include \"Rule.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "class Visitor;" + newline + this.tab() + "class ParserContext;" + newline);
      text.append(newline + this.tab() + "class " + "Terminal_" + "StringValue : public Rule" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      text.append(this.tab() + "Terminal_" + "StringValue(" + newline + this.add() + "const std::string& spelling, " + newline + this.add() + "const std::vector<const Rule*>& rules);" + newline);
      text.append(newline + this.tab() + "const " + "Terminal_" + "StringValue* clone(void) const;" + newline);
      text.append(newline + this.tab() + "static const " + "Terminal_" + "StringValue* parse(" + newline + this.add() + "ParserContext& context," + newline + this.add() + "const std::string& pattern);" + newline);
      text.append(newline + this.tab() + "void* accept(Visitor& visitor) const ;" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(destDir + "Terminal_" + "StringValue.hpp");
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createStringValueClass(String destDir) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Terminal_StringValue.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "using std::string;" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline + this.tab() + "using std::vector;" + newline);
      text.append(newline + this.tab() + "#include \"" + "Terminal_" + "StringValue.hpp\"" + newline + this.tab() + "#include \"Visitor.hpp\"" + newline + this.tab() + "#include \"ParserContext.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      text.append(newline + this.tab() + "Terminal_" + "StringValue::" + "Terminal_" + "StringValue(" + newline + this.add() + "const string& spelling, " + newline + this.add() + "const vector<const Rule*>& rules) : Rule(spelling, rules)" + newline + this.inc() + "{" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const " + "Terminal_" + "StringValue" + "* " + "Terminal_" + "StringValue::clone(void) const" + newline + this.inc() + "{" + newline + this.tab() + "return new " + "Terminal_" + "StringValue(this->spelling, this->rules);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void* " + "Terminal_" + "StringValue::accept(Visitor& visitor) const" + newline + this.inc() + "{" + newline + this.tab() + "return visitor.visit(this);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const " + "Terminal_" + "StringValue* " + "Terminal_" + "StringValue::parse(" + newline + this.add() + "ParserContext& context," + newline + this.add() + "const string& pattern)" + newline + this.inc() + "{" + newline + this.tab() + "context.push(\"StringValue\", pattern);" + newline + newline + this.tab() + "bool parsed = false;" + newline + newline + this.tab() + "Terminal_" + "StringValue* stringValue = NULL;" + newline + newline + this.tab() + "try" + newline + this.inc() + "{" + newline + this.tab() + "if (context.index + pattern.length() <= context.text.length())" + newline + this.inc() + "{" + newline + this.tab() + "string value = context.text.substr(context.index, pattern.length());" + newline + newline + this.tab() + "parsed = value.compare(pattern) == 0;" + newline + newline + this.tab() + "if (parsed)" + newline + this.inc() + "{" + newline + this.tab() + "context.index += pattern.length();" + newline + this.tab() + "stringValue = new " + "Terminal_" + "StringValue(value, vector<const Rule*>());" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "catch (...) {}" + newline + newline + this.tab() + "context.pop(\"StringValue\", parsed);" + newline + newline + this.tab() + "return stringValue;" + newline + this.dec() + "}" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(destDir + "Terminal_" + "StringValue.cpp");
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createNumericValueHeader(String destDir) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Terminal_NumericValue.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef Terminal_NumericValue_hpp" + newline + "#define Terminal_NumericValue_hpp" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "#include <vector>" + newline);
      text.append(newline + this.tab() + "#include \"Rule.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "class Visitor;" + newline + this.tab() + "class ParserContext;" + newline);
      text.append(newline + this.tab() + "class " + "Terminal_" + "NumericValue : public Rule" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      text.append(this.tab() + "Terminal_" + "NumericValue(" + newline + this.add() + "const std::string& spelling, " + newline + this.add() + "const std::vector<const Rule*>& rules);" + newline);
      text.append(newline + this.tab() + "const " + "Terminal_" + "NumericValue* clone(void) const;" + newline);
      text.append(newline + this.tab() + "static const " + "Terminal_" + "NumericValue* parse(" + newline + this.add() + "ParserContext& context," + newline + this.add() + "const std::string& spelling," + newline + this.add() + "const std::string& pattern," + newline + this.add() + "int length);" + newline);
      text.append(newline + this.tab() + "void* accept(Visitor& visitor) const;" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(destDir + "Terminal_" + "NumericValue.hpp");
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createNumericValueClass(String destDir) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Terminal_NumericValue.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "using std::string;" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline + this.tab() + "using std::vector;" + newline);
      text.append(newline + this.tab() + "#include <regex>" + newline + this.tab() + "using std::regex;" + newline);
      text.append(newline + this.tab() + "#include \"" + "Terminal_" + "NumericValue.hpp\"" + newline + this.tab() + "#include \"Visitor.hpp\"" + newline + this.tab() + "#include \"ParserContext.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      text.append(newline + this.tab() + "Terminal_" + "NumericValue::" + "Terminal_" + "NumericValue(" + newline + this.add() + "const string& spelling, " + newline + this.add() + "const vector<const Rule*>& rules) : Rule(spelling, rules)" + newline + this.inc() + "{" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const " + "Terminal_" + "NumericValue" + "* " + "Terminal_" + "NumericValue::clone(void) const" + newline + this.inc() + "{" + newline + this.tab() + "return new " + "Terminal_" + "NumericValue(this->spelling, this->rules);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void* " + "Terminal_" + "NumericValue::accept(Visitor& visitor) const" + newline + this.inc() + "{" + newline + this.tab() + "return visitor.visit(this);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const " + "Terminal_" + "NumericValue* " + "Terminal_" + "NumericValue::parse(" + newline + this.add() + "ParserContext& context," + newline + this.add() + "const string& spelling," + newline + this.add() + "const string& pattern," + newline + this.add() + "int length)" + newline + this.inc() + "{" + newline + this.tab() + "context.push(\"NumericValue\", spelling + \",\" + pattern);" + newline + newline + this.tab() + "bool parsed = false;" + newline + newline + this.tab() + "Terminal_" + "NumericValue* numericValue = NULL;" + newline + newline + this.tab() + "try" + newline + this.inc() + "{" + newline + this.tab() + "if (context.index + length <= context.text.length())" + newline + this.inc() + "{" + newline + this.tab() + "string value = context.text.substr(context.index, length);" + newline + newline + this.tab() + "regex rx(pattern);" + newline + this.tab() + "parsed = regex_match(value, rx);" + newline + newline + this.tab() + "if (parsed)" + newline + this.inc() + "{" + newline + this.tab() + "context.index += length;" + newline + this.tab() + "numericValue = new " + "Terminal_" + "NumericValue(value, vector<const Rule*>());" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.dec() + "}" + newline + this.tab() + "catch (...) {}" + newline + newline + this.tab() + "context.pop(\"NumericValue\", parsed);" + newline + newline + this.tab() + "return numericValue;" + newline + this.dec() + "}" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(destDir + "Terminal_" + "NumericValue.cpp");
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createParserExceptionHeader(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserException.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef ParserException_hpp" + newline + "#define ParserException_hpp" + newline);
      text.append(newline + "#include <iostream>" + newline + "#include <vector>" + newline + "#include <exception>" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "class ParserException : public std::exception" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      text.append(this.tab() + "ParserException(" + newline + this.tab() + this.tab() + "const std::string& reason," + newline + this.tab() + this.tab() + "const std::string& text," + newline + this.tab() + this.tab() + "unsigned int index," + newline + this.tab() + this.tab() + "const std::vector<std::string>& ruleStack);" + newline);
      text.append(newline + this.tab() + "ParserException(const ParserException& exception);" + newline);
      text.append(newline + this.tab() + "ParserException& operator=(const ParserException& exception);" + newline);
      text.append(newline + this.tab() + "virtual ~ParserException() throw();" + newline);
      text.append(newline + this.tab() + "const std::string& getReason(void) const;" + newline + this.tab() + "const std::string& getSubstring(void) const;" + newline + this.tab() + "unsigned int getSubstringIndex(void) const;" + newline + this.tab() + "const std::vector<std::string>& getRuleStack(void) const;" + newline);
      text.append(newline + this.tab() + "const char* what() throw();" + newline);
      text.append(newline + this.tab() + "void setCause(const ParserException& cause);" + newline + this.tab() + "const ParserException* getCause(void) const;" + newline);
      text.append(this.dec() + newline + this.inc() + "private:" + newline);
      text.append(this.tab() + "std::string reason;" + newline + this.tab() + "std::string text60;" + newline + this.tab() + "unsigned int index60;" + newline + this.tab() + "std::vector<std::string> ruleStack;" + newline + this.tab() + "std::string message;" + newline + newline + this.tab() + "ParserException* cause;" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createParserExceptionClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * ParserException.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <string>" + newline + this.tab() + "using std::string;" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline + this.tab() + "using std::vector;" + newline);
      text.append(newline + this.tab() + "#include <exception>" + newline + this.tab() + "using std::exception;" + newline);
      text.append(newline + this.tab() + "#include <regex>" + newline + this.tab() + "using std::regex;" + newline);
      text.append(newline + "#include \"ParserException.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      text.append(newline + this.tab() + "ParserException::ParserException(" + newline + this.add() + "const string& reason," + newline + this.add() + "const string& text," + newline + this.add() + "unsigned int index," + newline + this.add() + "const vector<string>& ruleStack) : " + newline + this.add() + "reason(reason)," + newline + this.add() + "ruleStack(ruleStack)," + newline + this.add() + "cause(NULL)" + newline + this.inc() + "{" + newline + this.tab() + "unsigned int start = (index < 30) ? 0: index - 30;" + newline + this.tab() + "unsigned int end = (text.length() < index + 30) ? text.length(): index + 30;" + newline + this.tab() + "text60 = text.substr(start, end - start);" + newline + this.tab() + "index60 = (index < 30) ? index : 30;" + newline + newline + this.tab() + "regex rx(\"[\\\\x00-\\\\x1F]\");" + newline + this.tab() + "text60 = regex_replace(text60, rx, string(\" \"));" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "ParserException::ParserException(const ParserException& exception) :" + newline + this.add() + "reason(exception.reason)," + newline + this.add() + "text60(exception.text60)," + newline + this.add() + "index60(exception.index60)," + newline + this.add() + "ruleStack(exception.ruleStack)," + newline + this.add() + "cause(NULL)" + newline + this.inc() + "{" + newline + this.tab() + "if (exception.cause != NULL)" + newline + this.add() + "cause = new ParserException(*exception.cause);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "ParserException& ParserException::operator=(const ParserException& exception)" + newline + this.inc() + "{" + newline + this.tab() + "if (&exception != this)" + newline + this.inc() + "{" + newline + this.tab() + "reason = exception.reason;" + newline + this.tab() + "text60 = exception.text60;" + newline + this.tab() + "index60 = exception.index60;" + newline + this.tab() + "ruleStack = exception.ruleStack;" + newline + newline + this.tab() + "delete cause;" + newline + this.tab() + "cause = NULL;" + newline + this.tab() + "if (exception.cause != NULL)" + newline + this.add() + "cause = new ParserException(*exception.cause);" + newline + this.dec() + "}" + newline + newline + this.tab() + "return *this;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "ParserException::~ParserException() throw()" + newline + this.inc() + "{" + newline + this.tab() + "delete cause;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const string& ParserException::getReason(void) const" + newline + this.inc() + "{" + newline + this.tab() + "return reason;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const string& ParserException::getSubstring(void) const" + newline + this.inc() + "{" + newline + this.tab() + "return text60;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "unsigned int ParserException::getSubstringIndex(void) const" + newline + this.inc() + "{" + newline + this.tab() + "return index60;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const vector<string>& ParserException::getRuleStack(void) const" + newline + this.inc() + "{" + newline + this.tab() + "return ruleStack;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const char* ParserException::what(void) throw()" + newline + this.inc() + "{" + newline + this.tab() + "string marker(\"                              \");" + newline + newline + this.tab() + "message = reason + \"\\n\";" + newline + this.tab() + "message += text60 + \"\\n\";" + newline + this.tab() + "message += marker.substr(0, index60) + \"^\\n\";" + newline + newline + this.tab() + "if (!ruleStack.empty())" + newline + this.inc() + "{" + newline + this.tab() + "message += \"rule stack:\\n\";" + newline + newline + this.tab() + "vector<string>::const_iterator s;" + newline + this.tab() + "for (s = ruleStack.begin(); s != ruleStack.end(); s++)" + newline + this.add() + "message += \"  \" + *s + \"\\n\";" + newline + this.dec() + "}" + newline + newline + this.tab() + "if (cause != NULL)" + newline + this.inc() + "{" + newline + this.tab() + "message += \"possible cause: \";" + newline + this.tab() + "message += cause->what();" + newline + this.dec() + "}" + newline + newline + this.tab() + "return message.c_str();" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void ParserException::setCause(const ParserException& cause)" + newline + this.inc() + "{" + newline + this.tab() + "delete this->cause;" + newline + newline + this.tab() + "this->cause = new ParserException(cause);" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "const ParserException* ParserException::getCause(void) const" + newline + this.inc() + "{" + newline + this.tab() + "return cause;" + newline + this.dec() + "}" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out = new FileOutputStream(filename);
      out.write(text.toString().getBytes());
      out.close();
   }

   private void createDisplayerHeader(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Displayer.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef Displayer_hpp" + newline + "#define Displayer_hpp" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline);
      text.append(newline + this.tab() + "#include \"Visitor.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "class Rule;" + newline);
      text.append(newline + this.tab() + "class Displayer : public Visitor" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      Iterator out = this.grammar.rules.iterator();

      String rulename;
      while(out.hasNext()) {
         Rule rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(this.tab() + "void* visit(const " + "Rule_" + rulename + "* rule);" + newline);
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         ExternalRule rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(this.tab() + "void* visit(const " + rulename + "* rule);" + newline);
      }

      text.append(newline + this.tab() + "void* visit(const " + "Terminal_" + "StringValue* value);" + newline + this.tab() + "void* visit(const " + "Terminal_" + "NumericValue* value);" + newline);
      text.append(this.dec() + newline + this.inc() + "private:" + newline);
      text.append(this.tab() + "void* visitRules(const std::vector<const Rule*>& rules);" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out1 = new FileOutputStream(filename);
      out1.write(text.toString().getBytes());
      out1.close();
   }

   private void createDisplayerClass(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * Displayer.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <iostream>" + newline + this.tab() + "using std::cout;" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline + this.tab() + "using std::vector;" + newline);
      text.append(newline + this.tab() + "#include \"Displayer.hpp\"" + newline);
      text.append(newline);
      Iterator out = this.grammar.rules.iterator();

      Rule rule;
      String rulename;
      while(out.hasNext()) {
         rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(this.tab() + "#include \"" + "Rule_" + rulename + ".hpp\"" + newline);
      }

      out = this.grammar.externalRules.iterator();

      ExternalRule rule1;
      while(out.hasNext()) {
         rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(this.tab() + "#include \"" + rulename + ".hpp\"" + newline);
      }

      text.append(this.tab() + "#include \"" + "Terminal_" + "StringValue.hpp\"" + newline);
      text.append(this.tab() + "#include \"" + "Terminal_" + "NumericValue.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      out = this.grammar.rules.iterator();

      while(out.hasNext()) {
         rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(newline + this.tab() + "void* Displayer::visit(const " + "Rule_" + rulename + "* rule)" + newline + this.inc() + "{" + newline + this.tab() + "return visitRules(rule->rules);" + newline + this.dec() + "}" + newline);
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(newline + this.tab() + "void* Displayer::visit(const " + rulename + "* rule)" + newline + this.inc() + "{" + newline + this.tab() + "cout << rule->spelling;" + newline + this.tab() + "return NULL;" + newline + this.dec() + "}" + newline);
      }

      text.append(newline + this.tab() + "void* Displayer::visit(const " + "Terminal_" + "StringValue* value)" + newline + this.inc() + "{" + newline + this.tab() + "cout << value->spelling;" + newline + this.tab() + "return NULL;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void* Displayer::visit(const " + "Terminal_" + "NumericValue* value)" + newline + this.inc() + "{" + newline + this.tab() + "cout << value->spelling;" + newline + this.tab() + "return NULL;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void* Displayer::visitRules(const vector<const Rule*>& rules)" + newline + this.inc() + "{" + newline + this.tab() + "vector<const Rule*>::const_iterator i;" + newline + this.tab() + "for (i = rules.begin(); i != rules.end(); i++)" + newline + this.add() + "(*i)->accept(*this);" + newline + newline + this.tab() + "return NULL;" + newline + this.dec() + "}" + newline);
      text.append(newline);
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * eof" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      FileOutputStream out1 = new FileOutputStream(filename);
      out1.write(text.toString().getBytes());
      out1.close();
   }

   private void createXmlDisplayerHeader(String filename) throws IOException {
      StringBuffer text = new StringBuffer();
      text.append("/* -----------------------------------------------------------------------------" + newline);
      text.append(" * XmlDisplayer.hpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + "#ifndef XmlDisplayer_hpp" + newline + "#define XmlDisplayer_hpp" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline);
      text.append(newline + this.tab() + "#include \"Visitor.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "namespace " + this.namespace + " {" + newline);
      }

      text.append(newline + this.tab() + "class Rule;" + newline);
      text.append(newline + this.tab() + "class XmlDisplayer : public Visitor" + newline + this.tab() + "{" + newline + this.inc() + "public:" + newline);
      text.append(this.tab() + "XmlDisplayer() : terminal(true) {}" + newline);
      text.append(newline);
      Iterator out = this.grammar.rules.iterator();

      String rulename;
      while(out.hasNext()) {
         Rule rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(this.tab() + "void* visit(const " + "Rule_" + rulename + "* rule);" + newline);
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         ExternalRule rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(this.tab() + "void* visit(const " + rulename + "* rule);" + newline);
      }

      text.append(newline + this.tab() + "void* visit(const " + "Terminal_" + "StringValue* value);" + newline + this.tab() + "void* visit(const " + "Terminal_" + "NumericValue* value);" + newline);
      text.append(this.dec() + newline + this.inc() + "private:" + newline);
      text.append(this.tab() + "bool terminal;" + newline);
      text.append(newline + this.tab() + "void* visitRules(const std::vector<const Rule*>& rules);" + newline);
      text.append(this.dec() + "};" + newline);
      if(this.namespace != null) {
         text.append(newline + "}" + newline);
      }

      text.append(newline + "#endif" + newline);
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
      text.append(" * XmlDisplayer.cpp" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" *" + newline);
      text.append(" * Producer : " + this.producedBy + newline);
      text.append(" * Produced : " + this.producedAt.toString() + newline);
      text.append(" *" + newline);
      text.append(" * -----------------------------------------------------------------------------" + newline);
      text.append(" */" + newline);
      text.append(newline + this.tab() + "#include <iostream>" + newline + this.tab() + "using std::cout;" + newline + this.tab() + "using std::endl;" + newline);
      text.append(newline + this.tab() + "#include <vector>" + newline + this.tab() + "using std::vector;" + newline);
      text.append(newline + this.tab() + "#include \"XmlDisplayer.hpp\"" + newline);
      text.append(newline);
      Iterator out = this.grammar.rules.iterator();

      Rule rule;
      String rulename;
      while(out.hasNext()) {
         rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(this.tab() + "#include \"" + "Rule_" + rulename + ".hpp\"" + newline);
      }

      out = this.grammar.externalRules.iterator();

      ExternalRule rule1;
      while(out.hasNext()) {
         rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(this.tab() + "#include \"" + rulename + ".hpp\"" + newline);
      }

      text.append(this.tab() + "#include \"" + "Terminal_" + "StringValue.hpp\"" + newline);
      text.append(this.tab() + "#include \"" + "Terminal_" + "NumericValue.hpp\"" + newline);
      if(this.namespace != null) {
         text.append(newline + "using namespace " + this.namespace + ";" + newline);
      }

      out = this.grammar.rules.iterator();

      while(out.hasNext()) {
         rule = (Rule)out.next();
         rulename = rule.rulename.spelling.replace('-', '_');
         text.append(newline + this.tab() + "void* XmlDisplayer::visit(const " + "Rule_" + rulename + "* rule)" + newline + this.inc() + "{" + newline + this.tab() + "if (!terminal) cout << endl;" + newline + this.tab() + "cout << \"<" + rule.rulename.spelling + ">\";" + newline + this.tab() + "terminal = false;" + newline + this.tab() + "visitRules(rule->rules);" + newline + this.tab() + "if (!terminal) cout << endl;" + newline + this.tab() + "cout << \"</" + rule.rulename.spelling + ">\";" + newline + this.tab() + "terminal = false;" + newline + this.tab() + "return NULL;" + newline + this.dec() + "}" + newline);
      }

      out = this.grammar.externalRules.iterator();

      while(out.hasNext()) {
         rule1 = (ExternalRule)out.next();
         rulename = rule1.spelling;
         text.append(newline + this.tab() + "void* XmlDisplayer::visit(const " + rulename + "* rule)" + newline + this.inc() + "{" + newline + this.tab() + "if (!terminal) cout << endl;" + newline + this.tab() + "cout << \"<" + rulename + ">\";" + newline + this.tab() + "cout << rule->spelling;" + newline + this.tab() + "cout << \"</" + rulename + ">\";" + newline + this.tab() + "terminal = false;" + newline + this.tab() + "return NULL;" + newline + this.dec() + "}" + newline);
      }

      text.append(newline + this.tab() + "void* XmlDisplayer::visit(const " + "Terminal_" + "StringValue* value)" + newline + this.inc() + "{" + newline + this.tab() + "cout << value->spelling;" + newline + this.tab() + "terminal = true;" + newline + this.tab() + "return NULL;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void* XmlDisplayer::visit(const " + "Terminal_" + "NumericValue* value)" + newline + this.inc() + "{" + newline + this.tab() + "cout << value->spelling;" + newline + this.tab() + "terminal = true;" + newline + this.tab() + "return NULL;" + newline + this.dec() + "}" + newline);
      text.append(newline + this.tab() + "void* XmlDisplayer::visitRules(const vector<const Rule*>& rules)" + newline + this.inc() + "{" + newline + this.tab() + "vector<const Rule*>::const_iterator i;" + newline + this.tab() + "for (i = rules.begin(); i != rules.end(); i++)" + newline + this.add() + "(*i)->accept(*this);" + newline + newline + this.tab() + "return NULL;" + newline + this.dec() + "}" + newline);
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
   static int access$808(CppEncoder x0) {
      return x0.alternationLevel++;
   }

   // $FF: synthetic method
   static int access$810(CppEncoder x0) {
      return x0.alternationLevel--;
   }
}
