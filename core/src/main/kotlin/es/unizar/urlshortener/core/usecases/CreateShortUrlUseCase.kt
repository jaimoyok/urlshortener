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
    fun create(url: String, data: ShortUrlProperties, qr: Boolean): ShortUrl
}

inteface CreateQRUseCase{
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
    private val createQRUseCase: CreateQRUseCase

) : CreateShortUrlUseCase {
    override fun create(url: String, data: ShortUrlProperties, qr: Boolean, days: Integer): ShortUrl =
        if (validatorService.isValid(url)) {
            val id: String = hashService.hasUrl(url)
            val expired: OffsetDateTime? = null
            if(days != 0){
                expired = OffsetDateTime.now().plusDays(days)
            } 
            val su = ShortUrl(
                hash = id,
                redirection = Redirection(target = url),
                properties = ShortUrlProperties(
                    safe = data.safe,
                    ip = data.ip,
                    sponsor = data.sponsor
                ),
                created = OffsetDateTime.now()
                expired = expired
            )
            shortUrlRepository.save(su)
            if (qr){

            }
        } else {
            throw InvalidUrlException(url)
        }
}
