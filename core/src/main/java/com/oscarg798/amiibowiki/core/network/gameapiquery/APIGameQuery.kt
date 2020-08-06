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

package com.oscarg798.amiibowiki.core.network.gameapiquery

import com.oscarg798.amiibowiki.core.extensions.QueryWithSearchAndWhereClauseIsNotSupportedException

data class APIGameQuery(
    val fields: Set<String>? = null,
    val whereClause: WhereClause? = null,
    val searchClause: String? = null,
    val limit: Int? = null
) {

    override fun toString(): String {
        if (isQueryValid()) {
            throw QueryWithSearchAndWhereClauseIsNotSupportedException()
        }
        var response = ""

        response += if (fields == null) {
            "$FIELD_CLAUSE $ALL_FIELDS_CLAUSE$CLAUSE_SEPARATOR"
        } else {
            "$FIELD_CLAUSE ${fields.joinToString(FIELD_SEPARATOR)}$CLAUSE_SEPARATOR"
        }

        searchClause?.let {
            response += "$SEARCH_CLAUSE \"$searchClause\"$CLAUSE_SEPARATOR"
        }

        whereClause?.let {
            response += whereClause.toString()
            response += CLAUSE_SEPARATOR
        }

        limit?.let {
            response += "$LIMIT_CLAUSE $limit$CLAUSE_SEPARATOR"
        }

        return response
    }

    private fun isQueryValid() = searchClause != null && whereClause != null
}

private const val SEARCH_CLAUSE = "search"
private const val ALL_FIELDS_CLAUSE = "*"
private const val FIELD_CLAUSE = "fields"
private const val LIMIT_CLAUSE = "limit"
private const val CLAUSE_SEPARATOR = ";"
private const val FIELD_SEPARATOR = ","

sealed class WhereClause {
    data class Id(val id: Int) : WhereClause() {
        override fun toString(): String = "where id=$id"
    }

    data class In(val ids: Collection<Int>) : WhereClause() {
        override fun toString(): String = "where id=(${ids.joinToString(",")})"
    }
}
