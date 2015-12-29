package io.github.qyou;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class AST {
   private ArrayList<Annotation> annotations = new ArrayList();

   public void addAnnotation(Annotation annotation) {
      this.annotations.add(annotation);
   }

   public void addAnnotations(ArrayList<Annotation> annotations) {
      this.annotations.addAll(annotations);
   }

   public ArrayList<Annotation> getAnnotations() {
      return this.annotations;
   }

   public Annotation getAnnotation(String name) {
      Annotation found = null;
      Iterator i$ = this.annotations.iterator();

      while(i$.hasNext()) {
         Annotation annotation = (Annotation)i$.next();
         if(annotation.name.equalsIgnoreCase(name)) {
            found = annotation;
         }
      }

      return found;
   }

   public abstract Object accept(Visitor var1, Object var2);
}
