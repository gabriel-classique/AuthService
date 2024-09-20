package com.xcvi.firebasesample

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth{
        return Firebase.auth
    }
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase{
        return Firebase.database
    }

    @Provides
    @Singleton
    fun provideAuthService(auth: FirebaseAuth, db: FirebaseDatabase): AuthenticationService{
        return AuthenticationService(auth, db)
    }
    @Provides
    @Singleton
    fun provideDataRepo(auth: FirebaseAuth, db: FirebaseDatabase): DataRepository{
        return DataRepository(auth, db)
    }
}