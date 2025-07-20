package io.yukkuric.hexparse.parsers

import io.yukkuric.hexparse.config.HexParseConfig
import java.util.regex.MatchResult
import java.util.regex.Pattern

object CodeCutter {
    var pCommentLine: Pattern = Pattern.compile("//.*")
    var pLineBreak: Pattern = Pattern.compile("[\\r\\n]")
    var pCommentBlock: Pattern = Pattern.compile("/\\*.*?\\*/")
    var pTokens: Pattern = Pattern.compile("\\\\|\\(|\\)|\\[|]|[\\w./\\-:#\u0100-\uffff]+")

    var pLineStart: Pattern = Pattern.compile("^\\s*")

    @Throws(IllegalArgumentException::class)
    @JvmStatic
    fun splitCode(code: String): MutableList<String> {

        val showIndent = HexParseConfig.parseCommentsAndIndents()
        return splitCode(code, showIndent)

    }

    private fun toIndent(match: MatchResult): String {
        return "tab_%d ".format(match.group().length)
    }

    /** (Token, Remaining) */
    private fun tokenizeToken(input: String): Pair<String, String> {
        val matcher = pTokens.matcher(input)
        require(matcher.find())



        return Pair(matcher.group(), input.substring(matcher.end()))
    }

    private fun consumeLineComment(input: String): Pair<String?, String> {
        val matcher = pCommentLine.matcher(input)
        matcher.find()
        return Pair(null ,input.substring(matcher.end()))
    }

    private fun consumeBlockComment(input: String): Pair<String?, String> {
        val matcher = pCommentBlock.matcher(input)
        matcher.find()
        return Pair(null ,input.substring(matcher.end()))
    }

    private var VALID_WHITESPACE = hashSetOf(
        ';',
        ',',
        ' ',
        '\t',
    )
    private fun consumeWhiteSpace(input: String): Pair<String?, String> {
        var index = 0

        while (VALID_WHITESPACE.contains(input.getOrNull(index))) index++

        return Pair(null, input.substring(index))
    }
    private fun consumeNewline(input: String, addIndent: Boolean): Pair<String?, String> {
        val input = input.substring(1) // remove newline
        return Pair(null, if (addIndent) {
            pLineStart.matcher(input).replaceFirst(CodeCutter::toIndent)
        } else {
            val matcher = pLineStart.matcher(input)
            matcher.find()
            input.substring(matcher.end())
        })
    }

    private fun consumeString(code: String): Pair<String, String> {
        var index = 1 // skip first '"'
        while (code[index] != '"') {
            if (code[index] == '\\' && code.getOrNull(index + 1) == '"') {
                index++ // skip escaped '"'
            }
            index++
        }

        return Pair(code.substring(0..index), code.substring(index + 1))
    }

    private fun consumeComment(code: String): Pair<String?, String> {
        return when (code.getOrNull(1)) {
            '*' -> {
                consumeBlockComment(code)
            }
            '/' -> {
                consumeLineComment(code)
            }
            else -> throw IllegalArgumentException()
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun splitCode(code: String, addIndent: Boolean): MutableList<String> {
        var code = code
        val list = mutableListOf<String>()
        while (code.isNotEmpty()) {
            var (token, newCode) = (when(code[0]) {
                '/' -> consumeComment(code)
                '"' -> consumeString(code)
                in VALID_WHITESPACE -> consumeWhiteSpace(code)
                '\n' -> consumeNewline(code, addIndent)
                else -> tokenizeToken(code)
            })
            if (code == newCode) {

                // nothing is getting consumed, infinite loop!
                // throw IllegalArgumentException("illegal characters in: ${code}");
                // Chop off the offending character
                newCode = code.substring(1)

            }
            code = newCode
            if (token != null) {
                list.add(token)
            }
        }

        return list
    }
}
