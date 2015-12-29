/* -----------------------------------------------------------------------------
 * Displayer.cpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#include <iostream>
using std::cout;

#include <vector>
using std::vector;

#include "Displayer.hpp"

#include "Rule_clock.hpp"
#include "Rule_name.hpp"
#include "Rule_hour.hpp"
#include "Rule_min.hpp"
#include "Rule_sec.hpp"
#include "Rule_sep.hpp"
#include "Terminal_StringValue.hpp"
#include "Terminal_NumericValue.hpp"

void* Displayer::visit(const Rule_clock* rule)
{
  return visitRules(rule->rules);
}

void* Displayer::visit(const Rule_name* rule)
{
  return visitRules(rule->rules);
}

void* Displayer::visit(const Rule_hour* rule)
{
  return visitRules(rule->rules);
}

void* Displayer::visit(const Rule_min* rule)
{
  return visitRules(rule->rules);
}

void* Displayer::visit(const Rule_sec* rule)
{
  return visitRules(rule->rules);
}

void* Displayer::visit(const Rule_sep* rule)
{
  return visitRules(rule->rules);
}

void* Displayer::visit(const Terminal_StringValue* value)
{
  cout << value->spelling;
  return NULL;
}

void* Displayer::visit(const Terminal_NumericValue* value)
{
  cout << value->spelling;
  return NULL;
}

void* Displayer::visitRules(const vector<const Rule*>& rules)
{
  vector<const Rule*>::const_iterator i;
  for (i = rules.begin(); i != rules.end(); i++)
    (*i)->accept(*this);

  return NULL;
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
