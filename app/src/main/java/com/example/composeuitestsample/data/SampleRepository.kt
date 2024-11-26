package com.example.composeuitestsample.data

import javax.inject.Inject

class SampleRepository @Inject constructor() {
    fun getSampleData(): String {
        return "Sample Data"
    }
}
