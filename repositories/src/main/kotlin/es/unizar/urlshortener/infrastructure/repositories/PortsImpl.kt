package es.unizar.urlshortener.infrastructure.repositories

import es.unizar.urlshortener.core.*

/**
 * Implementation of the port [ClickRepositoryService].
 */
class ClickRepositoryServiceImpl(
    private val clickEntityRepository: ClickEntityRepository
) : ClickRepositoryService {
    override fun save(cl: Click): Click = clickEntityRepository.save(cl.toEntity()).toDomain()
}

/**
 * Implementation of the port [ShortUrlRepositoryService].
 */
class ShortUrlRepositoryServiceImpl(
    private val shortUrlEntityRepository: ShortUrlEntityRepository
) : ShortUrlRepositoryService {
    override fun findByKey(id: String): ShortUrl? = shortUrlEntityRepository.findByHash(id)?.toDomain()

    override fun save(su: ShortUrl): ShortUrl = shortUrlEntityRepository.save(su.toEntity()).toDomain()

    override fun deleteById(id: String) {
        shortUrlEntityRepository.deleteById(id)
    }
}

/**
 * Implementation of the port [CodeQRRepositoryService].
 */
class CodeQRRepositoryServiceImpl(
    private val CodeQREntityRepository: QRCodeEntityRepository
) : CodeQRRepositoryService {
    override fun findByKey(id: String): QRCode? = CodeQREntityRepository.findByHash(id)?.toDomain()

    override fun save(qrCode: QRCode): QRCode = CodeQREntityRepository.save(qrCode.toEntity()).toDomain()
}

