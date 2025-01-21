package io.yukkuric.hexparse.parsers;

import io.yukkuric.hexparse.config.HexParseConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class CodeCutter {
    static Pattern pCommentLine = Pattern.compile("//.*");
    static Pattern pLineBreak = Pattern.compile("[\\r\\n]");
    static Pattern pCommentBlock = Pattern.compile("/\\*.*?\\*/");
    static Pattern pTokens = Pattern.compile("\\\\|\\(|\\)|\\[|]|[\\w./\\-:#]+");

    static Pattern pLineStart = Pattern.compile("^\\s*");

    public static List<String> splitCode(String code) {
        var showIndent = HexParseConfig.parseCommentsAndIndents();
        return splitCode(code, showIndent);
    }

    static String toIndent(MatchResult match) {
        return "tab_%d ".formatted(match.group().length());
    }

    public static List<String> splitCode(String code, boolean addIndent) {
        code = pCommentLine.matcher(code).replaceAll(" ");

        // auto insert indent
        if (addIndent) {
            List<String> filtered = new ArrayList<>();
            var firstLine = true;
            for (var l : code.split("\n")) {
                if (l.isBlank()) continue;
                if (firstLine) firstLine = false;
                else l = pLineStart.matcher(l).replaceAll(CodeCutter::toIndent);
                filtered.add(l);
            }
            code = String.join(" ", filtered);
        }

        code = pLineBreak.matcher(code).replaceAll(" ");
        code = pCommentBlock.matcher(code).replaceAll(" ");
        return pTokens.matcher(code).results().map(x -> x.group()).toList();
    }
}
