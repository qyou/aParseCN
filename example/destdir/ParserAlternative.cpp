/* -----------------------------------------------------------------------------
 * ParserAlternative.cpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#include <string>
using std::string;

#include <vector>
using std::vector;

#include "ParserAlternative.hpp"

ParserAlternative::ParserAlternative(unsigned int start) :
start(start), end(start)
{
}

ParserAlternative::ParserAlternative(const ParserAlternative& alternative) :
start(alternative.start), end(alternative.end)
{
  vector<const Rule*>::const_iterator r;

  for (r = alternative.rules.begin(); r != alternative.rules.end(); r++)
    this->rules.push_back((*r)->clone());
}

ParserAlternative& ParserAlternative::operator=(const ParserAlternative& alternative)
{
  if (&alternative != this)
  {
    start = alternative.start;
    end = alternative.end;

    vector<const Rule*>::const_iterator r;

    for (r = rules.begin(); r != rules.end(); r++)
      delete *r;

    rules.empty();

    for (r = alternative.rules.begin(); r != alternative.rules.end(); r++)
      rules.push_back((*r)->clone());
  }
  return *this;
}

ParserAlternative::~ParserAlternative()
{
  vector<const Rule*>::const_iterator r;

  for (r = rules.begin(); r != rules.end(); r++)
    delete *r;
}

void ParserAlternative::add(const Rule& rule, unsigned int end)
{
  rules.push_back(rule.clone());
  this->end = end;
}

void ParserAlternative::add(const std::vector<const Rule*>& rules, unsigned int end)
{
  vector<const Rule*>::const_iterator r;

  for (r = rules.begin(); r != rules.end(); r++)
    this->rules.push_back((*r)->clone());

  this->end = end;
}

const ParserAlternative* ParserAlternative::getBest(vector<const ParserAlternative*> alternatives)
{
  vector<const ParserAlternative*>::const_iterator a;

  const ParserAlternative* best = NULL;

  for (a = alternatives.begin(); a != alternatives.end(); a++)
  {
    if (best == NULL || (*a)->end > best->end)
    {
      best = *a;
    }
  }

  return best;
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
