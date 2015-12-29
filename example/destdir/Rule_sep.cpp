/* -----------------------------------------------------------------------------
 * Rule_sep.cpp
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

#include "Rule_sep.hpp"
#include "Visitor.hpp"
#include "ParserAlternative.hpp"
#include "ParserContext.hpp"

#include "Terminal_StringValue.hpp"

Rule_sep::Rule_sep(
  const string& spelling, 
  const vector<const Rule*>& rules) : Rule(spelling, rules)
{
}

Rule_sep::Rule_sep(const Rule_sep& rule) : Rule(rule)
{
}

Rule_sep& Rule_sep::operator=(const Rule_sep& rule)
{
  Rule::operator=(rule);
  return *this;
}

const Rule_sep* Rule_sep::clone() const
{
  return new Rule_sep(this->spelling, this->rules);
}

void* Rule_sep::accept(Visitor& visitor) const
{
  return visitor.visit(this);
}

const Rule_sep* Rule_sep::parse(ParserContext& context)
{
  context.push("sep");

  bool parsed = true;
  int s0 = context.index;
  ParserAlternative a0(s0);

  vector<const ParserAlternative*> as1;
  parsed = false;
  {
    int s1 = context.index;
    ParserAlternative a1(s1);
    parsed = true;
    if (parsed)
    {
      bool f1 = true;
      int c1 = 0;
      for (int i1 = 0; i1 < 1 && f1; i1++)
      {
        const Rule* rule = Terminal_StringValue::parse(context, ":");
        if ((f1 = rule != NULL))
        {
          a1.add(*rule, context.index);
          c1++;
          delete rule;
        }
      }
      parsed = c1 == 1;
    }
    if (parsed)
    {
      as1.push_back(new ParserAlternative(a1));
    }
    context.index = s1;
  }

  const ParserAlternative* b = ParserAlternative::getBest(as1);

  if ((parsed = b != NULL))
  {
    a0.add(b->rules, b->end);
    context.index = b->end;
  }

  for (vector<const ParserAlternative*>::const_iterator a = as1.begin(); a != as1.end(); a++)
  {
    delete *a;
  }

  const Rule* rule = NULL;
  if (parsed)
  {
    rule = new Rule_sep(context.text.substr(a0.start, a0.end - a0.start), a0.rules);
  }
  else
  {
    context.index = s0;
  }

  context.pop("sep", parsed);

  return (Rule_sep*)rule;
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
