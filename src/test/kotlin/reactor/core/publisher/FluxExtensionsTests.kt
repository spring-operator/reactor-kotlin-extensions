package reactor.core.publisher

import org.junit.Assert
import org.junit.Test
import reactor.test.verifyError
import java.io.IOException

class FluxExtensionsTests {

    @Test
    fun `Throwable to Flux`() {
        IllegalStateException()
                .toFlux<Any>()
                .test()
                .verifyError(IllegalStateException::class)
    }

    @Test
    fun `cast() with KClass parameter`() {
        val fluxOfAny: Flux<Any> = Flux.just("foo")
        fluxOfAny.cast(String::class).test().expectNext("foo").verifyComplete()
    }

    @Test
    fun `cast() with generic parameter`() {
        val fluxOfAny: Flux<Any> = Flux.just("foo")
        fluxOfAny.cast<String>().test().expectNext("foo").verifyComplete()
    }

    @Test
    fun doOnError() {
        val fluxOnError: Flux<Any> = IllegalStateException().toFlux()
        var invoked = false
        fluxOnError.doOnError(IllegalStateException::class, {
            invoked = true
        }).subscribe()
        Assert.assertTrue(invoked)
    }

    @Test
    fun onErrorMap() {
        IOException()
                .toFlux<Any>()
                .onErrorMap(IOException::class, ::IllegalStateException)
                .test()
                .verifyError<IllegalStateException>()
    }

    @Test
    fun `ofType() with KClass parameter`() {
        arrayOf("foo", 1).toFlux().ofType(String::class).test().expectNext("foo").verifyComplete()
    }

    @Test
    fun `ofType() with generic parameter`() {
        arrayOf("foo", 1).toFlux().ofType<String>().test().expectNext("foo").verifyComplete()
    }

    @Test
    fun onErrorResume() {
        IOException()
                .toFlux<String>()
                .onErrorResume(IOException::class, { "foo".toMono() })
                .test()
                .expectNext("foo")
                .verifyComplete()
    }

    @Test
    fun onErrorReturn() {
        IOException()
                .toFlux<String>()
                .onErrorReturn(IOException::class, "foo")
                .test()
                .expectNext("foo")
                .verifyComplete()
    }

}
