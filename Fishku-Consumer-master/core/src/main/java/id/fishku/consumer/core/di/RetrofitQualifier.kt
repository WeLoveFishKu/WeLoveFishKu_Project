package id.fishku.consumer.core.di

import javax.inject.Qualifier

class RetrofitQualifier {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HttpOtp

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HttpNotify
}