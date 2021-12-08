package es.unizar.urlshortener.core.usecases
import es.unizar.urlshortener.core.*
import java.util.Date
import java.time.OffsetDateTime

/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
 */
interface CreateShortUrlUseCase {
    fun create(url: String, data: ShortUrlProperties, qr: Boolean, days: Int): ShortUrl
}

interface CreateQRUseCase {
    fun createQR( url: ShortUrl) : ShortUrl
}

class CreateQRUseCaseImplementor : CreateQRUseCase {
    override fun createQR( url: ShortUrl) : ShortUrl {
        return url;
    }
}
/**
 * Implementation of [CreateShortUrlUseCase].
 */
class CreateShortUrlUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val validatorService: ValidatorService,
    private val hashService: HashService,
    private val createQRUseCase: CreateQRUseCase,
    private val securityService: SecurityService
) : CreateShortUrlUseCase {
    override fun create(url: String, data: ShortUrlProperties, qr: Boolean, days: Int): ShortUrl {
        if (validatorService.isValid(url)) {
            if (securityService.isSafe(url)) {
                val id: String = hashService.hasUrl(url)
                val expiredDate = OffsetDateTime.now().plusDays(days.toLong())
                val su = ShortUrl(
                    hash = id,
                    redirection = Redirection(target = url),
                    properties = ShortUrlProperties(
                        safe = data.safe,
                        ip = data.ip,
                        sponsor = data.sponsor
                    ),
                    created = OffsetDateTime.now(),
                    expired = expiredDate
                )
                shortUrlRepository.save(su)
                if (qr){
                }
                return su
            }else {
                throw UnsafeUrlException(url)
            }
        } else {
            throw InvalidUrlException(url)
        } 
    }
}
