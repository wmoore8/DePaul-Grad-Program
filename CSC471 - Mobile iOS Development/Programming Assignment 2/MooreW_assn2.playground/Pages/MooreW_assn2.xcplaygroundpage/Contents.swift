/* THIS IS PART 2 THE RANK FUNCTION */

func rank (_ array : [Int],Searching indexToFind : Int ) -> Int {
    return binarySearchHelper(Array: array, Finding: indexToFind)
}

func binarySearchHelper(Array array: [Int], Finding indexToFind : Int) -> Int {
    var min = 0
    var max = array.count - 1
    
    while (min <= max) {
        let mid = min + (max - min) / 2
     
        if (array[mid] == indexToFind) {
            return mid
        } else if (array[mid] < indexToFind) {
            min = mid + 1
        } else {
            max = mid - 1
        }
    }
    return -1;
}

let arrayExample = [10, 20, 30, 45, 56, 90, 986, 1156, 1573]

var test1 = rank(arrayExample,Searching:  56)
var test2 = rank(arrayExample,Searching:  47)
var test3 = rank(arrayExample,Searching:  1573)
var test4 = rank(arrayExample,Searching:  10)

print(test1)
print(test2)
print(test3)
print(test4)



/* THIS IS PART 3 THE FRACTION CLASS */

class Fraction {
    var numerator : Int
    var denominator : Int
    
    init(_ numerator : Int,_ denominator : Int) {
        if (denominator == 0) {
            Swift.print("WARNING: Denominator shouldn't be 0")
        }
        
        self.numerator = numerator
        self.denominator = denominator
    }
    
    init(_ f : Fraction) {
        self.numerator = f.numerator
        self.denominator = f.denominator
    }
    
    /*Next 3 are helper functions*/
    
    func flip() -> Fraction {
        let f = Fraction(self.denominator, self.numerator)
        return f
    }
    
    /*The following reducing common fraction algorithm came from https://www.geeksforgeeks.org/reduce-the-fraction-to-its-lowest-form/ */
    
    private func gcd(_ numer : Int,_ denom : Int) -> Int {
        if (denom == 0) {
            return numer
        } else {
            return gcd(denom, numer % denom)
        }
    }
    
    func reduce() {
        //let sign = numerator >= 0 ? 1 : -1
        var g : Int
        
        g = gcd(self.numerator, self.denominator)
        
        self.numerator = self.numerator / g
        self.denominator = self.denominator / g
    }
    
    func print() {
        Swift.print("\(numerator)/\(denominator)")
    }
    
    func toString() -> String {
        return "\(numerator)/\(denominator)"
    }
    
    func add(_ f : Fraction) -> Fraction {
        var numer = self.numerator
        let denom = self.denominator
        var tempNumer = f.numerator
        var tempDenom = f.denominator
        
        tempNumer *= denom
        numer *= tempDenom
        tempDenom *= denom
        tempNumer += numer
        
        let toReturn = Fraction(tempNumer, tempDenom)
        toReturn.reduce()
        
        return toReturn
    }
    
    func subtract(_ f: Fraction) -> Fraction {
        let numer = self.numerator
        let denom = self.denominator
        let tempNumer = f.numerator
        let tempDenom = f.denominator
        
        let toReturn = Fraction(((numer * tempDenom) - (denom * tempNumer)), denom * tempDenom)
        toReturn.reduce()
        
        return toReturn
    }
    
    func multiply(_ f : Fraction) -> Fraction {
        let numer = self.numerator
        let denom = self.denominator
        var tempNumer = f.numerator
        var tempDenom = f.denominator
        
        tempNumer *= numer
        tempDenom *= denom
        
        let toReturn = Fraction(tempNumer, tempDenom)
        toReturn.reduce()
        
        return toReturn
    }
    
    func divideby(_ f : Fraction) -> Fraction {
        var toReturn = Fraction(f.flip())
        
        toReturn = toReturn.multiply(self)
        
        toReturn.reduce()
        return toReturn
    }

}

var f1 = Fraction(2, 3)
var f2 = Fraction(2, 4)

f2.reduce()
print("2/4 reduced is \(f2.numerator)/\(f2.denominator)")

var f3 : Fraction = f2.add(f1)
print("\(f2.toString()) + \(f1.toString()) = \(f3.toString())")

f3 = f2.subtract(f1)
print("\(f2.toString()) - \(f1.toString()) = \(f3.toString())")

f3 = f2.multiply(f1)
print("\(f2.toString()) * \(f1.toString()) = \(f3.toString())")

f3 = f2.divideby(f1)
print("\(f2.toString()) / \(f1.toString()) = \(f3.toString())")

f1 = Fraction(-2, 4)

f3 = f2.add(f1)
print("\(f2.toString()) + \(f1.toString()) = \(f3.toString())")

f3 = f2.subtract(f1)
print("\(f2.toString()) - \(f1.toString()) = \(f3.toString())")

f3 = f2.multiply(f1)
print("\(f2.toString()) * \(f1.toString()) = \(f3.toString())")

f3 = f2.divideby(f1)
print("\(f2.toString()) / \(f1.toString()) = \(f3.toString())")







