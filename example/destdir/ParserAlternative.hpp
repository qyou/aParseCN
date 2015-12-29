/* -----------------------------------------------------------------------------
 * ParserAlternative.hpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#ifndef ParserAlternative_hpp
#define ParserAlternative_hpp

#include <string>
#include <vector>

#include "Rule.hpp"

class ParserAlternative
{
public:
  std::vector<const Rule*> rules;
  unsigned int start;
  unsigned int end;

  ParserAlternative(unsigned int start);
  ParserAlternative(const ParserAlternative& alternative);

  ParserAlternative& operator=(const ParserAlternative& alternative);

  ~ParserAlternative();

  void add(const Rule& rule, unsigned int end);
  void add(const std::vector<const Rule*>& rules, unsigned int end);

  static const ParserAlternative* getBest(vector<const ParserAlternative*> alternatives);
};

#endif
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
