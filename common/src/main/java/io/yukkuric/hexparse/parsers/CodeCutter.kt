package io.yukkuric.hexparse.parsers

import io.yukkuric.hexparse.config.HexParseConfig
import io.yukkuric.hexparse.misc.StringEscaper
import java.util.regex.Pattern

object CodeCutter {
    var pCommentLine: Pattern = Pattern.compile("//.*")
    var pLineBreak: Pattern = Pattern.compile("\\r?\\n")
    var pCommentBlock: Pattern = Pattern.compile("(?s)/\\*.*?\\*/")
    var pTokens: Pattern = Pattern.compile("\\\\|\\(|\\)|\\[|]|[\\w./\\-:#\u0100-\uffff]+")

    var pLineStart: Pattern = Pattern.compile("^\\s*")

    @Throws(IllegalArgumentException::class)
    @JvmStatic
    fun splitCode(code: String): MutableList<String> {

        val commentMode = HexParseConfig.getCommentParsingMode()
        val indentMode = HexParseConfig.getIndentParsingMode()
        return splitCode(
            code,
            indentMode == HexParseConfig.CommentParsingMode.ALL,
            commentMode == HexParseConfig.CommentParsingMode.ALL
        )
    }

    private fun toIndent(match: String): String {
        return "tab_%d ".format(match.length)
    }

    /** (Token, Remaining) */
    private fun consumeToken(code: String): Pair<String, String> {
        val matcher = pTokens.matcher(code)
        require(matcher.find())



        return Pair(matcher.group(), code.substring(matcher.end()))
    }

    private fun commentToCommentString(comment: String): String {
        return "c\"${StringEscaper.escape(comment)}\""
    }

    private fun consumeLineComment(code: String, commentsToIota: Boolean): Pair<String?, String> {
        val matcher = pCommentLine.matcher(code)
        matcher.find()
        val commentContents = matcher.group().substring(2)
        val commentTokenIfRequested = if (commentsToIota) commentToCommentString(commentContents) else null
        return Pair(commentTokenIfRequested,code.substring(matcher.end()))
    }

    private fun consumeBlockComment(code: String, commentsToIota: Boolean): Pair<String?, String> {
        val matcher = pCommentBlock.matcher(code)
        matcher.find()

        return if (commentsToIota) {
            var match = matcher.group()
            match = match.substring(2, match.length - 2)


            Pair(
                commentToCommentString(match),
                code.substring(matcher.end())
            )
        } else {
            Pair(
                null,
                code.substring(matcher.end())
            )
        }
    }

    private var VALID_WHITESPACE = hashSetOf(
        ';',
        ',',
        ' ',
        '\t',
    )
    private fun consumeWhiteSpace(code: String): Pair<String?, String> {
        var index = 0

        while (VALID_WHITESPACE.contains(code.getOrNull(index))) index++

        return Pair(null, code.substring(index))
    }
    private fun consumeNewline(code: String, addIndent: Boolean): Pair<String?, String> {
        val input = pLineBreak.matcher(code).replaceFirst("") // remove newline

        return (if (addIndent) {
                val matcher = pLineStart.matcher(input)
                matcher.find()

                Pair(toIndent(matcher.group()), input.substring(matcher.end()))
            }
            else {
                val matcher = pLineStart.matcher(input)
                matcher.find()
                Pair(null, input.substring(matcher.end()))
            })
    }

    private fun consumeString(code: String): Pair<String, String> {
        var index = 1 // skip first '"'
        while (code[index] != '"') {
            if (code[index] == '\\') {
                index++ // skip any escaped character '"'
            }
            index++

            if (index >= code.length) {
                throw IllegalArgumentException("unclosed string literal: $code")
            }
        }

        return Pair(code.substring(0..index), code.substring(index + 1))
    }

    private fun consumeComment(code: String, commentsToIota: Boolean): Pair<String?, String> {
        return when (code.getOrNull(1)) {
            '*' -> {
                consumeBlockComment(code, commentsToIota)
            }
            '/' -> {
                consumeLineComment(code, commentsToIota)
            }
            else -> throw IllegalArgumentException("invalid comment: $code")
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun splitCode(code: String, addIndent: Boolean, commentsToIota: Boolean = true): MutableList<String> {
        var code = code
        val list = mutableListOf<String>()
        while (code.isNotEmpty()) {
            var (token, newCode) = (when(code[0]) {
                '/' -> consumeComment(code, commentsToIota)
                '"' -> consumeString(code)
                in VALID_WHITESPACE -> consumeWhiteSpace(code)
                '\r', '\n' -> consumeNewline(code, addIndent)
                else -> consumeToken(code)
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
