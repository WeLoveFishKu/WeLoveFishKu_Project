package id.fishku.fishersellercore.di

import javax.inject.Qualifier

class RetrofitQualifier {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitOtp

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HttpOtp

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ParserOtp

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitSeller

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HttpSeller

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ParserSeller

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitNotify

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class HttpNotify

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class ParserNotify

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class FireSourceSeller
}