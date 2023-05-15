package id.fishku.consumer.core.utils

import id.fishku.consumer.core.domain.model.Fish
import id.fishku.consumer.core.domain.model.Order
import id.fishku.consumer.core.domain.model.Track

object DummyData {
    fun getAllFishes(): List<Fish> {
        val fishes = ArrayList<Fish>()
        for (i in 1..10) {
            val fish = Fish(
                fishID = i,
                name = "Ikan $i",
                location = "Location $i",
                weight = 5 * i,
                price = 500 * i
            )
            fishes.add(fish)
        }
        return fishes
    }

    fun getTrackOrder() = listOf(
        Track(
            title = "Send by seller",
            content = "order 342320823 has been sent by the seller on 12-04-2021, and received at the place of delivery of the package on 13-04-2021",
            date = "16-07-2021 12.10 PM",
            isActive = false
        ),
        Track(
            title = "Send to Semarang",
            content = "order 342320823 has been sent by the seller on 12-04-2021, and received at the place of delivery of the package on 13-04-2021",
            date = "16-07-2021 12.10 PM",
            isActive = false

        ),
        Track(
            title = "Send to you home",
            content = "order 342320823 has been sent by the seller on 12-04-2021, and received at the place of delivery of the package on 13-04-2021",
            date = "16-07-2021 12.10 PM",
            isActive = true
        )
    )
}