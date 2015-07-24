/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Jakob Hendeß
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.xlrnet.metadict.api.query;

import com.google.common.base.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xlrnet.metadict.api.language.GrammaticalForm;
import org.xlrnet.metadict.api.language.GrammaticalGender;
import org.xlrnet.metadict.api.language.Language;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The class {@link DictionaryObject} represents a part of an {@link BilingualEntry}. It effectively represents the
 * description of an entry in a certain language. This includes the word/phrase itself, but also additional information
 * like the grammatical gender, a description, possible abbreviations and a special domain where the word/phrase is
 * primarily used. It provides also additional fields like hyphenations, meanings and synonyms.
 */
public interface DictionaryObject extends Serializable {

    /**
     * Returns an abbreviation for this object.
     * <p/>
     * Example:
     * The abbreviation for "for example" is "e.g.".
     *
     * @return an abbreviation for this object.
     */
    @Nullable
    String getAbbreviation();

    /**
     * Returns a map of all irregular forms of this object. These include e.g. special plural forms or gender-dependent
     * forms (in case of nouns) or irregular tenses on verbs.
     *
     * @return a map of all irregular forms of this object.
     */
    @NotNull
    Map<GrammaticalForm, String> getAdditionalForms();

    /**
     * Returns a description for the object. The description field should be used for everything that is not a meaning
     * and does not fit in the other fields of this interface.
     *
     * @return a description for the object.
     */
    @Nullable
    String getDescription();

    /**
     * Returns the special domain this object is used in. This can e.g. be something like "tech." if the object is only
     * used in technical contexts.
     *
     * @return the special domain this object is used in.
     */
    @Nullable
    String getDomain();

    /**
     * Returns the general form of this object. The general form should be the most generic form of a word/phrase
     * whenever possible. This method should always return a non-null value, unless there is no specific general form
     * of a word.
     * That can e.g. be the case if the object represents an irregular word where each grammatical gender has a
     * different form and thus no general form exists.
     * <p/>
     * Example use cases:
     * <ul>
     * <li>Singular form of nouns</li>
     * <li>Present tense of verbs</li>
     * <li>Basic form of regular adjectives</li>
     * <li>Any type of phrase</li>
     * </ul>
     *
     * @return the general form of this object.
     */
    @NotNull
    String getGeneralForm();

    /**
     * Returns the grammatical gender of this object. This field should be used on nouns (or other types) where the
     * general form has a grammatical gender.
     *
     * @return the grammatical gender of this object.
     */
    @Nullable
    GrammaticalGender getGrammaticalGender();

    /**
     * Returns the {@link Language} this object is written in. The language has to be set always and may not be null.
     *
     * @return the language this object is written in.
     */
    @NotNull
    Language getLanguage();

    /**
     * Returns a string with a pronunciation of the general form of this entry. The pronunciation should be written
     * down using the international phonetic alphabet (IPA).
     *
     * @return a string with a pronunciation of the general form of this entry.
     */
    @Nullable
    String getPronunciation();

    /**
     * Returns a list of different meanings this objects may have. Each meaning's description should be a separate
     * string.
     * <p/>
     * Example:
     * If the word is "bench", a meaning might be "A long seat for several people, typically made of wood or stone."
     *
     * @return a list of different meanings this objects may have.
     */
    @NotNull
    Optional<List<String>> getMeanings();

    /**
     * Returns a list of syllables that this word consists of. Each element of the list represents a single syllable.
     * <p/>
     * Example:
     * If a word is divided like <i>dic|tion|ary</i>, the list should look like {"dic", "tion", "ary" }.
     *
     * @return a list of syllables that this word consists of.
     */
    @NotNull
    Optional<List<String>> getSyllabification();

    /**
     * Returns a list of synonyms for this object. Each element of the list represents a single synonym.
     *
     * @return a list of synonyms for this object.
     */
    @NotNull
    Optional<List<String>> getSynonyms();

    /**
     * Returns a list of alternately written forms for this entry.
     * <p/>
     * Example:
     * An alternative way of writing "color" might be "colour".
     *
     * @return a list of alternately written forms for this entry.
     */
    @NotNull
    Optional<List<String>> getAlternateForms();

}