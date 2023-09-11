package com.ajblass.bluetoothmessages.data

/**
 * Encapsulates request data changes.
 */
sealed class EventResult<out SuccessData : Any, out ErrorData : Any> {

	/** A request was made but no response has been received yet */
	data object Loading : EventResult<Nothing, Nothing>()

	/** A request was made and a response was received */
	data class Success<out SuccessData : Any>(val data: SuccessData) : EventResult<SuccessData, Nothing>()

	/** A request was made but the response resulted in an error */
	data class Error<out ErrorData : Any>(val info: ErrorData) : EventResult<Nothing, ErrorData>()

}
