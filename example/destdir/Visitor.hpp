/* -----------------------------------------------------------------------------
 * Visitor.hpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#ifndef visitor_hpp
#define visitor_hpp

class Rule_clock;
class Rule_name;
class Rule_hour;
class Rule_min;
class Rule_sec;
class Rule_sep;
class Terminal_StringValue;
class Terminal_NumericValue;

class Visitor
{
public:
  virtual void* visit(const Rule_clock* rule) = 0;
  virtual void* visit(const Rule_name* rule) = 0;
  virtual void* visit(const Rule_hour* rule) = 0;
  virtual void* visit(const Rule_min* rule) = 0;
  virtual void* visit(const Rule_sec* rule) = 0;
  virtual void* visit(const Rule_sep* rule) = 0;

  virtual void* visit(const Terminal_StringValue* value) = 0;
  virtual void* visit(const Terminal_NumericValue* value) = 0;
};

#endif
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
