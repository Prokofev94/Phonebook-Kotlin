package phonebook

import java.io.File
import kotlin.math.sqrt

var m = 0
var s = 0
var ms = 0

fun main() {
    val directoryOriginal = fileToList(File("C:/Users/Funck/Downloads/directory.txt"))
    val find = fileToList(File("C:/Users/Funck/Downloads/find.txt"))

    var directory = directoryOriginal.toMutableList()
    println("Start searching (linear search)...")
    var start = System.currentTimeMillis()
    var found = linearSearch(find, directory)
    val linearSearchTime = System.currentTimeMillis() - start
    printResultTime(found, linearSearchTime)
    println()


    val maxSortTime = linearSearchTime * 10
    directory = directoryOriginal.toMutableList()
    println("Start searching (bubble sort + jump search)...")
    start = System.currentTimeMillis()
    val isSorted = bubbleSort(directory, maxSortTime)
    var endSort = System.currentTimeMillis()
    found = if (isSorted) {
        jumpSearch(find, directory)
    } else {
        linearSearch(find, directory)
    }
    var end = System.currentTimeMillis()
    printResultTime(found, end - start)
    printSortTime(endSort - start, isSorted)
    printSearchTime(end - endSort)


    directory = directoryOriginal.toMutableList()
    println("Start searching (quick sort + binary search)...")
    start = System.currentTimeMillis()
    directory = quickSort(directory).toMutableList()
    endSort = System.currentTimeMillis()
    found = binarySearch(find, directory)
    end = System.currentTimeMillis()
    printResultTime(found, end - start)
    printSortTime(endSort - start)
    printSearchTime(end - endSort)


    directory = directoryOriginal.toMutableList()
    println("Start searching (hash table)...")
    start = System.currentTimeMillis()
    val hashTable = createHashTable(directory)
    val endCreate = System.currentTimeMillis()
    found = hashTableSearch(find, hashTable, directory.size)
    end = System.currentTimeMillis()
    printResultTime(found, end - start)
    printCreateTime(endCreate - start)
    printSearchTime(end - endCreate)
}

fun linearSearch(find: List<String>, directory: List<String>) = find.count { linearSearch(it, directory) > 0 }

fun linearSearch(person: String, directory: List<String>): Int {
    directory.forEach { if (person in it) return 1 }
    return 0
}

fun jumpSearch(find: List<String>, directory: List<String>) = find.count { jumpSearch(it, directory) > 0 }

fun jumpSearch(person: String, directory: List<String>): Int {
    val step = sqrt(directory.size.toDouble()).toInt()
    for (i in directory.indices step step) {
        if (person in directory[i]) return 1
        if (person < directory[i]) return linearSearch(person, directory.subList(i - step, i))
    }
    return 0
}

fun binarySearch(find: List<String>, directory: List<String>) = find.count { binarySearch(it, directory) > 0 }

fun binarySearch(person: String, directory: List<String>): Int {
    var left = 0
    var right = directory.lastIndex
    while (left <= right) {
        val middle = (left + right) / 2
        if (person in directory[middle]) return 1
        if (directory[middle].split(Regex(" "), 2)[1] > person) {
            right = middle - 1
        } else {
            left = middle + 1
        }
    }
    return 0
}

fun bubbleSort(list: MutableList<String>, maxTime: Long): Boolean {
    val start = System.currentTimeMillis()
    for (i in 1 until list.size) {
        for (j in 0 until list.size - i) {
            if (list[j].split(Regex(" "), 2)[1] > list[j + 1].split(Regex(" "), 2)[1]) {
                val temp = list[j]
                list[j] = list[j + 1]
                list[j + 1] = temp
            }
            if (System.currentTimeMillis() - start > maxTime) return false
        }
    }
    return true
}

fun quickSort(list: List<String>): List<String> {
    if (list.size < 2) return list
    val pivot = list.last()
    val left = mutableListOf<String>()
    val right = mutableListOf<String>()
    for (i in 0 until list.lastIndex) {
        if (list[i].split(Regex(" "), 2)[1] > pivot.split(Regex(" "), 2)[1]) {
            right += list[i]
        } else {
            left += list[i]
        }
    }
    return buildList {
        addAll(quickSort(left))
        add(pivot)
        addAll(quickSort(right))
    }
}

fun createHashTable(list: List<String>): HashMap<Int, MutableMap<String, String>> {
    val hashMap = HashMap<Int, MutableMap<String, String>>(list.size)
    list.forEach {
        val (value , key) = it.split(Regex(" "), 2)
        val hash = key.hashCode() % list.size
        if (hashMap[hash].isNullOrEmpty()) {
            hashMap[hash] = mutableMapOf(key to value)
        } else {
            hashMap[hash]!![key] = value
        }
    }
    return hashMap
}

fun hashTableSearch(find: List<String>, hashMap: HashMap<Int, MutableMap<String, String>>, h: Int) =
    find.count { hashMap[it.hashCode() % h]?.contains(it) ?: true }

fun fileToList(file: File) = file.readText().split("\n").toMutableList()

fun printResultTime(found: Int, time: Long) {
    countTime(time)
    println("Found $found / 500 entries. Time taken: $m min. $s sec. $ms ms.")
}

fun printSortTime(time: Long) = printSortTime(time, false)

fun printSortTime(time: Long, stop: Boolean) {
    countTime(time)
    print("Sorting time: $m min. $s sec. $ms ms.")
    if (stop) print(" - STOPPED, moved to linear search")
    println()
}

fun printSearchTime(time: Long) {
    countTime(time)
    println("Searching time: $m min. $s sec. $ms ms.\n")
}

fun printCreateTime(time: Long) {
    countTime(time)
    println("Creating time: $m min. $s sec. $ms ms.")
}

fun countTime(time: Long) {
    m = (time / 60000).toInt()
    s = (time / 1000 % 60).toInt()
    ms = (time % 1000).toInt()
}