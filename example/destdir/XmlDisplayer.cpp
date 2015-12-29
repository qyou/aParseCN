/* -----------------------------------------------------------------------------
 * XmlDisplayer.cpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#include <iostream>
using std::cout;
using std::endl;

#include <vector>
using std::vector;

#include "XmlDisplayer.hpp"

#include "Rule_clock.hpp"
#include "Rule_name.hpp"
#include "Rule_hour.hpp"
#include "Rule_min.hpp"
#include "Rule_sec.hpp"
#include "Rule_sep.hpp"
#include "Terminal_StringValue.hpp"
#include "Terminal_NumericValue.hpp"

void* XmlDisplayer::visit(const Rule_clock* rule)
{
  if (!terminal) cout << endl;
  cout << "<clock>";
  terminal = false;
  visitRules(rule->rules);
  if (!terminal) cout << endl;
  cout << "</clock>";
  terminal = false;
  return NULL;
}

void* XmlDisplayer::visit(const Rule_name* rule)
{
  if (!terminal) cout << endl;
  cout << "<name>";
  terminal = false;
  visitRules(rule->rules);
  if (!terminal) cout << endl;
  cout << "</name>";
  terminal = false;
  return NULL;
}

void* XmlDisplayer::visit(const Rule_hour* rule)
{
  if (!terminal) cout << endl;
  cout << "<hour>";
  terminal = false;
  visitRules(rule->rules);
  if (!terminal) cout << endl;
  cout << "</hour>";
  terminal = false;
  return NULL;
}

void* XmlDisplayer::visit(const Rule_min* rule)
{
  if (!terminal) cout << endl;
  cout << "<min>";
  terminal = false;
  visitRules(rule->rules);
  if (!terminal) cout << endl;
  cout << "</min>";
  terminal = false;
  return NULL;
}

void* XmlDisplayer::visit(const Rule_sec* rule)
{
  if (!terminal) cout << endl;
  cout << "<sec>";
  terminal = false;
  visitRules(rule->rules);
  if (!terminal) cout << endl;
  cout << "</sec>";
  terminal = false;
  return NULL;
}

void* XmlDisplayer::visit(const Rule_sep* rule)
{
  if (!terminal) cout << endl;
  cout << "<sep>";
  terminal = false;
  visitRules(rule->rules);
  if (!terminal) cout << endl;
  cout << "</sep>";
  terminal = false;
  return NULL;
}

void* XmlDisplayer::visit(const Terminal_StringValue* value)
{
  cout << value->spelling;
  terminal = true;
  return NULL;
}

void* XmlDisplayer::visit(const Terminal_NumericValue* value)
{
  cout << value->spelling;
  terminal = true;
  return NULL;
}

void* XmlDisplayer::visitRules(const vector<const Rule*>& rules)
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
