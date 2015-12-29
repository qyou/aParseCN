package io.github.qyou;

public class ChineseParserExample {

	public static void main(String[] args) {
		String[] passArgs = {
				"-language","java",
				"-main",
				"-visitors","XmlDisplayer",
				"src/system.abnf"
		};
		
		ChineseParser.main(passArgs);
	}

}
