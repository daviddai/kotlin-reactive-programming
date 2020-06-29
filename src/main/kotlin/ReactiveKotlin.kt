import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toObservable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

fun main() {
    println("=== Print element by call fun that returns an Observable ===")
    getObservableFromList(listOf("Java", "Kotlin", "", "Javascript"))
        .subscribe(
            { element -> println("item: $element") },
            { err -> println("error: $err") }
        )

    println("=== Mapping array by using zip ===")
    getObservableFromMappingLists(arrayOf("Roses", "Sunflowers", "Leaves", "Clouds", "Violets", "Plastics"), arrayOf("Red", "Yellow", "Green", "White or Grey", "Purple"))
        .subscribe { combined -> println(combined) }

    println("=== Merged array by using mergeFlat ===")
    getObservableFromMergingLists(arrayOf(arrayOf("Java", "Kotlin"), arrayOf("Javascript")))
        .subscribe { mergedArray -> mergedArray.forEach { println(it) } }


    println("=== Reactive Even or Odd ===")
    val subject = getSubjectWithEvenOddCheck()
    val observable = listOf<Int>(1, 2, 3, 4, 5).toObservable()
    observable.subscribe(subject)

    println("=== Fib Sequence for first 7 numbers ===")
    printFibSequence(7)

    val observer: Observer<Any> = object : Observer<Any> {

        override fun onSubscribe(d: Disposable) {
            println("Subscribed to $d")
        }

        override fun onNext(t: Any) {
            println("Next $t")
        }

        override fun onError(e: Throwable) {
            println("Error occurred: $e")
        }

        override fun onComplete() {
            println("All completed")
        }
    }

    println("==== Test Observer and Observable ===")
    val data = listOf(1, 2, 3, 4)
    data.toObservable().subscribe(observer)

    println("=====================================")
    observerStopsAt(3)

    println("=== Test Hot Observable ===")
    hotObservable()
}

fun getObservableFromList(list: List<String>) = Observable.create<String> { emitter ->
    list.forEach { element ->
        if (element == "") {
            emitter.onError(Exception("No value to show"))
        } else {
            emitter.onNext(element)
        }
    }

    emitter.onComplete()
}

fun getObservableFromMappingLists(items: Array<String>, colours: Array<String>): Observable<String> = Observable.zip(
    Observable.fromArray(*items),
    Observable.fromArray(*colours),
    BiFunction<String, String, String> { item, colour -> "$item is $colour" }
)

fun getObservableFromMergingLists(arrays: Array<Array<String>>): Observable<Array<String>> = Observable
    .fromArray(*arrays)
    .flatMap { array -> Observable.fromArray(array) }

fun getSubjectWithEvenOddCheck(): Subject<Int> {
    val subject: Subject<Int> = PublishSubject.create()

    subject.map { isEven(it) }.subscribe { println("The number $it is ${if (it) "Even" else "Odd"}") }

    return subject
}

fun isEven(number: Int) = number % 2 == 0

fun printFibSequence(number: Int) {
    val sequence = sequence {
        var a = 0
        var b = 1

        yield(a)
        yield(b)

        while (true) {
            val c = a + b
            yield(c)
            a = b
            b = c
        }

    }

    println(sequence.take(number).toList())
}

fun observerStopsAt(second: Int) {
    runBlocking {
        val observable: Observable<Long> = Observable.interval(1, TimeUnit.SECONDS)
        val observer: Observer<Long> = object : Observer<Long> {
            lateinit var disposable: Disposable

            override fun onComplete() {
                println("All completed")
            }

            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

            override fun onNext(item: Long) {
                if (item >= second && !disposable.isDisposed) {
                    disposable.dispose()
                } else {
                    println("Next: $item")
                }
            }

            override fun onError(e: Throwable) {
                println("Error occurred: $e")
            }
        }

        observable.subscribe(observer)

        delay(20000)
    }
}

fun hotObservable() {
    val connectableObserver = Observable
        .interval(100, TimeUnit.MILLISECONDS)
        .publish() // convert from cold observable to hot one

    connectableObserver.subscribe { println("Observer 1: $it") }
    connectableObserver.subscribe { println("Observer 2: $it") }

    connectableObserver.connect() // starts emitting

    runBlocking { delay(500) }

    connectableObserver.subscribe { println("Observer 3: $it") }

    runBlocking { delay(500) }
}