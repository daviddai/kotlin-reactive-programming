import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

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
    subject.onNext(10)
    subject.onNext(11)
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

    subject.map { isEven(it) }.subscribe { println("The number is ${if (it) "Even" else "Odd"}") }

    return subject
}

fun isEven(number: Int) = number % 2 == 0
