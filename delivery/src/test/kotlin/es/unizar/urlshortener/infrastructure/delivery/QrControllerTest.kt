// package es.unizar.urlshortener.infrastructure.delivery

// import es.unizar.urlshortener.core.*
// import es.unizar.urlshortener.core.usecases.CreateShortUrlUseCase
// import es.unizar.urlshortener.core.usecases.LogClickUseCase
// import es.unizar.urlshortener.infrastructure.delivery.QRController
// import org.junit.jupiter.api.Test
// import org.mockito.BDDMockito.given
// import org.mockito.BDDMockito.never
// import org.mockito.kotlin.verify
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
// import org.springframework.boot.test.mock.mockito.MockBean
// import org.springframework.http.MediaType
// import org.springframework.test.context.ContextConfiguration
// import org.springframework.test.web.servlet.MockMvc
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
// import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
// import java.time.OffsetDateTime

// @WebMvcTest
// @ContextConfiguration(classes = [
//     UrlShortenerControllerImpl::class,
//     RestResponseEntityExceptionHandler::class])
// class QrControllerTest {

//     @Autowired
//     private lateinit var mockMvc: MockMvc

//     @MockBean
//     private lateinit var qrController: QRController

//     @MockBean
//     private lateinit var logClickUseCase: LogClickUseCase

//     @MockBean
//     private lateinit var createShortUrlUseCase: CreateShortUrlUseCase

//     @Test
//     fun `redirectTo returns a redirect when the returns a not found when the qr does not exist`() {
//         given(qrController.redirectTo("key")).willAnswer { throw QrNotFound("key") }

//         mockMvc.perform(get("/getQR/{id}", "key"))
//             .andExpect(status().isTemporaryRedirect)
//             .andExpect(redirectedUrl("http://example.com/"))

//         verify(logClickUseCase).logClick("key", ClickProperties(ip = "127.0.0.1"))
//     }

//     @Test
//     fun `redirectTo returns a not found when the key does not exist`() {
//         given(redirectUseCase.redirectTo("key"))
//             .willAnswer { throw RedirectionNotFound("key") }

//         mockMvc.perform(get("/tiny-{id}", "key"))
//             .andDo(print())
//             .andExpect(status().isNotFound)
//             ct(jsonPath("$.statusCode").value(404)).andExpe

//         verify(logClickUseCase, never()).logClick("key", ClickProperties(ip = "127.0.0.1"))
//     }

//     @Test
//     fun `creates returns a basic redirect if it can compute a hash`() {
//         given(createShortUrlUseCase.create(
//             url = "http://example.com/",
//             data = ShortUrlProperties(ip = "127.0.0.1"),
//             days = 0
//         )).willReturn(ShortUrl("f684a3c4", Redirection("http://example.com/"), expired = OffsetDateTime.now().plusDays(0.toLong())))


//         mockMvc.perform(post("/api/link")
//             .param("url", "http://example.com/")
//             .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
//             .andDo(print())
//             .andExpect(status().isCreated)
//             .andExpect(redirectedUrl("http://localhost/tiny-f684a3c4"))
//             .andExpect(jsonPath("$.url").value("http://localhost/tiny-f684a3c4"))
//     }

//     @Test
//     fun `creates returns bad request if it can compute a hash`() {
//         given(createShortUrlUseCase.create(
//             url = "ftp://example.com/",
//             data = ShortUrlProperties(ip = "127.0.0.1"),
//             days = 0
//         )).willAnswer { throw InvalidUrlException("ftp://example.com/") }

//         mockMvc.perform(post("/api/link")
//             .param("url", "ftp://example.com/")
//             .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
//             .andExpect(status().isBadRequest)
//             .andExpect(jsonPath("$.statusCode").value(400))
//     }
// }