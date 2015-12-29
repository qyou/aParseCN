/* -----------------------------------------------------------------------------
 * Parser.cpp
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

#include <map>
using std::map;

#include <iostream>
using std::istream;

#include <algorithm>
using std::transform;

#include "Parser.hpp"

#include "ParserContext.hpp"
#include "ParserException.hpp"

#include "Rule.hpp"
#include "Rule_clock.hpp"
#include "Rule_name.hpp"
#include "Rule_hour.hpp"
#include "Rule_min.hpp"
#include "Rule_sec.hpp"
#include "Rule_sep.hpp"

typedef const Rule* (*pParser)(ParserContext&);

static map<string, pParser> buildParserMap(void)
{
  map<string, pParser> parsers;

  parsers["clock"] = (pParser)Rule_clock::parse;
  parsers["name"] = (pParser)Rule_name::parse;
  parsers["hour"] = (pParser)Rule_hour::parse;
  parsers["min"] = (pParser)Rule_min::parse;
  parsers["sec"] = (pParser)Rule_sec::parse;
  parsers["sep"] = (pParser)Rule_sep::parse;

  return parsers;
}

static map<string, pParser> parsers = buildParserMap();

const Rule* Parser::parse(const string& rulename, const string& text)
{
  return parse(rulename, text, false);
}

const Rule* Parser::parse(const string& rulename, istream& in)
{
  return parse(rulename, in, false);
}

const Rule* Parser::parse(const string& rulename, const string& text, bool trace)
{
  string lcRulename(rulename);
  transform(lcRulename.begin(), lcRulename.end(), lcRulename.begin(), tolower);

  ParserContext context(text, trace);

  const Rule* rule = NULL;

  pParser parser = parsers[lcRulename];

  if (parser != NULL)
  {
    rule = parser(context);
  }
  else
  {
    throw ParserException(
      string("unknown rule \"") + rulename + "\"",
      context.text,
      context.getErrorIndex(),
      context.getErrorStack());
  }

  if (rule == NULL)
  {
    throw ParserException(
      string("rule \"") + context.getErrorStack().back() + "\" failed",
      context.text,
      context.getErrorIndex(),
      context.getErrorStack());
  }

  if (context.text.length() > context.index)
  {
    ParserException primaryError(
      "extra data found",
      context.text,
      context.index,
      vector<string>());

    if (context.getErrorIndex() > context.index)
    {
      ParserException secondaryError(
        "rule \"" + context.getErrorStack().back() + "\" failed",
        context.text,
        context.getErrorIndex(),
        context.getErrorStack());

      primaryError.setCause(secondaryError);
    }

    throw primaryError;
  }

  return rule;
}

const Rule* Parser::parse(const string& rulename, istream& in, bool trace)
{
  string out;
  int ch;

  while ((ch = in.get()) != EOF)
    out += ch;

  return parse(rulename, out, trace);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
