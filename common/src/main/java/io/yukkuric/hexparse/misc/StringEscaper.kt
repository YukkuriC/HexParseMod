package io.yukkuric.hexparse.misc

class StringEscaper {
    companion object {
        fun escape(string: String): String {
            var string = string.replace("\\", "\\\\")
            string = string.replace("\r", "\\r")
            string = string.replace("\n", "\\n")
            string = string.replace("\"", "\\\"")
            return string
        }


        fun unescape(string: String): String {
            val unescapedString = StringBuilder()
            var currentPartStartIndex = 0


            // node.length() - 1 so that the charAt(index + 1) is always safe
            var index = 0
            while (index < string.length - 1) {
                if (string[index] == '\\') {
                    unescapedString.append(string, currentPartStartIndex, index)
                    unescapedString.append(
                        when (string[index + 1]) {
                            'n' -> '\n'
                            'r' -> '\r'
                            '\\' -> '\\'
                            '"' -> '"'
                            else -> throw IllegalArgumentException("invalid escape sequence")
                        }
                    )
                    currentPartStartIndex = index + 2
                    // skip the escaped char
                    index++
                }
                index++
            }

            unescapedString.append(string, currentPartStartIndex, string.length)

            require(!(
                        string[string.length - 1] == '\\'
                        && string.getOrNull(string.length - 2) != '\\'
                    )) { "illegal escape pattern, trailing backslash" }


            return unescapedString.toString()
        }
    }
}