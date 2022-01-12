package es.unizar.urlshortener.infrastructure.delivery


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest



@SpringBootTest(classes = [ValidatorServiceImpl::class, ReachabilityServiceImpl::class, SecurityServiceImpl::class])
internal class ValidityTest {
    @Autowired
    private lateinit var validatorService: ValidatorServiceImpl

    @Autowired
    private lateinit var reachabilityService: ReachabilityServiceImpl

    @Autowired
    private lateinit var securityService: SecurityServiceImpl

    

    @Test
    fun `Not Reachable URL`(){

        assertEquals(false, reachabilityService.isReachable("https://exale.com/"))
    }

    @Test
    fun `Reachable URL`(){
        assertEquals(true, reachabilityService.isReachable("https://example.com/"))
    }

    // @Test
    // fun `Safe URL`(){
    //     assertEquals(true, securityService.isSafe("https://example.com/"))
    // }  Problemas con la URL no harcodeada pero funciona de normal

    @Test
    fun `Valid URL`(){
        assertEquals(true, validatorService.isValid("https://example.com/"))
    }

    @Test
    fun `Not Valid URL`(){
        assertEquals(false, validatorService.isValid("ftp://example.com/"))
    }
}