package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.Redirection
import es.unizar.urlshortener.core.RedirectionNotFound
import es.unizar.urlshortener.core.ShortUrlRepositoryService
import es.unizar.urlshortener.core.ShortUrl

/**
 * Given a key returns a [Redirection] that contains a [URI target][Redirection.target]
 * and an [HTTP redirection mode][Redirection.mode].
 *
 * **Note**: This is an example of functionality.
 */
interface RedirectUseCase {
    fun redirectTo(key: String): Redirection
}

interface ExpiredUseCase {
    fun isExpired(shortUrl: ShortUrl): Boolean
}

/**
 * Implementation of [RedirectUseCase].
 */
class RedirectUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val expiredUseCase : ExpiredUseCase
) : RedirectUseCase {
    override fun redirectTo(key: String) = shortUrlRepository
        .findByKey(key)
        ?.filterExpired()
        ?.redirection
        ?: throw RedirectionNotFound(key)

    private fun ShortUrl.filterExpired() = when {
            expiredUseCase.isExpired(this) -> null
            else -> this
        }
    }   

class ExpiredUseCaseImpl : ExpiredUseCase {
    override fun isExpired(shortUrl : ShortUrl) : Boolean {
        return false
    }
}
