package com.radumarinescu.components.common

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GlobalResponse<T : Any?>(
    val data: T? = null,
    val success: Boolean,
    val message: String? = null,
    val exceptionType: String? = null
)