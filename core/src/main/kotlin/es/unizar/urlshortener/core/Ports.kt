package es.unizar.urlshortener.core

/**
 * [ClickRepositoryService] is the port to the repository that provides persistence to [Clicks][Click].
 */
interface ClickRepositoryService {
    fun save(cl: Click): Click
}

/**
 * [ShortUrlRepositoryService] is the port to the repository that provides management to [ShortUrl][ShortUrl].
 */
interface ShortUrlRepositoryService {
    fun deleteById(id: String)
    fun findByKey(id: String): ShortUrl?
    fun save(su: ShortUrl): ShortUrl
}

/**
 * [ValidatorService] is the port to the service that validates if an url can be shortened.
 *
 * **Note**: It is a design decision to create this port. It could be part of the core .
 */
interface ValidatorService {
    fun isValid(url: String): Boolean
}

/**
 * [SecurityService] is the port to the service that validates if an url is safe.
 *
 * **Note**: It is a design decision to create this port. It could be part of the core .
 */
interface SecurityService {
    fun isSafe(url: String): Boolean
}

/**
 * [ReachabilityService] is the port to the service that validates if an url is reachable.
 *
 * **Note**: It is a design decision to create this port. It could be part of the core .
 */
interface ReachabilityService {
    fun isReachable(url: String): Boolean
}

/**
 * [HashService] is the port to the service that creates a hash from a URL.
 *
 * **Note**: It is a design decision to create this port. It could be part of the core .
 */
interface HashService {
    fun hasUrl(url: String): String
}
/**
 * [QRService] is the port to the service that generates a qr from a short URL and a format.
 *
 * **Note**: It is a design decision to create this port. It could be part of the core .
 */
interface QRService{
    fun generateQR(url: String, format: QRFormat): ByteArray
}

/**
 * [QRCodeRepositoryService] is the port to the repository that provides management to [QRCode].
 */
interface CodeQRRepositoryService {
    fun findByKey(id: String): QRCode?
    fun save(qrCode: QRCode): QRCode
}
