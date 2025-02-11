package es.unizar.urlshortener.core

class InvalidUrlException(val url: String) : Exception("[$url] does not follow a supported schema")

class RedirectionNotFound(val key: String) : Exception("[$key] is not known")

class QrNotFound(val key: String) : Exception("[$key] does not have a QR")

class UnsafeUrlException(val url: String) : Exception("[$url] is not safe according to google safe browsing")

class UnreachableUrlException(val url: String) : Exception("[$url] is not reachable")

class NotReadyUrlException(val url: String) : Exception("[$url] is not ready yet try again later")