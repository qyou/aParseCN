/* -----------------------------------------------------------------------------
 * ParserMain.cpp
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Tue Dec 29 14:23:44 CST 2015
 *
 * -----------------------------------------------------------------------------
 */

#include <string>
using std::string;

#include <iostream>
using std::cout;
using std::endl;
#include <fstream>
using std::ifstream;

#include <map>
using std::map;

#include <exception>
using std::exception;

#include <algorithm>
using std::transform;

#include "Parser.hpp"
#include "ParserContext.hpp"
#include "ParserException.hpp"
#include "Rule.hpp"

#include "XmlDisplayer.hpp"

int main(int argc, char* argv[])
{
  string* lc_argv = new string[argc];
  for (int i = 0; i < argc; i++)
  {
    lc_argv[i] = argv[i];
    transform(lc_argv[i].begin(), lc_argv[i].end(), lc_argv[i].begin(), tolower);
  }

  map<string, string> arguments;
  string error("");
  bool ok = argc > 1;

  if (ok)
  {
    arguments["Trace"] = "Off";
    arguments["Rule"] = "clock";

    for (int i = 1; i < argc; i++)
    {
      if (lc_argv[i].compare("-trace") == 0)
      {
        arguments["Trace"] = "On";
      }
      else if (lc_argv[i].compare("-visitor") == 0)
      {
        if (i < argc - 1) arguments["Visitor"] = argv[++i];
      }
      else if (lc_argv[i].compare("-file") == 0)
      {
        if (i < argc - 1) arguments["File"] = argv[++i];
      }
      else if (lc_argv[i].compare("-string") == 0)
      {
        if (i < argc - 1) arguments["String"] = argv[++i];
      }
      else if (lc_argv[i].compare("-rule") == 0)
      {
        if (i < argc - 1) arguments["Rule"] = argv[++i];
      }
      else
      {
        error = string("unknown argument: ") + argv[i];
        ok = false;
      }
    }
  }

  delete[] lc_argv;

  if (ok)
  {
    if (arguments.find("File") == arguments.end() &&
        arguments.find("String") == arguments.end())
    {
      error = "insufficient arguments: -file or -string required";
      ok = false;
    }
  }

  const Rule* rule = NULL;

  if (!ok)
  {
    cout << "error: " << error << endl;
    cout << "usage: Parser [-rule rulename] [-trace] <-file file | -string string> [-visitor visitor]" << endl;
  }
  else
  {
    try
    {
      if (arguments.find("File") != arguments.end())
      {
        ifstream file(arguments["File"].c_str());
        if (!file.is_open())
        {
          cout << "io error: unable to open " << arguments["File"] << endl;
        }
        else
        {
          rule = 
            Parser::parse(
              arguments["Rule"], 
              file, 
              arguments["Trace"].compare("On") == 0);
        }
      }
      else if (arguments.find("String") != arguments.end())
      {
        rule = 
          Parser::parse(
            arguments["Rule"], 
            arguments["String"], 
            arguments["Trace"].compare("On") == 0);
      }

      if (arguments.find("Visitor") != arguments.end())
      {
        string argument = arguments["Visitor"];

        if (argument.compare("XmlDisplayer") == 0)
        {
          XmlDisplayer visitor;
          rule->accept(visitor);
          cout << endl;
        }
      }
    }
    catch (ParserException& e)
    {
      cout << "parser error: " << e.what();
    }
    catch (exception& e)
    {
      cout << "error: " << e.what() << endl;
    }
    catch (...)
    {
      cout << "unknown error" << endl;
    }
  }

  if (rule != NULL) delete rule;

  return rule != NULL ? 0 : 1;
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
