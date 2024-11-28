package com.example.composeuitestsample

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composeuitestsample.data.AppDatabase
import com.example.composeuitestsample.data.Bird
import com.example.composeuitestsample.data.BirdRepository
import com.example.composeuitestsample.data.DatabaseModule
import com.example.composeuitestsample.data.DispatcherModule
import com.example.composeuitestsample.data.IoDispatcher
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import org.robolectric.annotation.Config

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DispatcherModule::class])
class TestDispatcherModule {
    @Provides
    @Singleton
    fun provideTestDispatcher(): TestDispatcher = StandardTestDispatcher()

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher
}

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
object TestDatabaseModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "bird_database"
        )
            .allowMainThreadQueries() // test only
            .build()
    }
}


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class SampleDatabaseTest {
    class HiltInjectRule(private val rule: HiltAndroidRule) : TestWatcher() {
        override fun starting(description: Description?) {
            super.starting(description)
            rule.inject()
        }
    }

    class HiltAndComposeRule(private val testInstance: Any) : TestRule {
        val composeRule = createComposeRule()
        override fun apply(base: Statement?, description: Description?): Statement {
            val hiltAndroidRule = HiltAndroidRule(testInstance)
            return RuleChain.outerRule(hiltAndroidRule)
                .around(HiltInjectRule(hiltAndroidRule))
                .around(composeRule)
                .apply(base, description)
        }
    }

    @get:Rule(order = 0)
    val rule: HiltAndComposeRule = HiltAndComposeRule(this)

    @get:Rule(order = 1)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var birdRepository: BirdRepository

    @Inject
    lateinit var testDispatcher: TestDispatcher

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_database() = runTest(testDispatcher.scheduler) {
        assert(birdRepository.getAllBirds().isEmpty())
        birdRepository.insertBirds(Bird("1", "bird1", "red"))
        birdRepository.insertBirds(Bird("2", "bird2", "blue"))
        assert(birdRepository.getAllBirds().size == 2)
    }

    @Inject
    lateinit var viewModel: SampleViewModel

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_database_flow() {
        val dispatcher = testDispatcher
        Dispatchers.setMain(dispatcher)
        runTest(testDispatcher) {
//        birdRepository.insertBirds(Bird("1", "bird1", "red"))
//        birdRepository.insertBirds(Bird("2", "bird2", "blue"))

            rule.composeRule.setContent {
//            val state by birdRepository.getAllBirdsFlow()
//                .collectAsState(listOf(Bird("0", "bird0", "green")))

                val state by viewModel.birds.collectAsState()

                if (state.isEmpty()) {
                    Text("loaded")
                }
            }
            rule.composeRule.onNode(hasText("loaded"))
                .assertIsDisplayed()
        }
        Dispatchers.resetMain()
    }
}

class SampleViewModel @Inject constructor(birdRepository: BirdRepository) : ViewModel() {
    val birds: MutableStateFlow<List<Bird>> = MutableStateFlow(listOf(Bird("0", "bird0", "green")))

    init {
        viewModelScope.launch {
            birdRepository.getAllBirdsFlow()
                .collect {
                    birds.value = it
                }
        }
    }
}
