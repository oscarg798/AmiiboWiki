/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.core

import com.oscarg798.amiibowiki.core.extensions.QueryWithSearchAndWhereClauseIsNotSupportedException
import com.oscarg798.amiibowiki.core.network.gameapiquery.APIGameQuery
import com.oscarg798.amiibowiki.core.network.gameapiquery.WhereClause
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Test

class APIGameQueryTest {

    @Test
    fun `given a empty query when query is converted to string then it should return just all fields`() {
        val query = APIGameQuery()

        query.toString() shouldBeEqualTo "fields *;"
    }

    @Test
    fun `given a query with some fields when query is converted to string then it should return a query with the fields sent`() {
        val query = APIGameQuery(fields = FIELDS)

        query.toString() shouldBeEqualTo "fields id,game,name;"
    }

    @Test
    fun `given a query with some fields and a where clause when its converted to string then it should return the fields and the query`() {
        val query =
            APIGameQuery(fields = setOf("id", "game", "name"), whereClause = WhereClause.Id(ID))

        query.toString() shouldBeEqualTo "fields id,game,name;where id=14759;"
    }

    @Test
    fun `given a query just with where clause when its converted to string then it should return all fields and there clause`() {
        val query = APIGameQuery(whereClause = WhereClause.Id(ID))

        query.toString() shouldBeEqualTo "fields *;where id=14759;"
    }

    @Test
    fun `given a query with fields, where clause and limit when its converted to string then it should return the fields, the where clause and the limit`() {
        val query = APIGameQuery(fields = FIELDS, whereClause = WhereClause.Id(ID), limit = 50)

        query.toString() shouldBeEqualTo "fields id,game,name;where id=14759;limit 50;"
    }

    @Test
    fun `given a query without fields, where clause and limit when its converted to string then it should return the all fields, the where clause and the limit`() {
        val query = APIGameQuery(whereClause = WhereClause.Id(ID), limit = 50)

        query.toString() shouldBeEqualTo "fields *;where id=14759;limit 50;"
    }

    @Test
    fun `given a query without fields, without where clause and with limit when its converted to string then it should return the all fields and  the limit`() {
        val query = APIGameQuery(limit = 50)

        query.toString() shouldBeEqualTo "fields *;limit 50;"
    }

    @Test
    fun `given a query fields, without where clause and with limit when its converted to string then it should return the all fields and  the limit`() {
        val query = APIGameQuery(fields = FIELDS, limit = 50)

        query.toString() shouldBeEqualTo "fields id,game,name;limit 50;"
    }

    @Test
    fun `given a query without field and with search when its converted to string then it should return all fields and search`() {
        val query = APIGameQuery(searchClause = SEARCH_QUERY)

        query.toString() shouldBeEqualTo """
            fields *;search "zelda";
        """.trimIndent()
    }

    @Test
    fun `given a query without field, with limit and  with search when its converted to string then it should return all fields, limit clause and search`() {
        val query = APIGameQuery(searchClause = SEARCH_QUERY, limit = 40)

        query.toString() shouldBeEqualTo """
            fields *;search "zelda";limit 40;
        """.trimIndent()
    }

    @Test
    fun `given a query with fielsd, with limit and  with search when its converted to string then it should return all fields, limit clause and search`() {
        val query = APIGameQuery(searchClause = SEARCH_QUERY, limit = 40, fields = FIELDS)

        query.toString() shouldBeEqualTo """
            fields id,game,name;search "zelda";limit 40;
        """.trimIndent()
    }

    @Test
    fun `given a query with fielsd and  with search when its converted to string then it should return all fields, limit clause and search`() {
        val query = APIGameQuery(searchClause = SEARCH_QUERY, fields = FIELDS)

        query.toString() shouldBeEqualTo """
            fields id,game,name;search "zelda";
        """.trimIndent()
    }

    @Test(expected = QueryWithSearchAndWhereClauseIsNotSupportedException::class)
    fun `given a query without field, with limit, where clause and  with search when its converted to string then it should thrown`() {
        val query = APIGameQuery(
            searchClause = SEARCH_QUERY, limit = 40,
            whereClause = WhereClause.Id(
                ID
            )
        )

        query.toString() shouldBeEqualTo """
            fields *;search "zelda";limit 40;
        """.trimIndent()
    }

    @Test(expected = QueryWithSearchAndWhereClauseIsNotSupportedException::class)
    fun `given a query without field, with where clause and with search when its converted to string then it should thrown`() {
        val query = APIGameQuery(
            searchClause = SEARCH_QUERY,
            whereClause = WhereClause.Id(
                ID
            )
        )

        query.toString() shouldBeEqualTo """
            fields *;search "zelda";limit 40;
        """.trimIndent()
    }

    @Test(expected = QueryWithSearchAndWhereClauseIsNotSupportedException::class)
    fun `given a query with fields with where clause and with search when its converted to string then it should thrown`() {
        val query = APIGameQuery(
            fields = FIELDS,
            searchClause = SEARCH_QUERY,
            whereClause = WhereClause.Id(ID)
        )

        query.toString() shouldBeEqualTo """
            fields *;search "zelda";limit 40;
        """.trimIndent()
    }
}

private val SEARCH_QUERY = "zelda"
private val FIELDS = setOf("id", "game", "name")
private const val ID = 14759
