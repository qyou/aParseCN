/* -----------------------------------------------------------------------------
 * Rule_sec.hpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#ifndef Rule_sec_hpp
#define Rule_sec_hpp

#include <string>
#include <vector>

#include "Rule.hpp"

class Visitor;
class ParserContext;

class Rule_sec : public Rule
{
public:
  Rule_sec(const std::string& spelling, const std::vector<const Rule*>& rules);
  Rule_sec(const Rule_sec& rule);

  Rule_sec& operator=(const Rule_sec& rule);

  const Rule_sec* clone(void) const;

  static const Rule_sec* parse(ParserContext& context);

  void* accept(Visitor& visitor) const;
};

#endif
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
