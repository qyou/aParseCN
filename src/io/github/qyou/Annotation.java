package io.github.qyou;

import java.util.HashMap;

public class Annotation {
   public final String name;
   public final HashMap<String, String> elements;

   public Annotation(String name, HashMap<String, String> elements) {
      this.name = name;
      this.elements = elements;
   }

   public String getValue(String key) {
      return (String)this.elements.get(key);
   }
}
