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

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * Implementation of {@link ExternalContent} interface.
 */
public class ExternalContentImpl implements ExternalContent {

    private static final long serialVersionUID = -3405350644385979888L;

    private final String title;

    private final String description;

    private final URL link;

    ExternalContentImpl(String title, String description, URL link) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExternalContentImpl)) return false;
        ExternalContentImpl that = (ExternalContentImpl) o;
        return Objects.equal(title, that.title) &&
                Objects.equal(description, that.description) &&
                Objects.equal(link, that.link);
    }

    /**
     * Returns a description for the external content. This should be longer than the title of the content.
     *
     * @return a description for the external content.
     */
    @Nullable
    @Override
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns a link to the external content.
     *
     * @return a link to the external content.
     */
    @NotNull
    @Override
    public URL getLink() {
        return this.link;
    }

    /**
     * Returns the title of the external content. This should be shorter than the content's description.
     *
     * @return the title of the external content.
     */
    @NotNull
    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title, description, link);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("title", title)
                .add("description", description)
                .add("link", link)
                .toString();
    }
}
