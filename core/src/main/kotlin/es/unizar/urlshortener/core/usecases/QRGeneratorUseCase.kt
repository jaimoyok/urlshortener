package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*

/**
 * Given an id returns a [ByteArray] that contains the QR code
 *
 * **Note**: This is an example of functionality.
 */
interface QRGeneratorUseCase {
    fun generateQR(id: String, formatoQR: QRFormat): ByteArray
}

/**
 * Implementation of [QRGeneratorUseCase].
 */
class QRGeneratorUseCaseImpl(
        private val shortUrlRepository: ShortUrlRepositoryService,
        private val qrService: QRService
) : QRGeneratorUseCase {
    override fun generateQR(id: String, formatoQR: QRFormat): ByteArray =
        //Comprobando id/hash
        if (shortUrlRepository.findByKey(id) != null)
            qrService.generateQR("http://localhost:8080/tiny-$id", formatoQR)
        else
            throw RedirectionNotFound(id)
}
