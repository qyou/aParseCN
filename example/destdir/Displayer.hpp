/* -----------------------------------------------------------------------------
 * Displayer.hpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#ifndef Displayer_hpp
#define Displayer_hpp

#include <vector>

#include "Visitor.hpp"

class Rule;

class Displayer : public Visitor
{
public:
  void* visit(const Rule_clock* rule);
  void* visit(const Rule_name* rule);
  void* visit(const Rule_hour* rule);
  void* visit(const Rule_min* rule);
  void* visit(const Rule_sec* rule);
  void* visit(const Rule_sep* rule);

  void* visit(const Terminal_StringValue* value);
  void* visit(const Terminal_NumericValue* value);

private:
  void* visitRules(const std::vector<const Rule*>& rules);
};

#endif

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
