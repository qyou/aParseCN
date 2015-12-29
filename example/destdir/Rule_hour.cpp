/* -----------------------------------------------------------------------------
 * Rule_hour.cpp
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

#include "Rule_hour.hpp"
#include "Visitor.hpp"
#include "ParserAlternative.hpp"
#include "ParserContext.hpp"

#include "Terminal_NumericValue.hpp"

Rule_hour::Rule_hour(
  const string& spelling, 
  const vector<const Rule*>& rules) : Rule(spelling, rules)
{
}

Rule_hour::Rule_hour(const Rule_hour& rule) : Rule(rule)
{
}

Rule_hour& Rule_hour::operator=(const Rule_hour& rule)
{
  Rule::operator=(rule);
  return *this;
}

const Rule_hour* Rule_hour::clone() const
{
  return new Rule_hour(this->spelling, this->rules);
}

void* Rule_hour::accept(Visitor& visitor) const
{
  return visitor.visit(this);
}

const Rule_hour* Rule_hour::parse(ParserContext& context)
{
  context.push("hour");

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
        unsigned int g1 = context.index;
        vector<const ParserAlternative*> as2;
        parsed = false;
        {
          int s2 = context.index;
          ParserAlternative a2(s2);
          parsed = true;
          if (parsed)
          {
            bool f2 = true;
            int c2 = 0;
            for (int i2 = 0; i2 < 1 && f2; i2++)
            {
              const Rule* rule = Terminal_NumericValue::parse(context, "%x30-31", "[\\x30-\\x31]", 1);
              if ((f2 = rule != NULL))
              {
                a2.add(*rule, context.index);
                c2++;
                delete rule;
              }
            }
            parsed = c2 == 1;
          }
          if (parsed)
          {
            bool f2 = true;
            int c2 = 0;
            for (int i2 = 0; i2 < 1 && f2; i2++)
            {
              const Rule* rule = Terminal_NumericValue::parse(context, "%x30-39", "[\\x30-\\x39]", 1);
              if ((f2 = rule != NULL))
              {
                a2.add(*rule, context.index);
                c2++;
                delete rule;
              }
            }
            parsed = c2 == 1;
          }
          if (parsed)
          {
            as2.push_back(new ParserAlternative(a2));
          }
          context.index = s2;
        }

        const ParserAlternative* b = ParserAlternative::getBest(as2);

        if ((parsed = b != NULL))
        {
          a1.add(b->rules, b->end);
          context.index = b->end;
        }

        for (vector<const ParserAlternative*>::const_iterator a = as2.begin(); a != as2.end(); a++)
        {
          delete *a;
        }

        f1 = context.index > g1;
        if (parsed) c1++;
      }
      parsed = c1 == 1;
    }
    if (parsed)
    {
      as1.push_back(new ParserAlternative(a1));
    }
    context.index = s1;
  }
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
        unsigned int g1 = context.index;
        vector<const ParserAlternative*> as2;
        parsed = false;
        {
          int s2 = context.index;
          ParserAlternative a2(s2);
          parsed = true;
          if (parsed)
          {
            bool f2 = true;
            int c2 = 0;
            for (int i2 = 0; i2 < 1 && f2; i2++)
            {
              const Rule* rule = Terminal_NumericValue::parse(context, "%x32", "[\\x32]", 1);
              if ((f2 = rule != NULL))
              {
                a2.add(*rule, context.index);
                c2++;
                delete rule;
              }
            }
            parsed = c2 == 1;
          }
          if (parsed)
          {
            bool f2 = true;
            int c2 = 0;
            for (int i2 = 0; i2 < 1 && f2; i2++)
            {
              const Rule* rule = Terminal_NumericValue::parse(context, "%x30-33", "[\\x30-\\x33]", 1);
              if ((f2 = rule != NULL))
              {
                a2.add(*rule, context.index);
                c2++;
                delete rule;
              }
            }
            parsed = c2 == 1;
          }
          if (parsed)
          {
            as2.push_back(new ParserAlternative(a2));
          }
          context.index = s2;
        }

        const ParserAlternative* b = ParserAlternative::getBest(as2);

        if ((parsed = b != NULL))
        {
          a1.add(b->rules, b->end);
          context.index = b->end;
        }

        for (vector<const ParserAlternative*>::const_iterator a = as2.begin(); a != as2.end(); a++)
        {
          delete *a;
        }

        f1 = context.index > g1;
        if (parsed) c1++;
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
    rule = new Rule_hour(context.text.substr(a0.start, a0.end - a0.start), a0.rules);
  }
  else
  {
    context.index = s0;
  }

  context.pop("hour", parsed);

  return (Rule_hour*)rule;
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
