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
//interface GetQRUseCase {
    //ojo
    //fun getQR( hash: String) : String
//}
/*interface CreateQRUseCase {
    fun createQR( url: ShortUrl) : ShortUrl
}

class GetQRUseCaseImpl : GetQRUseCase {
    override fun getQR( hash: String) : String = "xD"
} */

/*class CreateQRUseCaseImplementor : CreateQRUseCase {
    override fun createQR( url: String ) : String {
        return url;
    }
}*/

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

    override fun create(url: String, data: ShortUrlProperties, days: Int): ShortUrl {
        if (validatorService.isValid(url)) {

            val id: String = hashService.hasUrl(url)
            //var aux: String? = null
            //if (qr){
            //    aux = qrService.generateQR(id);
            //}
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
                //qr = aux
                )
            shortUrlRepository.save(su)
           return su
        } else {
            throw InvalidUrlException(url)
        } 
    }
}
