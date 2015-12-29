/* -----------------------------------------------------------------------------
 * Rule_sep.hpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#ifndef Rule_sep_hpp
#define Rule_sep_hpp

#include <string>
#include <vector>

#include "Rule.hpp"

class Visitor;
class ParserContext;

class Rule_sep : public Rule
{
public:
  Rule_sep(const std::string& spelling, const std::vector<const Rule*>& rules);
  Rule_sep(const Rule_sep& rule);

  Rule_sep& operator=(const Rule_sep& rule);

  const Rule_sep* clone(void) const;

  static const Rule_sep* parse(ParserContext& context);

  void* accept(Visitor& visitor) const;
};

#endif
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
