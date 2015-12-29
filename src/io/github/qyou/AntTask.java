package io.github.qyou;

import java.util.ArrayList;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AntTask extends Task {
   private String grammarFile = "";
   private String language = "";
   private String packageName = "";
   private String includeDirs = "";
   private String destDir = "";
   private String visitors = "";
   private String namespace = "";
   private String encoder = "";
   private String encoderArgs = "";
   private boolean trace = false;
   private boolean annotate = false;
   private boolean main = false;

   public void execute() throws BuildException {
      ArrayList argList = new ArrayList();
      if(this.language.length() > 0) {
         argList.add("-language");
         argList.add(this.language);
      }

      if(this.packageName.length() > 0) {
         argList.add("-package");
         argList.add(this.packageName);
      }

      if(this.namespace.length() > 0) {
         argList.add("-namespace");
         argList.add(this.namespace);
      }

      if(this.encoder.length() > 0) {
         argList.add("-encoder");
         argList.add(this.encoder);
      }

      if(this.encoder.length() > 0) {
         argList.add("-encoderargs");
         argList.add(this.encoderArgs);
      }

      if(this.includeDirs.length() > 0) {
         argList.add("-includedirs");
         argList.add(this.includeDirs);
      }

      if(this.destDir.length() > 0) {
         argList.add("-destdir");
         argList.add(this.destDir);
      }

      if(this.trace) {
         argList.add("-trace");
      }

      if(this.annotate) {
         argList.add("-annotate");
      }

      if(this.main) {
         argList.add("-main");
      }

      if(this.visitors.length() > 0) {
         argList.add("-visitors");
         argList.add(this.visitors);
      }

      argList.add(this.grammarFile);
      String[] argArray = new String[argList.size()];
      argList.toArray(argArray);
      this.log("Compiling " + this.grammarFile);
      ChineseParser.main(argArray);
   }

   public void setLanguage(String language) {
      this.language = language;
   }

   public void setPackage(String packageName) {
      this.packageName = packageName;
   }

   public void setIncludeDirs(String includeDirs) {
      this.includeDirs = includeDirs;
   }

   public void setDestDir(String destDir) {
      this.destDir = destDir;
   }

   public void setTrace(boolean trace) {
      this.trace = trace;
   }

   public void setAnnotate(boolean annotate) {
      this.annotate = annotate;
   }

   public void setMain(boolean main) {
      this.main = main;
   }

   public void setVisitors(String visitors) {
      this.visitors = visitors;
   }

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public void setEncoder(String encoder) {
      this.encoder = encoder;
   }

   public void setEncoderArgs(String encoderArgs) {
      this.encoderArgs = encoderArgs;
   }

   public void setGrammar(String grammarFile) {
      this.grammarFile = grammarFile;
   }
}
