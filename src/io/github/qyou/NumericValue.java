package io.github.qyou;

public class NumericValue extends Element {
   public String spelling;
   public String regex;
   public int length;

   public NumericValue(String spelling) {
      this.spelling = spelling;
      this.length = 1;
      StringBuffer buffer = new StringBuffer();
      new String();
      String value;
      StringBuffer binary;
      int i;
      char ch;
      switch(spelling.charAt(1)) {
      case 'b':
         binary = new StringBuffer();

         for(i = 2; i < spelling.length(); ++i) {
            ch = spelling.charAt(i);
            switch(ch) {
            case '-':
               value = Integer.toString(Integer.parseInt(binary.toString(), 2), 16);
               buffer.append(value.length() <= 2?"\\\\x":"\\\\u");
               if(value.length() == 1 || value.length() == 3) {
                  buffer.append("0");
               }

               buffer.append(value);
               binary = new StringBuffer();
               buffer.append("-");
               break;
            case '.':
               value = Integer.toString(Integer.parseInt(binary.toString(), 2), 16);
               buffer.append(value.length() <= 2?"\\\\x":"\\\\u");
               if(value.length() == 1 || value.length() == 3) {
                  buffer.append("0");
               }

               buffer.append(value);
               binary = new StringBuffer();
               ++this.length;
               break;
            default:
               binary.append(ch);
            }
         }

         if(binary.length() > 0) {
            value = Integer.toString(Integer.parseInt(binary.toString(), 2), 16);
            buffer.append(value.length() <= 2?"\\\\x":"\\\\u");
            if(value.length() == 1 || value.length() == 3) {
               buffer.append("0");
            }

            buffer.append(value);
         }
         break;
      case 'd':
         binary = new StringBuffer();

         for(i = 2; i < spelling.length(); ++i) {
            ch = spelling.charAt(i);
            switch(ch) {
            case '-':
               value = Integer.toString(Integer.parseInt(binary.toString(), 10), 16);
               buffer.append(value.length() <= 2?"\\\\x":"\\\\u");
               if(value.length() == 1 || value.length() == 3) {
                  buffer.append("0");
               }

               buffer.append(value);
               binary = new StringBuffer();
               buffer.append("-");
               break;
            case '.':
               value = Integer.toString(Integer.parseInt(binary.toString(), 10), 16);
               buffer.append(value.length() <= 2?"\\\\x":"\\\\u");
               if(value.length() == 1 || value.length() == 3) {
                  buffer.append("0");
               }

               buffer.append(value);
               binary = new StringBuffer();
               ++this.length;
               break;
            default:
               binary.append(ch);
            }
         }

         if(binary.length() > 0) {
            value = Integer.toString(Integer.parseInt(binary.toString(), 10), 16);
            buffer.append(value.length() <= 2?"\\\\x":"\\\\u");
            if(value.length() == 1 || value.length() == 3) {
               buffer.append("0");
            }

            buffer.append(value);
         }
         break;
      case 'x':
         binary = new StringBuffer();

         for(i = 2; i < spelling.length(); ++i) {
            ch = spelling.charAt(i);
            switch(ch) {
            case '-':
               value = binary.toString();
               buffer.append(value.length() <= 2?"\\\\x":"\\\\u");
               buffer.append(value);
               binary = new StringBuffer();
               buffer.append("-");
               break;
            case '.':
               value = binary.toString();
               buffer.append(value.length() <= 2?"\\\\x":"\\\\u");
               buffer.append(value);
               binary = new StringBuffer();
               ++this.length;
               break;
            default:
               binary.append(ch);
            }
         }

         if(binary.length() > 0) {
            value = binary.toString();
            buffer.append(value.length() <= 2?"\\\\x":"\\\\u");
            buffer.append(value);
         }
      }

      this.regex = (this.length > 1?"(":"[") + buffer.toString() + (this.length > 1?")":"]");
   }

   public Object accept(Visitor visitor, Object argument) {
      return visitor.visit(this, argument);
   }
}
