import io.reactivex.Observable
import io.reactivex.functions.BiFunction

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
