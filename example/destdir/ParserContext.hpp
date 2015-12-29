/* -----------------------------------------------------------------------------
 * ParserContext.hpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#ifndef ParserContext_hpp
#define ParserContext_hpp

#include <string>
#include <vector>

class ParserContext
{
public:
  const std::string text;
  unsigned int index;

  ParserContext(const std::string& text, bool traceOn);
  ~ParserContext();

  void push(const std::string& rulename);
  void push(const std::string& rulename, const std::string& trace);
  void pop(const std::string& function, bool result);
  const std::vector<std::string>& getErrorStack(void) const;
  unsigned int getErrorIndex(void) const;

private:
  std::vector<unsigned int> startStack;
  std::vector<std::string> callStack;
  std::vector<std::string> errorStack;
  unsigned int errorIndex;
  unsigned int level;

  bool traceOn;
};

#endif
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
