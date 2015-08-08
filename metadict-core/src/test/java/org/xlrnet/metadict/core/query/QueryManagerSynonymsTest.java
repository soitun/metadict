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

package org.xlrnet.metadict.core.query;

import com.google.common.collect.Lists;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.ProducesAlternative;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.xlrnet.metadict.api.language.Language;
import org.xlrnet.metadict.api.query.*;
import org.xlrnet.metadict.core.main.MetadictCore;
import org.xlrnet.metadict.core.strategies.DefaultExecutionStrategy;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * Test for merging of synonyms.
 */
@RunWith(CdiRunner.class)
public class QueryManagerSynonymsTest {

    final SynonymGroup synonymGroup1 = ImmutableSynonymGroup.builder()
            .setBaseMeaning(ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "BASE_MEANING_1"))
            .addSynonym(ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "SYNONYM_1"))
            .build();

    final SynonymGroup synonymGroup2 = ImmutableSynonymGroup.builder()
            .setBaseMeaning(ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "BASE_MEANING_2"))
            .addSynonym(ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "SYNONYM_2"))
            .build();


    final SynonymEntry synonymEntry1 = ImmutableSynonymEntry.builder()
            .setBaseObject(ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "TEST_WORD_1"))
            .addSynonymGroup(synonymGroup1)
            .build();

    final QueryStepResult stepResult1 = new QueryStepResultBuilder()
            .setEngineQueryResult(
                    ImmutableBilingualQueryResult.builder()
                            .addSynonymEntry(synonymEntry1)
                            .build()
            )
            .setQueryStep(new BilingualQueryStep())
            .build();

    final SynonymEntry synonymEntry2 = ImmutableSynonymEntry.builder()
            .setBaseObject(ImmutableDictionaryObject.createSimpleObject(Language.ENGLISH, "TEST_WORD_2"))
            .addSynonymGroup(synonymGroup2)
            .build();

    final QueryStepResult stepResult2 = new QueryStepResultBuilder()
            .setEngineQueryResult(
                    ImmutableBilingualQueryResult.builder()
                            .addSynonymEntry(synonymEntry2)
                            .build()
            )
            .setQueryStep(new BilingualQueryStep())
            .build();

    @Mock
    @Produces
    MetadictCore coreMock = Mockito.mock(MetadictCore.class);

    @Inject
    private QueryManager queryManager;

    @Produces
    @ProducesAlternative
    @DefaultExecutionStrategy
    public QueryPlanExecutionStrategy produceQueryPlanExecutionStrategyMock() {
        Collection<QueryStepResult> resultMock = Lists.newArrayList(stepResult1, stepResult2);
        return new QueryPlanExecutionStrategyMock(resultMock);
    }

    @Produces
    @ProducesAlternative
    public QueryPlanningStrategy produceQueryPlanningStrategyMock() {
        return new NullQueryPlanningStrategy();
    }

    @Test
    public void testExecuteQuery() throws Exception {
        QueryRequest request = new QueryRequestBuilder(coreMock).setQueryString("TEST").build();
        QueryResponse queryResponse = queryManager.executeQuery(request);

        Collection<SynonymEntry> synonymEntries = queryResponse.getSynonymEntries();

        assertTrue("First synonym is missing", synonymEntries.contains(synonymEntry1));
        assertTrue("Second synonym is missing", synonymEntries.contains(synonymEntry2));
    }

}