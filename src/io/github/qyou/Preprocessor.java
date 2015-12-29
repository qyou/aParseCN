package io.github.qyou;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Preprocessor {
   private ArrayList<Error> errors;
   private TreeSet<Preprocessor.Include> included;
   private ArrayList<String> includeDirs = new ArrayList();

   public Preprocessor(ArrayList<Error> errors, String includeDirs) {
      this.errors = errors;
      StringTokenizer tokenizer = new StringTokenizer(includeDirs, ";,");

      while(tokenizer.hasMoreElements()) {
         this.includeDirs.add(tokenizer.nextToken());
      }

      if(this.includeDirs.size() > 0) {
         this.includeDirs.add(0, "./");
      }

   }

   public ChineseSource process(ChineseSource source) {
      this.included = new TreeSet();
      this.included.add(new Preprocessor.Include(source.file.getPath(), new Preprocessor.Position()));
      source = this.processIncludes(source);
      return source;
   }

   private ChineseSource processIncludes(ChineseSource source) {
      ArrayList includes = this.getIncludes(source);
      Iterator i$ = includes.iterator();

      while(i$.hasNext()) {
         Preprocessor.Include include = (Preprocessor.Include)i$.next();
         if(this.included.contains(include)) {
            this.errors.add(new Error(3, "duplicate include", source, include.position.line, include.position.column));
         } else {
            try {
               ChineseSource e = new ChineseSource();
               e.load(this.findInclude(include.filename, source));
               this.included.add(include);
               this.processIncludes(e);
               source.text.append(System.getProperty("line.separator"));
               source.append(e);
            } catch (IOException var6) {
               this.errors.add(new Error(3, var6.getMessage(), source, include.position.line, include.position.column));
            }
         }
      }

      return source;
   }

   private File findInclude(String filename, ChineseSource source) {
      File file = new File(filename);
      File include = file;
      if(!file.isAbsolute()) {
         if(file.getParent() != null) {
            include = new File(source.file.getParent(), filename);
         } else {
            int i = 0;
            boolean stop = false;

            while(!stop && i < this.includeDirs.size()) {
               File possibleInclude = new File((String)this.includeDirs.get(i++), file.getName());
               if(stop = possibleInclude.exists()) {
                  include = possibleInclude;
               }
            }
         }
      }

      return include;
   }

   private ArrayList<Preprocessor.Include> getIncludes(ChineseSource source) {
      boolean BASE = false;
      boolean COMMENT = true;
      boolean DIRECTIVE = true;
      ArrayList includes = new ArrayList();
      Preprocessor.Position position = new Preprocessor.Position();
      byte state = 0;

      while(position.index < source.text.length()) {
         char ch = source.text.charAt(position.index);
         switch(state) {
         case 0:
            switch(ch) {
            case '#':
               state = 1;
               break;
            case '$':
               state = 2;
            }

            ++position.index;
            ++position.column;
            break;
         case 1:
            if(ch == 10) {
               state = 0;
            }

            ++position.index;
            ++position.column;
            break;
         case 2:
            if(this.isInclude(source, position.index)) {
               int startIndex = position.index - 1;
               this.getInclude(source, position, includes);
               source.text.insert(startIndex, "#APARSE:");
            } else {
               ++position.index;
               ++position.column;
            }

            state = 0;
         }

         if(ch == 10) {
            ++position.line;
            position.column = 1;
         }
      }

      return includes;
   }

   private boolean isInclude(ChineseSource source, int index) {
      boolean include = false;
      boolean stop = false;

      for(int state = 0; !stop && index < source.text.length(); ++index) {
         char ch = source.text.charAt(index);
         switch(state) {
         case 0:
            stop = ch != 105 && ch != 73;
            break;
         case 1:
            stop = ch != 110 && ch != 78;
            break;
         case 2:
            stop = ch != 99 && ch != 67;
            break;
         case 3:
            stop = ch != 108 && ch != 76;
            break;
         case 4:
            stop = ch != 117 && ch != 85;
            break;
         case 5:
            stop = ch != 100 && ch != 69;
            break;
         case 6:
            stop = ch == 101 || ch == 69;
            include = true;
         }

         ++state;
      }

      return include;
   }

   private void getInclude(ChineseSource source, Preprocessor.Position position, ArrayList<Preprocessor.Include> includes) {
      boolean stop = false;
      int state = 0;
      StringBuffer include = new StringBuffer();
      Preprocessor.Position includePosition = null;
      position.index += 7;
      position.column += 7;

      while(!stop && position.index < source.text.length()) {
         char ch = source.text.charAt(position.index);
         switch(state) {
         case 0:
            if(ch != 32 && ch != 9) {
               this.errors.add(new Error(3, "space or tab expected", source, position.line, position.column));
               stop = true;
            } else {
               ++state;
            }
            break;
         case 1:
            if(ch != 32 && ch != 9) {
               if(ch == 34) {
                  include = new StringBuffer();
                  ++state;
               } else {
                  this.errors.add(new Error(3, "unexpected character", source, position.line, position.column));
                  stop = true;
               }
            }
            break;
         case 2:
            if(includePosition == null) {
               includePosition = new Preprocessor.Position(position);
            }

            if(ch == 34) {
               ++state;
            } else if(ch == 59) {
               this.errors.add(new Error(3, "unexpected character", source, position.line, position.column));
               include = new StringBuffer();
               stop = true;
            } else {
               include.append(ch);
            }
            break;
         case 3:
            if(ch != 32 && ch != 9 && ch != 10) {
               if(ch != 59) {
                  this.errors.add(new Error(3, "unexpected character", source, position.line, position.column));
                  include = new StringBuffer();
               }

               stop = true;
            }
         }

         ++position.index;
         ++position.column;
         if(ch == 10) {
            ++position.line;
            position.column = 1;
         }
      }

      if(include.length() > 0) {
         includes.add(new Preprocessor.Include(include.toString(), includePosition));
      }

   }

   class Include implements Comparable<Preprocessor.Include> {
      String filename;
      Preprocessor.Position position;

      Include(String filename, Preprocessor.Position position) {
         this.filename = filename;
         this.position = position;
      }

      public int compareTo(Preprocessor.Include a) {
         return this.filename.compareTo(a.filename);
      }
   }

   private class Position {
      int index = 0;
      int line = 1;
      int column = 1;

      Position() {
      }

      Position(Preprocessor.Position position) {
         this.index = position.index;
         this.line = position.line;
         this.column = position.column;
      }
   }
}
