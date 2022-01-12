package es.unizar.urlshortener.core.usecases
import es.unizar.urlshortener.core.*
import java.time.OffsetDateTime

/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
 */
interface CreateShortUrlUseCase {
    fun create(url: String, data: ShortUrlProperties, days: Int): ShortUrl
}

/**
 * Implementation of [CreateShortUrlUseCase].
 */
class CreateShortUrlUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val validatorService: ValidatorService,
    private val hashService: HashService,
    private val securityService: SecurityService,
    private val reachabilityService: ReachabilityService,
    private val qrUseCase: QRGeneratorUseCase
) : CreateShortUrlUseCase {
    /*
        create adds a new shortUrl in ShortUrlRepository
    */
    override fun create(url: String, data: ShortUrlProperties, days: Int): ShortUrl {
        if (validatorService.isValid(url)) {

            val id: String = hashService.hasUrl(url)
            val expiredDate = OffsetDateTime.now().plusDays(days.toLong())
            val su = ShortUrl(
                hash = id,
                redirection = Redirection(target = url),
                properties = ShortUrlProperties(
                    safe = null,
                    ip = data.ip,
                    sponsor = data.sponsor
                ),
                reachable = null,
                created = OffsetDateTime.now(),
                expired = expiredDate
                )
            shortUrlRepository.save(su)
           return su
        } else {
            throw InvalidUrlException(url)
        } 
    }
}
