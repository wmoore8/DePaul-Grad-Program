//
//  ViewController.swift
//  MooreW_assn3
//
//  Created by William Moore on 2/5/20.
//  Copyright © 2020 Willy Moore. All rights reserved.
//

import UIKit
import SwiftUI

class ViewController: UIViewController {
    
    var currentNumber: Int = 0
    var previousNumber: String = ""
    var isMathOperatorPressed: Bool = false
    
    

    @IBOutlet weak var Result: UILabel!
    
    @IBAction func calculatorNumber(_ sender: UIButton) {
        
        if let toUser = sender.currentTitle {
            previousNumber.append(toUser)
            Result.text = "\(previousNumber )"
        }
    }
    
    
    @IBAction func calculatorOperation(_ sender: UIButton) {
        isMathOperatorPressed = true
        
        if sender.currentTitle == "+" {
            
            if let tempVar = Int(previousNumber) {
                currentNumber = currentNumber + tempVar
            }
        
            previousNumber = ""
            
        } else if sender.currentTitle == "=" {
            
            if let tempVar = Int(previousNumber) {
                currentNumber = currentNumber + tempVar
            }
            
            previousNumber = ""
            
            Result.text = "\(currentNumber)"
            
            isMathOperatorPressed = false
            
        }
        
    }
    
    @IBAction func calculatorClear(_ sender: UIButton) {
        
        previousNumber = ""
        currentNumber = 0
        
        Result.text = ""
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }


}

