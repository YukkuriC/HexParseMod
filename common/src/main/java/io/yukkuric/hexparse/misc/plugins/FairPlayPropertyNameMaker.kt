package io.yukkuric.hexparse.misc.plugins

import java.util.*

// basically https://github.com/miyucomics/hexcellular/blob/e1c7d2f0c0eaee263aba92222d1767dc0632a6f1/src/main/java/miyucomics/hexcellular/PropertyNamer.kt
// under MIT

private val consonants = charArrayOf('j', 'k', 'l', 'm', 'n', 'p', 's', 't', 'w')
private val vowels = charArrayOf('a', 'e', 'i', 'o', 'u')
private val bannedSyllables = listOf("ji", "ti", "wo", "wu")

private val weights = listOf(2, 5, 3)
private val scanned = weights.runningFold(0) { sum, weight -> sum + weight }.drop(1)
private val peak = scanned.last()

object FairPlayPropertyNameMaker {
    lateinit var RNG: Random

    @JvmStatic
    fun generatePropertyName(original: String): String {
        // seeded from input string
        RNG = Random(original.hashCode().toLong())

        val word = StringBuilder()
        if (RNG.nextBoolean()) {
            word.append(vowels.random(RNG))
            if (RNG.nextBoolean())
                word.append('n')
        }

        val numberOfSyllables = generateNumberOfSyllables()
        repeat(numberOfSyllables) {
            val syllable = generateSyllable(word.endsWith('n'))
            word.append(syllable)
        }

        return word.toString()
    }

    private fun generateNumberOfSyllables(): Int {
        val index = RNG.nextInt(1, peak + 1)
        return scanned.indexOfFirst { index <= it } + 1
    }

    private fun generateSyllable(wasNasal: Boolean): String {
        var consonant = consonants.random(RNG)
        var vowel = vowels.random(RNG)
        var syllable = "$consonant$vowel"
        while (wasNasal && (consonant == 'm' || consonant == 'n') || syllable in bannedSyllables) {
            consonant = consonants.random(RNG)
            vowel = vowels.random(RNG)
            syllable = "$consonant$vowel"
        }
        if (RNG.nextBoolean() && !syllable.startsWith('n'))
            syllable += 'n'
        return syllable
    }

    private fun CharArray.random(rng: Random) = this[rng.nextInt(this.size)]
}
