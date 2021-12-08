package es.unizar.urlshortener.core

import java.time.OffsetDateTime
import java.util.*

/**
 * A [Click] captures a request of redirection of a [ShortUrl] identified by its [hash].
 */
data class Click(
    val hash: String,
    val properties: ClickProperties = ClickProperties(),
    val created: OffsetDateTime = OffsetDateTime.now()
)

/**
 * A [ShortUrl] is the mapping between a remote url identified by [redirection] and a local short url identified by [hash].
 */
data class ShortUrl(
    val hash: String,
    val redirection: Redirection,
    val created: OffsetDateTime = OffsetDateTime.now(),
    val properties: ShortUrlProperties = ShortUrlProperties(),
    val expired: OffsetDateTime
    //val qr: Boolean?
)

/**
 * A [Redirection] specifies the [target] and the [status code][mode] of a redirection.
 * By default, the [status code][mode] is 307 TEMPORARY REDIRECT.
 */
data class Redirection(
    val target: String,
    val mode: Int = 307
)

/**
 * A [ShortUrlProperties] is the bag of properties that a [ShortUrl] may have.
 */
data class ShortUrlProperties(
    val ip: String? = null,
    val sponsor: String? = null,
    val safe: Boolean = true,
    val owner: String? = null,
    val country: String? = null
)

/**
 * A [ClickProperties] is the bag of properties that a [Click] may have.
 */
data class ClickProperties(
    val ip: String? = null,
    val referrer: String? = null,
    val browser: String? = null,
    val platform: String? = null,
    val country: String? = null
)
/**
 * A [QRFormat] specifies the format of a QR generation [QRService].
 * By default, the [height] and [width] are 500.
 * By default, the [color] is "0xFF000000".
 * By default, the [background] is "0xFFFFFFFF"
 * By default, the [typeImage] is "image/png"
 */
data class QRFormat (
    val typeImage: String = "PNG",
    val width: Int = 600,
    val height: Int = 600,
    val color: String = "0xFF000000",  //Negro
    val background: String = "0xFFFFFFFF",  //Fondo Blanco
    val errorCorrectionLevel: String = "L"
)