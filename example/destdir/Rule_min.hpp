/* -----------------------------------------------------------------------------
 * Rule_min.hpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#ifndef Rule_min_hpp
#define Rule_min_hpp

#include <string>
#include <vector>

#include "Rule.hpp"

class Visitor;
class ParserContext;

class Rule_min : public Rule
{
public:
  Rule_min(const std::string& spelling, const std::vector<const Rule*>& rules);
  Rule_min(const Rule_min& rule);

  Rule_min& operator=(const Rule_min& rule);

  const Rule_min* clone(void) const;

  static const Rule_min* parse(ParserContext& context);

  void* accept(Visitor& visitor) const;
};

#endif
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
