package com.geekbrains.tests.automator

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class BehaviorTest {

    //Класс UiDevice предоставляет доступ к вашему устройству.
    //Именно через UiDevice вы можете управлять устройством, открывать приложения
    //и находить нужные элементы на экране
    private val uiDevice: UiDevice = UiDevice.getInstance(getInstrumentation())

    //Контекст нам понадобится для запуска нужных экранов и получения packageName
    private val context = ApplicationProvider.getApplicationContext<Context>()

    //Путь к классам нашего приложения, которые мы будем тестировать
    private val packageName = context.packageName

    @Before
    fun setup() {
        //Для начала сворачиваем все приложения, если у нас что-то запущено
        uiDevice.pressHome()

        //Запускаем наше приложение
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        //Мы уже проверяли Интент на null в предыдущем тесте, поэтому допускаем, что Интент у нас не null
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)//Чистим бэкстек от запущенных ранее Активити
        context.startActivity(intent)

        //Ждем, когда приложение откроется на смартфоне чтобы начать тестировать его элементы
        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT)
    }

    //Убеждаемся, что приложение открыто. Для этого достаточно найти на экране любой элемент
    //и проверить его на null
    @Test
    fun test_MainActivityIsStarted() {
        //Через uiDevice находим editText
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        //Проверяем на null
        Assert.assertNotNull(editText)
    }

    //Убеждаемся, что поиск работает как ожидается
    @Test
    fun test_SearchIsPositive() {
        //Через uiDevice находим editText
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        //Устанавливаем значение
        editText.text = "UiAutomator"

        val searchButton: UiObject2 = uiDevice.findObject(By.res(packageName, "searchButton"))

        searchButton.click()

        //Ожидаем конкретного события: появления текстового поля totalCountTextView.
        //Это будет означать, что сервер вернул ответ с какими-то данными, то есть запрос отработал.
        val changedText =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )
        //Убеждаемся, что сервер вернул корректный результат. Обратите внимание, что количество
        //результатов может варьироваться во времени, потому что количество репозиториев постоянно меняется.
        Assert.assertEquals(changedText.text.toString(), "Number of results: 672")
    }

    //Убеждаемся, что DetailsScreen открывается
    @Test
    fun test_OpenDetailsScreen() {
        //Находим кнопку
        val toDetails: UiObject2 = uiDevice.findObject(
            By.res(
                packageName,
                "toDetailsActivityButton"
            )
        )
        //Кликаем по ней
        toDetails.click()

        //Ожидаем конкретного события: появления текстового поля totalCountTextView.
        //Это будет означать, что DetailsScreen открылся и это поле видно на экране.
        val changedText =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )
        //Убеждаемся, что поле видно и содержит предполагаемый текст.
        //Обратите внимание, что текст должен быть "Number of results: 0",
        //так как мы кликаем по кнопке не отправляя никаких поисковых запросов.
        //Чтобы проверить отображение определенного количества репозиториев,
        //вам в одном и том же методе нужно отправить запрос на сервер и открыть DetailsScreen.
        Assert.assertEquals(changedText.text, "Number of results: 0")
    }

    @Test
    fun test_numberOfResults_passToDetailsActivity_correct() {
        val editText = uiDevice.wait(
            Until.findObject(By.res(packageName, "searchEditText")),
            TIMEOUT
        )
        editText.text = "UiAutomator"

        val searchButton: UiObject2 = uiDevice.findObject(By.res(packageName, "searchButton"))
        searchButton.click()

        val totalCountTextView = uiDevice.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")),
            TIMEOUT
        )
        val totalCount = totalCountTextView.text

        val toDetailsButton: UiObject2 =
            uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))

        toDetailsButton.click()

        val detailsTotalCountTextView: UiObject2 =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )

        Assert.assertEquals(totalCount, detailsTotalCountTextView.text)
    }

    @Test
    fun test_searchButton_click_when_inputText_isEmpty_doNothing() {
        val searchButton: UiObject2 = uiDevice.findObject(By.res(packageName, "searchButton"))
        searchButton.click()

        val totalCount: UiObject2? = uiDevice.findObject(By.res(packageName, "totalCountTextView"))

        Assert.assertNull(totalCount)
    }

    @Test
    fun test_onCloseOpen_doNotCrash() {
        val editText = uiDevice.findObject(By.res(packageName, "searchEditText"))
        editText.text = "UiAutomator"

        val searchButton: UiObject2 = uiDevice.findObject(By.res(packageName, "searchButton"))
        searchButton.click()

        uiDevice.pressHome()

        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)

        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)), TIMEOUT)

        val textView: UiObject2? =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )

        Assert.assertNotNull(textView)
    }

    @Test
    fun test_onDeviceRotation_MainActivity_doNotCrash() {
        val searchText = "UiAutomator"

        var editText: UiObject2? = uiDevice.wait(
            Until.findObject(By.res(packageName, "searchEditText")),
            TIMEOUT
        )

        editText?.text = searchText

        uiDevice.setOrientationLeft()
        uiDevice.freezeRotation()

        editText = uiDevice.wait(
            Until.findObject(By.res(packageName, "searchEditText")),
            TIMEOUT
        )

        Assert.assertNotNull(editText)
        Assert.assertEquals(searchText, editText.text)

        uiDevice.unfreezeRotation()
        uiDevice.setOrientationNatural()

        editText = uiDevice.wait(
            Until.findObject(By.res(packageName, "searchEditText")),
            TIMEOUT
        )

        Assert.assertNotNull(editText)
        Assert.assertEquals(searchText, editText.text)
    }

    @Test
    fun test_DetailsActivity_buttons_workCorrect() {
        val toDetails: UiObject2 = uiDevice.findObject(
            By.res(
                packageName,
                "toDetailsActivityButton"
            )
        )

        toDetails.click()

        val changedText =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )
        Assert.assertEquals(changedText.text, "Number of results: 0")

        val plusButton = uiDevice.findObject(By.res(packageName, "incrementButton"))
        val minusButton = uiDevice.findObject(By.res(packageName, "decrementButton"))

        for (i in 1..10) {
            plusButton.click()
        }

        Assert.assertEquals(changedText.text, "Number of results: 10")

        for (i in 1..20) {
            minusButton.click()
        }

        Assert.assertEquals(changedText.text, "Number of results: -10")
    }

    @Test
    fun test_onDeviceRotation_DetailsActivity_doNotCrash() {
        val numberOfResultsText = "Number of results: 0"

        val toDetails: UiObject2 = uiDevice.findObject(
            By.res(
                packageName,
                "toDetailsActivityButton"
            )
        )

        toDetails.click()

        var totalCount =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )
        Assert.assertEquals(totalCount.text, numberOfResultsText)

        uiDevice.setOrientationLeft()
        uiDevice.freezeRotation()

        totalCount =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )

        Assert.assertNotNull(totalCount)
        Assert.assertEquals(numberOfResultsText, totalCount.text)

        uiDevice.unfreezeRotation()
        uiDevice.setOrientationNatural()

        totalCount =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )

        Assert.assertNotNull(totalCount)
        Assert.assertEquals(numberOfResultsText, totalCount.text)
    }

    @Test
    fun test_backButton_correct() {
        val editText = uiDevice.wait(
            Until.findObject(By.res(packageName, "searchEditText")),
            TIMEOUT
        )
        editText.text = "UiAutomator"

        val searchButton: UiObject2 = uiDevice.findObject(By.res(packageName, "searchButton"))
        searchButton.click()

        var totalCountTextView = uiDevice.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")),
            TIMEOUT
        )
        val totalCount = totalCountTextView.text

        val toDetailsButton: UiObject2 =
            uiDevice.findObject(By.res(packageName, "toDetailsActivityButton"))

        toDetailsButton.click()

        val detailsTotalCountTextView: UiObject2 =
            uiDevice.wait(
                Until.findObject(By.res(packageName, "totalCountTextView")),
                TIMEOUT
            )

        Assert.assertNotNull(detailsTotalCountTextView)

        uiDevice.pressBack()

        totalCountTextView = uiDevice.wait(
            Until.findObject(By.res(packageName, "totalCountTextView")),
            TIMEOUT
        )

        Assert.assertEquals(totalCount, totalCountTextView.text)
    }

    companion object {
        private const val TIMEOUT = 5000L
    }
}
