package app.library

import DataManagerFactory
import dao.ShowManager
import io.kotlintest.Spec
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk

class LibraryManagerTest : StringSpec() {

    private val dataManagerFactory: DataManagerFactory = mockk()

    private lateinit var libraryManager: LibraryManager

    override fun beforeSpec(spec: Spec) {
        val showManagerMock = mockk<ShowManager>()
        every { showManagerMock.get(any()) } returns null
        every { showManagerMock.insert(any()) } returns ""
        every { dataManagerFactory.showDao } returns showManagerMock

        libraryManager = LibraryManager(dataManagerFactory)
    }

    init {
//        "Creation of show with foreign characters in the name" {
//            val show = libraryManager.getOrCreateShow("tt3163844")
//
//            show.shouldNotBeNull()
//        }
    }
}