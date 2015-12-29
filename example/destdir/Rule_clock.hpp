/* -----------------------------------------------------------------------------
 * Rule_clock.hpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#ifndef Rule_clock_hpp
#define Rule_clock_hpp

#include <string>
#include <vector>

#include "Rule.hpp"

class Visitor;
class ParserContext;

class Rule_clock : public Rule
{
public:
  Rule_clock(const std::string& spelling, const std::vector<const Rule*>& rules);
  Rule_clock(const Rule_clock& rule);

  Rule_clock& operator=(const Rule_clock& rule);

  const Rule_clock* clone(void) const;

  static const Rule_clock* parse(ParserContext& context);

  void* accept(Visitor& visitor) const;
};

#endif
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
