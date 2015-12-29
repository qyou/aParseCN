package io.github.qyou;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;


public class ChineseParser  {
	
	private static final String version = "2.5";
	   private static final boolean dumpEnabled = false;
	   private ChineseScanner scanner;
	   private ChineseToken token;
	   private ArrayList<Error> errors;
	   private boolean trace = false;

	   public static void main(String[] args) {
	      Properties arguments = new Properties();
	      if(getArguments(args, arguments)) {
	         try {
	            ArrayList e = new ArrayList();
	            String grammarFilename = arguments.getProperty("GrammarFile");
	            File currentDir = new File(".");
	            if(grammarFilename.startsWith(currentDir.getCanonicalPath())) {
	               grammarFilename = grammarFilename.substring(currentDir.getCanonicalPath().length() + 1);
	            }

	            ChineseSource source = new ChineseSource();
	            source.load(new File(grammarFilename));
	            Preprocessor preprocessor = new Preprocessor(e, arguments.getProperty("IncludeDirs"));
	            source = preprocessor.process(source);
	            if(arguments.getProperty("Dump").equals("On")) {
	               source.dump();
	            }

	            ChineseParser parser = new ChineseParser(e, arguments.getProperty("Trace").equals("On"));
	            Grammar grammar = parser.parse(source);
	            if(e.size() == 0) {
	               Checker encoder = new Checker(e);
	               encoder.check(grammar);
	            }

	            if(e.size() != 0) {
	               Iterator encoder2 = e.iterator();

	               while(encoder2.hasNext()) {
	                  Error encoder1 = (Error)encoder2.next();
	                  System.out.println(encoder1.toMessage());
	               }

	               System.out.println(e.size() + (e.size() > 1?" errors":" error"));
	            } else if(arguments.getProperty("Encoder") != null) {
	               String encoder3 = arguments.getProperty("Encoder");
	               Encoder encoder5 = (Encoder)Class.forName(encoder3).newInstance();
	               encoder5.encode(grammar, "com.parse2.aparse.Parser 2.5", arguments);
	            } else if(arguments.getProperty("Language").equalsIgnoreCase("java")) {
	               JavaEncoder encoder4 = new JavaEncoder();
	               encoder4.encode(grammar, "com.parse2.aparse.Parser 2.5", arguments);
	            } else if(arguments.getProperty("Language").equalsIgnoreCase("cpp")) {
	               CppEncoder encoder6 = new CppEncoder();
	               encoder6.encode(grammar, "com.parse2.aparse.Parser 2.5", arguments);
	            } else if(arguments.getProperty("Language").equalsIgnoreCase("cs")) {
	               CsEncoder encoder7 = new CsEncoder();
	               encoder7.encode(grammar, "com.parse2.aparse.Parser 2.5", arguments);
	            }
	         } catch (IOException var11) {
	            System.out.println("io error: " + var11.getMessage());
	         } catch (IllegalArgumentException var12) {
	            System.out.println("argument error: " + var12.getMessage());
	         } catch (IllegalAccessException var13) {
	            System.out.println("encoder error: illegal access - " + var13.getMessage());
	         } catch (InstantiationException var14) {
	            System.out.println("encoder error: instantiation failure - " + var14.getMessage());
	         } catch (ClassNotFoundException var15) {
	            System.out.println("encoder error: class not found - " + var15.getMessage());
	         }

	      }
	   }

	   static boolean getArguments(String[] args, Properties properties) {
	      String error = "";
	      boolean ok = args.length > 0;
	      if(!ok) {
	         error = "insufficient arguments";
	      } else {
	         properties.setProperty("Trace", "Off");
	         properties.setProperty("Dump", "Off");
	         properties.setProperty("Annotate", "Off");
	         properties.setProperty("DestDir", "." + System.getProperty("file.separator"));
	         properties.setProperty("IncludeDirs", "");
	         properties.setProperty("Language", "Java");
	         properties.setProperty("Main", "No");

	         for(int i = 0; i < args.length; ++i) {
	            if(args[i].equals("-trace")) {
	               properties.setProperty("Trace", "On");
	            } else if(args[i].equals("-annotate")) {
	               properties.setProperty("Annotate", "On");
	            } else if(args[i].equals("-package")) {
	               ++i;
	               properties.setProperty("Package", args[i]);
	            } else if(args[i].equals("-destdir")) {
	               ++i;
	               properties.setProperty("DestDir", args[i]);
	            } else if(args[i].equals("-includedirs")) {
	               ++i;
	               properties.setProperty("IncludeDirs", args[i]);
	            } else if(args[i].equals("-language")) {
	               ++i;
	               properties.setProperty("Language", args[i]);
	            } else if(args[i].equals("-main")) {
	               properties.setProperty("Main", "Yes");
	            } else if(args[i].equals("-namespace")) {
	               ++i;
	               properties.setProperty("Namespace", args[i]);
	            } else if(args[i].equals("-visitors")) {
	               ++i;
	               properties.setProperty("Visitors", args[i]);
	            } else if(args[i].equals("-encoder")) {
	               ++i;
	               properties.setProperty("Encoder", args[i]);
	            } else if(args[i].equals("-encoderargs")) {
	               ++i;
	               properties.setProperty("EncoderArgs", args[i]);
	            } else if(i == args.length - 1) {
	               properties.setProperty("GrammarFile", args[i]);
	            } else {
	               error = "unknown argument : " + args[i];
	               ok = false;
	            }
	         }
	      }

	      if(ok && properties.getProperty("GrammarFile") == null) {
	         error = "insufficient arguments";
	         ok = false;
	      }

	      if(ok && !properties.getProperty("Language").equalsIgnoreCase("java") && !properties.getProperty("Language").equalsIgnoreCase("cpp") && !properties.getProperty("Language").equalsIgnoreCase("cs")) {
	         error = "invalid -language option";
	         ok = false;
	      }

	      if(!ok) {
	         System.out.println("com.parse2.aParse.parser 2.5");
	         System.out.println("error: " + error);
	         System.out.println("usage: com.parse2.aParse.parser [-language java | cpp | cs] [-trace] [-destdir directory] [-includedirs directory;...] file");
	         System.out.println(" java: [-package packagename]");
	         System.out.println("  cpp: [-namespace namespace] [-main] [-visitors visitor;...]");
	         System.out.println("   cs: [-namespace namespace]");
	      }

	      return ok;
	   }

	   public ChineseParser(ArrayList<Error> errors, boolean trace) {
	      this.errors = errors;
	      this.trace = trace;
	   }

	   public Grammar parse(ChineseSource source) {
	      if(this.trace) {
	         System.out.println("Parser.parse()");
	      }

	      ArrayList rules = new ArrayList();
	      ArrayList addingRules = new ArrayList();
	      this.scanner = new ChineseScanner(source, this.errors);
	      this.token = this.scanner.scan();

	      while(this.token.kind != 1) {
	         this.parseRule(rules, addingRules);
	      }

	      Iterator i = addingRules.iterator();

	      while(i.hasNext()) {
	         boolean found = false;
	         Rule addingRule = (Rule)i.next();
	         Iterator j = rules.iterator();

	         while(j.hasNext() && !found) {
	            Rule rule = (Rule)j.next();
	            if(addingRule.rulename.spelling.equalsIgnoreCase(rule.rulename.spelling)) {
	               rule.append(addingRule);
	               found = true;
	            }
	         }

	         if(!found) {
	            this.errors.add(new Error(2, "undeclared parent rule", addingRule.rulename.source, addingRule.rulename.line, addingRule.rulename.column));
	         }
	      }

	      return this.errors.size() == 0?new Grammar(rules):null;
	   }

	   private void acceptIt() {
	      if(this.trace) {
	         System.out.println("Parser.acceptIt(" + this.token + ")");
	      }

	      this.token = this.scanner.scan();
	   }

	   private void accept(int kind, String errorText) {
	      if(this.trace) {
	         System.out.println("Parser.accept(" + ChineseToken.getDescripton(kind) + ")");
	      }

	      if(this.token.kind != kind) {
	         this.errors.add(new Error(1, errorText, this.token.source, this.token.line, this.token.column));
	      }

	      this.acceptIt();
	   }

	   private void advance(int kind) {
	      do {
	         this.token = this.scanner.scan();
	      } while(this.token.kind != kind && this.token.kind != 1);

	   }

	   private void parseRule(ArrayList<Rule> rules, ArrayList<Rule> addingRules) {
	      if(this.trace) {
	         System.out.println("->Parser.parseRule()");
	      }

	      ArrayList annotations = this.parseAnnotations();
	      Rulename rulename = new Rulename(this.token.spelling, this.token.source, this.token.line, this.token.column);
	      this.accept(2, "identifier expected");
	      rulename.addAnnotations(annotations);
	      switch(this.token.kind) {
	      case 11:
	         this.acceptIt();
	         rules.add(new Rule(rulename, this.parseAlternation()));
	         this.accept(4, "end-of-rule ; expected");
	         break;
	      case 12:
	         this.acceptIt();
	         addingRules.add(new Rule(rulename, this.parseAlternation()));
	         this.accept(4, "end-of-rule ; expected");
	         break;
	      default:
	         this.errors.add(new Error(1, "assignment = or /= expected", this.token.source, this.token.line, this.token.column));
	         this.advance(4);
	         this.acceptIt();
	      }

	      if(this.trace) {
	         System.out.println("<-Parser.parseRule()");
	      }

	   }

	   private Alternation parseAlternation() {
	      if(this.trace) {
	         System.out.println("->Parser.parseAlternation()");
	      }

	      ArrayList concatenations = new ArrayList();

	      while(this.token.kind != 4 && this.token.kind != 15 && this.token.kind != 17 && this.token.kind != 1) {
	         concatenations.add(this.parseConcatenation());
	         if(this.token.kind != 4 && this.token.kind != 15 && this.token.kind != 17 && this.token.kind != 1) {
	            this.accept(13, "alternative / expected");
	         }
	      }

	      if(this.trace) {
	         System.out.println("<-Parser.parseAlternation()");
	      }

	      return new Alternation(concatenations);
	   }

	   private Concatenation parseConcatenation() {
	      if(this.trace) {
	         System.out.println("->Parser.parseConcatenation()");
	      }

	      ArrayList repetitions = new ArrayList();

	      while(this.token.kind != 4 && this.token.kind != 13 && this.token.kind != 15 && this.token.kind != 17 && this.token.kind != 1) {
	         repetitions.add(this.parseRepetition());
	      }

	      if(this.trace) {
	         System.out.println("<-Parser.parseConcatenation()");
	      }

	      return new Concatenation(repetitions);
	   }

	   private Repetition parseRepetition() {
	      if(this.trace) {
	         System.out.println("->Parser.parseRepetition()");
	      }

	      Repeat repeat = null;
	      ArrayList annotations = this.parseAnnotations();
	      Element element;
	      switch(this.token.kind) {
	      case 3:
	      case 10:
	         repeat = this.parseRepeat();
	         element = this.parseElement(annotations);
	         break;
	      case 16:
	         repeat = new Repeat(0, 1);
	         element = this.parseElement(annotations);
	         break;
	      default:
	         repeat = new Repeat(1, 1);
	         element = this.parseElement(annotations);
	      }

	      if(this.trace) {
	         System.out.println("<-Parser.parseRepetition()");
	      }

	      return new Repetition(repeat, element);
	   }

	   private Element parseElement(ArrayList<Annotation> annotations) {
	      if(this.trace) {
	         System.out.println("->Parser.parseElement()");
	      }

	      Object element = null;
	      switch(this.token.kind) {      
	      case 2:
	         element = new Rulename(this.token.spelling, this.token.source, this.token.line, this.token.column);
	         this.acceptIt();
	         ((Element)element).addAnnotations(annotations);
	         break;
	      case 3:
	      case 4:
	      case 10:
	      case 11:
	      case 12:
	      case 13:
	      case 15:
	      case 17:
	      default:
	         this.errors.add(new Error(1, "element expected", this.token.source, this.token.line, this.token.column));
	         this.acceptIt();
	         break;
	      case 5:
	         this.errors.add(new Error(1, "prose-val not supported", this.token.source, this.token.line, this.token.column));
	         this.acceptIt();
	         break;
	      case 6:
	         element = new StringValue(this.token.spelling);
	         this.acceptIt();
	         ((Element)element).addAnnotations(annotations);
	         break;
	      case 7:
	      case 8:
	      case 9:
	         element = new NumericValue(this.token.spelling);
	         this.acceptIt();
	         ((Element)element).addAnnotations(annotations);
	         break;
	      case 14:
	         this.acceptIt();
	         element = new Group(this.parseAlternation());
	         this.accept(15, "group-end ) expected");
	         ((Element)element).addAnnotations(annotations);
	         break;
	      case 16:
	         this.acceptIt();
	         element = new Group(this.parseAlternation());
	         this.accept(17, "option-end ] expected");
	         ((Element)element).addAnnotations(annotations);
	         break;
	      case 18:
	         this.acceptIt();
	         element = this.parseDirective();
	         ((Element)element).addAnnotations(annotations);
	      }

	      if(this.trace) {
	         System.out.println("<-Parser.parseElement()");
	      }

	      return (Element)element;
	   }

	   private Repeat parseRepeat() {
	      int atLeast = 0;
	      int atMost = 0;
	      switch(this.token.kind) {
	      case 3:
	         atLeast = Integer.parseInt(this.token.spelling);
	         this.acceptIt();
	         if(this.token.kind == 10) {
	            this.acceptIt();
	            if(this.token.kind == 3) {
	               atMost = Integer.parseInt(this.token.spelling);
	               this.acceptIt();
	            }
	         } else {
	            atMost = atLeast;
	         }
	         break;
	      case 10:
	         this.acceptIt();
	         if(this.token.kind == 3) {
	            atMost = Integer.parseInt(this.token.spelling);
	            this.acceptIt();
	         }
	         break;
	      default:
	         this.errors.add(new Error(1, "ill-formed repeat", this.token.source, this.token.line, 1));
	      }

	      return new Repeat(atLeast, atMost);
	   }

	   private Element parseDirective() {
	      if(this.trace) {
	         System.out.println("->Parser.parseDirective()");
	      }

	      ExternalRule element = null;
	      if(this.token.kind == 2) {
	         if(this.token.spelling.equalsIgnoreCase("rule")) {
	            this.acceptIt();
	            element = this.parseExternalRule();
	         } else {
	            this.errors.add(new Error(1, "unsupported directive", this.token.source, this.token.line, this.token.column));
	         }
	      } else {
	         this.errors.add(new Error(1, "ill-formed directive", this.token.source, this.token.line, this.token.column));
	      }

	      if(this.trace) {
	         System.out.println("<-Parser.parseDirective()");
	      }

	      return element;
	   }

	   private ExternalRule parseExternalRule() {
	      if(this.trace) {
	         System.out.println("->Parser.parseExternalRule()");
	      }

	      ExternalRule rule = null;
	      switch(this.token.kind) {
	      case 14:
	         this.acceptIt();
	         if(this.token.kind == 6) {
	            rule = new ExternalRule(this.token.spelling.substring(1, this.token.spelling.length() - 1));
	            this.acceptIt();
	            this.accept(15, ") expected");
	         } else {
	            this.errors.add(new Error(1, "ill-formed $rule directive", this.token.source, this.token.line, this.token.column));
	         }
	         break;
	      default:
	         this.errors.add(new Error(1, "ill-formed $rule directive", this.token.source, this.token.line, this.token.column));
	      }

	      if(this.trace) {
	         System.out.println("<-Parser.parseExternalRule()");
	      }

	      return rule;
	   }

	   private ArrayList<Annotation> parseAnnotations() {
	      if(this.trace) {
	         System.out.println("->Parser.parseAnnotation()");
	      }

	      ArrayList annotations = new ArrayList();

	      while(true) {
	         while(this.token.kind == 19) {
	            this.acceptIt();
	            if(this.token.kind == 2) {
	               String name = this.token.spelling;
	               this.acceptIt();
	               HashMap elements = new HashMap();
	               if(this.token.kind == 14) {
	                  this.acceptIt();
	                  if(this.token.kind == 15) {
	                     this.acceptIt();
	                  } else if(this.token.kind == 6) {
	                     elements.put("value", this.token.spelling.substring(1, this.token.spelling.length() - 1));
	                     this.acceptIt();
	                     this.accept(15, ") expected");
	                  } else {
	                     boolean parseElement = true;

	                     while(parseElement) {
	                        if(this.token.kind == 2) {
	                           String elementName = this.token.spelling;
	                           this.acceptIt();
	                           this.accept(11, "= expected");
	                           if(this.token.kind == 6) {
	                              elements.put(elementName, this.token.spelling.substring(1, this.token.spelling.length() - 1));
	                              this.acceptIt();
	                           } else {
	                              this.errors.add(new Error(1, "ill-formed annotation", this.token.source, this.token.line, this.token.column));
	                              parseElement = false;
	                           }
	                        } else {
	                           this.errors.add(new Error(1, "ill-formed annotation", this.token.source, this.token.line, this.token.column));
	                           parseElement = false;
	                        }

	                        if(parseElement && this.token.kind == 20) {
	                           this.acceptIt();
	                        } else {
	                           parseElement = false;
	                        }
	                     }

	                     this.accept(15, ") expected");
	                  }
	               }

	               annotations.add(new Annotation(name, elements));
	            } else {
	               this.errors.add(new Error(1, "ill-formed annotation", this.token.source, this.token.line, this.token.column));
	            }
	         }

	         if(this.trace) {
	            System.out.println("<-Parser.parseAnnotation()");
	         }

	         return annotations;
	      }
	   }

}
