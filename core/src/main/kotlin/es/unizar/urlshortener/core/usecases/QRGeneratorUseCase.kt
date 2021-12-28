package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*

/**
 * Given an id returns a [ByteArray] that contains the QR code
 *
 * **Note**: This is an example of functionality.
 */
interface QRGeneratorUseCase {
    fun generateQR(id: String): ByteArray
}

/**
 * Implementation of [QRGeneratorUseCase].
 */
class QRGeneratorUseCaseImpl(
        private val shortUrlRepository: ShortUrlRepositoryService,
        private val qrService: QRService,
        private val uRIReachableService : ReachabilityService,
        private val qrCodeRepository: CodeQRRepositoryService
) : QRGeneratorUseCase {
    override fun generateQR(id: String): ByteArray {
        //Check id/hash
        val redirection: Redirection = shortUrlRepository.findByKey(id)?.redirection
            ?: throw RedirectionNotFound(id)
        //Check url is reachable
        if (!uRIReachableService.isReachable(redirection.target)) {
            throw UnreachableUrlException(redirection.target)
        }
        //if the hash exists in the db, the program returns the qr code stored in the db
        //in other case, the program generates the qr code with the default format
        return qrCodeRepository.findByKey(id)?.qrCode
            ?: qrService.generateQR("http://localhost:8080/tiny-$id", QRFormat())
    }

}
