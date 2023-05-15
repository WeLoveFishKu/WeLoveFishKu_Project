package id.fishku.consumer.core.data.source.remote

import android.util.Log
import com.google.gson.JsonObject
import id.fishku.consumer.core.data.source.remote.network.*
import id.fishku.consumer.core.data.source.remote.request.NotificationRequest
import id.fishku.consumer.core.data.source.remote.request.OtpRequest
import id.fishku.consumer.core.data.source.remote.response.*
import id.fishku.consumer.core.utils.getErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(
    private val mainApiService: MainApiService,
    private val modelApiService: ModelApiService,
    private val waApiService: WaApiService,
    private val notifyService: NotificationService
) {
    suspend fun loginUser(email: String, password: String): Flow<ApiResponse<LoginResponse>> =
        flow {
            try {
                val response = mainApiService.loginUser(email, password)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val message = e.getErrorMessage()
                        Log.d("LOGIN", "loginUser: $message")
                        if (message != null) {
                            emit(ApiResponse.Error(message))
                        }
                    }
                    else -> {
                        emit(ApiResponse.Error(e.message.toString()))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        address: String,
    ): Flow<ApiResponse<RegisterResponse>> =
        flow {
            try {
                val response =
                    mainApiService.registerUser(name, email, password, phoneNumber, address)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val message = e.getErrorMessage()
                        if (message != null) {
                            emit(ApiResponse.Error(message))
                        }
                    }
                    else -> {
                        emit(ApiResponse.Error(e.message.toString()))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getAllFish(): Flow<ApiResponse<List<FishItem>>> = flow {
        try {
            val response = mainApiService.getAllFish()
            val fishes = response.fishes
            if (!fishes.isNullOrEmpty()) {
                emit(ApiResponse.Success(fishes))
            } else {
                emit(ApiResponse.Empty)
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun searchFishes(query: String): Flow<ApiResponse<List<FishItem>>> = flow {
        try {
            val response = mainApiService.searchFishes(query)
            val fishes = response.fishes
            if (fishes != null) {
                emit(ApiResponse.Success(fishes))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDetailFish(fishID: Int): Flow<ApiResponse<DetailFishItem>> = flow {
        try {
            val response = mainApiService.getDetilFish(fishID)
            val fish = response.fishes?.get(0)
            if (fish != null) {
                emit(ApiResponse.Success(fish))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getCart(consumerID: Int): Flow<ApiResponse<List<CartItem>>> = flow {
        try {
            val response = mainApiService.getCart(consumerID)
            val carts = response.data
            if (carts != null) {
                emit(ApiResponse.Success(carts))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun editCart(cartID: Int, newWeight: Int): Flow<ApiResponse<String>> = flow {
        try {
            val response = mainApiService.editCart(cartID, newWeight)
            val message = response.message
            if (message != null) {
                emit(ApiResponse.Success(message))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun deleteCart(cartID: Int): Flow<ApiResponse<String>> = flow {
        try {
            val response = mainApiService.deleteCart(cartID)
            val message = response.message
            if (message != null) {
                emit(ApiResponse.Success(message))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun inputCart(
        fishID: Int,
        consumerID: Int,
        notes: String,
        weight: Int,
    ): Flow<ApiResponse<String>> = flow {
        try {
            val response = mainApiService.inputCart(fishID, consumerID, notes, weight)
            val message = response.message
            if (message != null) {
                emit(ApiResponse.Success(message))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getAllOrderByIdConsumer(consumerID: Int): Flow<ApiResponse<List<OrderItem>>> =
        flow {
            try {
                val response = mainApiService.getAllOrderByIdConsumer(consumerID)
                val orders = response.orders
                if (orders != null) {
                    emit(ApiResponse.Success(orders))
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val message = e.getErrorMessage()
                        if (message != null) {
                            emit(ApiResponse.Error(message))
                        }
                    }

                    else -> emit(ApiResponse.Error(e.message.toString()))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getOrderDetail(
        consumerID: Int,
        orderingID: Int,
    ): Flow<ApiResponse<List<OrderDetailItem>>> =
        flow {
            try {
                val response = mainApiService.getOrderDetail(consumerID, orderingID)
                val orderDetail = response.orderDetail
                if (orderDetail != null) {
                    emit(ApiResponse.Success(orderDetail))
                }
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val message = e.getErrorMessage()
                        if (message != null) {
                            emit(ApiResponse.Error(message))
                        }
                    }

                    else -> emit(ApiResponse.Error(e.message.toString()))
                }
            }
        }.flowOn(Dispatchers.IO)

    suspend fun inputOrder(
        date: String,
        notes: String,
        status: String,
        courier: String,
        address: String,
        invoiceUrl: String,
        latitude: Double,
        longitude: Double,
    ): Flow<ApiResponse<InputOrderResponse>> = flow {
        try {
            val response =
                mainApiService.inputOrder(
                    date,
                    notes,
                    status,
                    courier,
                    address,
                    invoiceUrl,
                    latitude.toString(),
                    longitude.toString()
                )
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun inputOrderDetail(
        cunsomerID: Int,
        orderData: JsonObject,
    ): Flow<ApiResponse<InputOrderDetailResponse>> = flow {
        try {
            val response =
                mainApiService.inputOrderDetail(cunsomerID, orderData)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun uploadImageDetection(
        fishName: String?,
        image: MultipartBody.Part,
    ): Flow<ApiResponse<DetectionFishResponse>> = flow {
        try {
            val response = modelApiService.uploadImageDetection(fishName, image)
            val prediction = response.prediction
            if (prediction != null) {
                emit(ApiResponse.Success(response))
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getListFishDetection(): Flow<ApiResponse<List<FishDetectionItem>>> = flow {
        try {
            val response = mainApiService.getListFishDetection()
            val fishes = response.fishes
            if (!fishes.isNullOrEmpty()) {
                emit(ApiResponse.Success(fishes))
            } else {
                emit(ApiResponse.Empty)
            }
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun createInvoice(
        externalID: String,
        payerEmail: String,
        description: String,
        amount: Int,
    ): Flow<ApiResponse<InvoiceResponse>> = flow {
        try {
            val response =
                mainApiService.createInvoice(externalID, payerEmail, description, amount)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun sendCodeOtp(request: OtpRequest): Flow<ApiResponse<OtpResponse>> = flow {
        try {
            val response = waApiService.sendOtpCode(request)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.message
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun pushNotification(request: NotificationRequest): Flow<ApiResponse<NotificationResponse>> = flow {
        try {
            val response = notifyService.pushNotification(request)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.message
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }

                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun editProfile(
        consumerID: Int,
        name: RequestBody,
        phoneNumber: RequestBody,
        phone: RequestBody,
        photoUrl: MultipartBody.Part,
    ): Flow<ApiResponse<EditProfileResponse>> = flow {
        try {
            val response =
                mainApiService.editProfile(consumerID, name, phoneNumber, phone, photoUrl)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            when (e) {
                is HttpException -> {
                    val message = e.getErrorMessage()
                    if (message != null) {
                        emit(ApiResponse.Error(message))
                    }
                }
                else -> emit(ApiResponse.Error(e.message.toString()))
            }
        }
    }.flowOn(Dispatchers.IO)
}