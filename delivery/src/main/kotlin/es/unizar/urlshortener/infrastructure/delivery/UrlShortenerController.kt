package es.unizar.urlshortener.infrastructure.delivery

import es.unizar.urlshortener.core.ClickProperties
import es.unizar.urlshortener.core.QRCode2
import es.unizar.urlshortener.core.Secu
import es.unizar.urlshortener.core.Reach
import es.unizar.urlshortener.core.ShortUrlProperties
import es.unizar.urlshortener.core.QRFormat
import es.unizar.urlshortener.core.usecases.LogClickUseCase
import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
import es.unizar.urlshortener.core.usecases.RedirectUseCase
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.servlet.http.HttpServletRequest
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired

/**
 * The specification of the controller.
 */
interface UrlShortenerController {

    /**
     * Redirects and logs a short url identified by its [id].
     *
     * **Note**: Delivery of use cases [RedirectUseCase] and [LogClickUseCase].
     */
    fun redirectTo(id: String, request: HttpServletRequest): ResponseEntity<Void>


    fun getQR(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Void>
    /**
     * Creates a short url from details provided in [data].
     *
     * **Note**: Delivery of use case [CreateShortUrlUseCase].
     */
    fun shortener(data: ShortUrlDataIn, request: HttpServletRequest, createQR: Boolean): ResponseEntity<ShortUrlDataOut>

}

/**
 * Data required to create a short url.
 */
data class ShortUrlDataIn(
    val qr: Boolean? = null,
    val url: String,
    val sponsor: String? = null,
    val qrTypeImage: String? = null,
    val qrWidth: Int? = null,
    val qrHeight: Int? = null,
    val qrColor: String? = null,
    val qrBackground: String? = null,
    val qrErrorCorrectionLevel: String? = null,
    val days: Int  
)

/**
 * Data returned after the creation of a short url.
 */
data class ShortUrlDataOut(
    val qr: URI? = null,
    val url: URI? = null,
    val properties: Map<String, Any> = emptyMap()
)


/**
 * The implementation of the controller.
 *
 * **Note**: Spring Boot is able to discover this [RestController] without further configuration.
 */
@RestController
class UrlShortenerControllerImpl(
    val redirectUseCase: RedirectUseCase,
    val logClickUseCase: LogClickUseCase,
    val createShortUrlUseCase: CreateShortUrlUseCase
) : UrlShortenerController {

    @Autowired
    private val template: RabbitTemplate? = null


    @GetMapping("/tiny-{id:.*}")
    override fun redirectTo(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Void> =
        redirectUseCase.redirectTo(id).let {
            logClickUseCase.logClick(id, ClickProperties(ip = request.remoteAddr))
            val h = HttpHeaders()
            h.location = URI.create(it.target)
            ResponseEntity<Void>(h, HttpStatus.valueOf(it.mode))
        }

    @GetMapping("/qr/{id:.*}")
    override fun getQR(@PathVariable id: String, request: HttpServletRequest): ResponseEntity<Void> =
        redirectUseCase.redirectTo(id).let {
            logClickUseCase.logClick(id, ClickProperties(ip = request.remoteAddr))
            val h = HttpHeaders()
            h.location = URI.create(it.target)
            ResponseEntity<Void>(h, HttpStatus.valueOf(it.mode))
        }

    @PostMapping("/api/link", consumes = [ MediaType.APPLICATION_FORM_URLENCODED_VALUE ])
    override fun shortener(data: ShortUrlDataIn, request: HttpServletRequest, createQR: Boolean): ResponseEntity<ShortUrlDataOut> =
        createShortUrlUseCase.create(
            url = data.url,
            data = ShortUrlProperties(
                ip = request.remoteAddr,
                sponsor = data.sponsor
            ),
            days = data.days
        ).let {

            //print(data)
            val h = HttpHeaders()
            val url = linkTo<UrlShortenerControllerImpl> { redirectTo(it.hash, request) }.toUri()
            h.location = url
            val response : ShortUrlDataOut
            if (data.qr != null && data.qr != false){
                var fmt = QRFormat()
                val typeImage: String = data.qrTypeImage ?: fmt.typeImage
                val width: Int = data.qrWidth ?: fmt.width
                val height: Int = data.qrHeight ?: fmt.height
                val color: String = data.qrColor ?: fmt.color
                val background: String = data.qrBackground ?: fmt.background
                val errorCorrectionLevel: String = data.qrErrorCorrectionLevel ?: fmt.errorCorrectionLevel
                fmt = QRFormat(typeImage,width,height,color,background,errorCorrectionLevel)

                //Encolar tarea generación QRCODE usando RabbitMQ
                template?.convertAndSend("QRCODE_exchange", "QRCODE_routingKey", QRCode2(it.hash, fmt))
                template?.convertAndSend("SECURITY_exchange", "SECURITY_routingKey",Secu(it.hash))
                template?.convertAndSend("REACH_exchange", "REACH_routingKey", Reach(it.hash))


                val qr = linkTo<QRControllerImpl> { redirectTo(it.hash, request) }.toUri()
                response = ShortUrlDataOut(
                    url = url,
                    qr = qr,
                    properties = mapOf(
                        "safe" to it.properties.safe
                    )
                )
            } else{
                response = ShortUrlDataOut(
                    url = url,
                    properties = mapOf(
                        "safe" to it.properties.safe
                    )
                )
            }
            ResponseEntity<ShortUrlDataOut>(response, h, HttpStatus.CREATED)
        }
 }
