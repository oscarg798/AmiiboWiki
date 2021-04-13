package com.oscarg798.amiibowiki.core.failures

class ArgumentNotFoundException(argumentName: String) : IllegalArgumentException("$argumentName argument not found")
