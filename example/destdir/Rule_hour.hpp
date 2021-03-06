/* -----------------------------------------------------------------------------
 * Rule_hour.hpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#ifndef Rule_hour_hpp
#define Rule_hour_hpp

#include <string>
#include <vector>

#include "Rule.hpp"

class Visitor;
class ParserContext;

class Rule_hour : public Rule
{
public:
  Rule_hour(const std::string& spelling, const std::vector<const Rule*>& rules);
  Rule_hour(const Rule_hour& rule);

  Rule_hour& operator=(const Rule_hour& rule);

  const Rule_hour* clone(void) const;

  static const Rule_hour* parse(ParserContext& context);

  void* accept(Visitor& visitor) const;
};

#endif
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
