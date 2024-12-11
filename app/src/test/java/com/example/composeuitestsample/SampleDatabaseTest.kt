package com.example.composeuitestsample

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isRoot
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
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
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        testDispatcher: TestDispatcher
    ): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "bird_database"
        )
            .allowMainThreadQueries() // test only
//            .setQueryExecutor(testDispatcher.asExecutor())
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
    fun verify_database() = runTest(testDispatcher) {
        assert(birdRepository.getAllBirds().isEmpty())
        birdRepository.insertBirds(Bird("1", "bird1", "red"))
        birdRepository.insertBirds(Bird("2", "bird2", "blue"))
        assert(birdRepository.getAllBirds().size == 2)
    }

    @Inject
    lateinit var viewModel: SampleViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_database_query() = runTest(testDispatcher) {
        birdRepository.insertBirds(Bird("1", "bird1", "red"))

        rule.composeRule.apply {
            setContent {
                val state by viewModel.birdsByQuery.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.fetchBirdsByQuery()
                }

                if (state?.size == 1) {
                    Text("loaded")
                }
            }

            testDispatcher.scheduler.advanceTimeBy(1000)
            testDispatcher.scheduler.runCurrent()
            onNode(isRoot())
                .assertIsDisplayed()
        }
    }

    @Config(qualifiers = RobolectricDeviceQualifiers.Pixel7)
    @Test
    fun verify_database_flow() = runTest(testDispatcher) {
        birdRepository.insertBirds(Bird("1", "bird1", "red"))

        rule.composeRule.apply {
            setContent {
                val state by viewModel.birds.collectAsState()

                if (state?.size == 1) {
                    Text("loaded")
                }
            }
            testDispatcher.scheduler.runCurrent()
            onNode(isRoot())
                .assertIsDisplayed()
        }

    }

}

class SampleViewModel @Inject constructor(private val birdRepository: BirdRepository) :
    ViewModel() {
    private var _birdsByQuery: MutableStateFlow<List<Bird>?> = MutableStateFlow(null)
    val birdsByQuery: StateFlow<List<Bird>?> = _birdsByQuery

    fun fetchBirdsByQuery() {
        viewModelScope.launch {
            _birdsByQuery.value = birdRepository.getAllBirds()
        }
    }

    val birds: StateFlow<List<Bird>?> = birdRepository.getAllBirdsFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )

}
