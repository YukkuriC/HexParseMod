package io.yukkuric.hexparse_client.parsers;

import java.util.List;
import java.util.regex.Pattern;

public class CodeCutter {
    static Pattern pCommentLine = Pattern.compile("//.*");
    static Pattern pLineBreak = Pattern.compile("[\\r\\n]");
    static Pattern pCommentBlock = Pattern.compile("/\\*.*?\\*/");
    static Pattern pTokens = Pattern.compile("\\\\|\\(|\\)|\\[|]|[\\w./\\-:]+");

    public static List<String> splitCode(String code) {
        code = pCommentLine.matcher(code).replaceAll(" ");

        code = pLineBreak.matcher(code).replaceAll(" ");
        code = pCommentBlock.matcher(code).replaceAll(" ");
        return pTokens.matcher(code).results().map(x -> x.group()).toList();
    }
}
