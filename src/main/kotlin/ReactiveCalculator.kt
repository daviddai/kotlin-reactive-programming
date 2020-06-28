import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class ReactiveCalculator(a: Int, b: Int) {
    val subjectCalculator: Subject<ReactiveCalculator> = PublishSubject.create()

    var nums:Pair<Int, Int> = Pair(0, 0)

    init {
        nums = Pair(a, b)

        subjectCalculator.subscribe {
            calculateAddition()
            calculateSubtraction()
            calculateMultiplicaiton()
            calculateDivision()
        }

        subjectCalculator.onNext(this)
    }

    private inline fun calculateAddition() {
        println("Add = ${nums.first + nums.second}")
    }

    private inline fun calculateSubtraction() {
        println("Sub = ${nums.first - nums.second}")
    }

    private inline fun calculateMultiplicaiton() {
        println("Sub = ${nums.first * nums.second}")
    }

    private inline fun calculateDivision() {
        println("Sub = ${nums.first / (nums.second * 1.0)}")
    }

    inline fun modifyNumbers(a: Int, b: Int) {
        nums = Pair(a, b)
        subjectCalculator.onNext(this)
    }

}

fun main() {
    val reactiveCalculator = ReactiveCalculator(1, 2)
    reactiveCalculator.modifyNumbers(3, 4)
}