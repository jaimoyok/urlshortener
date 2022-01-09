package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.Redirection
import es.unizar.urlshortener.core.RedirectionNotFound
import es.unizar.urlshortener.core.UnsafeUrlException
import es.unizar.urlshortener.core.UnreachableUrlException
import es.unizar.urlshortener.core.NotReadyUrlException
import es.unizar.urlshortener.core.ShortUrlRepositoryService
import es.unizar.urlshortener.core.ShortUrl
import java.time.OffsetDateTime

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
        ?.filterSafe()
        ?.filterReachable()
        ?.redirection
        ?: throw RedirectionNotFound(key)

    private fun ShortUrl.filterExpired() = when {
            expiredUseCase.isExpired(this) -> null
            else -> this
        }

    private fun ShortUrl.filterSafe() :ShortUrl {
        if (this.properties.safe != null){
            if(this.properties.safe == true){
                return this
            } 
            else{
                throw UnsafeUrlException(this.hash)
            }
        }
        else{
            throw NotReadyUrlException(this.hash)
        }
    }

    private fun ShortUrl.filterReachable() :ShortUrl {
        if (this.reachable != null){
            if(this.reachable == true){
                return this
            } 
            else{
                throw UnreachableUrlException(this.hash)
            }
        }
        else{
            throw NotReadyUrlException(this.hash)
        }
    }
}

    

class ExpiredUseCaseImpl (
    private val shortUrlRepository: ShortUrlRepositoryService
): ExpiredUseCase {
    override fun isExpired(shortUrl : ShortUrl) : Boolean {
        var diff = shortUrl.expired.compareTo(OffsetDateTime.now())
        if(diff < 0){
            shortUrlRepository.deleteById(shortUrl.hash)
            return true
        } 
        else return false
    }
}
