package com.example.composeuitestsample

import javax.inject.Inject

class SampleRepository @Inject constructor() {
    fun getSampleData(): String {
        return "Sample Data"
    }
}
